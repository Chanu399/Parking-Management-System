package com.nibm.parking.app;

import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.io.IOException;
import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.nibm.parking.exception.PaymentFailedException;
import com.nibm.parking.model.Customer;
import com.nibm.parking.model.ParkingRecord;
import com.nibm.parking.model.ParkingSlot;
import com.nibm.parking.model.Payment;
import com.nibm.parking.model.Vehicle;
import com.nibm.parking.persistance.DataStore;
import com.nibm.parking.service.CustomerManager;
import com.nibm.parking.service.ParkingManager;
import com.nibm.parking.service.PaymentManager;
import com.nibm.parking.service.VehicleManager;

/**
 * A simple Swing front end for the parking system, covering the same
 * operations as the console menu across four tabs: Parking, Customers,
 * Vehicles, and Payments.
 */
public class SwingApp {

    private final VehicleManager vehicleManager;
    private final CustomerManager customerManager;
    private final ParkingManager parkingManager;
    private final PaymentManager paymentManager;

    private JFrame frame;
    private JLabel totalLabel;

    private DefaultTableModel slotsTableModel;
    private DefaultTableModel recordsTableModel;
    private DefaultTableModel customersTableModel;
    private DefaultTableModel vehiclesTableModel;
    private DefaultTableModel paymentsTableModel;

    private SwingApp(VehicleManager vehicleManager,
                      CustomerManager customerManager,
                      ParkingManager parkingManager,
                      PaymentManager paymentManager) {
        this.vehicleManager = vehicleManager;
        this.customerManager = customerManager;
        this.parkingManager = parkingManager;
        this.paymentManager = paymentManager;
    }

