package com.nibm.parking.persistance;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.nibm.parking.model.Customer;
import com.nibm.parking.model.ParkingRecord;
import com.nibm.parking.model.ParkingSlot;
import com.nibm.parking.model.Payment;
import com.nibm.parking.model.Vehicle;
import com.nibm.parking.service.CustomerManager;
import com.nibm.parking.service.ParkingManager;
import com.nibm.parking.service.PaymentManager;
import com.nibm.parking.service.VehicleManager;
import com.nibm.parking.util.SimpleJson;

/**
 * A minimal JSON-file "database" for the parking system.
 * It is not a real database - it just persists everything to one JSON file
 * (parking_data.json in the working directory) so data survives a restart.
 */
public class DataStore {

    private static final String FILE_PATH = "parking_data.json";

    // ---------- SAVE ----------

    public static void saveAll(VehicleManager vehicleManager,
                                CustomerManager customerManager,
                                ParkingManager parkingManager,
                                PaymentManager paymentManager) throws IOException {

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("vehicles", vehiclesToJson(vehicleManager.getAllVehicles()));
        root.put("customers", customersToJson(customerManager.getAllCustomers()));
        root.put("parkingSlots", slotsToJson(parkingManager.getAllParkingSlots()));
        root.put("parkingRecords", recordsToJson(parkingManager.getAllParkingRecords()));
        root.put("payments", paymentsToJson(paymentManager.getAllPayments()));

        String json = SimpleJson.toJson(root);
        Files.writeString(Path.of(FILE_PATH), json, StandardCharsets.UTF_8);
    }

