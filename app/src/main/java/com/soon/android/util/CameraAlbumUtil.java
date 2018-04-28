package com.soon.android.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

/**
 * Created by LYH on 2018/4/26.
 */

public class CameraAlbumUtil {

    private static final String TAG = "Util";

    public static Uri imageUri;

    public static String imagePath;//图片的路径，用户上传图片

    /**
     * 打开摄像头
     * @param context
     * @param TAKE_PHOTO 用于标识 打开摄像头拍照 的int变量
     * @return
     */
    public static Uri takePhote(Context context, final int TAKE_PHOTO){
        //创建File对象，用户存储拍照的图片
        File outputImage = new File(context.getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(context,
                    "com.example.cameraalbumtest.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }

        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        ((Activity)context).startActivityForResult(intent, TAKE_PHOTO);
        imagePath = imageUri.getPath();
        return imageUri;
    }

    /**+
     * 请求 打开相册 的权限
     * @param context
     * @param CHOOSE_PHOTO
     */
    public static void requestPermissions(Context context, final int CHOOSE_PHOTO){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) context, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else{
            openAlbum(context, CHOOSE_PHOTO);
        }
    }

    /**
     * 打开相册
     * @param context
     * @param CHOOSE_PHOTO
     */
    public static void openAlbum(Context context, final int CHOOSE_PHOTO){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        ((Activity)context).startActivityForResult(intent, CHOOSE_PHOTO);//打开相册
    }

    /**
     *
     * @param context
     * @param data
     * @param picture
     */
    @TargetApi(19)
    public static void handleImageOnKitKat(Context context, Intent data, ImageView picture){
        imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(context, uri)){
            //如果是document类型的URi， 则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://dowmloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(context, contentUri, null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(context, uri, null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath, picture);//根据图片路径显示图片
    }

    public static void handleImageBeforeKitKat(Context context, Intent data, ImageView picture){
        Uri uri = data.getData();
        imagePath = null;
        String imagePath = getImagePath(context, uri, null);
        displayImage(imagePath, picture);
    }

    private static String getImagePath(Context context, Uri uri, String selection){
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private static void displayImage(String imagePath, ImageView picture){
        if (imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
        }else{
            Log.d(TAG, "failed to get image");
        }
    }

}
