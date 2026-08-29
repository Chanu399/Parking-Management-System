import java.time.LocalDate;

public class Customer {

    private int CustomerId;
    private String NIC;
    private String Name;
    private String PhoneNumber;
    private LocalDate RegistrationDate;
    private String NumberPlate;

    public Customer(int CustomerId,
                    String NIC,
                    String Name,
                    String PhoneNumber,
                    LocalDate RegistrationDate,
                    String NumberPlate) {

        this.CustomerId = CustomerId;
        this.NIC = NIC;
        this.Name = Name;
        this.PhoneNumber = PhoneNumber;
        this.RegistrationDate = RegistrationDate;
        this.NumberPlate = NumberPlate;
    }

    // Getters

    public int getCustomerId() {
        return CustomerId;
    }

    public String getNIC() {
        return NIC;
    }

    public String getName() {
        return Name;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public LocalDate getRegistrationDate() {
        return RegistrationDate;
    }

    public String getNumberPlate() {
        return NumberPlate;
    }

    // Setters

    public void setName(String Name) {
        this.Name = Name;
    }

    public void setPhoneNumber(String PhoneNumber) {
        this.PhoneNumber = PhoneNumber;
    }

    public void setNumberPlate(String NumberPlate) {
        this.NumberPlate = NumberPlate;
    }

    // Display details

    public void displayDetails() {
        System.out.println("Customer ID: " + CustomerId);
        System.out.println("Customer NIC: " + NIC);
        System.out.println("Customer Name: " + Name);
        System.out.println("Phone Number: " + PhoneNumber);
        System.out.println("Registration Date: " + RegistrationDate);
        System.out.println("Vehicle Number Plate: " + NumberPlate);
    }
}