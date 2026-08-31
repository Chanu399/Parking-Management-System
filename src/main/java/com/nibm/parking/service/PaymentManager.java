package com.nibm.parking.service;

import java.util.ArrayList;

import com.nibm.parking.exception.PaymentFailedException;
import com.nibm.parking.model.ParkingRecord;
import com.nibm.parking.model.Payment;

public class PaymentManager {
    private ArrayList<Payment> payments = new ArrayList<>();
    private int paymentSequence = 0;

    // 2 validation checks
    // valid amount
    public Payment makePayment(ParkingRecord parkingRecordRef, String paymentMethod) throws PaymentFailedException {
        if (parkingRecordRef.getParkingFee() <= 0) {
            throw new PaymentFailedException("Invalid amount for record " + parkingRecordRef.getRecordId());
        }
        //alredy paied
        for (Payment existing : payments) {
            if (existing.getParkingRecordRef().getRecordId() == parkingRecordRef.getRecordId()
                    && existing.getPaymentStatus().equals("Paid")) {
                //fail calling
                throw new PaymentFailedException("Record " + parkingRecordRef.getRecordId() + " has already been paid");
            }
        }
        // success payment
        paymentSequence++;
        Payment payment = new Payment(paymentSequence, parkingRecordRef, paymentMethod);
        payment.processPayment();
        payments.add(payment);
        System.out.println("Payment added successfully");
        return payment;
    }

    //loop payment and call display details
    public void viewPayments() {
        for (Payment payment : payments) {
            payment.displayDetails();
            System.out.println("--------------------");
        }
    }

    //Update
    public void updatePaymentStatus(int paymentId, String newStatus) {
        for (Payment payment : payments) {
            if (payment.getPaymentId() == paymentId) {
                payment.setPaymentStatus(newStatus);
                return;
            }
        }
    }

    //Delete
    public void deletePayment(int paymentId) {
        payments.removeIf(payment -> payment.getPaymentId() == paymentId);
    }

    //Get total collected
    public double getTotalCollected() {
        double total = 0;
        for (Payment payment : payments) {
            if (payment.getPaymentStatus().equals("Paid")) {
                total += payment.getAmount();
            }
        }
        return total;
    }

    //For saving to storage
    public ArrayList<Payment> getAllPayments() {
        return payments;
    }

    //For restoring from storage 
    public void loadPayments(ArrayList<Payment> loadedPayments) {
        this.payments = loadedPayments;
        int maxId = 0;
        for (Payment payment : loadedPayments) {
            if (payment.getPaymentId() > maxId) {
                maxId = payment.getPaymentId();
            }
        }
        this.paymentSequence = maxId;
    }
}