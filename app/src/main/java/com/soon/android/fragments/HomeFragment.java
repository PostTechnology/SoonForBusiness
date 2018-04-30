package com.soon.android.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.soon.android.R;
import com.soon.android.bmobBean.Goods;
import com.soon.android.bmobBean.Order;
import com.soon.android.util.BarChartManager;
import com.soon.android.util.LineChartManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class HomeFragment extends Fragment {

    @BindView(R.id.revenueText)
    TextView revenueText;

    @BindView(R.id.orderText)
    TextView orderText;

    @BindView(R.id.goodsText)
    TextView goodsText;

    @BindView(R.id.praise)
    TextView praiseText;

    @BindView(R.id.order_amount)
    TextView orderAmountText;

    @BindView(R.id.order_finish)
    TextView orderFinishText;

    @BindView(R.id.order_during)
    TextView orderDuringText;

    @BindView(R.id.revenue_today)
    TextView revenueTodayText;

    @BindView(R.id.chart)
    BarChart mBarChart;

    @BindView(R.id.linechart)
    LineChart mLineChart;

    @BindView(R.id.piechart)
    PieChart mPieChart;

    private Handler handler1 = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ArrayList<String> obj = (ArrayList<String>) msg.obj;
                    revenueText.setText(obj.get(0));
                    orderText.setText(obj.get(1));
                    break;
                default:
                    break;
            }
        }
    };

    private Handler handler2 = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ArrayList<String> obj = (ArrayList<String>) msg.obj;
                    orderAmountText.setText(obj.get(0));
                    orderFinishText.setText(obj.get(1));
                    orderDuringText.setText(obj.get(2));
                    revenueTodayText.setText(obj.get(3));
                    break;
                default:
                    break;
            }
        }
    };

    private Handler handler3 = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String obj = (String) msg.obj;
                    praiseText.setText(obj);
                    break;
                default:
                    break;
            }
        }
    };

    private Handler handler4 = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String obj = (String) msg.obj;
                    goodsText.setText(obj);
                    break;
                default:
                    break;
            }
        }
    };

    private Handler drawHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ArrayList<ArrayList<Float>> obj = (ArrayList<ArrayList<Float>>) msg.obj;
                    drawBarCharts(obj);
                    break;
                default:
                    break;
            }
        }
    };

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBarChart.setNoDataText("没有数据哦！");
        mLineChart.setNoDataText("没有数据哦！");
        mPieChart.setNoDataText("没有数据哦！");
