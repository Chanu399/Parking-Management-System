package com.nibm.parking.model;

public class ParkingSlot {
    //Attributes
    private int slotNo;
    private String slotType;
    private String status;
    private String numberPlate;
    private Vehicle assignedVehicle;

    public ParkingSlot(int slotNo, String slotType) {
        //Constructor
        this.slotNo = slotNo;
        this.slotType = slotType;
        this.status = "Available";
        this.numberPlate = null;
        this.assignedVehicle = null;
    }

    public int getSlotNo() {
        return slotNo;
    }

    public String getStatus() {
        return status;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public Vehicle getAssignedVehicle() {
        return assignedVehicle;
    }

    public boolean isAvailable() {
        return status.equals("Available");
    }

    public boolean canFitVehicle(String vehicleType) {
        if (slotType.equals("Motorcycle")) {
            return vehicleType.equals("Motorcycle");
        }
        if (slotType.equals("Three wheeler")) {
            return vehicleType.equals("Three wheeler") || vehicleType.equals("ThreeWheeler");
        }
        if (slotType.equals("Car/Van")) {
            return vehicleType.equals("Car") || vehicleType.equals("Van");
        }
        return false;
    }

    //Kept for backward compatibility - parks without a known owner NIC
    public void assignVehicle(String numberPlate, String vehicleType) {
        assignVehicle(numberPlate, vehicleType, null);
    }

    public void assignVehicle(String numberPlate, String vehicleType, String nic) {
        if (!isAvailable()) {
            System.out.println("Parking slot is already occupied.");
            return;
        }
        if (!canFitVehicle(vehicleType)) {
            System.out.println("Vehicle cannot use this parking slot.");
            return;
        }
        this.numberPlate = numberPlate;
        this.status = "Occupied";
        this.assignedVehicle = new Vehicle(numberPlate, vehicleType, nic);
    }

    public void releaseSlot() {
        this.status = "Available";
        this.numberPlate = null;
        this.assignedVehicle = null;
    }

    //Displaying information
    public void displayDetails() {
        System.out.println("Slot Number:" + slotNo);
        System.out.println("Slot Type:" + slotType);
        System.out.println("Status:" + status);

        if (numberPlate != null) {
            System.out.println("Vehicle Number:" + numberPlate);
        }
    }
}