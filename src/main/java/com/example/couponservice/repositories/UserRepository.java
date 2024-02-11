package com.example.couponservice.repositories;

import com.example.couponservice.model.User;

public interface UserRepository {

    User getUserByEmail(String email);

    User getByTokenId(String tokenId);
}
