package com.soon.android.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.soon.android.R;
import com.soon.android.adapters.CommentListAdapter;
import com.soon.android.bmobBean.Order;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class CommentFragment extends Fragment {

    @BindView(R.id.comment_list)
    RecyclerView recyclerView;

    public CommentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_comment, container, false);;
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences preferences = getActivity().getSharedPreferences("store", Context.MODE_PRIVATE);
        String objectId = preferences.getString("objectId", "");
        BmobQuery<Order> query = new BmobQuery<>();
        query.addWhereEqualTo("storeObjectId", objectId);
        query.findObjects(new FindListener<Order>() {
            @Override
            public void done(final List<Order> list, BmobException e) {
                if(e == null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            List<Order> orderList = new ArrayList<>();
                            for(Order order : list){
                                if(order.getStatus() == 3){
                                    orderList.add(order);
                                }
                            }
                            CommentListAdapter adapter = new CommentListAdapter(R.layout.comment_list_item, orderList);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(adapter);
                        }
                    });
                }else{
                    Toast.makeText(getActivity(), "error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
