package com.nibm.parking.service;

import com.nibm.parking.model.ParkingRecord;
import com.nibm.parking.model.ParkingSlot;

import java.util.ArrayList;

public class ParkingManager 
{
    //Storing all parking slots
    private ArrayList<ParkingSlot> ParkingSlots;
    //Storing all parking records
    private ArrayList<ParkingRecord> ParkingRecords;
//Constructor
    public ParkingManager(){
        ParkingSlots = new ArrayList<>();
        ParkingRecords = new ArrayList<>();
    }
    public void createParkingSlots(){
        for (int  i = 1; i <= 15; i++){
            ParkingSlots.add(new ParkingSlot(i,"Motorcycle"));
        }
        for (int i = 16; i <= 30; i++){
            ParkingSlots.add(new ParkingSlot(i, "Three wheeler"));
        }
        for (int i = 31; i <= 65; i++){
            ParkingSlots.add(new ParkingSlot(i, "Car/Van"));
        }
    }

public void addParkingSlot(ParkingSlot slot){
    ParkingSlots.add(slot);
}
public ParkingSlot findAvailableSlot(String vehicleType){
    for (ParkingSlot slot : ParkingSlots){
        if (slot.isAvailable() && slot.canFitVehicle(vehicleType)){
            return slot;
        }
    }
    return null;
}
public void parkVehicle(String numberPlate,String vehicleType){
    for (ParkingRecord record : ParkingRecords){
        if (record.getNumberPlate().equals(numberPlate) && record.getExitTime() == null) {
            System.out.println("this vehicle is alredy parked");
            return;
        }
    }
    ParkingSlot slot = findAvailableSlot(vehicleType);
    if (slot == null){
        System.out.println("No parking slot is available.");
        return;
    }
     slot.assignVehicle(numberPlate, vehicleType);
     ParkingRecord record = new ParkingRecord(ParkingRecords.size() +1,numberPlate,slot);
     ParkingRecords.add(record);
     
     System.out.println("Vehicle is parked.");
     System.out.println("Number Plate:" + numberPlate);
     System.out.println("Vehicle Type: " + vehicleType);
     System.out.println("Assigned Slot:" + slot.getSlotNo());

}

public void removeVehicle(String numberPlate)
{ 
    for (ParkingRecord record : ParkingRecords)
    {
        if (record.getNumberPlate().equals(numberPlate) && record.getExitTime() == null){
            record.recordExit();
            ParkingSlot slot = record.getParkingSlot();
            slot.releaseSlot();
            System.out.println("Vehicle removed successfully.");
            System.out.println("Number Plate:" +numberPlate);
            System.out.println("Parking Fee:Rs. " + record.getParkingFee());
            return;
        }
}
System.out.println("Vehicle is not currently parked");
}
public void viewParkingSlots(){
    if (ParkingSlots.isEmpty()){
        System.out.println("No parking slots are available");
    return;
}
}
public void viewParkingRecords(){
  for (ParkingRecord record : ParkingRecords){
     record.displayRecord();
  }
}
public ParkingRecord findParkingRecord(int recordId){
    for (ParkingRecord record : ParkingRecords){
        if (record.getRecordId() == recordId){
            return record;
       
           }
    }
}
  return null;
  }

