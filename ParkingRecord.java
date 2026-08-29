package com.nibm.parking.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class ParkingRecord
{
    private int RecordId;
    private String NumberPlate;
    private ParkingSlot parkingslot;
    private LocalDateTime EntryTime;
    private LocalDateTime ExitTime;
    private double ParkingFee;
//Constructor to create a parking record
    public ParkingRecord(int RecordId, String NumberPlate, ParkingSlot parkingslot)
   { 
     this.RecordId = RecordId;
     this.NumberPlate = NumberPlate;
     this.parkingslot = parkingslot;
     this.EntryTime = LocalDateTime.now();
     this.ExitTime = null;
     //Initial Parking fee
     this.ParkingFee = 0.0;
    }
    public int getRecordId(){
        return RecordId;
    }
    public String getNumberPlate(){
        return NumberPlate;
    }
    public ParkingSlot getParkingSlot(){
        return parkingslot;
    }
    public LocalDateTime getEntryTime(){
        return EntryTime;
    }
    public LocalDateTime getExitTime(){
        return ExitTime;
    }
    public double getParkingFee(){
        return ParkingFee;
    }
    public void recordExit(){
        ExitTime = LocalDateTime.now();
        calculateFee();
    }
   public long calculateDuration(){
    if (ExitTime == null){
        return 0;
    }
    return Duration.between(EntryTime, ExitTime).toMinutes();
   }
   //Calculating the parking fee
   public double calculateFee(){
     long minutes = calculateDuration();
     double hourlyrate = 100.0;
     double hours = Math.ceil(minutes / 60.0);

     ParkingFee = hours * hourlyrate;
     return ParkingFee;
   }
   //Displaying the parking record
   public void displayRecord(){
    System.out.println("Record ID:" + RecordId);
    System.out.println("Number Plate:" + NumberPlate);
    System.out.println("Slot Number:" + parkingslot.getSlotNo());
    System.out.println("Entry Time : " + EntryTime );
    System.out.println("Exit Time : " + ExitTime);
    System.out.println("Parking Fee: Rs. " + ParkingFee);
      
   }
}
