import java.util.ArrayList;

public class CustomerManager {

    private ArrayList<Customer> customers = new ArrayList<>();

    // Create
    public void addCustomer(Customer customer) {
        customers.add(customer);
        System.out.println("Customer added successfully");
    }

    // Read
    public void viewCustomer() {
        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }

        for (Customer customer : customers) {
            System.out.println("Customer ID: " + customer.getCustomerId());
            System.out.println("Customer NIC: " + customer.getNIC());
            System.out.println("Customer Name: " + customer.getName());
            System.out.println("Phone Number: " + customer.getPhoneNumber());
            System.out.println("Registration Date: " + customer.getRegistrationDate());
            System.out.println("Vehicle Number Plate: " + customer.getNumberPlate());
            System.out.println("--------------------");
        }
    }

    // Update
    public void updateCustomer(int CustomerId, String newName,
                               String newPhoneNumber, String newNumberPlate) {
        for (Customer customer : customers) {
            if (customer.getCustomerId() == CustomerId) {
                customer.setName(newName);
                customer.setPhoneNumber(newPhoneNumber);
                customer.setNumberPlate(newNumberPlate);

                System.out.println("Customer updated successfully");
                return;
            }
        }

        System.out.println("Customer not found");
    }

    // Delete
    public void deleteCustomer(int CustomerId) {
        boolean removed = customers.removeIf(
            customer -> customer.getCustomerId() == CustomerId
        );

        if (removed) {
            System.out.println("Customer deleted successfully");
        } else {
            System.out.println("Customer not found");
        }
    }
}