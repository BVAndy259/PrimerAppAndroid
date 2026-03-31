package com.lordkratos.gestion501.model;

public class Customer {
    private String customerId;
    private String customerUid;
    private String names;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String dni;
    private String direction;
    private String base64Image;

    public Customer() {}

    public Customer(String customerId, String customerUid, String names, String lastName, String email, String phoneNumber, String dni, String direction, String base64Image) {
        this.customerId = customerId;
        this.customerUid = customerUid;
        this.names = names;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dni = dni;
        this.direction = direction;
        this.base64Image = base64Image;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerUid() {
        return customerUid;
    }

    public void setCustomerUid(String customerUid) {
        this.customerUid = customerUid;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }
}
