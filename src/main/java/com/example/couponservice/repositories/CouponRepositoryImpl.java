package com.example.couponservice.repositories;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.example.couponservice.model.Coupon;

/**
 * Implementation of the CouponRepository interface
 */

public class CouponRepositoryImpl implements CouponRepository{

    private final Table couponTable;

    public CouponRepositoryImpl(DynamoDB dynamoDB) {
        this.couponTable = dynamoDB.getTable("Coupons");
    }


    /**
     * Retrieves a Coupon object based on the provided coupon ID.
     *
     * @param couponId The unique identifier of the coupon to retrieve.
     * @return A Coupon object representing the retrieved coupon information,
     *         or null if no coupon is found.
     * @throws RuntimeException If an error occurs while accessing the DynamoDB table.
     */
    @Override
    public Coupon getCouponById(String couponId) {
        try{
            Item item = couponTable.getItem("couponId", couponId);
            if(item != null){
                return createCouponFromItem(item);
            }
            return null;
        } catch (Exception e){
            throw new RuntimeException("Error getting coupon by id: " + e);
        }
    }

    /**
     * Updates the "used" status of a coupon in DynamoDB.
     *
     * @param couponId The unique identifier of the coupon to update.
     * @param status The new "used" status for the coupon (true - used, false - unused).
     * @return True if the update operation was successful, false otherwise.
     * @throws RuntimeException If an error occurs while updating the DynamoDB table.
     */
    @Override
    public boolean updateCouponStatus(String couponId, boolean status) {
        try{
            UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                    .withPrimaryKey("couponId", couponId)
                    .withUpdateExpression("set reservationStatus = :s")
                    .withValueMap(new ValueMap().withBoolean(":s", status));


            UpdateItemOutcome outcome = couponTable.updateItem(updateItemSpec);
            return outcome.getUpdateItemResult().getSdkHttpMetadata().getHttpStatusCode() == 200;
        } catch (Exception e){
            throw new RuntimeException("Error updating coupon status");
        }

    }
    /**
     * Saves a new coupon to DynamoDB.
     *
     * @param date The date the coupon was generated.
     * @param email The email address associated with the coupon.
     * @param couponCode The unique code for the coupon.
     * @throws RuntimeException If an error occurs while saving the coupon to DynamoDB.
     */
    @Override
    public void saveCoupon(String date, String email, String couponCode) {
        try{
            Item item = new Item()
                    .withPrimaryKey("couponId", couponCode)
                    .withString("date", date)
                    .withString("email", email)
                    .withBoolean("used", false);

            couponTable.putItem(item);
        } catch (Exception e){
            throw new RuntimeException("Error saving coupon");
        }
    }

    /**
     * Creates a Coupon object from a DynamoDB Item representing a coupon.
     *
     * @param item The DynamoDB Item containing coupon data.
     * @return A Coupon object representing the retrieved coupon information.
     */
    private Coupon createCouponFromItem(Item item){
        Coupon coupon = new Coupon();
        coupon.setCouponId(item.getString("couponId"));
        coupon.setDate(item.getString("date"));
        coupon.setEmail(item.getString("email"));
        coupon.setUsed(item.getBoolean("used"));
        return coupon;
    }
}
