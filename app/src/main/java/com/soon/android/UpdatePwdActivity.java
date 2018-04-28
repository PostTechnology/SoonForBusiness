package com.soon.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class UpdatePwdActivity extends AppCompatActivity {

    @BindView(R.id.update_pwd_toolbar)
    Toolbar Toolbar;

    @BindView(R.id.old_pwd)
    EditText oldPwdText;

    @BindView(R.id.new_pwd)
    EditText newPwdText;

    @BindView(R.id.check_pwd)
    EditText checkPwdText;

    @BindView(R.id.update_pwd_btn)
    Button updatePwdBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pwd);
        ButterKnife.bind(this);
        setSupportActionBar(Toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        updatePwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPwd = oldPwdText.getText().toString();
                String newPwd = newPwdText.getText().toString();
                if(newPwd.equals(checkPwdText.getText().toString())){
                    BmobUser.updateCurrentUserPassword(oldPwd, newPwd, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e == null){
                                Toast.makeText(UpdatePwdActivity.this, "密码修改成功！请重新登录", Toast.LENGTH_SHORT).show();
                                BmobUser.logOut();   //清除缓存用户对象
                                BmobUser currentUser = BmobUser.getCurrentUser(); // 现在的currentUser是null了
                                finish();
                            }else{
                                Toast.makeText(UpdatePwdActivity.this, "密码修改失败！请联系工作人员", Toast.LENGTH_SHORT).show();
                                Log.d("TAG", "error: " + e.getMessage());
                            }
                        }
                    });
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 活动跳转
    public static void actionStart(Context context){
        Intent intent = new Intent(context, UpdatePwdActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
