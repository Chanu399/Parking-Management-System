package com.nibm.parking.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ParkingRecord {
    private int recordId;
    private String numberPlate;
    private ParkingSlot parkingSlot;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double parkingFee;

    //Constructor to create a parking record
    public ParkingRecord(int recordId, String numberPlate, ParkingSlot parkingSlot) {
        this.recordId = recordId;
        this.numberPlate = numberPlate;
        this.parkingSlot = parkingSlot;
        this.entryTime = LocalDateTime.now();
        this.exitTime = null;
        //Initial Parking fee
        this.parkingFee = 0.0;
    }

    //Used only when restoring a record from saved data 
    public ParkingRecord(int recordId, String numberPlate, ParkingSlot parkingSlot,
                          LocalDateTime entryTime, LocalDateTime exitTime, double parkingFee) {
        this.recordId = recordId;
        this.numberPlate = numberPlate;
        this.parkingSlot = parkingSlot;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.parkingFee = parkingFee;
    }

    public int getRecordId() {
        return recordId;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public ParkingSlot getParkingSlot() {
        return parkingSlot;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public double getParkingFee() {
        return parkingFee;
    }

    public String getEntryTimeFormatted() {
        return entryTime == null ? "N/A" : entryTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getExitTimeFormatted() {
        return exitTime == null ? "N/A" : exitTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public void recordExit() {
        exitTime = LocalDateTime.now();
        calculateFee();
    }

    public long calculateDuration() {
        if (exitTime == null) {
            return 0;
        }
        return Duration.between(entryTime, exitTime).toMinutes();
    }

    //Calculating the parking fee
    public double calculateFee() {
        long minutes = calculateDuration();
        double hourlyRate = 100.0;
        double hours = Math.ceil(minutes / 60.0);

        parkingFee = hours * hourlyRate;
        return parkingFee;
    }

    //Displaying the parking record
    public void displayRecord() {
        System.out.println("Record ID:" + recordId);
        System.out.println("Number Plate:" + numberPlate);
        System.out.println("Slot Number:" + parkingSlot.getSlotNo());
        System.out.println("Entry Time : " + entryTime);
        System.out.println("Exit Time : " + exitTime);
        System.out.println("Parking Fee: Rs. " + parkingFee);
    }
}