package com.soon.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.soon.android.adapters.OrderDetailsFoodListAdapter;
import com.soon.android.bmobBean.Order;
import com.soon.android.gson.DeliveryAddress;
import com.soon.android.gson.GoodList;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderDetailsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.des)
    TextView des;
    @BindView(R.id.order_num)
    TextView orderNum;
    @BindView(R.id.order_sum_price)
    TextView orderSumPrice;
    @BindView(R.id.store_name)
    TextView storeName;
    @BindView(R.id.food_recycler_view)
    RecyclerView foodRecyclerView;
    @BindView(R.id.sum_price)
    TextView sumPrice;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.tel)
    TextView tel;
    @BindView(R.id.location)
    TextView location;
    @BindView(R.id.doorNum)
    TextView doorNum;
    @BindView(R.id.address)
    LinearLayout address;

    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("订单信息");
        }

        Intent intent = getIntent();
        order = (Order) intent.getSerializableExtra("order_infor");
        init();
    }

    //初始化界面数据
    private void init(){
        orderNum.setText(order.getObjectId());
        storeName.setText(order.getStoreName());
        orderSumPrice.setText(order.getSumPrice() + "");
        sumPrice.setText("￥" + order.getSumPrice());

        Gson gson = new Gson();
        DeliveryAddress deliveryAddress = gson.fromJson(order.getDeliveryAddress(), DeliveryAddress.class);
        name.setText(deliveryAddress.getName());
        tel.setText(deliveryAddress.getTel());
        location.setText(deliveryAddress.getLocation());
        doorNum.setText(deliveryAddress.getDoorNum());

        List<GoodList> goodLists = gson.fromJson(order.getGoodsList(), new TypeToken<List<GoodList>>(){}.getType());

        OrderDetailsFoodListAdapter adapter = new OrderDetailsFoodListAdapter(R.layout.submit_order_food_list_item, goodLists);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        foodRecyclerView.setLayoutManager(layoutManager);
        foodRecyclerView.setAdapter(adapter);

    }

    public static void actionStart(Context context, Order order){
        Intent intent = new Intent(context, OrderDetailsActivity.class);
        intent.putExtra("order_infor", order);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }
}
