package com.nibm.parking.service;

import com.nibm.parking.model.ParkingRecord;
import com.nibm.parking.model.ParkingSlot;

import java.util.ArrayList;

public class ParkingManager {
    //Storing all parking slots
    private ArrayList<ParkingSlot> parkingSlots;
    //Storing all parking records
    private ArrayList<ParkingRecord> parkingRecords;

    //Constructor
    public ParkingManager() {
        parkingSlots = new ArrayList<>();
        parkingRecords = new ArrayList<>();
    }

    public void createParkingSlots() {
        for (int i = 1; i <= 15; i++) {
            parkingSlots.add(new ParkingSlot(i, "Motorcycle"));
        }
        for (int i = 16; i <= 30; i++) {
            parkingSlots.add(new ParkingSlot(i, "Three wheeler"));
        }
        for (int i = 31; i <= 65; i++) {
            parkingSlots.add(new ParkingSlot(i, "Car/Van"));
        }
    }

    public void addParkingSlot(ParkingSlot slot) {
        parkingSlots.add(slot);
    }

    public ParkingSlot findAvailableSlot(String vehicleType) {
        for (ParkingSlot slot : parkingSlots) {
            if (slot.isAvailable() && slot.canFitVehicle(vehicleType)) {
                return slot;
            }
        }
        return null;
    }

    //Kept for backward compatibility - parks without a known owner NIC
    public void parkVehicle(String numberPlate, String vehicleType) {
        parkVehicle(numberPlate, vehicleType, null);
    }

    public void parkVehicle(String numberPlate, String vehicleType, String nic) {
        for (ParkingRecord record : parkingRecords) {
            if (record.getNumberPlate().equals(numberPlate) && record.getExitTime() == null) {
                System.out.println("This vehicle is already parked");
                return;
            }
        }
        ParkingSlot slot = findAvailableSlot(vehicleType);
        if (slot == null) {
            System.out.println("No parking slot is available.");
            return;
        }
        slot.assignVehicle(numberPlate, vehicleType, nic);
        ParkingRecord record = new ParkingRecord(parkingRecords.size() + 1, numberPlate, slot);
        parkingRecords.add(record);

        System.out.println("Vehicle is parked.");
        System.out.println("Number Plate:" + numberPlate);
        System.out.println("Vehicle Type: " + vehicleType);
        System.out.println("Assigned Slot:" + slot.getSlotNo());
    }

    public void removeVehicle(String numberPlate) {
        for (ParkingRecord record : parkingRecords) {
            if (record.getNumberPlate().equals(numberPlate) && record.getExitTime() == null) {
                record.recordExit();
                ParkingSlot slot = record.getParkingSlot();
                slot.releaseSlot();
                System.out.println("Vehicle removed successfully.");
                System.out.println("Number Plate:" + numberPlate);
                System.out.println("Parking Fee:Rs. " + record.getParkingFee());
                return;
            }
        }
        System.out.println("Vehicle is not currently parked");
    }

    public void viewParkingSlots() {
        if (parkingSlots.isEmpty()) {
            System.out.println("No parking slots are available");
            return;
        }
        for (ParkingSlot slot : parkingSlots) {
            slot.displayDetails();
            System.out.println("--------------------");
        }
    }

    public void viewParkingRecords() {
        for (ParkingRecord record : parkingRecords) {
            record.displayRecord();
        }
    }

    public ParkingRecord findParkingRecord(int recordId) {
        for (ParkingRecord record : parkingRecords) {
            if (record.getRecordId() == recordId) {
                return record;
            }
        }
        return null;
    }
}