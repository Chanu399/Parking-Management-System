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
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles found.");
            return;
        }
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
        boolean removed = vehicles.removeIf(vehicle -> vehicle.getNumberPlate().equals(numberPlate));
        if (removed) {
            System.out.println("Vehicle deleted successfully");
        } else {
            System.out.println("Vehicle not found");
        }
    }

    // Looks up whether a number plate is registered, e.g. before allowing
    // that vehicle to be parked. Returns null if not found.
    public Vehicle findVehicleByNumberPlate(String numberPlate) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getNumberPlate().equals(numberPlate)) {
                return vehicle;
            }
        }
        return null;
    }

    //For saving to storage
    public ArrayList<Vehicle> getAllVehicles() {
        return vehicles;
    }

    //For restoring from storage
    public void loadVehicles(ArrayList<Vehicle> loadedVehicles) {
        this.vehicles = loadedVehicles;
    }
}