package com.example.couponservice.handlers;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.example.couponservice.model.Coupon;
import com.example.couponservice.model.User;
import com.example.couponservice.repositories.CouponRepository;
import com.example.couponservice.repositories.CouponRepositoryImpl;
import com.example.couponservice.repositories.UserRepository;
import com.example.couponservice.repositories.UserRepositoryImpl;
import com.example.couponservice.services.GenerateCoupons;
import com.example.couponservice.services.NotificationService;
import com.example.couponservice.services.NotificationServiceImpl;
import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * AWS Lambda handler for processing coupon requests.
 *
 * This handler class handles incoming HTTP requests for generating and managing coupons.
 * It interacts with DynamoDB for user and coupon data storage, and uses SES for sending notification emails.
 */
public class CouponHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    private final NotificationService notificationService;

    public CouponHandler(CouponRepository couponRepository, UserRepository userRepository, NotificationService notificationService) {
        this.couponRepository = couponRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;

    }

    public CouponHandler() {
        AmazonDynamoDB amazonDynamoDBCoupon = AmazonDynamoDBClientBuilder.defaultClient();
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(System.getenv("MY_AWS_ACCESS_KEY_ID"), System.getenv("MY_AWS_SECRET_ACCESS_KEY"));
        AmazonSimpleEmailService ses = AmazonSimpleEmailServiceClient.builder()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(System.getenv("MY_AWS_REGION"))
                .build();

        NotificationService notificationService = new NotificationServiceImpl(ses);
        this.couponRepository = new CouponRepositoryImpl(new DynamoDB(amazonDynamoDBCoupon));
        this.userRepository = new UserRepositoryImpl(new DynamoDB(amazonDynamoDBCoupon));
        this.notificationService = notificationService;

    }


    /**
     * Handles incoming Lambda requests.
     *
     * Dispatches requests based on the HTTP path and method.
     *
     * @param apiGatewayProxyRequestEvent The Lambda Function input containing request details.
     * @param context The Lambda execution environment context object.
     * @return An APIGatewayProxyResponseEvent object representing the Lambda function response.
     */
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Handling http post for /users API endpoint");

        if ("/restaurant/confirmation/useCoupon".equals(apiGatewayProxyRequestEvent.getPath())) {
            return useCoupon(apiGatewayProxyRequestEvent, context);
        }

        String tokenIdFromQueryParameters = apiGatewayProxyRequestEvent.getQueryStringParameters().get("tokenId");

        if (tokenIdFromQueryParameters == null || tokenIdFromQueryParameters.isEmpty()) {
            return new APIGatewayProxyResponseEvent().withStatusCode(400).withBody("tokenId is missing");
        }
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);


        User user = null;
        try {
            user = userRepository.getByTokenId(tokenIdFromQueryParameters);
            String couponCode = GenerateCoupons.generateCoupon();

            if (user == null || user.getEmail().isEmpty()) {
                return new APIGatewayProxyResponseEvent().withStatusCode(400).withBody("User not found");
            } else {
                notificationService.sendCouponEmail(couponCode, user.getEmail());
                couponRepository.saveCoupon(user.getEmail(), couponCode, formattedDateTime);
            }
        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent().withStatusCode(500).withBody("Internal server error");
        }
        return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody("Coupon code generated and sent to user");
    }

    /**
     * @param apiGatewayProxyRequestEvent The Lambda Function input containing request details.
     * @param context The Lambda execution environment context object.
     * @return An APIGatewayProxyResponseEvent object representing the Lambda function response.
     */
    public APIGatewayProxyResponseEvent useCoupon(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Handling http post for /restaurant/confirmation/useCoupon API endpoint");
        if ("POST".equals(apiGatewayProxyRequestEvent.getHttpMethod())) {
            try {
                String requestBody = apiGatewayProxyRequestEvent.getBody();
                Map<String, String> requestDetails = new Gson().fromJson(requestBody, Map.class);
                String couponId = requestDetails.get("couponId");
                logger.log("Request body: " + requestBody);
                logger.log("Request body couponID: " + couponId);

                if (couponId == null || couponId.isEmpty()) {
                    return new APIGatewayProxyResponseEvent().withStatusCode(400).withBody("tokenId or couponCode is missing");
                }

                Coupon userCoupon = couponRepository.getCouponById(couponId);
                logger.log("Coupon: " + userCoupon);
                if (userCoupon.isUsed()) {
                    logger.log("Coupon already used");
                    return new APIGatewayProxyResponseEvent().withStatusCode(400).withBody("Coupon already used");

                }
                if (couponRepository.updateCouponStatus(couponId, true)) {
                    logger.log("Coupon used successfully");
                    return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody("Coupon used successfully");

                }
                return new APIGatewayProxyResponseEvent().withStatusCode(500).withBody("Internal server error");
            } catch (Exception e) {
                logger.log("Error is here: " + e.getMessage());
                return new APIGatewayProxyResponseEvent().withStatusCode(500).withBody("Internal server error");
            }
        }else{
            return new APIGatewayProxyResponseEvent().withStatusCode(400).withBody("Invalid request method");
        }

        }
    }

