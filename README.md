# Parking Management System

A Java-based parking management application designed to manage vehicle parking, customer records, vehicle registrations, slot allocation, and payment processing. The system supports both a modern Swing desktop interface and a console-based fallback mode, and it persists data to a local JSON file so information remains available after restarting the application.

## Project Overview

This project simulates a real-world parking management workflow for a small parking facility. It allows administrators to:

- register and manage customers
- register and manage vehicles
- allocate parking slots by vehicle type
- track vehicle entry and exit
- calculate parking charges
- process payments
- view total revenue collected
- persist system data between sessions

The application is structured using Java packages to follow a clean model-service-layer architecture and is ready for future expansion without requiring Maven or Gradle.

## Features

### Customer Management
- Add new customers
- View all customers
- Update customer details
- Delete customers
- Validate customer NIC before allowing parking or vehicle registration

### Vehicle Management
- Register vehicles under a valid customer NIC
- View all registered vehicles
- Update vehicle number plates
- Delete registered vehicles
- Restrict parking when a vehicle has not been registered

### Parking Slot Management
- Create parking slots by category:
  - Motorcycle
  - Three wheeler
  - Car/Van
- Check slot availability
- Allocate a suitable slot based on vehicle type
- Release slots when vehicles exit
- View parking slot status and assigned vehicles

### Parking Records
- Record vehicle entry time
- Record exit time
- Calculate parking duration
- Calculate parking fee based on time elapsed
- View parking history and records

### Payment Management
- Process payment against a parking record
- Validate payment amount before accepting payment
- Prevent duplicate payment for the same record
- View payment history
- Calculate total collected revenue

### User Interface
- Professional Swing-based desktop GUI
- Console fallback mode if the GUI cannot initialize
- Graceful handling of startup and runtime issues

### Data Persistence
- Saves data to a local JSON file: parking_data.json
- Restores previously saved records on application startup

## Project Structure

```text
Parking-Management-System/
├── README.md
├── .gitignore
├── .gitattributes
├── parking_data.json          # Generated at runtime
├── out/                       # Compiled Java classes (generated)
└── src/
    └── main/
        └── java/
            └── com/
                └── nibm/
                    └── parking/
                        ├── app/
                        │   ├── Main.java
                        │   └── SwingApp.java
                        ├── exception/
                        │   └── PaymentFailedException.java
                        ├── model/
                        │   ├── Customer.java
                        │   ├── ParkingRecord.java
                        │   ├── ParkingSlot.java
                        │   ├── Payment.java
                        │   └── Vehicle.java
                        ├── persistance/
                        │   └── DataStore.java
                        ├── service/
                        │   ├── CustomerManager.java
                        │   ├── ParkingManager.java
                        │   ├── PaymentManager.java
                        │   └── VehicleManager.java
                        └── util/
                            └── SimpleJson.java
```

## Main Classes

### App Layer
- Main.java - entry point of the application
- SwingApp.java - desktop GUI front end

### Model Layer
- Customer.java - represents a customer
- Vehicle.java - represents a vehicle
- ParkingSlot.java - represents a parking slot
- ParkingRecord.java - tracks entry/exit and fee information
- Payment.java - models a payment transaction

### Service Layer
- CustomerManager.java - customer operations
- VehicleManager.java - vehicle registry operations
- ParkingManager.java - slot allocation and parking lifecycle logic
- PaymentManager.java - payment processing and total collection tracking

### Persistence Layer
- DataStore.java - loads and saves application state to JSON

## Technology Stack

- Java SE
- Swing (GUI)
- Standard Java collections
- JSON-based local persistence
- Plain javac compilation (no Maven or Gradle required)

## Requirements

- Java Development Kit (JDK) installed
- A desktop environment for the Swing GUI
- Windows, Linux, or macOS terminal support

## How to Run

Open PowerShell in the project root and run:

```powershell
cd "c:\Users\User\OneDrive\Documents\oop cw\chan park\Parking-Management-System"
javac -d out $(Get-ChildItem -Recurse -Filter *.java | Select-Object -ExpandProperty FullName)
java -cp out com.nibm.parking.app.Main
```

If the GUI is not available in the current environment, the application automatically falls back to the console menu so the system remains usable.

## Startup Behavior

At startup, the program:

1. loads any previously saved data from parking_data.json
2. creates parking slots if no previous data exists
3. launches the Swing interface
4. falls back to console mode if the desktop interface cannot start

## Notes

- This project is intentionally structured without Maven or Gradle so it can be compiled using plain javac.
- The application is suitable for academic coursework, system demonstration, and further extension.
- Data is stored in the working directory as parking_data.json.

## License

This project is intended for academic and educational use.

## Author / Project Context

This parking management system was developed as a Java coursework project to demonstrate object-oriented programming concepts, package-based architecture, GUI integration, persistence, and business logic modeling.
