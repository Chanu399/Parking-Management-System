package com.nibm.parking.model;

public class Payment {
    private int paymentId;
    private ParkingRecord parkingRecordRef;
    private double amount;
    private String paymentDate;
    private String paymentStatus;
    private String paymentMethod;

    //constructor
    //new paymen
    public Payment(int paymentId, ParkingRecord parkingRecordRef, String paymentMethod) {
        this.paymentId = paymentId;
        this.parkingRecordRef = parkingRecordRef;
        this.amount = parkingRecordRef.getParkingFee();
        this.paymentMethod = paymentMethod;
        this.paymentStatus = "Pending";
    }

    //Used when restoring a payment from  datastoraged
    public Payment(int paymentId, ParkingRecord parkingRecordRef, String paymentMethod,
                    double amount, String paymentStatus, String paymentDate) {
        this.paymentId = paymentId;
        this.parkingRecordRef = parkingRecordRef;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
    }

    //getters
    public int getPaymentId() {
        return paymentId;
    }

    public ParkingRecord getParkingRecordRef() {
        return parkingRecordRef;
    }

    public double getAmount() {
        return amount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    //setters
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    //process payment
    public void processPayment() {
        if (this.amount > 0) {
            this.paymentStatus = "Paid";
            this.paymentDate = java.time.LocalDateTime.now().toString();
            System.out.println("Payment successful.");
        } else {
            this.paymentStatus = "Failed";
            this.paymentDate = java.time.LocalDateTime.now().toString();
            System.out.println("Payment failed.");
        }
    }

    //display details
    public void displayDetails() {
        System.out.println("Payment ID:" + this.paymentId);
        System.out.println("Amount:Rs. " + this.amount);
        System.out.println("Payment Method:" + this.paymentMethod);
        System.out.println("Payment Status:" + this.paymentStatus);
        System.out.println("Payment Date:" + this.paymentDate);
    }

    //generate receipt
    public String generateReceipt() {
        String receipt = "";
        receipt += "===== PARKING RECEIPT =====\n";
        receipt += "Record ID:" + this.parkingRecordRef.getRecordId() + "\n";
        receipt += "Vehicle No:" + this.parkingRecordRef.getParkingSlot().getAssignedVehicle().getNumberPlate() + "\n";
        receipt += "Entry Time:" + this.parkingRecordRef.getEntryTimeFormatted() + "\n";
        receipt += "Exit Time:" + this.parkingRecordRef.getExitTimeFormatted() + "\n";
        receipt += "Fee:Rs. " + this.amount + "\n";
        receipt += "----------------------------\n";
        receipt += "Payment ID:" + this.paymentId + "\n";
        receipt += "Method:" + this.paymentMethod + "\n";
        receipt += "Status:" + this.paymentStatus + "\n";
        receipt += "Date:" + this.paymentDate + "\n";
        receipt += "============================\n";
        return receipt;
    }
}