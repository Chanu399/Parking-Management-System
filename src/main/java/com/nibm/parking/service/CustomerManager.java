package com.nibm.parking.service;

import java.util.ArrayList;

import com.nibm.parking.model.Customer;

public class CustomerManager {

    private ArrayList<Customer> customers = new ArrayList<>();

    // Create
    public void addCustomer(Customer customer) {
        customers.add(customer);
        System.out.println("Customer added successfully");
    }

    // Read
    public void viewCustomers() {
        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }
        for (Customer customer : customers) {
            customer.displayDetails();
            System.out.println("--------------------");
        }
    }

    // Update
    public void updateCustomer(int customerId, String newName,
                                String newPhoneNumber, String newNumberPlate) {
        for (Customer customer : customers) {
            if (customer.getCustomerId() == customerId) {
                customer.setName(newName);
                customer.setPhoneNumber(newPhoneNumber);
                customer.setNumberPlate(newNumberPlate);

                System.out.println("Customer updated successfully");
                return;
            }
        }

        System.out.println("Customer not found");
    }

    // Delete
    public void deleteCustomer(int customerId) {
        boolean removed = customers.removeIf(
            customer -> customer.getCustomerId() == customerId
        );

        if (removed) {
            System.out.println("Customer deleted successfully");
        } else {
            System.out.println("Customer not found");
        }
    }

    // Links a number plate (e.g. one being parked) back to a registered customer
    public Customer findCustomerByNumberPlate(String numberPlate) {
        for (Customer customer : customers) {
            if (customer.getNumberPlate().equals(numberPlate)) {
                return customer;
            }
        }
        return null;
    }

    // Links a NIC entered at parking time back to a registered customer.
    // Returns null if no matching customer exists — caller should just
    // park without a customer link in that case.
    public Customer findCustomerByNIC(String nic) {
        for (Customer customer : customers) {
            if (customer.getNIC().equals(nic)) {
                return customer;
            }
        }
        return null;
    }

    //For saving to storage
    public ArrayList<Customer> getAllCustomers() {
        return customers;
    }

    //For restoring from storage
    public void loadCustomers(ArrayList<Customer> loadedCustomers) {
        this.customers = loadedCustomers;
    }
}