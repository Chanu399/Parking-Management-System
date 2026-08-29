import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ParkingManager parkingManager = new ParkingManager();
        CustomerManager customerManager = new CustomerManager();
        PaymentManager paymentManager = new PaymentManager();

        parkingManager.createParkingSlots();

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
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            choice = Integer.parseInt(sc.nextLine().trim());

            switch (choice) {
                case 1:
                    System.out.print("Enter Number Plate: ");
                    String plate = sc.nextLine();
                    System.out.print("Enter Vehicle Type (Motorcycle/Three wheeler/Car/Van): ");
                    String type = sc.nextLine();
                    parkingManager.parkVehicle(plate, type);
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
                    customerManager.viewCustomer();
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

                case 0:
                    System.out.println("Exiting system. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 0);

        sc.close();
    }
}