    // Entry point called from Main. Throws if the GUI can't start at all
    // (e.g. no display available), so the caller can fall back to console mode.
    public static void launch(VehicleManager vehicleManager,
                               CustomerManager customerManager,
                               ParkingManager parkingManager,
                               PaymentManager paymentManager) throws Exception {
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException("No display available");
        }
        SwingApp app = new SwingApp(vehicleManager, customerManager, parkingManager, paymentManager);
        SwingUtilities.invokeAndWait(app::buildAndShow);
    }

    private void buildAndShow() {
        frame = new JFrame("Parking Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(950, 620);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Parking", buildParkingTab());
        tabs.addTab("Customers", buildCustomersTab());
        tabs.addTab("Vehicles", buildVehiclesTab());
        tabs.addTab("Payments", buildPaymentsTab());

        frame.add(tabs, BorderLayout.CENTER);
        refreshAll();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // ---------- Parking tab ----------

    private JPanel buildParkingTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel form = new JPanel(new java.awt.GridLayout(0, 2, 6, 6));
        JTextField plateField = new JTextField();
        JComboBox<String> typeCombo = new JComboBox<>(new String[] {"Motorcycle", "Three wheeler", "Car", "Van"});
        JTextField nicField = new JTextField();
        JButton parkButton = new JButton("Park Vehicle");
        JButton removeButton = new JButton("Remove Vehicle (plate above)");

        form.add(new JLabel("Number Plate:"));
        form.add(plateField);
        form.add(new JLabel("Vehicle Type:"));
        form.add(typeCombo);
        form.add(new JLabel("NIC:"));
        form.add(nicField);
        form.add(parkButton);
        form.add(removeButton);

        parkButton.addActionListener(e -> {
            String plate = plateField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();
            String nic = nicField.getText().trim();
            if (plate.isEmpty() || nic.isEmpty()) {
                showNotice("Enter a number plate and NIC.");
                return;
            }
            Vehicle matchedVehicle = vehicleManager.findVehicleByNumberPlate(plate);
            if (matchedVehicle == null) {
                showNotice("Vehicle not registered. Vehicle cannot be parked.");
                return;
            }
            Customer matchedCustomer = customerManager.findCustomerByNIC(nic);
            if (matchedCustomer == null) {
                showNotice("NIC not registered. Vehicle cannot be parked.");
                return;
            }
            parkingManager.parkVehicle(plate, type, matchedCustomer.getNIC());
            saveAndRefresh();
        });

        removeButton.addActionListener(e -> {
            String plate = plateField.getText().trim();
            if (plate.isEmpty()) {
                showNotice("Enter a number plate to remove.");
                return;
            }
            parkingManager.removeVehicle(plate);
            saveAndRefresh();
        });

        slotsTableModel = new DefaultTableModel(new Object[] {"Slot No", "Type", "Status", "Plate", "NIC"}, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable slotsTable = new JTable(slotsTableModel);

        recordsTableModel = new DefaultTableModel(
                new Object[] {"Record ID", "Plate", "Slot No", "Entry", "Exit", "Fee"}, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable recordsTable = new JTable(recordsTableModel);

        JTabbedPane tables = new JTabbedPane();
        tables.addTab("Parking Slots", new JScrollPane(slotsTable));
        tables.addTab("Parking Records", new JScrollPane(recordsTable));

        panel.add(form, BorderLayout.NORTH);
        panel.add(tables, BorderLayout.CENTER);
        return panel;
    }

    // ---------- Customers tab ----------

    private JPanel buildCustomersTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel form = new JPanel(new java.awt.GridLayout(0, 2, 6, 6));
        JTextField idField = new JTextField();
        JTextField nicField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JButton addButton = new JButton("Add Customer");
        JButton updateButton = new JButton("Update Name/Phone (ID above)");
        JButton deleteButton = new JButton("Delete (ID above)");

        form.add(new JLabel("Customer ID:"));
        form.add(idField);
        form.add(new JLabel("NIC:"));
        form.add(nicField);
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Phone:"));
        form.add(phoneField);
        form.add(addButton);
        form.add(updateButton);
        form.add(deleteButton);
        form.add(new JLabel());

        addButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String nic = nicField.getText().trim();
                String name = nameField.getText().trim();
                String phone = phoneField.getText().trim();
                if (nic.isEmpty() || name.isEmpty() || phone.isEmpty()) {
                    showNotice("Fill in NIC, Name, and Phone.");
                    return;
                }
                Customer customer = new Customer(id, nic, name, phone, LocalDate.now());
                customerManager.addCustomer(customer);
                saveAndRefresh();
            } catch (NumberFormatException ex) {
                showNotice("Customer ID must be a number.");
            }
        });

        updateButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                customerManager.updateCustomer(id, nameField.getText().trim(), phoneField.getText().trim());
                saveAndRefresh();
            } catch (NumberFormatException ex) {
                showNotice("Customer ID must be a number.");
            }
        });

        deleteButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                customerManager.deleteCustomer(id);
                saveAndRefresh();
            } catch (NumberFormatException ex) {
                showNotice("Customer ID must be a number.");
            }
        });

        customersTableModel = new DefaultTableModel(
                new Object[] {"ID", "NIC", "Name", "Phone", "Registered", "Plates"}, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable customersTable = new JTable(customersTableModel);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(customersTable), BorderLayout.CENTER);
        return panel;
    }

    // ---------- Vehicles tab ----------

    private JPanel buildVehiclesTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel form = new JPanel(new java.awt.GridLayout(0, 2, 6, 6));
        JTextField plateField = new JTextField();
        JComboBox<String> typeCombo = new JComboBox<>(new String[] {"Motorcycle", "ThreeWheeler", "Car", "Van"});
        JTextField nicField = new JTextField();
        JTextField newPlateField = new JTextField();
        JButton registerButton = new JButton("Register Vehicle");
        JButton updateButton = new JButton("Update Plate (old above -> new below)");
        JButton deleteButton = new JButton("Delete (plate above)");

        form.add(new JLabel("Number Plate:"));
        form.add(plateField);
        form.add(new JLabel("Vehicle Type:"));
        form.add(typeCombo);
        form.add(new JLabel("Owner NIC:"));
        form.add(nicField);
        form.add(new JLabel("New Number Plate:"));
        form.add(newPlateField);
        form.add(registerButton);
        form.add(updateButton);
        form.add(deleteButton);
        form.add(new JLabel());

        registerButton.addActionListener(e -> {
            String plate = plateField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();
            String nic = nicField.getText().trim();
            if (plate.isEmpty() || nic.isEmpty()) {
                showNotice("Enter a number plate and owner NIC.");
                return;
            }
            Customer ownerCustomer = customerManager.findCustomerByNIC(nic);
            if (ownerCustomer == null) {
                showNotice("NIC not registered. Vehicle cannot be registered.");
                return;
            }
            vehicleManager.addVehicle(new Vehicle(plate, type, nic));
            ownerCustomer.addNumberPlate(plate);
            saveAndRefresh();
        });

        updateButton.addActionListener(e -> {
            String plate = plateField.getText().trim();
            String newPlate = newPlateField.getText().trim();
            if (plate.isEmpty() || newPlate.isEmpty()) {
                showNotice("Enter both the current and new number plate.");
                return;
            }
            vehicleManager.updateVehicle(plate, newPlate);
            saveAndRefresh();
        });

        deleteButton.addActionListener(e -> {
            String plate = plateField.getText().trim();
            if (plate.isEmpty()) {
                showNotice("Enter a number plate to delete.");
                return;
            }
            vehicleManager.deleteVehicle(plate);
            saveAndRefresh();
        });

        vehiclesTableModel = new DefaultTableModel(new Object[] {"Plate", "Type", "Owner NIC"}, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable vehiclesTable = new JTable(vehiclesTableModel);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(vehiclesTable), BorderLayout.CENTER);
        return panel;
    }

    // ---------- Payments tab ----------

    private JPanel buildPaymentsTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel form = new JPanel(new java.awt.GridLayout(0, 2, 6, 6));
        JTextField recordIdField = new JTextField();
        JComboBox<String> methodCombo = new JComboBox<>(new String[] {"Cash", "Card"});
        JButton payButton = new JButton("Make Payment");
        totalLabel = new JLabel("Total Collected: Rs. 0.0");

        form.add(new JLabel("Record ID:"));
        form.add(recordIdField);
        form.add(new JLabel("Payment Method:"));
        form.add(methodCombo);
        form.add(payButton);
        form.add(totalLabel);

        payButton.addActionListener(e -> {
            try {
                int recordId = Integer.parseInt(recordIdField.getText().trim());
                ParkingRecord record = parkingManager.findParkingRecord(recordId);
                if (record == null) {
                    showNotice("Parking record not found.");
                    return;
                }
                String method = (String) methodCombo.getSelectedItem();
                paymentManager.makePayment(record, method);
                saveAndRefresh();
            } catch (NumberFormatException ex) {
                showNotice("Record ID must be a number.");
            } catch (PaymentFailedException ex) {
                showNotice("Payment failed: " + ex.getMessage());
            }
        });

        paymentsTableModel = new DefaultTableModel(
                new Object[] {"Payment ID", "Record ID", "Amount", "Date", "Status", "Method"}, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable paymentsTable = new JTable(paymentsTableModel);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(paymentsTable), BorderLayout.CENTER);
        return panel;
    }

    // ---------- Shared refresh / save / notice helpers ----------

    private void refreshAll() {
        slotsTableModel.setRowCount(0);
        for (ParkingSlot slot : parkingManager.getAllParkingSlots()) {
            slotsTableModel.addRow(new Object[] {
                    slot.getSlotNo(),
                    slot.getSlotType(),
                    slot.getStatus(),
                    slot.getNumberPlate() == null ? "" : slot.getNumberPlate(),
                    slot.getAssignedVehicle() == null ? "" : slot.getAssignedVehicle().getNIC()
            });
        }

        recordsTableModel.setRowCount(0);
        for (ParkingRecord record : parkingManager.getAllParkingRecords()) {
            recordsTableModel.addRow(new Object[] {
                    record.getRecordId(),
                    record.getNumberPlate(),
                    record.getParkingSlot().getSlotNo(),
                    record.getEntryTimeFormatted(),
                    record.getExitTimeFormatted(),
                    record.getParkingFee()
            });
        }

        customersTableModel.setRowCount(0);
        for (Customer customer : customerManager.getAllCustomers()) {
            customersTableModel.addRow(new Object[] {
                    customer.getCustomerId(),
                    customer.getNIC(),
                    customer.getName(),
                    customer.getPhoneNumber(),
                    customer.getRegistrationDate(),
                    String.join(", ", customer.getNumberPlates())
            });
        }

        vehiclesTableModel.setRowCount(0);
        for (Vehicle vehicle : vehicleManager.getAllVehicles()) {
            vehiclesTableModel.addRow(new Object[] {
                    vehicle.getNumberPlate(),
                    vehicle.getVehicleType(),
                    vehicle.getNIC()
            });
        }

        paymentsTableModel.setRowCount(0);
        for (Payment payment : paymentManager.getAllPayments()) {
            paymentsTableModel.addRow(new Object[] {
                    payment.getPaymentId(),
                    payment.getParkingRecordRef().getRecordId(),
                    payment.getAmount(),
                    payment.getPaymentDate(),
                    payment.getPaymentStatus(),
                    payment.getPaymentMethod()
            });
        }

        if (totalLabel != null) {
            totalLabel.setText("Total Collected: Rs. " + paymentManager.getTotalCollected());
        }
    }

    private void saveAndRefresh() {
        try {
            DataStore.saveAll(vehicleManager, customerManager, parkingManager, paymentManager);
        } catch (IOException e) {
            showNotice("Warning: could not save data (" + e.getMessage() + ")");
        }
        refreshAll();
    }

    private void showNotice(String message) {
        JOptionPane.showMessageDialog(frame, message, "Notice", JOptionPane.INFORMATION_MESSAGE);
    }
}