package com.nibm.parking.app;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Scanner;

import com.nibm.parking.exception.PaymentFailedException;
import com.nibm.parking.model.Customer;
import com.nibm.parking.model.ParkingRecord;
import com.nibm.parking.model.Vehicle;
import com.nibm.parking.persistance.DataStore;
import com.nibm.parking.service.CustomerManager;
import com.nibm.parking.service.ParkingManager;
import com.nibm.parking.service.PaymentManager;
import com.nibm.parking.service.VehicleManager;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ParkingManager parkingManager = new ParkingManager();
        CustomerManager customerManager = new CustomerManager();
        PaymentManager paymentManager = new PaymentManager();
        VehicleManager vehicleManager = new VehicleManager();

        boolean loadedFromFile = false;
        try {
            loadedFromFile = DataStore.loadAll(vehicleManager, customerManager, parkingManager, paymentManager);
        } catch (IOException e) {
            System.out.println("Could not read saved data (" + e.getMessage() + "). Starting fresh.");
        }
        if (loadedFromFile) {
            System.out.println("Loaded saved data from parking_data.json");
        } else {
            parkingManager.createParkingSlots();
        }

        int choice;
        do {
            System.out.println("\n===== PARKING MANAGEMENT SYSTEM =====");
            System.out.println("1. Park a Vehicle");
            System.out.println("2. Remove a Vehicle");
            System.out.println("3. View Parking Slots");
            System.out.println("4. View Parking Records");
            System.out.println("5. Add Customer");
            System.out.println("6. View Customers");
            System.out.println("7. Update Customer");
            System.out.println("8. Delete Customer");
            System.out.println("9. Make Payment");
            System.out.println("10. View Payments");
            System.out.println("11. View Total Collected");
            System.out.println("12. Register Vehicle");
            System.out.println("13. View Registered Vehicles");
            System.out.println("14. Update Vehicle Number Plate");
            System.out.println("15. Delete Vehicle");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            choice = Integer.parseInt(sc.nextLine().trim());

            switch (choice) {
                case 1:
                    System.out.print("Enter Number Plate: ");
                    String plate = sc.nextLine();
                    System.out.print("Enter Vehicle Type (Motorcycle/Three wheeler/Car/Van): ");
                    String type = sc.nextLine();
                    System.out.print("Enter NIC: ");
                    String parkNic = sc.nextLine();
                    // The number plate must be a registered vehicle, or it's
                    // refused entry entirely.
                    Vehicle matchedVehicle = vehicleManager.findVehicleByNumberPlate(plate);
                    if (matchedVehicle == null) {
                        System.out.println("Vehicle not registered. Vehicle cannot be parked.");
                        break;
                    }
                    // The NIC must also belong to a registered customer, or the
                    // vehicle is refused entry entirely.
                    Customer matchedCustomer = customerManager.findCustomerByNIC(parkNic);
                    if (matchedCustomer == null) {
                        System.out.println("NIC not registered. Vehicle cannot be parked.");
                        break;
                    }
                    parkingManager.parkVehicle(plate, type, matchedCustomer.getNIC());
                    break;

                case 2:
                    System.out.print("Enter Number Plate to remove: ");
                    String removePlate = sc.nextLine();
                    parkingManager.removeVehicle(removePlate);
                    break;

                case 3:
                    parkingManager.viewParkingSlots();
                    break;

                case 4:
                    parkingManager.viewParkingRecords();
                    break;

                case 5:
                    System.out.print("Enter Customer ID: ");
                    int custId = Integer.parseInt(sc.nextLine());
                    System.out.print("Enter NIC: ");
                    String nic = sc.nextLine();
                    System.out.print("Enter Name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter Phone Number: ");
                    String phone = sc.nextLine();
                    System.out.print("Enter Vehicle Number Plate: ");
                    String custPlate = sc.nextLine();
                    Customer customer = new Customer(custId, nic, name, phone, LocalDate.now(), custPlate);
                    customerManager.addCustomer(customer);
                    break;

                case 6:
                    customerManager.viewCustomers();
                    break;

                case 7:
                    System.out.print("Enter Customer ID to update: ");
                    int updateId = Integer.parseInt(sc.nextLine());
                    System.out.print("Enter new Name: ");
                    String newName = sc.nextLine();
                    System.out.print("Enter new Phone Number: ");
                    String newPhone = sc.nextLine();
                    System.out.print("Enter new Number Plate: ");
                    String newPlate = sc.nextLine();
                    customerManager.updateCustomer(updateId, newName, newPhone, newPlate);
                    break;

                case 8:
                    System.out.print("Enter Customer ID to delete: ");
                    int deleteId = Integer.parseInt(sc.nextLine());
                    customerManager.deleteCustomer(deleteId);
                    break;

                case 9:
                    System.out.print("Enter Record ID for payment: ");
                    int recordId = Integer.parseInt(sc.nextLine());
                    ParkingRecord record = parkingManager.findParkingRecord(recordId);
                    if (record == null) {
                        System.out.println("Parking record not found.");
                        break;
                    }
                    System.out.print("Enter Payment Method (Cash/Card): ");
                    String method = sc.nextLine();
                    try {
                        paymentManager.makePayment(record, method);
                    } catch (PaymentFailedException e) {
                        System.out.println("Payment failed: " + e.getMessage());
                    }
                    break;

                case 10:
                    paymentManager.viewPayments();
                    break;

                case 11:
                    System.out.println("Total Collected: Rs. " + paymentManager.getTotalCollected());
                    break;

                case 12:
                    System.out.print("Enter Number Plate: ");
                    String vehPlate = sc.nextLine();
                    System.out.print("Enter Vehicle Type (Motorcycle/ThreeWheeler/Car/Van): ");
                    String vehType = sc.nextLine();
                    System.out.print("Enter Owner NIC: ");
                    String vehNic = sc.nextLine();
                    vehicleManager.addVehicle(new Vehicle(vehPlate, vehType, vehNic));
                    break;

                case 13:
                    vehicleManager.viewVehicles();
                    break;

                case 14:
                    System.out.print("Enter current Number Plate: ");
                    String currentPlate = sc.nextLine();
                    System.out.print("Enter new Number Plate: ");
                    String newVehPlate = sc.nextLine();
                    vehicleManager.updateVehicle(currentPlate, newVehPlate);
                    break;

                case 15:
                    System.out.print("Enter Number Plate to delete: ");
                    String deletePlate = sc.nextLine();
                    vehicleManager.deleteVehicle(deletePlate);
                    break;

                case 0:
                    System.out.println("Exiting system. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid choice. Try again.");
            }

            try {
                DataStore.saveAll(vehicleManager, customerManager, parkingManager, paymentManager);
            } catch (IOException e) {
                System.out.println("Warning: could not save data (" + e.getMessage() + ")");
            }
        } while (choice != 0);

        sc.close();
    }
}