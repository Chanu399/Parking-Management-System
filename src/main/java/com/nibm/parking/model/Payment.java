package com.nibm.parking.model;

public class Payment {
    private int PaymentId;
    private ParkingRecord ParkingRecordRef;
    private double Amount;
    private String PaymentDate;
    private String PaymentStatus;
    private String PaymentMethod;

    //constructor
    public Payment(int PaymentId, ParkingRecord ParkingRecordRef, String PaymentMethod) {
        this.PaymentId = PaymentId;
        this.ParkingRecordRef = ParkingRecordRef;
        this.Amount = ParkingRecordRef.getParkingFee();
        this.PaymentMethod = PaymentMethod;
        this.PaymentStatus = "Pending";
    }

    //getters
    public int getPaymentId() {
        return PaymentId;
    }

    public ParkingRecord getParkingRecordRef() {
        return ParkingRecordRef;
    }

    public double getAmount() {
        return Amount;
    }

    public String getPaymentDate() {
        return PaymentDate;
    }

    public String getPaymentStatus() {
        return PaymentStatus;
    }

    public String getPaymentMethod() {
        return PaymentMethod;
    }

    //setters
    public void setPaymentStatus(String PaymentStatus) {
        this.PaymentStatus = PaymentStatus;
    }

    public void setPaymentMethod(String PaymentMethod) {
        this.PaymentMethod = PaymentMethod;
    }

    //process payment
    public void processPayment() {
        if (this.Amount > 0) {
            this.PaymentStatus = "Paid";
            this.PaymentDate = java.time.LocalDateTime.now().toString();
            System.out.println("Payment successful.");
        } else {
            this.PaymentStatus = "Failed";
            this.PaymentDate = java.time.LocalDateTime.now().toString();
            System.out.println("Payment failed.");
        }
    }

    //display details
    public void displayDetails() {
        System.out.println("Payment ID:" + this.PaymentId);
        System.out.println("Amount:Rs. " + this.Amount);
        System.out.println("Payment Method:" + this.PaymentMethod);
        System.out.println("Payment Status:" + this.PaymentStatus);
        System.out.println("Payment Date:" + this.PaymentDate);
    }

    //generate receipt
    public String generateReceipt() {
        String receipt = "";
        receipt += "===== PARKING RECEIPT =====\n";
        receipt += "Record ID:" + this.ParkingRecordRef.getRecordId() + "\n";
        receipt += "Vehicle No:" + this.ParkingRecordRef.getParkingSlot().getAssignedVehicle().getNumberPlate() + "\n";
        receipt += "Entry Time:" + this.ParkingRecordRef.getEntryTimeFormatted() + "\n";
        receipt += "Exit Time:" + this.ParkingRecordRef.getExitTimeFormatted() + "\n";
        receipt += "Fee:Rs. " + this.Amount + "\n";
        receipt += "----------------------------\n";
        receipt += "Payment ID:" + this.PaymentId + "\n";
        receipt += "Method:" + this.PaymentMethod + "\n";
        receipt += "Status:" + this.PaymentStatus + "\n";
        receipt += "Date:" + this.PaymentDate + "\n";
        receipt += "============================\n";
        return receipt;
    }
}