//        Toast.makeText(getActivity(),"开始绘图",Toast.LENGTH_SHORT).show();
        SharedPreferences preferences = getActivity().getSharedPreferences("store", Context.MODE_PRIVATE);
        String objectId = preferences.getString("objectId", "");
        initHeader(objectId);
        initBarCharts(objectId);
        initLineCharts(objectId);
        initPieCharts(objectId);
    }

    // 加载栏目
    private void initHeader(String objectId){
        BmobQuery<Order> orderQuery = new BmobQuery<>();
        orderQuery.addWhereEqualTo("storeObjectId", objectId);
        orderQuery.findObjects(new FindListener<Order>() {
            @Override
            public void done(List<Order> list, BmobException e) {
                if (e == null) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
                    String today = formatter.format(curDate);
                    double revenue = 0f;
                    int ordernum = 0;
                    double praise = 0f;
                    int praisenum = 0;
                    int orderAmount = 0;
                    int orderFinish = 0;
                    double revenueToday = 0f;
                    for (Order order : list) {
                        if (order.getUpdatedAt().substring(0, 7).equals("2018-04") && order.getStatus() > 1) {
                            revenue += order.getSumPrice();
                            ordernum++;
                            if (order.getStatus() == 3){
                                try{
                                    praise += order.getRating();
                                    praisenum++;
                                }catch (Exception ex){

                                }
                            }
                        }
                        if (order.getUpdatedAt().substring(0, 10).equals(today) && order.getStatus() > 1) {
                            revenueToday += order.getSumPrice();
                            orderAmount++;
                            if (order.getStatus() == 3){
                                orderFinish++;
                            }
                        }
                    }
                    ArrayList<String> arrayList1 = new ArrayList<>();
                    ArrayList<String> arrayList2 = new ArrayList<>();
                    String data_praise = praisenum == 0 ? "0" : String.format("%.2f", praise/praisenum);
                    arrayList1.add(String.format("%.2f", revenue)+"元");
                    arrayList1.add(Integer.toString(ordernum)+"单");
                    arrayList2.add(Integer.toString(orderAmount));
                    Log.i("data", "arrayList1: " + arrayList1.get(0) + "," + arrayList1.get(1) + "," + arrayList1.get(0));
                    arrayList2.add(Integer.toString(orderFinish));
                    arrayList2.add(Integer.toString(orderAmount - orderFinish));
                    arrayList2.add(String.format("%.2f", revenueToday));
                    Message msg1 = new Message();
                    Message msg2 = new Message();
                    Message msg3 = new Message();
                    msg1.what = 1;
                    msg2.what = 1;
                    msg3.what = 1;
                    msg1.obj = arrayList1;
                    msg2.obj = arrayList2;
                    msg3.obj = data_praise;
                    handler1.sendMessage(msg1);
                    handler2.sendMessage(msg2);
                    handler3.sendMessage(msg3);
                } else {
                    Log.d("TAG", "headererror: " + e.getMessage());
                }
            }
        });

        BmobQuery<Goods> goodsQuery = new BmobQuery<>();
        goodsQuery.addWhereEqualTo("storeObjectId", objectId);
        goodsQuery.findObjects(new FindListener<Goods>() {
            @Override
            public void done(List<Goods> list, BmobException e) {
                if (e == null) {
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = Integer.toString(list.size()) + "个";
                    handler4.sendMessage(msg);
                } else {
                    Log.d("TAG", "headererror: " + e.getMessage());
                }
            }
        });
    }

    // 加载柱状图表格
    private void initBarCharts(String objectId){
        BmobQuery<Order> query = new BmobQuery<>();
        query.addWhereEqualTo("storeObjectId", objectId);
        query.findObjects(new FindListener<Order>() {
            @Override
            public void done(List<Order> list, BmobException e) {
                if(e == null){
                    ArrayList<String> dateList = new ArrayList<>();
                    for(int i = 4 ; i > 0; i--){
                        Calendar c = Calendar.getInstance();
                        c.add(Calendar.MONTH, i - 4);
                        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM");
                        dateList.add(format.format(c.getTime()));
                    }
                    ArrayList<ArrayList<Float>> values = new ArrayList<>();
                    for(int i = 0; i < 4; i++){
                        values.add(new ArrayList<Float>());
                        for(int j = 0; j < 4; j++){
                            values.get(i).add(0f);
                        }
                    }
                    for(Order order : list){
                        if(order.getUpdatedAt().substring(0, 7).equals(dateList.get(0)) && order.getStatus() > 1){
                            values.get(0).set(1, values.get(0).get(1)+1);
                            if(order.getStatus() == 3){
                                values.get(0).set(0, values.get(0).get(0) + order.getSumPrice());
                                values.get(0).set(2, values.get(0).get(2) + order.getRating());
                                values.get(0).set(3, values.get(0).get(3) + 1);
                            }
                        }else if(order.getUpdatedAt().substring(0, 7).equals(dateList.get(1)) && order.getStatus() > 1){
                            values.get(1).set(1, values.get(1).get(1)+1);
                            if(order.getStatus() == 3){
                                values.get(1).set(0, values.get(1).get(0) + order.getSumPrice());
                                values.get(1).set(2, values.get(1).get(2) + order.getRating());
                                values.get(1).set(3, values.get(1).get(3) + 1);
                            }
                        }else if(order.getUpdatedAt().substring(0, 7).equals(dateList.get(2)) && order.getStatus() > 1){
                            values.get(2).set(1, values.get(2).get(1)+1);
                            if(order.getStatus() == 3){
                                values.get(2).set(0, values.get(2).get(0) + order.getSumPrice());
                                values.get(2).set(2, values.get(2).get(2) + order.getRating());
                                values.get(2).set(3, values.get(2).get(3) + 1);
                            }
                        }else if(order.getUpdatedAt().substring(0, 7).equals(dateList.get(3)) && order.getStatus() > 1){
                            values.get(3).set(1, values.get(3).get(1)+1);
                            if(order.getStatus() == 3){
                                values.get(3).set(0, values.get(3).get(0) + order.getSumPrice());
                                values.get(3).set(2, values.get(3).get(2) + order.getRating());
                                values.get(3).set(3, values.get(3).get(3) + 1);
                            }
                        }
                    }
                    for(int i = 0; i < 4; i++){
                        values.get(i).set(2,values.get(i).get(3) == 0 ? 0 : values.get(i).get(2)/values.get(i).get(3));
                        values.get(i).remove(3);
                    }
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = values;
                    drawHandler.sendMessage(msg);
                }else{
                    Log.d("TAG", "barerror: " + e.getMessage());
                }
            }
        });
    }

    // 绘画柱状表格
    private void drawBarCharts(ArrayList<ArrayList<Float>> dataCharts){
        BarChartManager barChartManager = new BarChartManager(mBarChart);
        ArrayList<Float> xValues = new ArrayList<>();
        for (int i = 0; i < dataCharts.size(); i++) {
            xValues.add((float) i);
        }

        //设置y轴的数据()
        List<List<Float>> yValues = new ArrayList<>();
        Log.i("TAG", "ysize: " + dataCharts.size());
        int index = 0;
        for (int i = 0; i < dataCharts.get(1).size(); i++) {
            List<Float> yValue = new ArrayList<>();
            for(List<Float> value : dataCharts){
                yValue.add(value.get(index));
                Log.i("TAG", "value: "+value);
            }
            index++;
            yValues.add(yValue);
        }

        //颜色集合
        List<Integer> colours = new ArrayList<>();
        colours.add(Color.GREEN);
        colours.add(Color.BLUE);
        colours.add(Color.RED);

        //线的名字集合
        List<String> names = new ArrayList<>();
        names.add("营收额");
        names.add("订单量");
        names.add("好评d度");

        //创建多条折线的图表
//        barChartManager.showBarChart(xValues, yValues.get(0), names.get(1), colours.get(3));
        barChartManager.showBarChart(xValues, yValues, names, colours);
        barChartManager.setXAxis(6.5f, 0f, 5);

//        Toast.makeText(getActivity(),"完成绘图",Toast.LENGTH_SHORT).show();
    }

    // 加载折线图数据
    private void initLineCharts(String objectId){
        BmobQuery<Order> query = new BmobQuery<>();
        query.addWhereEqualTo("storeObjectId", objectId);
        query.findObjects(new FindListener<Order>() {
            @Override
            public void done(List<Order> list, BmobException e) {
                if (e == null) {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat format =  new SimpleDateFormat("dd");
                    int today = Integer.parseInt(format.format(c.getTime()));
                    List<Float> sumPrice = new ArrayList<>();
                    for(int i = 0; i < 7; i++){
                        sumPrice.add(0f);
                    }
                    for(Order order : list){
                        String date = order.getUpdatedAt().split(" ")[0];
                        int day = Integer.parseInt(date.split("-")[2]);
                        int index =today - day;
                        if(index < 7){
                            sumPrice.set(index, sumPrice.get(index) + order.getSumPrice());
                        }
                    }
                    Log.i("TAG", "today: " + today);

                    drawLineCharts(sumPrice);
                }else{
                    Log.d("TAG", "error: " + e.getMessage());
                }
            }
        });
    }

    // 绘画折线图
    private void drawLineCharts(List<Float> yValue){
        LineChartManager lineChartManager = new LineChartManager(mLineChart);
        //设置x轴的数据
        ArrayList<Float> xValues = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            xValues.add((float) i);
        }

        //创建多条折线的图表
        lineChartManager.showLineChart(xValues, yValue, "销量", Color.CYAN);
        lineChartManager.setDescription("七天总销量");
        lineChartManager.setYAxis(100, 0, 7);
        lineChartManager.setHightLimitLine(70,"销量基准线",Color.RED);
    }

    // 加载饼图数据
    private void initPieCharts(String objectId){
        BmobQuery<Order> query = new BmobQuery<>();
        query.addWhereEqualTo("storeObjectId", objectId);
        query.findObjects(new FindListener<Order>() {
            @Override
            public void done(List<Order> list, BmobException e) {
                if (e == null) {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    List<Integer> times = new ArrayList<>();
                    for(int i = 0; i < 3; i++){
                        times.add(0);
                    }
                    for(Order order : list){
                        try {
                            Date today = format.parse(order.getUpdatedAt());
                            if(today.getHours() < 12){
                                times.set(0,times.get(0) + 1);
                            }else if(today.getHours() > 12 && today.getHours() < 18){
                                times.set(1,times.get(1) + 1);
                            }else{
                                times.set(2,times.get(2) + 1);
                            }
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }

                    drawPieCharts(times);
                }else{
                    Log.d("TAG", "error: " + e.getMessage());
                }
            }
        });
    }

    // 绘画饼图
    private void drawPieCharts(List<Integer> times){
        mPieChart.setUsePercentValues(true);
        mPieChart.getDescription().setEnabled(false);
        mPieChart.setExtraOffsets(5, 10, 5, 5);

        mPieChart.setDragDecelerationFrictionCoef(0.95f);
        //设置中间文件
        mPieChart.setCenterText(generateCenterSpannableText());

        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setHoleColor(Color.WHITE);

        mPieChart.setTransparentCircleColor(Color.WHITE);
        mPieChart.setTransparentCircleAlpha(110);

        mPieChart.setHoleRadius(58f);
        mPieChart.setTransparentCircleRadius(61f);

        mPieChart.setDrawCenterText(true);

        mPieChart.setRotationAngle(0);
        // 触摸旋转
        mPieChart.setRotationEnabled(true);
        mPieChart.setHighlightPerTapEnabled(true);

        //变化监听
//        mPieChart.setOnChartValueSelectedListener(this);

        //模拟数据
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        entries.add(new PieEntry(times.get(0), "早餐"));
        entries.add(new PieEntry(times.get(1), "午餐"));
        entries.add(new PieEntry(times.get(2), "晚餐"));

        //设置数据
        setData(entries);

        mPieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        Legend l = mPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // 输入标签样式
        mPieChart.setEntryLabelColor(Color.WHITE);
        mPieChart.setEntryLabelTextSize(12f);
    }

    //设置中间文字
    private SpannableString generateCenterSpannableText() {
        //原文：MPAndroidChart\ndeveloped by Philipp Jahoda
        SpannableString s = new SpannableString("早中晚销量分布图");
        //s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
        //s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        // s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        //s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
        // s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        // s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }

    //设置数据
    private void setData(ArrayList<PieEntry> entries) {
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        //数据和颜色
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        mPieChart.setData(data);
        mPieChart.highlightValues(null);
        //刷新
        mPieChart.invalidate();
    }
}
