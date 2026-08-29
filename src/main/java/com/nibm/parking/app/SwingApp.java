package com.nibm.parking.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

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
 * Modern Swing front-end for the parking management system.
 * Clean card-based layout, consistent spacing, polished tables,
 * and a professional color palette across Parking, Customers,
 * Vehicles, and Payments tabs.
 */
public class SwingApp {

    // ── Color palette ──────────────────────────────────────────────────────
    private static final Color PRIMARY       = new Color(37, 99, 235);   // Blue-600
    private static final Color PRIMARY_HOVER = new Color(29, 78, 216);   // Blue-700
    private static final Color PRIMARY_LIGHT = new Color(219, 234, 254); // Blue-100
    private static final Color SUCCESS       = new Color(22, 163, 74);   // Green-600
    private static final Color SUCCESS_HOVER = new Color(21, 128, 61);   // Green-700
    private static final Color DANGER        = new Color(220, 38, 38);   // Red-600
    private static final Color DANGER_HOVER  = new Color(185, 28, 28);   // Red-700
    private static final Color WARNING       = new Color(217, 119, 6);   // Amber-600
    private static final Color BG            = new Color(248, 250, 252); // Slate-50
    private static final Color CARD_BG       = Color.WHITE;
    private static final Color BORDER        = new Color(226, 232, 240); // Slate-200
    private static final Color TEXT_PRIMARY  = new Color(15, 23, 42);    // Slate-900
    private static final Color TEXT_SECONDARY= new Color(100, 116, 139); // Slate-500
    private static final Color TABLE_ALT     = new Color(241, 245, 249); // Slate-100
    private static final Color HEADER_BG     = new Color(30, 41, 59);    // Slate-800

    // ── Typography ─────────────────────────────────────────────────────────
    private static final Font FONT_TITLE  = new Font("SansSerif", Font.BOLD, 18);
    private static final Font FONT_HEADING= new Font("SansSerif", Font.BOLD, 14);
    private static final Font FONT_BODY   = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FONT_SMALL  = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font FONT_BUTTON = new Font("SansSerif", Font.BOLD, 13);

    private final VehicleManager vehicleManager;
    private final CustomerManager customerManager;
    private final ParkingManager parkingManager;
    private final PaymentManager paymentManager;

    private JFrame frame;
    private JLabel totalLabel;
    private JLabel statusLabel;

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

