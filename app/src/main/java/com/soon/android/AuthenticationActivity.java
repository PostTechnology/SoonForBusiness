package com.soon.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.soon.android.bmobBean.Store;
import com.soon.android.util.CameraAlbumUtil;
import com.soon.android.util.ProgressDialogUtil;

import java.io.File;
import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class AuthenticationActivity extends AppCompatActivity {

    @BindView(R.id.authentication_toolbar)
    android.support.v7.widget.Toolbar Toolbar;

    @BindView(R.id.bl_img)
    ImageView BlImg;

    @BindView(R.id.store_name)
    EditText StoreNameText;

    @BindView(R.id.store_local)
    TextView StoreLocalText;

    public static final int TAKE_PHOTO = 3;//拍照的标识

    public static final int CHOOSE_PHOTO = 4;//从相册中选择的标识

    private ProgressDialog progressDialog;// 加载框

    private Double longitude;

    private Double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        ButterKnife.bind(this);

        setSupportActionBar(Toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @OnClick({R.id.bl_img,R.id.store_local, R.id.authentication_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bl_img:
                showDialog();
                break;
            case R.id.store_local:
                Intent intent = new Intent(AuthenticationActivity.this, LocalPoiActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.authentication_btn:
                if(StoreLocalText.getText().equals("") || StoreLocalText.getText() == null){
                    Toast.makeText(AuthenticationActivity.this,"请选择好位置后再进行提交！", Toast.LENGTH_SHORT).show();
                }else{
                    savePhoto();
                }
                break;
        }
    }

    private void createStore(BmobFile bmobFile){
        progressDialog = ProgressDialogUtil.getProgressDialog(AuthenticationActivity.this, "提交信息", "Waiting...", false);
        progressDialog.show();
        BmobUser currentUser = BmobUser.getCurrentUser();
        Log.i("TAG", "bmobFile: " + bmobFile.getFilename());
        Store store = new Store();
        store.setUserObjectId(currentUser.getObjectId());
        store.setName(StoreNameText.getText().toString());
        store.setImage(bmobFile);
        store.setLongitude(longitude);
        store.setLatitude(latitude);
        store.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e == null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(AuthenticationActivity.this,"认证成功！请重新登录", Toast.LENGTH_SHORT).show();
                            BmobUser.logOut();   //清除缓存用户对象
                            finish();
                        }
                    });
                }else{
                    Log.i("TAG","认证失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }


    private void savePhoto(){
        //String picPath = imagePath.substring(1);
        //上传文件
        String picPath = CameraAlbumUtil.imagePath;
        Toast.makeText(this, picPath, Toast.LENGTH_SHORT).show();

        final BmobFile bmobFile = new BmobFile(new File(picPath));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    Log.d("TAG", "上传文件成功:" + bmobFile.getFileUrl());
                    createStore(bmobFile);
                }else{
                    Log.d("TAG", "上传文件失败：" + e.getMessage());
                }
            }
            @Override
            public void onProgress(Integer value) {
                super.onProgress(value);
                Log.d("TAG", "onProgress: " + value);
            }
        });
    }

    //显示列表对话框
    private void showDialog() {
        new AlertDialog.Builder(this)
                .setTitle("选择上传方式")
                .setItems(new String[]{"从相册中选择", "拍照上传"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0://从相册中选择
                                CameraAlbumUtil.requestPermissions(AuthenticationActivity.this, CHOOSE_PHOTO);
                                break;
                            case 1://拍照上传
                                CameraAlbumUtil.takePhote(AuthenticationActivity.this, TAKE_PHOTO);
                                break;
                            default:
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CameraAlbumUtil.openAlbum(AuthenticationActivity.this, CHOOSE_PHOTO);
                } else {
                    Toast.makeText(this, "you denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    StoreLocalText.setText(data.getStringExtra("data_return"));
                    longitude = data.getDoubleExtra("data_lng",0d);
                    latitude = data.getDoubleExtra("data_lat",0d);
//                    Toast.makeText(AuthenticationActivity.this, selectAddress + "," + longitude + "," + latitude, Toast.LENGTH_SHORT).show();
                }
                break;
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(CameraAlbumUtil.imageUri));
                        BlImg.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上系统使用这个方法处理图片
                        CameraAlbumUtil.handleImageOnKitKat(AuthenticationActivity.this, data, BlImg);
                    } else {
                        //4.4一下系统使用这个方法处理图片
                        CameraAlbumUtil.handleImageBeforeKitKat(AuthenticationActivity.this, data, BlImg);
                    }
                }
                break;
            default:
                break;
        }
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
        Intent intent = new Intent(context, AuthenticationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
