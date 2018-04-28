package com.soon.android.util;

import java.text.DecimalFormat;

/**
 * Created by LYH on 2018/3/7.
 */

public class DecimalUtil {

    public static String decimalForTwo(double price){
        //String p = "";

        DecimalFormat decimalFormat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        StringBuilder p = new StringBuilder(decimalFormat.format(price));
        //p = decimalFormat.format(price);
        if (price >= 1){

        }else if (price < 1 && price >= 0){
            p.insert(0, '0');
        }else {
            p.delete(0, p.length());
            p.append(0);
        }

        return p.toString();
    }
}
