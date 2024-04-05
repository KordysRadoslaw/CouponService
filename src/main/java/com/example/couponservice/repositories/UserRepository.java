package com.example.couponservice.repositories;

import com.example.couponservice.model.User;

/**
 * User Repository interface defines methods for interacting with the User Data.
 * It provides functionalities for retrieving user data from the database.
 *
 */
public interface UserRepository {

    /**
     * Retrievies the User basen on the provided
     * @param email
     * @return A User object represanting the user with the provided email.
     */

    User getUserByEmail(String email);

    /**
     * Retrieves the User based on the provided tokenId.
     * @param tokenId
     * @return A User object representing the user with the provided tokenId.
     */

    User getByTokenId(String tokenId);
}
