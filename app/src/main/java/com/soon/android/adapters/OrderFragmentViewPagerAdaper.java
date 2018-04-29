package com.soon.android.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.soon.android.fragments.CommentFragment;
import com.soon.android.fragments.DoneOrderFragment;
import com.soon.android.fragments.OngoingOrderFragment;
import com.soon.android.fragments.PaidOrderFragment;

/**
 * Created by LYH on 2018/4/29.
 */

public class OrderFragmentViewPagerAdaper extends FragmentPagerAdapter {

    private Fragment fragment;

    public OrderFragmentViewPagerAdaper(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                fragment =  new OngoingOrderFragment();
                break;
            case 1:
                fragment =  new PaidOrderFragment();
                break;
            case 2:
                fragment =  new DoneOrderFragment();
                break;
            case 3:
                fragment =  new CommentFragment();
                break;
            default:
                fragment =  new OngoingOrderFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
