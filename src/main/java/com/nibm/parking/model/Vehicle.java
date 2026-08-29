package com.nibm.parking.model;

public class Vehicle {
    private String numberPlate;
    private String vehicleType;
    private String nic;

    //constructor
    public Vehicle(String numberPlate, String vehicleType, String nic) {
        this.numberPlate = numberPlate;
        this.vehicleType = vehicleType;
        this.nic = nic;
    }

    //getters
    public String getNumberPlate() {
        return numberPlate;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public String getNIC() {
        return nic;
    }

    //setters
    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void setNIC(String nic) {
        this.nic = nic;
    }

    //display details
    public void displayDetails() {
        System.out.println("Number Plate:" + this.numberPlate);
        System.out.println("Customer NIC:" + (this.nic == null ? "N/A" : this.nic));
    }
}

//child class 1
class Car extends Vehicle {
    public Car(String numberPlate, String nic) {
        super(numberPlate, "Car", nic);
    }

    public void displayDetails() {
        super.displayDetails();
        System.out.println("Vehicle Type:Car");
    }
}

//child class 2
class ThreeWheeler extends Vehicle {
    public ThreeWheeler(String numberPlate, String nic) {
        super(numberPlate, "ThreeWheeler", nic);
    }

    public void displayDetails() {
        super.displayDetails();
        System.out.println("Vehicle Type:Three Wheeler");
    }
}

//child class 3
class Motorcycle extends Vehicle {
    public Motorcycle(String numberPlate, String nic) {
        super(numberPlate, "Motorcycle", nic);
    }

    public void displayDetails() {
        super.displayDetails();
        System.out.println("Vehicle Type:Motorcycle");
    }
}

//child class 4
class Van extends Vehicle {
    public Van(String numberPlate, String nic) {
        super(numberPlate, "Van", nic);
    }

    public void displayDetails() {
        super.displayDetails();
        System.out.println("Vehicle Type:Van");
    }
}