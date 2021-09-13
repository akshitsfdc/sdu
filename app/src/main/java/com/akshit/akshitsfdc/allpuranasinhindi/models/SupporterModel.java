package com.akshit.akshitsfdc.allpuranasinhindi.models;


public class SupporterModel implements Comparable<SupporterModel>{

    private String name;
    private String email;
    private String uId;
    private boolean primeMember;
    private long createdTime;
    private String phone;
    private String photoUrl;
    private int amount;
    private String msg;
    private long supportTimeStamp;
    
    public SupporterModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public boolean isPrimeMember() {
        return primeMember;
    }

    public void setPrimeMember(boolean primeMember) {
        this.primeMember = primeMember;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getSupportTimeStamp() {
        return supportTimeStamp;
    }

    public void setSupportTimeStamp(long supportTimeStamp) {
        this.supportTimeStamp = supportTimeStamp;
    }

    @Override
    public int compareTo(SupporterModel o) {
        long difference = this.getSupportTimeStamp() - o.getSupportTimeStamp();
        if(difference > 0){
            return 1;
        }else if(difference < 0){
            return -1;
        }else {
            return 0;
        }
    }
}
