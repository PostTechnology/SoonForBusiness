package com.soon.android.adapters;

import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.soon.android.R;
import com.soon.android.bmobBean.Order;
import com.soon.android.gson.DeliveryAddress;
import com.soon.android.util.DecimalUtil;


import java.util.List;

/**
 * Created by LYH on 2018/3/8.
 */

public class OrderListAdapter extends BaseQuickAdapter<Order, BaseViewHolder> {

    public OrderListAdapter(int layoutResId, List<Order> data){
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Order item) {
        Gson gson = new Gson();
        Button receiveOrder = (Button) helper.getView(R.id.receive_order);//接单按钮
        Button rejectOrder = (Button) helper.getView(R.id.reject_order);//拒接接单按钮
        Button seeTheEvaluation = (Button) helper.getView(R.id.see_the_evaluation);//查看评价按钮

        helper.setText(R.id.order_number, "订单编号 " + item.getObjectId())
                .setText(R.id.created_at, item.getCreatedAt())
                .setText(R.id.order_sum_price, "￥" + DecimalUtil.decimalForTwo(item.getSumPrice()));
        switch (item.getStatus()){
            case 0:
                helper.setText(R.id.order_status, "待付款");
                break;
            case 1:
                helper.setText(R.id.order_status, "已付款");
                break;
            case 2:
                helper.setText(R.id.order_status, "已接单");
                rejectOrder.setVisibility(View.GONE);
                receiveOrder.setVisibility(View.GONE);
                break;
            case 3:
                helper.setText(R.id.order_status, "已完成");
                rejectOrder.setVisibility(View.GONE);
                receiveOrder.setVisibility(View.GONE);
                seeTheEvaluation.setVisibility(View.VISIBLE);
                break;
            case -1:
                helper.setText(R.id.order_status, "已取消");
                receiveOrder.setVisibility(View.GONE);
                rejectOrder.setVisibility(View.GONE);
                seeTheEvaluation.setVisibility(View.GONE);
                break;
            default:
                break;
        }

        //解析显示用户收货信息
        DeliveryAddress deliveryAddress = gson.fromJson(item.getDeliveryAddress(), DeliveryAddress.class);
        helper.setText(R.id.user_name, deliveryAddress.getName())
                .setText(R.id.user_tel, deliveryAddress.getTel() + "")
                .setText(R.id.user_addr, deliveryAddress.getLocation() + " " + deliveryAddress.getDoorNum());

        //绑定子项点击事件
        helper.addOnClickListener(R.id.receive_order)
                .addOnClickListener(R.id.see_the_evaluation)
                .addOnClickListener(R.id.reject_order);
    }
}
