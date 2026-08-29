package com.nibm.parking model;
public class ParkingSlot
{
    //Attributes
    private int SlotNo;
    private string SlotType;
    private string Status;
    private string NumberPlate;
    public ParkingSlot(int SlotNo, string SlotType)
    {
        //Constructor 
        this.SlotNo = SlotNo;
        this.SlotType = SlotType;
        this.Status = "Available";
        this.NumberPlate = null;
    }

    public int getSlotNo()
    {
        return slotno;
    }
    public String getStatus;
    {
        return status;
    }
    public String get NumberPlate()
    {
        return numberPlate;
    }
    public boolean isAvailable()
    {
        return status.equals("Available");

    }
    public boolean canFitVehicle(String vehicleType)
    { 
         if(SlotType.equals("Motorcycle")){
            return vehicleType.equqls("Motorcycle");
         }
        if(SlotType.equals("Car/Van")){
            return vehicleType.equals("Car")
             ||vehicleType.equals("Van";)
        }
        return false;

    }

    public void assignVehicle(String NumberPlate, String vehicleType)
    
    {
        if (!isAvailable()){
            System,out.println("Parking slot is already occupied.");
            return
        }
        if (!canFitVehicle(vehicleType)){
            System.out.println("Vehicle cannot use this parking slot.");
            return;
        }
        this.NumberPlate = NumberPlate;
        this.Status = "Occupied";
    }
    //Displaying information
    public void displayDetails(){
        System.out.println("Slot Number:" +SlotNo);
        System.out.println("Slot Type:" +SlotType);
        System.out.println("Status" +Status);

        if (numberPlate != null){
            System.out.println("Vehicle Number:" +numberPlate);
        }
    }
    }