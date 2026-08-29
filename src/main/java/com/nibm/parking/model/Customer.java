package com.nibm.parking.model;

import java.time.LocalDate;
import java.util.ArrayList;

public class Customer {

    private int customerId;
    private String nic;
    private String name;
    private String phoneNumber;
    private LocalDate registrationDate;
    private ArrayList<String> numberPlates;

    // Normal constructor — used when creating a brand-new customer with no
    // vehicles registered under them yet.
    public Customer(int customerId,
                     String nic,
                     String name,
                     String phoneNumber,
                     LocalDate registrationDate) {

        this.customerId = customerId;
        this.nic = nic;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.registrationDate = registrationDate;
        this.numberPlates = new ArrayList<>();
    }

    // Restore constructor — used when reloading a customer, along with their
    // already-known vehicle plates, from saved storage.
    public Customer(int customerId,
                     String nic,
                     String name,
                     String phoneNumber,
                     LocalDate registrationDate,
                     ArrayList<String> numberPlates) {

        this.customerId = customerId;
        this.nic = nic;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.registrationDate = registrationDate;
        this.numberPlates = numberPlates;
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

    public ArrayList<String> getNumberPlates() {
        return numberPlates;
    }

    // Setters

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // Adds a vehicle plate to this customer's list — e.g. called when a
    // vehicle is registered under their NIC.
    public void addNumberPlate(String numberPlate) {
        numberPlates.add(numberPlate);
    }

    // Display details

    public void displayDetails() {
        System.out.println("Customer ID: " + customerId);
        System.out.println("Customer NIC: " + nic);
        System.out.println("Customer Name: " + name);
        System.out.println("Phone Number: " + phoneNumber);
        System.out.println("Registration Date: " + registrationDate);
        System.out.println("Vehicle Number Plates: " + numberPlates);
    }
}