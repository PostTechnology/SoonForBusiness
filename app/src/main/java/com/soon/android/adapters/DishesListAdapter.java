package com.soon.android.adapters;

import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.soon.android.R;
import com.soon.android.bmobBean.Goods;
import com.soon.android.util.DecimalUtil;
import com.suke.widget.SwitchButton;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by LYH on 2018/4/23.
 */

public class DishesListAdapter extends BaseQuickAdapter<Goods, BaseViewHolder> {

    public DishesListAdapter(int layoutResId, List data){
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final Goods item) {
        final Goods goods = item;
        //Glide.with(mContext).load(item.getImageFile().getFileUrl()).into((ImageView) helper.getView(R.id.food_img));
        ((SimpleDraweeView)helper.getView(R.id.food_img)).setImageURI(item.getImageFile().getFileUrl());
        helper.setText(R.id.food_name, item.getName())
                .setText(R.id.discount, item.getDiscount() + "")
                .setText(R.id.sales_volume, "月售 " + item.getSalesVolume() + " 份")
                .setText(R.id.food_price, "￥" + DecimalUtil.decimalForTwo(item.getPrice() * item.getDiscount() * 0.1));

        //设置开关按钮状态
        if (item.getStatus()){
            ((SwitchButton)helper.getView(R.id.sell_out_button)).setChecked(true);
        }
        //设置开关按钮的事件
        ((SwitchButton)helper.getView(R.id.sell_out_button)).setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                Goods goods1 = new Goods();
                if (goods.getStatus()){
                    goods1.setStatus(false);
                }else {
                    goods1.setStatus(true);
                }
                goods1.update(goods.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            Log.i("bmob","更新成功");
                        }else{
                            Log.i("bmob","更新失败："+e.getMessage()+","+e.getErrorCode());
                        }
                    }
                });
            }
        });
    }
}
