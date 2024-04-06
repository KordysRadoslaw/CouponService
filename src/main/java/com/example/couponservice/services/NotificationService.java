package com.example.couponservice.services;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;

/**
 * Notification Service interface defines methods for sending email notifications to the users.
 */
public interface NotificationService {

    /**
     * Sends a coupon notification email to the provided recipient address.
     *
     * @param coupon The coupon code to include in the email body.
     * @param email The recipient's email address.
     */
    public void sendCouponEmail(String coupon, String email);

}
