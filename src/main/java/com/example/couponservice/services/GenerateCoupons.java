package com.example.couponservice.services;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;

/**
 * Class responsible for generating unique coupon codes.
 */
public class GenerateCoupons {

    private final static SecureRandom random = new SecureRandom();
    /**
     * Generates a unique coupon code by combining a UUID, timestamp, and random number.
     *
     * @return A unique String representing a generated coupon code.
     */
    public static String generateCoupon() {
        UUID uuid = UUID.randomUUID();
        uuid.toString().replace("-", "");
        String convertedUuid = uuid.toString().substring(0, 8);
        long timestamp = Instant.now().toEpochMilli();
        int randomNumber = random.nextInt(100);
        String couponCode = convertedUuid + timestamp + randomNumber;

        return couponCode;
    }
}
