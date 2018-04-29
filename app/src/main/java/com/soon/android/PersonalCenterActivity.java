package com.soon.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.soon.android.bmobBean.Store;
import com.soon.android.util.CameraAlbumUtil;
import com.soon.android.util.ProgressDialogUtil;

import java.io.File;
import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class PersonalCenterActivity extends AppCompatActivity {

    @BindView(R.id.personal_center_toolbar)
    Toolbar Toolbar;

    @BindView(R.id.store_new_name)
    EditText StoreNewNameText;

    @BindView(R.id.store_offerPrice)
    EditText StoreOfferPriceText;

    @BindView(R.id.store_description)
    EditText StoreDescriptionText;

    @BindView(R.id.personal_img)
    ImageView PersonalImg;

    public static final int TAKE_PHOTO = 3;//拍照的标识

    public static final int CHOOSE_PHOTO = 4;//从相册中选择的标识

    private ProgressDialog progressDialog;// 加载框

    private boolean isUpImg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        ButterKnife.bind(this);

        setSupportActionBar(Toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        init();
    }

    @OnClick({R.id.personal_img, R.id.update_info_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.personal_img:
                showDialog();
                break;
            case R.id.update_info_btn:
                updateStore();
                finish();
                break;
        }
    }

    private void init(){
        SharedPreferences preferences = getSharedPreferences("store", Context.MODE_PRIVATE);
        String name = preferences.getString("name","");
        String imageUrl = Environment.getExternalStorageDirectory() + "/" + preferences.getString("image","");
        Float offerPrice = preferences.getFloat("offerPrice",0f);
        String description = preferences.getString("description","");

        Bitmap bitmap = BitmapFactory.decodeFile(imageUrl);
        PersonalImg.setImageBitmap(bitmap);
        StoreNewNameText.setText(name);
        StoreOfferPriceText.setText(offerPrice + "");
        StoreDescriptionText.setText(description);
    }

    private void updateStore(){
        if(isUpImg){
            String picPath = CameraAlbumUtil.imagePath;
            Toast.makeText(this, picPath, Toast.LENGTH_SHORT).show();

            final BmobFile bmobFile = new BmobFile(new File(picPath));
            bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        //bmobFile.getFileUrl()--返回的上传文件的完整地址
                        Log.d("TAG", "上传文件成功:" + bmobFile.getFileUrl());
                        progressDialog = ProgressDialogUtil.getProgressDialog(PersonalCenterActivity.this, "提交信息", "Waiting...", false);
                        progressDialog.show();
                        SharedPreferences preferences = getSharedPreferences("store", Context.MODE_PRIVATE);
                        String objectId = preferences.getString("objectId", "");
                        Store store = new Store();
                        store.setName(StoreNewNameText.getText().toString());
                        store.setOfferPrice(Float.parseFloat(StoreOfferPriceText.getText().toString()));
                        store.setDescription(StoreDescriptionText.getText().toString());
                        store.setImage(bmobFile);
                        store.update(objectId, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                            Toast.makeText(PersonalCenterActivity.this,"更新成功!", Toast.LENGTH_SHORT).show();
                                            SharedPreferences.Editor editor = getSharedPreferences("store",MODE_PRIVATE).edit();
                                            editor.putString("name", StoreNewNameText.getText().toString());
//                                            editor.putString("image", bmobFile.getFilename());
                                            try{
                                                editor.putFloat("offerPrice", Float.parseFloat(StoreOfferPriceText.getText().toString()));
                                            }catch (Exception e1){
                                                editor.putFloat("offerPrice", 0f);
//                                                    Log.i("Exception", "错误信息: " + e.getMessage());
                                            }finally {
                                                editor.putString("description", StoreDescriptionText.getText().toString());
                                                editor.apply();
                                            }
                                        }
                                    });
                                }else{
                                    Log.i("bmob","更新失败："+e.getMessage()+","+e.getErrorCode());
                                }
                            }
                        });
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
        }else{
            progressDialog = ProgressDialogUtil.getProgressDialog(PersonalCenterActivity.this, "提交信息", "Waiting...", false);
            progressDialog.show();
            SharedPreferences preferences = getSharedPreferences("store", Context.MODE_PRIVATE);
            String objectId = preferences.getString("objectId", "");
            Store store = new Store();
            store.setName(StoreNewNameText.getText().toString());
            store.setOfferPrice(Float.parseFloat(StoreOfferPriceText.getText().toString()));
            store.setDescription(StoreDescriptionText.getText().toString());
            store.update(objectId, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(PersonalCenterActivity.this,"更新成功!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        Log.i("bmob","更新失败："+e.getMessage()+","+e.getErrorCode());
                    }
                }
            });
        }

    }

    //显示列表对话框
    private void showDialog() {
        new AlertDialog.Builder(this)
                .setTitle("选择上传方式")
                .setItems(new String[]{"从相册中选择", "拍照上传"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isUpImg = true;
                        switch (i) {
                            case 0://从相册中选择
                                CameraAlbumUtil.requestPermissions(PersonalCenterActivity.this, CHOOSE_PHOTO);
                                break;
                            case 1://拍照上传
                                CameraAlbumUtil.takePhote(PersonalCenterActivity.this, TAKE_PHOTO);
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
                    CameraAlbumUtil.openAlbum(PersonalCenterActivity.this, CHOOSE_PHOTO);
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
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(CameraAlbumUtil.imageUri));
                        PersonalImg.setImageBitmap(bitmap);
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
                        CameraAlbumUtil.handleImageOnKitKat(PersonalCenterActivity.this, data, PersonalImg);
                    } else {
                        //4.4一下系统使用这个方法处理图片
                        CameraAlbumUtil.handleImageBeforeKitKat(PersonalCenterActivity.this, data, PersonalImg);
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
        Intent intent = new Intent(context, PersonalCenterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
