package com.soon.android.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.soon.android.R;

import java.util.List;

/**
 * Created by LYH on 2018/1/31.
 */

public class SortListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public SortListAdapter(int layoutResId, List<String> data){
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.sort_name, item);
    }
}
