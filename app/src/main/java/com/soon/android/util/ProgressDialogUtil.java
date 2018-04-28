package com.soon.android.util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by LYH on 2018/4/26.
 */

public class ProgressDialogUtil {

    public static ProgressDialog getProgressDialog(Context context, String title, String message, boolean cancelable){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(cancelable);
        return progressDialog;
    }

}
