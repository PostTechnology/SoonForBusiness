package com.soon.android.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.soon.android.R;
import com.soon.android.bmobBean.Order;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class CommentListAdapter extends BaseQuickAdapter<Order, BaseViewHolder> {
    public CommentListAdapter(int layoutResId, List<Order> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Order item) {
        List<String> ratingText = new ArrayList<>();
        ratingText.add("真难吃");
        ratingText.add("吐槽");
        ratingText.add("较差");
        ratingText.add("一般");
        ratingText.add("满意");
        ratingText.add("超赞");
        helper.setText(R.id.rating, ratingText.get((int)Math.ceil(item.getRating())))
                .setText(R.id.comment, item.getComment() == null ? "无" : item.getComment())
                .setText(R.id.comment_time, item.getUpdatedAt().substring(0,10));
        ((MaterialRatingBar)helper.getView(R.id.rating_stars)).setRating(item.getRating());
    }
}
