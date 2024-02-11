package com.example.couponservice.repositories;

import com.example.couponservice.model.Coupon;

public interface CouponRepository {

    Coupon getCouponById(String couponId);

    boolean updateCouponStatus(String couponId, boolean status);

    void saveCoupon(String date, String email, String couponCode);



}
