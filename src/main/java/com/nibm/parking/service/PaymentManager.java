package com.nibm.parking.service;

import java.util.ArrayList;

import com.nibm.parking.exception.PaymentFailedException;
import com.nibm.parking.model.ParkingRecord;
import com.nibm.parking.model.Payment;

public class PaymentManager {
    private ArrayList<Payment> payments = new ArrayList<>();
    private int paymentSequence = 0;

    //Create
    public Payment makePayment(ParkingRecord parkingRecordRef, String paymentMethod) throws PaymentFailedException {
        if (parkingRecordRef.getParkingFee() <= 0) {
            throw new PaymentFailedException("Invalid amount for record " + parkingRecordRef.getRecordId());
        }
        for (Payment existing : payments) {
            if (existing.getParkingRecordRef().getRecordId() == parkingRecordRef.getRecordId()
                    && existing.getPaymentStatus().equals("Paid")) {
                throw new PaymentFailedException("Record " + parkingRecordRef.getRecordId() + " has already been paid");
            }
        }
        paymentSequence++;
        Payment payment = new Payment(paymentSequence, parkingRecordRef, paymentMethod);
        payment.processPayment();
        payments.add(payment);
        System.out.println("Payment added successfully");
        return payment;
    }

    //Read
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
}