package com.nibm.parking.model;

public class ParkingSlot {
    //Attributes
    private int SlotNo;
    private String SlotType;
    private String Status;
    private String NumberPlate;
    private Vehicle assignedVehicle;

    public ParkingSlot(int SlotNo, String SlotType) {
        //Constructor
        this.SlotNo = SlotNo;
        this.SlotType = SlotType;
        this.Status = "Available";
        this.NumberPlate = null;
        this.assignedVehicle = null;
    }

    public int getSlotNo() {
        return SlotNo;
    }

    public String getStatus() {
        return Status;
    }

    public String getNumberPlate() {
        return NumberPlate;
    }

    public Vehicle getAssignedVehicle() {
        return assignedVehicle;
    }

    public boolean isAvailable() {
        return Status.equals("Available");
    }

    public boolean canFitVehicle(String vehicleType) {
        if (SlotType.equals("Motorcycle")) {
            return vehicleType.equals("Motorcycle");
        }
        if (SlotType.equals("Three wheeler")) {
            return vehicleType.equals("Three wheeler") || vehicleType.equals("ThreeWheeler");
        }
        if (SlotType.equals("Car/Van")) {
            return vehicleType.equals("Car") || vehicleType.equals("Van");
        }
        return false;
    }

    public void assignVehicle(String NumberPlate, String vehicleType) {
        if (!isAvailable()) {
            System.out.println("Parking slot is already occupied.");
            return;
        }
        if (!canFitVehicle(vehicleType)) {
            System.out.println("Vehicle cannot use this parking slot.");
            return;
        }
        this.NumberPlate = NumberPlate;
        this.Status = "Occupied";
        this.assignedVehicle = new Vehicle(NumberPlate, vehicleType, 0);
    }

    public void releaseSlot() {
        this.Status = "Available";
        this.NumberPlate = null;
        this.assignedVehicle = null;
    }

    //Displaying information
    public void displayDetails() {
        System.out.println("Slot Number:" + SlotNo);
        System.out.println("Slot Type:" + SlotType);
        System.out.println("Status" + Status);

        if (NumberPlate != null) {
            System.out.println("Vehicle Number:" + NumberPlate);
        }
    }
}