package com.example.couponservice.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
@DynamoDBTable(tableName = "RestaurantReservation")
public class User {

    private String firstName;
    private String lastName;
    private String email;
    private String numberOfGuests;
    private String tokenId;

    private String reservationStatus;


    public User() {
    }

    public User(String firstName, String lastName, String email, String numberOfGuests, String tokenId, String reservationStatus) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.numberOfGuests = numberOfGuests;
        this.tokenId = tokenId;
        this.reservationStatus = reservationStatus;
    }

    @DynamoDBAttribute
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @DynamoDBAttribute
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @DynamoDBAttribute
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDBAttribute
    public String getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(String numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }
    @DynamoDBAttribute
    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }
    @DynamoDBAttribute
    public String getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(String reservationStatus) {
        this.reservationStatus = reservationStatus;
    }
}