    private static List<Object> vehiclesToJson(List<Vehicle> vehicles) {
        List<Object> list = new ArrayList<>();
        for (Vehicle v : vehicles) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("numberPlate", v.getNumberPlate());
            m.put("vehicleType", v.getVehicleType());
            m.put("nic", v.getNIC());
            list.add(m);
        }
        return list;
    }

    private static List<Object> customersToJson(List<Customer> customers) {
        List<Object> list = new ArrayList<>();
        for (Customer c : customers) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("customerId", c.getCustomerId());
            m.put("nic", c.getNIC());
            m.put("name", c.getName());
            m.put("phoneNumber", c.getPhoneNumber());
            m.put("registrationDate", c.getRegistrationDate().toString());
            m.put("numberPlate", c.getNumberPlate());
            list.add(m);
        }
        return list;
    }

    private static List<Object> slotsToJson(List<ParkingSlot> slots) {
        List<Object> list = new ArrayList<>();
        for (ParkingSlot s : slots) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("slotNo", s.getSlotNo());
            m.put("slotType", s.getSlotType());
            m.put("status", s.getStatus());
            m.put("numberPlate", s.getNumberPlate());
            Vehicle assigned = s.getAssignedVehicle();
            m.put("vehicleType", assigned == null ? null : assigned.getVehicleType());
            m.put("nic", assigned == null ? null : assigned.getNIC());
            list.add(m);
        }
        return list;
    }

    private static List<Object> recordsToJson(List<ParkingRecord> records) {
        List<Object> list = new ArrayList<>();
        for (ParkingRecord r : records) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("recordId", r.getRecordId());
            m.put("numberPlate", r.getNumberPlate());
            m.put("slotNo", r.getParkingSlot().getSlotNo());
            m.put("entryTime", r.getEntryTime() == null ? null : r.getEntryTime().toString());
            m.put("exitTime", r.getExitTime() == null ? null : r.getExitTime().toString());
            m.put("parkingFee", r.getParkingFee());
            list.add(m);
        }
        return list;
    }

    private static List<Object> paymentsToJson(List<Payment> payments) {
        List<Object> list = new ArrayList<>();
        for (Payment p : payments) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("paymentId", p.getPaymentId());
            m.put("recordId", p.getParkingRecordRef().getRecordId());
            m.put("amount", p.getAmount());
            m.put("paymentDate", p.getPaymentDate());
            m.put("paymentStatus", p.getPaymentStatus());
            m.put("paymentMethod", p.getPaymentMethod());
            list.add(m);
        }
        return list;
    }

    // ---------- LOAD ----------

    /**
     * Loads saved data into the given managers.
     * Returns false (and leaves the managers untouched) if no saved file exists yet.
     */
    @SuppressWarnings("unchecked")
    public static boolean loadAll(VehicleManager vehicleManager,
                                   CustomerManager customerManager,
                                   ParkingManager parkingManager,
                                   PaymentManager paymentManager) throws IOException {

        Path path = Path.of(FILE_PATH);
        if (!Files.exists(path)) {
            return false;
        }

        String json = Files.readString(path, StandardCharsets.UTF_8);
        if (json.isBlank()) {
            return false;
        }

        Map<String, Object> root = (Map<String, Object>) SimpleJson.parse(json);

        // Vehicles (the standalone vehicle registry)
        ArrayList<Vehicle> vehicles = new ArrayList<>();
        for (Object o : (List<Object>) root.getOrDefault("vehicles", new ArrayList<>())) {
            Map<String, Object> m = (Map<String, Object>) o;
            vehicles.add(new Vehicle(
                    (String) m.get("numberPlate"),
                    (String) m.get("vehicleType"),
                    (String) m.get("nic")
            ));
        }
        vehicleManager.loadVehicles(vehicles);

        // Customers
        ArrayList<Customer> customers = new ArrayList<>();
        for (Object o : (List<Object>) root.getOrDefault("customers", new ArrayList<>())) {
            Map<String, Object> m = (Map<String, Object>) o;
            customers.add(new Customer(
                    toInt(m.get("customerId")),
                    (String) m.get("nic"),
                    (String) m.get("name"),
                    (String) m.get("phoneNumber"),
                    LocalDate.parse((String) m.get("registrationDate")),
                    (String) m.get("numberPlate")
            ));
        }
        customerManager.loadCustomers(customers);

        // Parking slots (must be rebuilt before records, since records reference them)
        ArrayList<ParkingSlot> slots = new ArrayList<>();
        Map<Integer, ParkingSlot> slotsByNo = new LinkedHashMap<>();
        for (Object o : (List<Object>) root.getOrDefault("parkingSlots", new ArrayList<>())) {
            Map<String, Object> m = (Map<String, Object>) o;
            ParkingSlot slot = new ParkingSlot(
                    toInt(m.get("slotNo")),
                    (String) m.get("slotType"),
                    (String) m.get("status"),
                    (String) m.get("numberPlate"),
                    (String) m.get("vehicleType"),
                    (String) m.get("nic")
            );
            slots.add(slot);
            slotsByNo.put(slot.getSlotNo(), slot);
        }

        // Parking records (relink each one to its rebuilt slot)
        ArrayList<ParkingRecord> records = new ArrayList<>();
        Map<Integer, ParkingRecord> recordsById = new LinkedHashMap<>();
        for (Object o : (List<Object>) root.getOrDefault("parkingRecords", new ArrayList<>())) {
            Map<String, Object> m = (Map<String, Object>) o;
            ParkingSlot slot = slotsByNo.get(toInt(m.get("slotNo")));
            String entryStr = (String) m.get("entryTime");
            String exitStr = (String) m.get("exitTime");
            ParkingRecord record = new ParkingRecord(
                    toInt(m.get("recordId")),
                    (String) m.get("numberPlate"),
                    slot,
                    entryStr == null ? null : LocalDateTime.parse(entryStr),
                    exitStr == null ? null : LocalDateTime.parse(exitStr),
                    toDouble(m.get("parkingFee"))
            );
            records.add(record);
            recordsById.put(record.getRecordId(), record);
        }
        parkingManager.loadData(slots, records);

        // Payments (relink each one to its rebuilt record)
        ArrayList<Payment> payments = new ArrayList<>();
        for (Object o : (List<Object>) root.getOrDefault("payments", new ArrayList<>())) {
            Map<String, Object> m = (Map<String, Object>) o;
            ParkingRecord record = recordsById.get(toInt(m.get("recordId")));
            if (record == null) {
                continue; // orphaned payment in the file - skip rather than crash
            }
            payments.add(new Payment(
                    toInt(m.get("paymentId")),
                    record,
                    (String) m.get("paymentMethod"),
                    toDouble(m.get("amount")),
                    (String) m.get("paymentStatus"),
                    (String) m.get("paymentDate")
            ));
        }
        paymentManager.loadPayments(payments);

        return true;
    }

    private static int toInt(Object o) {
        return ((Number) o).intValue();
    }

    private static double toDouble(Object o) {
        return ((Number) o).doubleValue();
    }
}