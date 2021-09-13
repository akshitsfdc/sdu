package com.akshit.akshitsfdc.allpuranasinhindi.models;

import java.util.ArrayList;

public class UserOrderDB {

    private ArrayList<UserOrderModel> orderList;

    public UserOrderDB() {
    }

    public UserOrderDB(ArrayList<UserOrderModel> orderList) {
        this.setOrderList(orderList);
    }

    public ArrayList<UserOrderModel> getOrderList() {
        return orderList;
    }

    public void setOrderList(ArrayList<UserOrderModel> orderList) {
        this.orderList = orderList;
    }
}
