package com.soon.android;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.soon.android.bmobBean.Goods;
import com.soon.android.util.CameraAlbumUtil;
import com.soon.android.util.ProgressDialogUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import me.gujun.android.taggroup.TagGroup;

public class AddDishesActivity extends AppCompatActivity {

    private static final String TAG = "AddDishesActivity";

    @BindView(R.id.toobar)
    Toolbar toobar;

    @BindView(R.id.tag_group)
    TagGroup tagGroup;
    @BindView(R.id.dishes_name)
    EditText dishesName;
    @BindView(R.id.dishes_sort)
    EditText dishesSort;
    @BindView(R.id.dishes_img)
    ImageView dishesImg;
    @BindView(R.id.dishes_price)
    EditText dishesPrice;
    @BindView(R.id.dishes_discount)
    EditText dishesDiscount;
    @BindView(R.id.add_button)
    Button addButton;
    @BindView(R.id.is_could_sell)
    CheckBox isCouldSell;

    private List<String> sortData = new ArrayList<>();//菜品类别信息数据

    private static final int SORTLOAD = 2;//菜品类别信息 加载message what字段

    public static final int TAKE_PHOTO = 3;//拍照的标识

    public static final int CHOOSE_PHOTO = 4;//从相册中选择的标识

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SORTLOAD:
                    for (Goods good : (List<Goods>) msg.obj) {
                        sortData.add(good.getSort());
                    }
                    loadSort(sortData);
                    break;
                default:
                    break;
            }
        }
    };

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dishes);
        ButterKnife.bind(this);
        setSupportActionBar(toobar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("添加菜品");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        querySort("HNle888E");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, AddDishesActivity.class);
        context.startActivity(intent);
    }

    //从Goods表查询对应id店铺的商品类别
    private void querySort(String storeObjectId) {
        //只返回Goods表的对应的店铺id的sort这列的值
        String bql = "select distinct sort from Goods where storeObjectId = ? order by +sort";
        new BmobQuery<Goods>().doSQLQuery(bql, new SQLQueryListener<Goods>() {

            @Override
            public void done(BmobQueryResult<Goods> result, BmobException e) {
                if (e == null) {
                    List<Goods> list = (List<Goods>) result.getResults();
                    if (list != null && list.size() > 0) {
                        Message message = new Message();
                        message.what = SORTLOAD;
                        message.obj = list;
                        handler.sendMessage(message);
                        Log.i("smile", "查询成功：共" + list.size() + "条数据。");
                    } else {
                        Log.i("smile", "查询成功，无数据返回");
                    }
                } else {
                    Log.i("smile", "错误码：" + e.getErrorCode() + "，错误描述：" + e.getMessage());
                }
            }
        }, storeObjectId);
    }

    private void loadSort(List<String> sortData) {
        String[] tags = sortData.toArray(new String[sortData.size()]);
        System.out.println(Arrays.toString(tags));
        tagGroup.setTags(tags);
        tagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                dishesSort.setText(tag);
            }
        });
    }

    @OnClick({R.id.dishes_img, R.id.add_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dishes_img:
                showDialog();
                break;
            case R.id.add_button:
                progressDialog = ProgressDialogUtil.getProgressDialog(AddDishesActivity.this, "添加菜品", "Waiting...", false);
                progressDialog.show();
                savePhoto();
                break;
        }
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
                    Log.d(TAG, "上传文件成功:" + bmobFile.getFileUrl());

                    //存储对象
                    //saveNewGood(new BmobFile(new File(bmobFile.getFileUrl())), "HNle888E");
                    saveNewGood(bmobFile, "HNle888E");
                }else{
                    Log.d(TAG, "上传文件失败：" + e.getMessage());
                }
            }

            @Override
            public void onProgress(Integer value) {
                super.onProgress(value);
                Log.d(TAG, "onProgress: " + value);
            }
        });

    }

    private void saveNewGood(BmobFile bmobFile, String storeObjectId){
        Goods newGoods = new Goods();
        //设置新增商品值
        //newGoods.setStoreObjectId("HNle888E");
        newGoods.setStoreObjectId(storeObjectId);
        if (isCouldSell.isChecked()){
            newGoods.setStatus(true);
        }else {
            newGoods.setStatus(false);
        }
        newGoods.setSort(dishesSort.getText().toString());
        newGoods.setPrice(Float.parseFloat(dishesPrice.getText().toString()));
        newGoods.setName(dishesName.getText().toString());
        newGoods.setImageFile(bmobFile);
        newGoods.setDiscount(Float.parseFloat(dishesDiscount.getText().toString()));
        newGoods.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    Log.d(TAG, "创建数据成功：" + s);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            clearContent();
                            Toast.makeText(AddDishesActivity.this, "菜品添加成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    //菜品添加成功后，清除之前输入的数据
    private void clearContent(){
        dishesImg.setImageResource(R.drawable.ic_add_button);
        dishesName.setText("");
        dishesSort.setText("");
        dishesPrice.setText("");
        dishesDiscount.setText("");
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
                                CameraAlbumUtil.requestPermissions(AddDishesActivity.this, CHOOSE_PHOTO);
                                break;
                            case 1://拍照上传
                                CameraAlbumUtil.takePhote(AddDishesActivity.this, TAKE_PHOTO);
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
                    CameraAlbumUtil.openAlbum(AddDishesActivity.this, CHOOSE_PHOTO);
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
                        dishesImg.setImageBitmap(bitmap);
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
                        CameraAlbumUtil.handleImageOnKitKat(AddDishesActivity.this, data, dishesImg);
                    } else {
                        //4.4一下系统使用这个方法处理图片
                        CameraAlbumUtil.handleImageBeforeKitKat(AddDishesActivity.this, data, dishesImg);
                    }
                }
                break;
            default:
                break;
        }
    }

}
