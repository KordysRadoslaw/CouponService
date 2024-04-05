package com.example.couponservice.repositories;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.example.couponservice.model.User;

/**
 * Implementation of the UserRepository interface.
 */
public class UserRepositoryImpl implements UserRepository{

    private final Table userTable;

    public UserRepositoryImpl(DynamoDB dynamoDB) {
        this.userTable = dynamoDB.getTable("RestaurantReservation");
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserByEmail(String email) {
        Item item = userTable.getItem("email", email);
        if(item != null){
            return createUserFromItem(item);

        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getByTokenId(String tokenId) {
        Item item = userTable.getItem("tokenId", tokenId);
        if(item != null){
            return createUserFromItem(item);
        }
        return null;
    }

    /**
     * Creates a User object based on the provided DynamoDB Item.
     *
     * @param item The DynamoDB Item containing user data.
     * @return A User object representing the retrieved user information.
     */
    private User createUserFromItem(Item item){
        User user = new User();
        user.setFirstName(item.getString("firstName"));
        user.setLastName(item.getString("lastName"));
        user.setEmail(item.getString("email"));
        user.setNumberOfGuests(item.getString("numberOfGuests"));
        user.setTokenId(item.getString("tokenId"));
        return user;
    }
}
