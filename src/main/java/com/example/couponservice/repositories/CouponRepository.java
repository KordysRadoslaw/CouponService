package com.example.couponservice.repositories;

import com.example.couponservice.model.Coupon;

/**
 * Coupon Repository interface defines methods for interacting with the Coupon Data.
 */
public interface CouponRepository {

    /**
     * Retrieves the Coupon based on the provided couponId.
     * @param couponId
     * @return A Coupon object representing the coupon with the provided couponId.
     */

    Coupon getCouponById(String couponId);

    /**
     * Updates the Coupon status based on the provided couponId and status.
     * @param couponId
     * @param status
     * @return boolean about status of the update.
     */

    boolean updateCouponStatus(String couponId, boolean status);


    /**
     * Saves the Coupon based on the provided date, email and couponCode.
     * @param date
     * @param email
     * @param couponCode
     */
    void saveCoupon(String date, String email, String couponCode);



}
