package com.soon.android.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.soon.android.MyApplication;
import com.soon.android.OrderDetailsActivity;
import com.soon.android.R;
import com.soon.android.adapters.OrderListAdapter;
import com.soon.android.bmobBean.Order;
import com.soon.android.util.ProgressDialogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class DoneOrderFragment extends Fragment {

    @BindView(R.id.order_list)
    RecyclerView orderList;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    Unbinder unbinder;

    private ProgressDialog progressDialog;

    private String storeObjectId;

    private List<Order> orderListData = new ArrayList<>();//订单数据

    private final int ORDERLISTDATA = 2;//订单数据

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ORDERLISTDATA:
                    orderListData = (List<Order>) msg.obj;
                    loadOrderList();
                    swipeRefresh.setRefreshing(false);
                    progressDialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    public DoneOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_done_order, container, false);
        unbinder = ButterKnife.bind(this, view);

        progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(), null, "Waiting...", false);

        SharedPreferences preferences = getActivity().getSharedPreferences("store", Context.MODE_PRIVATE);
        storeObjectId = preferences.getString("objectId", "");

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryOrdersByStoreId(storeObjectId);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (orderListData.size() == 0 || orderListData.isEmpty()) {
            progressDialog.show();
            queryOrdersByStoreId(storeObjectId);
        } else {
            loadOrderList();
        }
    }

    //根据店铺id查询对应的订单
    private void queryOrdersByStoreId(String storeObjectId) {
        Toast.makeText(MyApplication.getContext(), "id:" + storeObjectId, Toast.LENGTH_SHORT).show();
        BmobQuery<Order> query = new BmobQuery<Order>();
        query.addWhereEqualTo("storeObjectId", storeObjectId);
        query.addWhereEqualTo("status", 3);
        query.order("-createdAt");
        query.findObjects(new FindListener<Order>() {
            @Override
            public void done(List<Order> object, BmobException e) {
                if (e == null) {
                    if (object.size() == 0 || object.isEmpty()){
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "暂无数据", Toast.LENGTH_SHORT).show();
                    }else{
                        Message message = new Message();
                        message.what = ORDERLISTDATA;
                        message.obj = object;
                        handler.sendMessage(message);
                    }
                } else {
                    //Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    //加载订单列表
    private void loadOrderList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        orderList.setLayoutManager(layoutManager);
        OrderListAdapter adapter = new OrderListAdapter(R.layout.order_list_item, orderListData);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                //Toast.makeText(getActivity(), "onItemChildClick" + position, Toast.LENGTH_SHORT).show();
                switch (view.getId()){
                    case R.id.receive_order:
                        changeOrderStatus(orderListData.get(position).getObjectId(), storeObjectId, 2);
                        break;
                    case R.id.see_the_evaluation:
                        break;
                    case R.id.reject_order:
                        changeOrderStatus(orderListData.get(position).getObjectId(), storeObjectId, -1);;
                        break;
                    default:
                }
            }
        });

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Order order = orderListData.get(position);
                OrderDetailsActivity.actionStart(getActivity(), order);
            }
        });

        orderList.setAdapter(adapter);
    }

    //修改订单状态
    private void changeOrderStatus(String orderId, final String storeObjectId, int status){
        progressDialog.show();
        Order order = new Order();
        order.setStatus(status);
        order.update(orderId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.i("bmob","更新成功");
                    queryOrdersByStoreId(storeObjectId);
                }else{
                    Log.i("bmob","更新失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
