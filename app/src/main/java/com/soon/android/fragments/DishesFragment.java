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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.soon.android.AddDishesActivity;
import com.soon.android.MyApplication;
import com.soon.android.R;
import com.soon.android.adapters.DishesListAdapter;
import com.soon.android.adapters.SortListAdapter;
import com.soon.android.bmobBean.Goods;
import com.soon.android.util.ProgressDialogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class DishesFragment extends Fragment {


    @BindView(R.id.toobar)
    Toolbar toobar;
    @BindView(R.id.dishes_list)
    RecyclerView dishesList;
    Unbinder unbinder;
    @BindView(R.id.sort_list)
    RecyclerView sortList;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;

    private DishesListAdapter dishesListAdapter;

    private SortListAdapter sortListAdapter;

    private List<Goods> dishesData = new ArrayList<>();//菜品信息数据

    private final int DISHESLOAD = 0;//菜品信息 加载message what字段

    private List<String> sortData = new ArrayList<>();//菜品类别信息数据

    private static final int SORTLOAD = 1;//菜品类别信息 加载message what字段

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DISHESLOAD:
                    dishesData = (List<Goods>) msg.obj;
                    loadGoods();
                    swipeRefresh.setRefreshing(false);
                    progressDialog.dismiss();
                    break;
                case SORTLOAD:
                    List<String> list = new ArrayList<>();
                    for (Goods good : (List<Goods>) msg.obj) {
                        list.add(good.getSort());
                    }
                    sortData = list;
                    loadSort(sortData);
                    break;
                default:
                    break;
            }
        }
    };

    private ProgressDialog progressDialog;

    private String storeObjectId;

    public DishesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dishes, container, false);
        unbinder = ButterKnife.bind(this, view);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toobar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("菜品");
        }
        setHasOptionsMenu(true);//加上这句话，menu才会显示出来

        SharedPreferences preferences = getActivity().getSharedPreferences("store", Context.MODE_PRIVATE);
        storeObjectId = preferences.getString("objectId", "");


        //设置下拉刷新
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryGoods(storeObjectId);
                querySort(storeObjectId);
            }
        });

        progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(), null, "Waiting...", false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if ((dishesData.size() == 0 || dishesData.isEmpty()) && (sortData.size() == 0 && sortData.isEmpty())) {
            progressDialog.show();
            queryGoods(storeObjectId);
            querySort(storeObjectId);
        } else {
            loadGoods();
            loadSort(sortData);
        }
    }

    //从Goods表查询对应id店铺的商品类别
    private void querySort(String storeObjectId) {
        //只返回Goods表的对应的店铺id的sort这列的值
        String bql = "select distinct sort from Goods where storeObjectId = ? order by +sort";
        new BmobQuery<Goods>().doSQLQuery(bql, new SQLQueryListener<Goods>() {

            @Override
            public void done(BmobQueryResult<Goods> result, BmobException e) {
                if (e == null) {
                    List<Goods> list = (List<Goods>) result.getResults();
                    if (list != null && list.size() > 0) {
                        Message message = new Message();
                        message.what = SORTLOAD;
                        message.obj = list;
                        handler.sendMessage(message);
                        Log.i("smile", "查询成功：共" + list.size() + "条数据。");
                    } else {
                        Log.i("smile", "查询成功，无数据返回");
                    }
                } else {
                    Log.i("smile", "错误码：" + e.getErrorCode() + "，错误描述：" + e.getMessage());
                }
            }
        }, storeObjectId);
    }

    private void loadSort(List<String> sortData) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        sortList.setLayoutManager(layoutManager);
        sortListAdapter = new SortListAdapter(R.layout.sort_list_item, sortData);
        sortList.setAdapter(sortListAdapter);
    }


    //从Goods表查询对应id店铺的商品
    private void queryGoods(String storeObjectId) {
        String bql = "select * from Goods where storeObjectId = ? order by +sort";
        new BmobQuery<Goods>().doSQLQuery(bql, new SQLQueryListener<Goods>() {

            @Override
            public void done(BmobQueryResult<Goods> result, BmobException e) {
                if (e == null) {
                    List<Goods> list = (List<Goods>) result.getResults();
                    if (list != null && list.size() > 0) {
                        Message message = new Message();
                        message.what = DISHESLOAD;
                        message.obj = list;
                        handler.sendMessage(message);
                        Log.i("smile", "查询成功：共" + list.size() + "条数据。");
                    } else {
                        Log.i("smile", "查询成功，无数据返回");
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "暂无数据", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i("smile", "错误码：" + e.getErrorCode() + "，错误描述：" + e.getMessage());
                }
            }
        }, storeObjectId);
    }

    //加载菜品信息
    private void loadGoods() {
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
        dishesList.setLayoutManager(layoutManager1);
        dishesListAdapter = new DishesListAdapter(R.layout.dishes_list_item, dishesData);
        dishesListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
        dishesList.setAdapter(dishesListAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_dish:
                Toast.makeText(MyApplication.getContext(), "添加菜品", Toast.LENGTH_SHORT).show();
                AddDishesActivity.actionStart(getActivity());
                break;
            default:
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
