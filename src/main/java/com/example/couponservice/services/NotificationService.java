package com.example.couponservice.services;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;

public class NotificationService {

    private final AmazonSimpleEmailService ses;


    public NotificationService(AmazonSimpleEmailService ses){
        this.ses = ses;
    }


    public void sendCouponEmail(String coupon, String email){
        String accessKey = System.getenv("MY_AWS_ACCESS_KEY_ID");
        String secretKey = System.getenv("MY_AWS_SECRET_ACCESS_KEY");
        String awsRegion = System.getenv("MY_AWS_REGION");

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);

        AmazonSimpleEmailService ses = AmazonSimpleEmailServiceClient.builder()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(awsRegion)
                .build();

        SendEmailRequest request = new SendEmailRequest()
                .withSource("kordys.radoslaw@gmail.com")
                .withDestination(
                        new Destination().withToAddresses(email))
                .withMessage(new Message()
                        .withSubject(new Content().withData("Custom Restaurant Web App"))
                        .withBody(new Body()
                                .withText(new Content().withData("Hi buddy! its your coupon: " + coupon + " Enjoy!"))));

        ses.sendEmail(request);
    }
}
