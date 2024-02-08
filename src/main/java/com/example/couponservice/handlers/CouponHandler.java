package com.example.couponservice.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.example.couponservice.services.DynamoDBService;
import com.example.couponservice.services.NotificationService;
import com.example.couponservice.services.GenerateCoupons;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


public class CouponHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

//    private final static SecureRandom random = new SecureRandom();
    private final DynamoDBService dynamoDBServiceCoupon;
    private final DynamoDBService dynamoDBServiceRestaurantReservation;
    private final NotificationService notificationService;
    private final AmazonDynamoDB amazonDynamoDBCoupon;
    private final AmazonDynamoDB amazonDynamoDBReservation;


    public CouponHandler(){
        this.amazonDynamoDBCoupon = AmazonDynamoDBClient.builder().build();
        this.amazonDynamoDBReservation = AmazonDynamoDBClient.builder().build();
        this.dynamoDBServiceCoupon = new DynamoDBService(new DynamoDB(amazonDynamoDBCoupon));
        this.dynamoDBServiceRestaurantReservation = new DynamoDBService(new DynamoDB(amazonDynamoDBReservation));
        this.notificationService = new NotificationService(new AmazonSimpleEmailServiceClient());
    }

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {

        LambdaLogger logger = context.getLogger();
        logger.log("Handling http post for /users API endpoint");


        if ("/restaurant/confirmation/useCoupon".equals(apiGatewayProxyRequestEvent.getPath())) {
            return useCoupon(apiGatewayProxyRequestEvent, context);
        }

        String requestBody = apiGatewayProxyRequestEvent.getBody();
        Gson gson = new Gson();

        Map<String, String> requestDetails = gson.fromJson(requestBody, Map.class);

        String tokenIdFromPath = apiGatewayProxyRequestEvent.getQueryStringParameters().get("tokenId");

        if (tokenIdFromPath == null || tokenIdFromPath.isEmpty()) {
            return new APIGatewayProxyResponseEvent().withStatusCode(400).withBody("Token ID not found in request path");
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);

        Map<String, String> returnValue = new HashMap<>();
        String user = null;

        try{
            user = dynamoDBServiceRestaurantReservation.getUserByToken(tokenIdFromPath);
            String couponCode = GenerateCoupons.generateCoupon();
            String date = formattedDateTime;

            if(user == null || user.isEmpty()){
                returnValue.put("message", "user not found");
                return new APIGatewayProxyResponseEvent().withStatusCode(400).withBody("user not found");

            }else{

                notificationService.sendCouponEmail(couponCode, user);
                dynamoDBServiceCoupon.saveCoupon(date, user, couponCode);

            }
        }catch(Exception e){

            return new APIGatewayProxyResponseEvent().withStatusCode(400).withBody("error  " + e);
        }
        return new APIGatewayProxyResponseEvent().withBody("user is: " + user + " congratulations! You have received a coupon!");
    }

    public APIGatewayProxyResponseEvent useCoupon(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Handling http post for /users API endpoint");

        if("POST".equals(apiGatewayProxyRequestEvent.getHttpMethod())){
            try{
                String requestBody = apiGatewayProxyRequestEvent.getBody();
                Map<String, String> requestDetails = new Gson().fromJson(requestBody, Map.class);
                String couponId = requestDetails.get("couponId");

                if(couponId == null || couponId.isEmpty()){
                    return new APIGatewayProxyResponseEvent().withStatusCode(400).withBody("Coupon ID not found in request body");
                }
                Map<String, String> userCoupon = dynamoDBServiceCoupon.getCoupon(couponId);
                if(userCoupon.get("used").equals("true")){
                    return new APIGatewayProxyResponseEvent().withStatusCode(400).withBody("Coupon already used");

                }
                if(dynamoDBServiceCoupon.updateCouponStatus(userCoupon.get("couponId"), true)){
                    return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody("Coupon used");
                }
                return new APIGatewayProxyResponseEvent().withStatusCode(400).withBody("Coupon not found");

            } catch (JsonSyntaxException e) {
                return new APIGatewayProxyResponseEvent().withStatusCode(400).withBody("Invalid JSON format");
            }
        }else {
            return new APIGatewayProxyResponseEvent().withStatusCode(405).withBody("Wrong HTTP method. Use POST.");
        }
    }

}
