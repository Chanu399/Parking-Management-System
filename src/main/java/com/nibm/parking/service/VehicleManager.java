package com.nibm.parking.service;

import java.util.ArrayList;

import com.nibm.parking.model.Vehicle;

public class VehicleManager {
    private ArrayList<Vehicle> vehicles = new ArrayList<>();

    //Create
    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        System.out.println("Vehicle added successfully");
    }

    //Read
    public void viewVehicles() {
        for (Vehicle vehicle : vehicles) {
            vehicle.displayDetails();
            System.out.println("--------------------");
        }
    }

    //Update
    public void updateVehicle(String numberPlate, String newNumberPlate) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getNumberPlate().equals(numberPlate)) {
                vehicle.setNumberPlate(newNumberPlate);
                System.out.println("Vehicle updated successfully");
                return;
            }
        }
        System.out.println("Vehicle not found");
    }

    //Delete
    public void deleteVehicle(String numberPlate) {
        vehicles.removeIf(vehicle -> vehicle.getNumberPlate().equals(numberPlate));
        System.out.println("Vehicle deleted successfully");
    }
}