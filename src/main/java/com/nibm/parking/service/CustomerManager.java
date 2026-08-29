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
    public void updateCustomer(int customerId, String newName, String newPhoneNumber) {
        for (Customer customer : customers) {
            if (customer.getCustomerId() == customerId) {
                customer.setName(newName);
                customer.setPhoneNumber(newPhoneNumber);

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

    // Links a NIC entered at parking/vehicle-registration time back to a
    // registered customer. Returns null if no matching customer exists.
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