    public static void launch(VehicleManager vehicleManager,
                              CustomerManager customerManager,
                              ParkingManager parkingManager,
                              PaymentManager paymentManager) throws Exception {
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException("No display available");
        }
        // Prefer Nimbus for a cleaner baseline, then apply our own styling
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // Fall back to system L&F
        }
        SwingApp app = new SwingApp(vehicleManager, customerManager, parkingManager, paymentManager);
        SwingUtilities.invokeAndWait(app::buildAndShow);
    }

    private void buildAndShow() {
        frame = new JFrame("Parking Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1000, 680));
        frame.setSize(1100, 720);
        frame.getContentPane().setBackground(BG);

        // Header bar
        JPanel header = createHeader();
        frame.add(header, BorderLayout.NORTH);

        // Main tabs
        JTabbedPane tabs = createStyledTabbedPane();
        tabs.addTab("  Parking  ", buildParkingTab());
        tabs.addTab("  Customers  ", buildCustomersTab());
        tabs.addTab("  Vehicles  ", buildVehiclesTab());
        tabs.addTab("  Payments  ", buildPaymentsTab());

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BG);
        center.setBorder(new EmptyBorder(12, 16, 8, 16));
        center.add(tabs, BorderLayout.CENTER);
        frame.add(center, BorderLayout.CENTER);

        // Status bar
        JPanel statusBar = createStatusBar();
        frame.add(statusBar, BorderLayout.SOUTH);

        refreshAll();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // ── Header ─────────────────────────────────────────────────────────────

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        header.setPreferredSize(new Dimension(0, 56));

        JLabel title = new JLabel("Parking Management System");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Manage slots · customers · vehicles · payments");
        subtitle.setFont(FONT_SMALL);
        subtitle.setForeground(new Color(148, 163, 184));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(2));
        titleBlock.add(subtitle);

        header.add(titleBlock, BorderLayout.WEST);
        return header;
    }

    // ── Status bar ─────────────────────────────────────────────────────────

    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                new EmptyBorder(6, 16, 6, 16)));

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(FONT_SMALL);
        statusLabel.setForeground(TEXT_SECONDARY);

        totalLabel = new JLabel("Total Collected: Rs. 0.00");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        totalLabel.setForeground(SUCCESS);

        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(totalLabel, BorderLayout.EAST);
        return bar;
    }

    // ── Styled tabbed pane ─────────────────────────────────────────────────

    private JTabbedPane createStyledTabbedPane() {
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(FONT_HEADING);
        tabs.setBackground(BG);
        tabs.setForeground(TEXT_PRIMARY);
        tabs.setBorder(BorderFactory.createEmptyBorder());
        tabs.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                highlight = PRIMARY;
                lightHighlight = PRIMARY;
                shadow = BORDER;
                darkShadow = BORDER;
                focus = PRIMARY;
            }

            @Override
            protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
                return 36;
            }

            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                              int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected) {
                    g2.setColor(CARD_BG);
                    g2.fillRoundRect(x, y, w, h + 4, 8, 8);
                    g2.setColor(PRIMARY);
                    g2.fillRect(x + 4, y + h - 2, w - 8, 3);
                } else {
                    g2.setColor(BG);
                    g2.fillRect(x, y, w, h);
                }
                g2.dispose();
            }

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                // no content border
            }
        });
        return tabs;
    }

    // ── Reusable UI helpers ────────────────────────────────────────────────

    private JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)));
        return card;
    }

    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_HEADING);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BODY);
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(FONT_BODY);
        field.setPreferredSize(new Dimension(180, 34));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(PRIMARY);

        // Simple placeholder simulation
        if (placeholder != null && !placeholder.isEmpty()) {
            field.setForeground(TEXT_SECONDARY);
            field.setText(placeholder);
            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (field.getText().equals(placeholder)) {
                        field.setText("");
                        field.setForeground(TEXT_PRIMARY);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (field.getText().isEmpty()) {
                        field.setForeground(TEXT_SECONDARY);
                        field.setText(placeholder);
                    }
                }
            });
        }
        return field;
    }

    private String getFieldValue(JTextField field, String placeholder) {
        String text = field.getText().trim();
        if (placeholder != null && text.equals(placeholder)) {
            return "";
        }
        return text;
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(FONT_BODY);
        combo.setPreferredSize(new Dimension(180, 34));
        combo.setBackground(Color.WHITE);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBorder(new LineBorder(BORDER, 1, true));
        return combo;
    }

    private JButton createPrimaryButton(String text) {
        return createStyledButton(text, PRIMARY, PRIMARY_HOVER, Color.WHITE);
    }

    private JButton createSuccessButton(String text) {
        return createStyledButton(text, SUCCESS, SUCCESS_HOVER, Color.WHITE);
    }

    private JButton createDangerButton(String text) {
        return createStyledButton(text, DANGER, DANGER_HOVER, Color.WHITE);
    }

    private JButton createSecondaryButton(String text) {
        return createStyledButton(text, new Color(241, 245, 249), new Color(226, 232, 240), TEXT_PRIMARY);
    }

    private JButton createStyledButton(String text, Color bg, Color hover, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(hover.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(hover);
                } else {
                    g2.setColor(bg);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BUTTON);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 24, 36));
        btn.setBorder(new EmptyBorder(6, 16, 6, 16));
        return btn;
    }

    private JScrollPane createStyledScrollPane(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(BORDER, 1, true));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBackground(Color.WHITE);
        return scroll;
    }

    private void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);
        table.setForeground(TEXT_PRIMARY);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(TEXT_SECONDARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 36));

        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ALT);
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setHorizontalAlignment(column == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);
                return c;
            }
        });
    }

    private DefaultTableModel createNonEditableModel(Object[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
    }

    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    // ── Parking tab ────────────────────────────────────────────────────────

    private JPanel buildParkingTab() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setOpaque(false);

        // Form card
        JPanel formCard = createCard();
        formCard.setLayout(new BorderLayout(0, 12));

        formCard.add(createSectionTitle("Park / Remove Vehicle"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JTextField plateField = createTextField("e.g. ABC-1234");
        JComboBox<String> typeCombo = createComboBox(new String[]{"Motorcycle", "Three wheeler", "Car", "Van"});
        JTextField nicField = createTextField("e.g. 199012345678");

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(createFieldLabel("Number Plate"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(plateField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        form.add(createFieldLabel("Vehicle Type"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        form.add(typeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(createFieldLabel("Customer NIC"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(nicField, gbc);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        JButton parkButton = createSuccessButton("Park Vehicle");
        JButton removeButton = createDangerButton("Remove Vehicle");
        btnRow.add(parkButton);
        btnRow.add(removeButton);

        gbc.gridx = 2; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weightx = 0;
        form.add(btnRow, gbc);

        formCard.add(form, BorderLayout.CENTER);

        parkButton.addActionListener(e -> {
            String plate = getFieldValue(plateField, "e.g. ABC-1234");
            String type = (String) typeCombo.getSelectedItem();
            String nic = getFieldValue(nicField, "e.g. 199012345678");
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
            setStatus("Parked " + plate + " successfully.");
            saveAndRefresh();
        });

        removeButton.addActionListener(e -> {
            String plate = getFieldValue(plateField, "e.g. ABC-1234");
            if (plate.isEmpty()) {
                showNotice("Enter a number plate to remove.");
                return;
            }
            parkingManager.removeVehicle(plate);
            setStatus("Removed vehicle " + plate + ".");
            saveAndRefresh();
        });

        // Tables
        slotsTableModel = createNonEditableModel(new Object[]{"Slot No", "Type", "Status", "Plate", "NIC"});
        JTable slotsTable = new JTable(slotsTableModel);
        styleTable(slotsTable);

        recordsTableModel = createNonEditableModel(
                new Object[]{"Record ID", "Plate", "Slot No", "Entry", "Exit", "Fee"});
        JTable recordsTable = new JTable(recordsTableModel);
        styleTable(recordsTable);

        JTabbedPane tables = createStyledTabbedPane();
        tables.addTab("  Parking Slots  ", createStyledScrollPane(slotsTable));
        tables.addTab("  Parking Records  ", createStyledScrollPane(recordsTable));

        root.add(formCard, BorderLayout.NORTH);
        root.add(tables, BorderLayout.CENTER);
        return root;
    }

    // ── Customers tab ──────────────────────────────────────────────────────

    private JPanel buildCustomersTab() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setOpaque(false);

        JPanel formCard = createCard();
        formCard.setLayout(new BorderLayout(0, 12));
        formCard.add(createSectionTitle("Manage Customers"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JTextField idField = createTextField("e.g. 1001");
        JTextField nicField = createTextField("e.g. 199012345678");
        JTextField nameField = createTextField("Full name");
        JTextField phoneField = createTextField("e.g. 0771234567");

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(createFieldLabel("Customer ID"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(idField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        form.add(createFieldLabel("NIC"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        form.add(nicField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(createFieldLabel("Name"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(nameField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        form.add(createFieldLabel("Phone"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        form.add(phoneField, gbc);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        JButton addButton = createSuccessButton("Add Customer");
        JButton updateButton = createPrimaryButton("Update Name / Phone");
        JButton deleteButton = createDangerButton("Delete");
        btnRow.add(addButton);
        btnRow.add(updateButton);
        btnRow.add(deleteButton);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; gbc.weightx = 0;
        gbc.insets = new Insets(10, 4, 4, 8);
        form.add(btnRow, gbc);

        formCard.add(form, BorderLayout.CENTER);

        addButton.addActionListener(e -> {
            try {
                String idText = getFieldValue(idField, "e.g. 1001");
                int id = Integer.parseInt(idText);
                String nic = getFieldValue(nicField, "e.g. 199012345678");
                String name = getFieldValue(nameField, "Full name");
                String phone = getFieldValue(phoneField, "e.g. 0771234567");
                if (nic.isEmpty() || name.isEmpty() || phone.isEmpty()) {
                    showNotice("Fill in NIC, Name, and Phone.");
                    return;
                }
                Customer customer = new Customer(id, nic, name, phone, LocalDate.now());
                customerManager.addCustomer(customer);
                setStatus("Customer " + name + " added.");
                saveAndRefresh();
            } catch (NumberFormatException ex) {
                showNotice("Customer ID must be a number.");
            }
        });

        updateButton.addActionListener(e -> {
            try {
                String idText = getFieldValue(idField, "e.g. 1001");
                int id = Integer.parseInt(idText);
                String name = getFieldValue(nameField, "Full name");
                String phone = getFieldValue(phoneField, "e.g. 0771234567");
                customerManager.updateCustomer(id, name, phone);
                setStatus("Customer #" + id + " updated.");
                saveAndRefresh();
            } catch (NumberFormatException ex) {
                showNotice("Customer ID must be a number.");
            }
        });

        deleteButton.addActionListener(e -> {
            try {
                String idText = getFieldValue(idField, "e.g. 1001");
                int id = Integer.parseInt(idText);
                customerManager.deleteCustomer(id);
                setStatus("Customer #" + id + " deleted.");
                saveAndRefresh();
            } catch (NumberFormatException ex) {
                showNotice("Customer ID must be a number.");
            }
        });

        customersTableModel = createNonEditableModel(
                new Object[]{"ID", "NIC", "Name", "Phone", "Registered", "Plates"});
        JTable customersTable = new JTable(customersTableModel);
        styleTable(customersTable);

        root.add(formCard, BorderLayout.NORTH);
        root.add(createStyledScrollPane(customersTable), BorderLayout.CENTER);
        return root;
    }

    // ── Vehicles tab ───────────────────────────────────────────────────────

    private JPanel buildVehiclesTab() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setOpaque(false);

        JPanel formCard = createCard();
        formCard.setLayout(new BorderLayout(0, 12));
        formCard.add(createSectionTitle("Manage Vehicles"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JTextField plateField = createTextField("e.g. ABC-1234");
        JComboBox<String> typeCombo = createComboBox(new String[]{"Motorcycle", "ThreeWheeler", "Car", "Van"});
        JTextField nicField = createTextField("Owner NIC");
        JTextField newPlateField = createTextField("New plate (for update)");

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(createFieldLabel("Number Plate"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(plateField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        form.add(createFieldLabel("Vehicle Type"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        form.add(typeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(createFieldLabel("Owner NIC"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(nicField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        form.add(createFieldLabel("New Number Plate"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        form.add(newPlateField, gbc);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        JButton registerButton = createSuccessButton("Register Vehicle");
        JButton updateButton = createPrimaryButton("Update Plate");
        JButton deleteButton = createDangerButton("Delete");
        btnRow.add(registerButton);
        btnRow.add(updateButton);
        btnRow.add(deleteButton);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 4, 4, 8);
        form.add(btnRow, gbc);

        formCard.add(form, BorderLayout.CENTER);

        registerButton.addActionListener(e -> {
            String plate = getFieldValue(plateField, "e.g. ABC-1234");
            String type = (String) typeCombo.getSelectedItem();
            String nic = getFieldValue(nicField, "Owner NIC");
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
            setStatus("Vehicle " + plate + " registered.");
            saveAndRefresh();
        });

        updateButton.addActionListener(e -> {
            String plate = getFieldValue(plateField, "e.g. ABC-1234");
            String newPlate = getFieldValue(newPlateField, "New plate (for update)");
            if (plate.isEmpty() || newPlate.isEmpty()) {
                showNotice("Enter both the current and new number plate.");
                return;
            }
            vehicleManager.updateVehicle(plate, newPlate);
            setStatus("Plate updated: " + plate + " → " + newPlate);
            saveAndRefresh();
        });

        deleteButton.addActionListener(e -> {
            String plate = getFieldValue(plateField, "e.g. ABC-1234");
            if (plate.isEmpty()) {
                showNotice("Enter a number plate to delete.");
                return;
            }
            vehicleManager.deleteVehicle(plate);
            setStatus("Vehicle " + plate + " deleted.");
            saveAndRefresh();
        });

        vehiclesTableModel = createNonEditableModel(new Object[]{"Plate", "Type", "Owner NIC"});
        JTable vehiclesTable = new JTable(vehiclesTableModel);
        styleTable(vehiclesTable);

        root.add(formCard, BorderLayout.NORTH);
        root.add(createStyledScrollPane(vehiclesTable), BorderLayout.CENTER);
        return root;
    }

    // ── Payments tab ───────────────────────────────────────────────────────

    private JPanel buildPaymentsTab() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setOpaque(false);

        JPanel formCard = createCard();
        formCard.setLayout(new BorderLayout(0, 12));
        formCard.add(createSectionTitle("Process Payment"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JTextField recordIdField = createTextField("e.g. 1");
        JComboBox<String> methodCombo = createComboBox(new String[]{"Cash", "Card"});

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(createFieldLabel("Record ID"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(recordIdField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        form.add(createFieldLabel("Payment Method"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        form.add(methodCombo, gbc);

        JButton payButton = createSuccessButton("Make Payment");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 4, 4, 8);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setOpaque(false);
        btnRow.add(payButton);
        form.add(btnRow, gbc);

        formCard.add(form, BorderLayout.CENTER);

        payButton.addActionListener(e -> {
            try {
                String idText = getFieldValue(recordIdField, "e.g. 1");
                int recordId = Integer.parseInt(idText);
                ParkingRecord record = parkingManager.findParkingRecord(recordId);
                if (record == null) {
                    showNotice("Parking record not found.");
                    return;
                }
                String method = (String) methodCombo.getSelectedItem();
                paymentManager.makePayment(record, method);
                setStatus("Payment recorded for record #" + recordId + " (" + method + ").");
                saveAndRefresh();
            } catch (NumberFormatException ex) {
                showNotice("Record ID must be a number.");
            } catch (PaymentFailedException ex) {
                showNotice("Payment failed: " + ex.getMessage());
            }
        });

        paymentsTableModel = createNonEditableModel(
                new Object[]{"Payment ID", "Record ID", "Amount", "Date", "Status", "Method"});
        JTable paymentsTable = new JTable(paymentsTableModel);
        styleTable(paymentsTable);

        root.add(formCard, BorderLayout.NORTH);
        root.add(createStyledScrollPane(paymentsTable), BorderLayout.CENTER);
        return root;
    }

    // ── Shared refresh / save / notice ─────────────────────────────────────

    private void refreshAll() {
        slotsTableModel.setRowCount(0);
        for (ParkingSlot slot : parkingManager.getAllParkingSlots()) {
            slotsTableModel.addRow(new Object[]{
                    slot.getSlotNo(),
                    slot.getSlotType(),
                    slot.getStatus(),
                    slot.getNumberPlate() == null ? "" : slot.getNumberPlate(),
                    slot.getAssignedVehicle() == null ? "" : slot.getAssignedVehicle().getNIC()
            });
        }

        recordsTableModel.setRowCount(0);
        for (ParkingRecord record : parkingManager.getAllParkingRecords()) {
            recordsTableModel.addRow(new Object[]{
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
            customersTableModel.addRow(new Object[]{
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
            vehiclesTableModel.addRow(new Object[]{
                    vehicle.getNumberPlate(),
                    vehicle.getVehicleType(),
                    vehicle.getNIC()
            });
        }

        paymentsTableModel.setRowCount(0);
        for (Payment payment : paymentManager.getAllPayments()) {
            paymentsTableModel.addRow(new Object[]{
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
        setStatus(message);
    }
}
