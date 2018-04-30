package com.soon.android.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.soon.android.R;
import com.soon.android.gson.GoodList;
import com.soon.android.util.DecimalUtil;

import java.util.List;

/**
 * Created by LYH on 2018/4/30.
 */

public class OrderDetailsFoodListAdapter extends BaseQuickAdapter<GoodList, BaseViewHolder> {

    public OrderDetailsFoodListAdapter(int layoutResId, List<GoodList> data){
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GoodList item) {
        helper.setText(R.id.name, item.getName())
                .setText(R.id.sum, "x" + item.getSum())
                .setText(R.id.discount, item.getDiscount() + "折")
                .setText(R.id.price, "￥" + DecimalUtil.decimalForTwo(item.getPrice() * item.getDiscount() * 0.1));
    }
}
