package com.soon.android.gson;

/**
 * Created by LYH on 2018/4/30.
 */

public class GoodList {
    /**
     * id : 79
     * storeObectId : HNle888E
     * goodsObectId : b047393c2e
     * name : 测试
     * price : 0.1
     * discount : 1.0
     * sum : 1
     */

    private int id;
    private String storeObectId;
    private String goodsObectId;
    private String name;
    private double price;
    private double discount;
    private int sum;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStoreObectId() {
        return storeObectId;
    }

    public void setStoreObectId(String storeObectId) {
        this.storeObectId = storeObectId;
    }

    public String getGoodsObectId() {
        return goodsObectId;
    }

    public void setGoodsObectId(String goodsObectId) {
        this.goodsObectId = goodsObectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
