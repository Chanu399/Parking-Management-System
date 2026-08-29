package com.nibm.parking.model;

import java.time.LocalDate;

public class Customer {

    private int customerId;
    private String nic;
    private String name;
    private String phoneNumber;
    private LocalDate registrationDate;
    private String numberPlate;

    public Customer(int customerId,
                     String nic,
                     String name,
                     String phoneNumber,
                     LocalDate registrationDate,
                     String numberPlate) {

        this.customerId = customerId;
        this.nic = nic;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.registrationDate = registrationDate;
        this.numberPlate = numberPlate;
    }

    // Getters

    public int getCustomerId() {
        return customerId;
    }

    public String getNIC() {
        return nic;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    // Setters

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }

    // Display details

    public void displayDetails() {
        System.out.println("Customer ID: " + customerId);
        System.out.println("Customer NIC: " + nic);
        System.out.println("Customer Name: " + name);
        System.out.println("Phone Number: " + phoneNumber);
        System.out.println("Registration Date: " + registrationDate);
        System.out.println("Vehicle Number Plate: " + numberPlate);
    }
}