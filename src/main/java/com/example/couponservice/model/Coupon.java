package com.example.couponservice.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "Coupons")
public class Coupon {
    private String couponId;
    private String date;
    private String email;
    private boolean used;

    private String value;

    public Coupon() {
    }

    public Coupon(String couponId, String date, String email, boolean used, String value) {
        this.couponId = couponId;
        this.date = date;
        this.email = email;
        this.used = used;
        this.value = value;
    }

    @DynamoDBHashKey(attributeName = "couponId")
    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    @DynamoDBAttribute
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    @DynamoDBAttribute
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    @DynamoDBAttribute
    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
    @DynamoDBAttribute
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
