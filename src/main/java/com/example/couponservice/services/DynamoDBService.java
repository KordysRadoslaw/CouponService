package com.example.couponservice.services;


import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;


public class DynamoDBService {

    private static final SecureRandom random = new SecureRandom();
    private final Table couponTable;
    private final Table userTable;
    private final DynamoDB dynamoDB;


    public DynamoDBService(DynamoDB dynamoDB) {
        this.dynamoDB = dynamoDB;
        this.couponTable = dynamoDB.getTable("Coupons");
        this.userTable = dynamoDB.getTable("RestaurantReservation");

    }
    public Map<String, String> getCoupon(String couponId) {
        Item item = couponTable.getItem("couponId", couponId);

        if (item != null) {
            Map<String, String> couponDetails = new HashMap<>();
            couponDetails.put("couponId", item.getString("couponId"));
            couponDetails.put("used", item.getString("used"));
            return couponDetails;
        } else {
            return null;
        }
    }

    public boolean updateCouponStatus(String couponId, boolean used){
        try {
            UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                    .withPrimaryKey("couponId", couponId)
                    .withUpdateExpression("set used = :used")
                    .withValueMap(new ValueMap().withBoolean(":used", used));

            UpdateItemOutcome outcome = couponTable.updateItem(updateItemSpec);
            return outcome.getUpdateItemResult().getSdkHttpMetadata().getHttpStatusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
    public String getUserByToken(String tokenId) {
        try {
            ItemCollection<QueryOutcome> items = userTable.getIndex("tokenIdIndex").query("tokenId", tokenId);

            Iterator<Item> iterator = items.iterator();
            if (iterator.hasNext()) {
                Item item = iterator.next();
                String userEmail = item.getString("email");
                System.out.println("Found user with tokenId: " + tokenId + ", email: " + userEmail);
                return userEmail;
            } else {
                System.out.println("User not found for tokenId: " + tokenId);
                return null;
            }
        } catch (Exception e) {
            System.out.println("Error in getUserByToken: " + e.getMessage());
            throw new RuntimeException("Błąd: " + e.getMessage());
        }
    }

    //save coupon to db
    public boolean saveCoupon(String date, String user, String coupon){
        try {
            Item item = new Item()
                    .withPrimaryKey("couponId", coupon)
                    .with("date", date)
                    .with("email", user)
                    .with("used", false)
                    .with("value", "5%");
//pamietaj zeby pod uzyciu zmienic used na true


            couponTable.putItem(item);
            return true;
        }catch (Exception e) {
            return false;

        }
    }
}
