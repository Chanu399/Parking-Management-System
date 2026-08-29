# Parking Management System

## 1. Introduction

The Parking Management System is a Java-based standalone application developed to automate and simplify the management of parking operations in a small or medium-sized facility. The system allows users to manage customers, vehicles, parking slots, parking records, and payment transactions in a single integrated application.

The project was developed to demonstrate the practical use of object-oriented programming (OOP), Java class modeling, GUI application development, package-based project architecture, and data persistence. It is designed as a coursework project with a clear separation between domain models, service logic, and persistence features.

The application supports both:
- a graphical Swing interface for user-friendly interaction
- a console-based fallback mode for environments without a desktop display

This makes the project flexible, dependable, and suitable for both coursework demonstration and real-world prototype extension.

---

## 2. Problem Statement

Traditional parking administration often involves manual record keeping, errors in slot allocation, and difficulty in tracking vehicle entry/exit and payments. This system addresses those issues by automating the core tasks of:

- customer registration
- vehicle registration and tracking
- parking slot availability checking
- time-based charge calculation
- payment processing
- revenue tracking
- persistent storage of system data

---

## 3. Objectives

The primary objectives of the project are to:

- model real-world entities using OOP principles
- implement a structured Java system with packages and layers
- manage parking operations efficiently
- calculate charges and track collected revenue
- provide a user-friendly interface for operation
- demonstrate clean separation of concerns between model, service, and persistence layers

---

## 4. Scope of the Application

The application is designed around the following scope:

- add and manage customer records
- register vehicles under customer ownership
- validate vehicle registration before parking
- allocate parking slots based on vehicle type
- monitor active parking sessions
- collect payment for each parking record
- persist system data on disk for future sessions

This system is primarily intended for academic and prototype-level usage rather than large commercial parking operations.

---

## 5. Features and Functionalities

### Customer Features
- Add customer details
- View all customers
- Update customer profile
- Delete customer records
- Validate customer NIC before parking or registration

### Vehicle Features
- Register vehicles
- View all vehicles
- Update vehicle plate numbers
- Delete vehicle records
- Check whether a vehicle is registered before allowing parking

### Parking Slot Features
- Create parking slots by type
- Allocate available slots according to vehicle type
- Release slots after exit
- Display slot details and current status

### Parking Record Features
- Track entry time and exit time
- Compute duration of stay
- Calculate parking fee
- Store parking history for review

### Payment Features
- Make payment against a valid parking record
- Prevent duplicate payment for the same record
- Display summary of all payments
- Display total revenue collected

### Data Persistence Features
- Save data to JSON format
- Restore saved data when the app starts

### Interface Features
- Swing graphical desktop interface
- Console fallback mode
- Graceful error handling for failed GUI startup

---

## 6. Use of OOP Concepts

This project demonstrates several key Object-Oriented Programming concepts.

### Encapsulation
Each class contains its own attributes and exposes only necessary access through getter and setter methods. For example:

- Customer stores customer information privately
- Vehicle stores plate number, type, and owner NIC privately
- ParkingSlot manages its slot state internally
- Payment controls its own payment status and amount

This allows data to be protected and accessed in a controlled manner.

### Abstraction
The system hides implementation details behind class methods such as:

- addCustomer()
- parkVehicle()
- makePayment()
- findAvailableSlot()

Users interact with the system through the services rather than internal logic.

### Inheritance
The project defines a class hierarchy where Vehicle acts as a base model, and specialized vehicle concepts can be extended from it. This design provides a reusable foundation for vehicle-specific behavior.

### Polymorphism
The app uses method overloading and method reuse across different services. For example, multiple overloaded methods allow different ways to call parking or vehicle logic depending on the context.

### Association and Aggregation
The system models relationships between objects:

- A Customer can have multiple registered vehicle plates
- A ParkingSlot is associated with a Vehicle during occupancy
- A ParkingRecord is associated with a ParkingSlot
- A Payment is associated with a ParkingRecord

These relationships reflect real-world object associations and are essential to the design.

---

## 7. System Workflow

The core workflow of the application is shown below:

1. Start the system
2. Load existing data from JSON if available
3. Create default parking slots if no prior state exists
4. Launch the desktop interface or console mode
5. User performs actions such as:
   - add customer
   - add vehicle
   - park vehicle
   - remove vehicle
   - process payment
6. System validates business rules
7. Data is updated in memory
8. Data is saved to JSON at the end of operations or during each session flow

### Typical Use Case Flow

```text
Customer Registration
        ↓
Vehicle Registration
        ↓
Parking Slot Allocation
        ↓
Vehicle Entry Recorded
        ↓
Vehicle Exit Recorded
        ↓
Fee Calculation
        ↓
Payment Processing
        ↓
Revenue Update and Save
```

---

## 8. Application Architecture

The project follows a layered architecture that separates major responsibilities into packages.

### Presentation Layer
- Main.java
- SwingApp.java

Handles the user interface and interaction flow.

### Service Layer
- CustomerManager.java
- VehicleManager.java
- ParkingManager.java
- PaymentManager.java

