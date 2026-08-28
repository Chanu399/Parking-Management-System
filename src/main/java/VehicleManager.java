import java.util.ArrayList;
public class VehicleManager
{
    private ArrayList<Vehicle> vehicles =new ArrayList<>();

//Create
    public void addVehicle(Vehicle vehicle)
    {
        
        vehicles.add(vehicle);
        System.out.println("Vehicle added successfully");
    }
//Read
    public void viewVehicle()
    {
        for (Vehicle vehicle:vehicles)
          {
            vehicle.displayDetails();
            System.out.println("--------------------");
          }
    }
//update
    public void updateVehicle(String NumberPlate, String NewNumberPlate)
    {
        for(Vehicle vehicle:vehicles)
        {
            if (vehicle.getNumberPlate().equals(NumberPlate))
            {
                vehicle.setNumberPlate(NewNumberPlate);
                System.out.println("Vehicle updated successfully");
                return;
            }
        }
        System.out.println("Vehicle not found");
    }
//delete
    public void deleteVehicle(String NumberPlate)
    {
        vehicles.removeIf(vehicle ->vehicle.getNumberPlate().equals(NumberPlate));
        System.out.println("Vehicle deleted successfully");
    }  
}
