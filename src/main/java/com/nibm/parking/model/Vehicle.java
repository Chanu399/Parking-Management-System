package com.nibm.parking.model;

public class Vehicle {
    private String NumberPlate;
    private String VehicleType;
    private int NIC;

    //constructor
    public Vehicle(String NumberPlate, String VehicleType, int NIC) {
        this.NumberPlate = NumberPlate;
        this.VehicleType = VehicleType;
        this.NIC = NIC;
    }

    //getters
    public String getNumberPlate() {
        return NumberPlate;
    }

    public String getVehicleType() {
        return VehicleType;
    }

    public int getNIC() {
        return NIC;
    }

    //setters
    public void setNumberPlate(String NumberPlate) {
        this.NumberPlate = NumberPlate;
    }

    public void setVehicleType(String VehicleType) {
        this.VehicleType = VehicleType;
    }

    public void setNIC(int NIC) {
        this.NIC = NIC;
    }

    //display details
    public void displayDetails() {
        System.out.println("Number Plate:" + this.NumberPlate);
        System.out.println("Customer NIC:" + this.NIC);
    }
}

//child class 1
class Car extends Vehicle {
    //constructor
    public Car(String NumberPlate, int NIC) {
        super(NumberPlate, "Car", NIC);
    }

    public void displayDetails() {
        super.displayDetails();
        System.out.println("Vehicle Type:Car");
    }
}

//child class 2
class ThreeWheeler extends Vehicle {
    //constructor
    public ThreeWheeler(String NumberPlate, int NIC) {
        super(NumberPlate, "ThreeWheeler", NIC);
    }

    public void displayDetails() {
        super.displayDetails();
        System.out.println("Vehicle Type:Three Wheeler");
    }
}

//child class 3
class Motorcycle extends Vehicle {
    //constructor
    public Motorcycle(String NumberPlate, int NIC) {
        super(NumberPlate, "Motorcycle", NIC);
    }

    public void displayDetails() {
        super.displayDetails();
        System.out.println("Vehicle Type:Motorcycle");
    }
}

//Child class 4
class Van extends Vehicle {
    //constructor
    public Van(String NumberPlate, int NIC) {
        super(NumberPlate, "Van", NIC);
    }

    public void displayDetails() {
        super.displayDetails();
        System.out.println("Vehicle Type:Van");
    }
}