Contains the domain logic and ensures validation, coordination, and workflow actions between models and persistence.

### Model Layer
- Customer.java
- Vehicle.java
- ParkingSlot.java
- ParkingRecord.java
- Payment.java

Represents the core business objects in the system.

### Persistence Layer
- DataStore.java
- SimpleJson.java

Handles JSON serialization and deserialization so the system can save and reload its state.

### Exception Layer
- PaymentFailedException.java

Represents payment-related error handling.

This architecture keeps the code modular and easier to maintain, test, and extend.

---

## 9. Data Model and ERD Representation

The application is built around several interconnected entities.

### Entity Relationships

- One Customer can have many Vehicles
- One Vehicle can be associated with one customer at a time
- One ParkingSlot can be occupied by one Vehicle at a time
- One ParkingRecord belongs to one Vehicle and one ParkingSlot
- One Payment belongs to one ParkingRecord

### ERD-style Conceptual Model

```text
CUSTOMER
---------
customerId (PK)
nic
name
phoneNumber
registrationDate

HAS MANY
    ↓
VEHICLE
--------
numberPlate (PK)
vehicleType
nic (FK to CUSTOMER.nic)

CUSTOMER
    ↓
PARKING SLOT
-------------
slotNo (PK)
slotType
status
numberPlate

VEHICLE
    ↓
PARKING RECORD
---------------
recordId (PK)
numberPlate (FK to VEHICLE.numberPlate)
slotNo (FK to PARKING SLOT.slotNo)
entryTime
exitTime
parkingFee

PARKING RECORD
    ↓
PAYMENT
--------
paymentId (PK)
recordId (FK to PARKING RECORD.recordId)
amount
paymentDate
paymentStatus
paymentMethod
```

This conceptual ERD reflects the actual relationship logic used in the code and supports the system’s business rules.

---

## 10. Design Decisions

### Package-based Organization
The project was restructured into a package layout to better reflect Java best practices and improve maintainability.

### Separation of Concerns
Models represent data, services manage behavior, and the persistence layer handles storage. This makes the project easier to extend and debug.

### Local JSON Storage
A lightweight JSON storage approach was chosen to avoid adding external database dependencies and to keep the application simple and portable.

### Swing UI with Console Fallback
The system includes both a desktop interface and a console option so it can run in environments with or without graphics support.

### Validation-Driven Operation
Key rules are enforced before operations proceed:
- a vehicle must be registered before parking
- a customer must exist before registration
- a payment cannot be processed twice for the same record

---

## 11. Presentation Guidance

The application can be demonstrated in a presentation using the following structure:

### Slide 1: Title and Objective
- Parking Management System
- Objective: automate parking operations using Java

### Slide 2: Problem and Solution
- Manual parking management problems
- Automated system with improved accuracy and control

### Slide 3: OOP Concepts Used
- Encapsulation
- Abstraction
- Inheritance
- Polymorphism
- Association

### Slide 4: Architecture Overview
- App layer
- Service layer
- Model layer
- Persistence layer
- Exception handling

### Slide 5: ERD / Data Design
- Show customer, vehicle, slot, record, and payment relationships

### Slide 6: Functional Demo
- Register customer
- Register vehicle
- Park vehicle
- View slots
- Process payment

### Slide 7: Challenges and Improvements
- Data persistence improvements
- future database migration
- advanced reporting
- multi-user support

### Slide 8: Conclusion
- summary of system capabilities and learning outcomes

---

## 12. Implementation Details

The implementation uses Java classes to represent the domain and provide service behavior.

### Example Flow

When a user parks a vehicle:
1. the front-end collects the vehicle number plate and type
2. the service checks whether the vehicle is registered
3. the system checks and allocates an available slot
4. a ParkingRecord is created
5. the information is saved and displayed to the user

When a payment is made:
1. the user selects a parking record
2. the PaymentManager validates the fee
3. a Payment object is created
4. payment status is updated to Paid
5. total revenue is recalculated

---

## 13. Run Instructions

Open PowerShell in the project root and run:

```powershell
cd "c:\Users\User\OneDrive\Documents\oop cw\chan park\Parking-Management-System"
javac -d out $(Get-ChildItem -Recurse -Filter *.java | Select-Object -ExpandProperty FullName)
java -cp out com.nibm.parking.app.Main
```

If the GUI cannot be launched, the system automatically falls back to the console interface.

---

## 14. Project Outcome

The project successfully demonstrates a complete Java-based solution for parking management, combining object-oriented design, layered architecture, data persistence, and desktop application functionality. It shows how a real-world scenario can be translated into a practical software system using clean Java design principles.

---

## 15. Conclusion

The Parking Management System serves as a strong example of how Java can be used to build a standalone software application with structured design, maintainable code, and meaningful business functionality. It provides an effective foundation for further enhancement, including database integration, reporting, multi-user support, and advanced analytics.

---

## 16. License and Academic Use

This project is intended for educational and coursework use. It can be adapted or expanded for further academic development and demonstration.
