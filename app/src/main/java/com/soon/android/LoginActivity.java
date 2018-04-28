package com.soon.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.soon.android.bmobBean.Store;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.account)
    EditText accountEdits;

    @BindView(R.id.password)
    EditText passwordEdit;

    @BindView(R.id.login)
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userAccount = accountEdits.getText().toString();
                final String userPassword = passwordEdit.getText().toString();
                BmobUser user = new BmobUser();
                user.setUsername(userAccount);
                user.setPassword(userPassword);
                user.login(new SaveListener<BmobUser>() {
                    @Override
                    public void done(final BmobUser o, BmobException e) {
                        if(e == null){
                            BmobQuery<Store> query = new BmobQuery<>();
                            query.addWhereEqualTo("userObjectId", o.getObjectId());
                            query.findObjects(new FindListener<Store>() {
                                @Override
                                public void done(List<Store> list, BmobException e) {
                                    if (e == null) {
                                        SharedPreferences.Editor editor = getSharedPreferences("store",MODE_PRIVATE).edit();
                                        if(list.size() > 0){
                                            Store store = list.get(0);
                                            editor.putString("name", store.getName());
                                            editor.putString("objectId", store.getObjectId());
                                            editor.putString("image", store.getImage().getFilename());
                                            editor.putString("state", "已认证");
                                            editor.apply();
                                            Toast.makeText(LoginActivity.this, "登录成功！" + store.getName(), Toast.LENGTH_SHORT).show();
                                            Log.d("TAG", "success");
                                            BmobFile bmobFile = store.getImage();
                                            if(bmobFile != null){
                                                downloadFile(bmobFile);
                                            }
                                        }else{
                                            editor.putString("state", "未认证");
                                            editor.apply();
                                        }
                                        finish();
                                    }else{
                                        Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(LoginActivity.this, "用户名或密码错误!", Toast.LENGTH_SHORT).show();
                            Log.d("TAG", "error: " + e.getMessage());
                        }
                    }
                });
            }
        });
    }

    // 将头像缓存到本地
    private void downloadFile(BmobFile file){
        //允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
        File saveFile = new File(Environment.getExternalStorageDirectory(), file.getFilename());
        file.download(saveFile, new DownloadFileListener() {

            @Override
            public void onStart() {
                Toast.makeText(LoginActivity.this, "开始下载...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void done(String savePath,BmobException e) {
                if(e==null){
                    Toast.makeText(LoginActivity.this, "下载成功,保存路径:"+savePath, Toast.LENGTH_SHORT).show();
                    Log.i("Bmob", "done: "+savePath);
                }else{
                    Toast.makeText(LoginActivity.this, "下载失败："+e.getErrorCode()+","+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
                Log.i("bmob","下载进度："+value+","+newworkSpeed);
            }

        });
    }

    // 活动跳转
    public static void actionStart(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
