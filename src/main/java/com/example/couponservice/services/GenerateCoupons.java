package com.example.couponservice.services;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;

public class GenerateCoupons {

    private final static SecureRandom random = new SecureRandom();
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
