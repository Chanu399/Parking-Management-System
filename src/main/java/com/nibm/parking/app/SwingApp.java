package com.nibm.parking.app;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
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
 * Professional modern Swing front-end for the Parking Management System.
 */
public class SwingApp {

    // ── Color system ───────────────────────────────────────────────────────
    private static final Color PRIMARY         = new Color(59, 130, 246);
    private static final Color PRIMARY_DARK    = new Color(37, 99, 235);
    private static final Color PRIMARY_DEEPER  = new Color(29, 78, 216);
    private static final Color PRIMARY_LIGHT   = new Color(219, 234, 254);
    private static final Color PRIMARY_SOFT    = new Color(239, 246, 255);

    private static final Color SUCCESS         = new Color(16, 185, 129);
    private static final Color SUCCESS_DARK    = new Color(5, 150, 105);
    private static final Color SUCCESS_SOFT    = new Color(209, 250, 229);

    private static final Color DANGER          = new Color(239, 68, 68);
    private static final Color DANGER_DARK     = new Color(220, 38, 38);
    private static final Color DANGER_SOFT     = new Color(254, 226, 226);

    private static final Color WARNING         = new Color(245, 158, 11);
    private static final Color WARNING_SOFT    = new Color(254, 243, 199);

    private static final Color BG              = new Color(241, 245, 249);
    private static final Color SURFACE         = Color.WHITE;
    private static final Color SURFACE_ALT     = new Color(248, 250, 252);
    private static final Color BORDER          = new Color(226, 232, 240);
    private static final Color TEXT_PRIMARY    = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY  = new Color(100, 116, 139);
    private static final Color TEXT_MUTED      = new Color(148, 163, 184);
    private static final Color HEADER_BG       = new Color(15, 23, 42);
    private static final Color TABLE_ALT       = new Color(248, 250, 252);
    private static final Color TABLE_HEADER_BG = new Color(241, 245, 249);
    private static final Color SHADOW          = new Color(15, 23, 42, 18);

    // ── Typography ─────────────────────────────────────────────────────────
    private static final Font FONT_TITLE   = new Font("SansSerif", Font.BOLD, 18);
    private static final Font FONT_HEADING = new Font("SansSerif", Font.BOLD, 14);
    private static final Font FONT_BODY    = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FONT_SMALL   = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font FONT_BUTTON  = new Font("SansSerif", Font.BOLD, 13);

    private final VehicleManager vehicleManager;
    private final CustomerManager customerManager;
    private final ParkingManager parkingManager;
    private final PaymentManager paymentManager;

    private JFrame frame;
    private JLabel totalLabel;
    private JLabel statusLabel;
    private JLabel statusDot;

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
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }
        SwingApp app = new SwingApp(vehicleManager, customerManager, parkingManager, paymentManager);
        SwingUtilities.invokeAndWait(app::buildAndShow);
    }

    private void buildAndShow() {
        frame = new JFrame("Parking Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1080, 700));
        frame.setSize(1180, 760);
        frame.getContentPane().setBackground(BG);

        frame.add(createHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = createStyledTabbedPane();
        tabs.addTab("  Parking  ", buildParkingTab());
        tabs.addTab("  Customers  ", buildCustomersTab());
        tabs.addTab("  Vehicles  ", buildVehiclesTab());
        tabs.addTab("  Payments  ", buildPaymentsTab());

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BG);
        center.setBorder(new EmptyBorder(14, 18, 10, 18));
        center.add(tabs, BorderLayout.CENTER);
        frame.add(center, BorderLayout.CENTER);

        frame.add(createStatusBar(), BorderLayout.SOUTH);

        refreshAll();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // ── Header (stable, no custom paint glitches) ──────────────────────────

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, PRIMARY),
                new EmptyBorder(14, 22, 14, 22)));
        header.setPreferredSize(new Dimension(0, 64));

        // Left: logo + title
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);

        JLabel logo = new JLabel("  P  ");
        logo.setFont(new Font("SansSerif", Font.BOLD, 16));
        logo.setForeground(Color.WHITE);
        logo.setOpaque(true);
        logo.setBackground(PRIMARY);
        logo.setBorder(new EmptyBorder(6, 10, 6, 10));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setBorder(new EmptyBorder(0, 14, 0, 0));

        JLabel title = new JLabel("Parking Management System");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Slots  ·  Customers  ·  Vehicles  ·  Payments");
        subtitle.setFont(FONT_SMALL);
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(2));
        titleBlock.add(subtitle);

        left.add(logo);
        left.add(titleBlock);

        // Right: online badge
        JLabel badge = new JLabel("  ●  System Online  ");
        badge.setFont(new Font("SansSerif", Font.BOLD, 11));
        badge.setForeground(new Color(203, 213, 225));
        badge.setOpaque(true);
        badge.setBackground(new Color(30, 41, 59));
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
                new EmptyBorder(6, 10, 6, 10)));

        header.add(left, BorderLayout.WEST);
        header.add(badge, BorderLayout.EAST);
        return header;
    }

    // ── Status bar ─────────────────────────────────────────────────────────

    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(SURFACE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                new EmptyBorder(8, 18, 8, 18)));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        statusDot = new JLabel("●");
        statusDot.setFont(new Font("SansSerif", Font.PLAIN, 10));
        statusDot.setForeground(SUCCESS);

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(FONT_SMALL);
        statusLabel.setForeground(TEXT_SECONDARY);

        left.add(statusDot);
        left.add(statusLabel);

        totalLabel = new JLabel("  Total Collected: Rs. 0.00  ");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        totalLabel.setForeground(SUCCESS_DARK);
        totalLabel.setOpaque(true);
        totalLabel.setBackground(SUCCESS_SOFT);
        totalLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SUCCESS_SOFT, 1),
                new EmptyBorder(4, 10, 4, 10)));

        bar.add(left, BorderLayout.WEST);
        bar.add(totalLabel, BorderLayout.EAST);
        return bar;
    }

    // ── Tabbed pane – active tab clearly visible ───────────────────────────

    private JTabbedPane createStyledTabbedPane() {
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(FONT_HEADING);
        tabs.setBackground(BG);
        tabs.setForeground(TEXT_SECONDARY);
        tabs.setBorder(BorderFactory.createEmptyBorder());

        // Use a simple, reliable tab UI so the active tab is always readable
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
                return 42;
            }

            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                              int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected) {
                    // White rounded pill + blue bottom bar
                    g2.setColor(SURFACE);
                    g2.fillRoundRect(x + 2, y + 6, w - 4, h - 4, 10, 10);
                    g2.setColor(PRIMARY);
                    g2.fillRoundRect(x + 8, y + h - 5, w - 16, 4, 3, 3);
                } else {
                    g2.setColor(BG);
                    g2.fillRect(x, y, w, h);
                }
                g2.dispose();
            }

            @Override
            protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics,
                                     int tabIndex, String title, Rectangle textRect, boolean isSelected) {
                g.setFont(font);
                if (isSelected) {
                    g.setColor(PRIMARY_DARK);
                } else {
                    g.setColor(TEXT_SECONDARY);
                }
                int strWidth = metrics.stringWidth(title);
                int x = textRect.x + (textRect.width - strWidth) / 2;
                int y = textRect.y + metrics.getAscent() + 2;
                g.drawString(title, x, y);
            }

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                // no border
            }

            @Override
            protected void paintFocusIndicator(Graphics g, int tabPlacement,
                                               Rectangle[] rects, int tabIndex,
                                               Rectangle iconRect, Rectangle textRect,
                                               boolean isSelected) {
                // suppress focus rectangle
            }
        });
        return tabs;
    }

    // ── Shared helpers ─────────────────────────────────────────────────────

    private JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SHADOW);
                g2.fillRoundRect(2, 3, getWidth() - 4, getHeight() - 4, 14, 14);
                g2.setColor(SURFACE);
                g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 14, 14);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 14, 14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 18, 16, 18));
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
        label.setFont(FONT_SMALL);
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(FONT_BODY);
        field.setPreferredSize(new Dimension(190, 36));
        field.setBackground(SURFACE_ALT);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER, 10, 1),
                new EmptyBorder(6, 12, 6, 12)));

        if (placeholder != null && !placeholder.isEmpty()) {
            field.setForeground(TEXT_MUTED);
            field.setText(placeholder);
            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (field.getText().equals(placeholder)) {
                        field.setText("");
                        field.setForeground(TEXT_PRIMARY);
                    }
                    field.setBorder(BorderFactory.createCompoundBorder(
                            new RoundedBorder(PRIMARY, 10, 2),
                            new EmptyBorder(5, 11, 5, 11)));
                    field.setBackground(SURFACE);
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (field.getText().isEmpty()) {
                        field.setForeground(TEXT_MUTED);
                        field.setText(placeholder);
                    }
                    field.setBorder(BorderFactory.createCompoundBorder(
                            new RoundedBorder(BORDER, 10, 1),
                            new EmptyBorder(6, 12, 6, 12)));
                    field.setBackground(SURFACE_ALT);
                }
            });
        }
        return field;
    }

    private String getFieldValue(JTextField field, String placeholder) {
        String text = field.getText().trim();
        if (placeholder != null && text.equals(placeholder)) return "";
        return text;
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(FONT_BODY);
        combo.setPreferredSize(new Dimension(190, 36));
        combo.setBackground(SURFACE_ALT);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBorder(new RoundedBorder(BORDER, 10, 1));
        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton("▾");
                btn.setBorder(BorderFactory.createEmptyBorder());
                btn.setContentAreaFilled(false);
                btn.setFocusPainted(false);
                btn.setForeground(TEXT_SECONDARY);
                return btn;
            }
        });
        return combo;
    }

    private JButton createPrimaryButton(String text) {
        return createStyledButton(text, PRIMARY, PRIMARY_DARK, PRIMARY_DEEPER, Color.WHITE);
    }

    private JButton createSuccessButton(String text) {
        return createStyledButton(text, SUCCESS, SUCCESS_DARK, SUCCESS_DARK.darker(), Color.WHITE);
    }

    private JButton createDangerButton(String text) {
        return createStyledButton(text, DANGER, DANGER_DARK, DANGER_DARK.darker(), Color.WHITE);
    }

    private JButton createStyledButton(String text, Color bg, Color hover, Color pressed, Color fg) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            private boolean pressedState = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                    @Override public void mousePressed(MouseEvent e) { pressedState = true; repaint(); }
                    @Override public void mouseReleased(MouseEvent e){ pressedState = false; repaint(); }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill = pressedState ? pressed : (hovered ? hover : bg);
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
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
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 16, 36));
        return btn;
    }

    private JScrollPane createStyledScrollPane(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new RoundedBorder(BORDER, 10, 1));
        scroll.getViewport().setBackground(SURFACE);
        scroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scroll.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        return scroll;
    }

    private void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setBackground(SURFACE);
        table.setForeground(TEXT_PRIMARY);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TEXT_SECONDARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 38));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                c.setBackground(TABLE_HEADER_BG);
                c.setForeground(TEXT_SECONDARY);
                c.setFont(new Font("SansSerif", Font.BOLD, 12));
                c.setBorder(new EmptyBorder(0, 12, 0, 12));
                c.setHorizontalAlignment(SwingConstants.LEFT);
                return c;
            }
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? SURFACE : TABLE_ALT);
                } else {
                    c.setBackground(PRIMARY_LIGHT);
                }
                c.setBorder(new EmptyBorder(0, 12, 0, 12));
                c.setHorizontalAlignment(SwingConstants.LEFT);

                String colName = tbl.getColumnName(column).toLowerCase();
                if (value != null && colName.contains("status")) {
                    String v = value.toString().toLowerCase();
                    if (v.contains("available") || v.contains("paid") || v.contains("success")) {
                        c.setForeground(SUCCESS_DARK);
                        c.setFont(new Font("SansSerif", Font.BOLD, 12));
                    } else if (v.contains("occupied") || v.contains("pending") || v.contains("fail")) {
                        c.setForeground(DANGER_DARK);
                        c.setFont(new Font("SansSerif", Font.BOLD, 12));
                    } else {
                        c.setForeground(TEXT_PRIMARY);
                    }
                } else {
                    c.setForeground(TEXT_PRIMARY);
                }
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

    private void setStatus(String message, boolean success) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusDot.setForeground(success ? SUCCESS : WARNING);
        }
    }

    // ── Popup helpers (every action gets a dialog) ─────────────────────────

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(frame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
        setStatus(message, true);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
        setStatus(message, false);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(frame, message, "Notice", JOptionPane.WARNING_MESSAGE);
        setStatus(message, false);
    }

    // ── Rounded border ─────────────────────────────────────────────────────

    private static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        private final int thickness;

        RoundedBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x + thickness / 2, y + thickness / 2,
                    width - thickness, height - thickness, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness + 2, thickness + 2, thickness + 2, thickness + 2);
        }
    }

    // ── Modern scrollbar ───────────────────────────────────────────────────

    private static class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(203, 213, 225);
            trackColor = SURFACE_ALT;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(0, 0));
            btn.setMinimumSize(new Dimension(0, 0));
            btn.setMaximumSize(new Dimension(0, 0));
            return btn;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, java.awt.Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x + 3, thumbBounds.y + 2,
                    thumbBounds.width - 6, thumbBounds.height - 4, 8, 8);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, java.awt.Rectangle trackBounds) {
        }
    }

    // ── Parking tab ────────────────────────────────────────────────────────

    private JPanel buildParkingTab() {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setOpaque(false);

        JPanel formCard = createCard();
        formCard.setLayout(new BorderLayout(0, 12));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        titleRow.add(createSectionTitle("Park / Remove Vehicle"), BorderLayout.WEST);
        JLabel hint = new JLabel("Vehicle must be registered & NIC must exist");
        hint.setFont(FONT_SMALL);
        hint.setForeground(TEXT_MUTED);
        titleRow.add(hint, BorderLayout.EAST);
        formCard.add(titleRow, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 10);
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

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);
        JButton parkButton = createSuccessButton("  Park Vehicle  ");
        JButton removeButton = createDangerButton("  Remove Vehicle  ");
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
                showWarning("Please enter a number plate and NIC.");
                return;
            }
            Vehicle matchedVehicle = vehicleManager.findVehicleByNumberPlate(plate);
            if (matchedVehicle == null) {
                showError("Vehicle not registered. Vehicle cannot be parked.");
                return;
            }
            Customer matchedCustomer = customerManager.findCustomerByNIC(nic);
            if (matchedCustomer == null) {
                showError("NIC not registered. Vehicle cannot be parked.");
                return;
            }
            try {
                parkingManager.parkVehicle(plate, type, matchedCustomer.getNIC());
                // Try to find which slot was assigned
                String slotInfo = "";
                for (ParkingSlot slot : parkingManager.getAllParkingSlots()) {
                    if (plate.equals(slot.getNumberPlate())) {
                        slotInfo = " at Slot " + slot.getSlotNo() + " (" + slot.getSlotType() + ")";
                        break;
                    }
                }
                showSuccess("Vehicle " + plate + " parked successfully" + slotInfo + ".");
                saveAndRefresh();
            } catch (Exception ex) {
                showError("Could not park vehicle: " + ex.getMessage());
            }
        });

        removeButton.addActionListener(e -> {
            String plate = getFieldValue(plateField, "e.g. ABC-1234");
            if (plate.isEmpty()) {
                showWarning("Please enter a number plate to remove.");
                return;
            }
            try {
                parkingManager.removeVehicle(plate);
                showSuccess("Vehicle " + plate + " has been removed from parking.");
                saveAndRefresh();
            } catch (Exception ex) {
                showError("Could not remove vehicle: " + ex.getMessage());
            }
        });

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
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setOpaque(false);

        JPanel formCard = createCard();
        formCard.setLayout(new BorderLayout(0, 12));
        formCard.add(createSectionTitle("Manage Customers"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 10);
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

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);
        JButton addButton = createSuccessButton("  Add Customer  ");
        JButton updateButton = createPrimaryButton("  Update Name / Phone  ");
        JButton deleteButton = createDangerButton("  Delete  ");
        btnRow.add(addButton);
        btnRow.add(updateButton);
        btnRow.add(deleteButton);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        gbc.insets = new Insets(12, 4, 4, 10);
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
                    showWarning("Please fill in NIC, Name, and Phone.");
                    return;
                }
                Customer customer = new Customer(id, nic, name, phone, LocalDate.now());
                customerManager.addCustomer(customer);
                showSuccess("Customer \"" + name + "\" (ID: " + id + ") added successfully.");
                saveAndRefresh();
            } catch (NumberFormatException ex) {
                showError("Customer ID must be a number.");
            } catch (Exception ex) {
                showError("Could not add customer: " + ex.getMessage());
            }
        });

        updateButton.addActionListener(e -> {
            try {
                String idText = getFieldValue(idField, "e.g. 1001");
                int id = Integer.parseInt(idText);
                String name = getFieldValue(nameField, "Full name");
                String phone = getFieldValue(phoneField, "e.g. 0771234567");
                customerManager.updateCustomer(id, name, phone);
                showSuccess("Customer #" + id + " updated successfully.");
                saveAndRefresh();
            } catch (NumberFormatException ex) {
                showError("Customer ID must be a number.");
            } catch (Exception ex) {
                showError("Could not update customer: " + ex.getMessage());
            }
        });

        deleteButton.addActionListener(e -> {
            try {
                String idText = getFieldValue(idField, "e.g. 1001");
                int id = Integer.parseInt(idText);
                customerManager.deleteCustomer(id);
                showSuccess("Customer #" + id + " deleted successfully.");
                saveAndRefresh();
            } catch (NumberFormatException ex) {
                showError("Customer ID must be a number.");
            } catch (Exception ex) {
                showError("Could not delete customer: " + ex.getMessage());
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
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setOpaque(false);

        JPanel formCard = createCard();
        formCard.setLayout(new BorderLayout(0, 12));
        formCard.add(createSectionTitle("Manage Vehicles"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 10);
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

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);
        JButton registerButton = createSuccessButton("  Register Vehicle  ");
        JButton updateButton = createPrimaryButton("  Update Plate  ");
        JButton deleteButton = createDangerButton("  Delete  ");
        btnRow.add(registerButton);
        btnRow.add(updateButton);
        btnRow.add(deleteButton);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        gbc.insets = new Insets(12, 4, 4, 10);
        form.add(btnRow, gbc);

        formCard.add(form, BorderLayout.CENTER);

        registerButton.addActionListener(e -> {
            String plate = getFieldValue(plateField, "e.g. ABC-1234");
            String type = (String) typeCombo.getSelectedItem();
            String nic = getFieldValue(nicField, "Owner NIC");
            if (plate.isEmpty() || nic.isEmpty()) {
                showWarning("Please enter a number plate and owner NIC.");
                return;
            }
            Customer ownerCustomer = customerManager.findCustomerByNIC(nic);
            if (ownerCustomer == null) {
                showError("NIC not registered. Vehicle cannot be registered.");
                return;
            }
            try {
                vehicleManager.addVehicle(new Vehicle(plate, type, nic));
                ownerCustomer.addNumberPlate(plate);
                showSuccess("Vehicle " + plate + " (" + type + ") registered successfully for NIC " + nic + ".");
                saveAndRefresh();
            } catch (Exception ex) {
                showError("Could not register vehicle: " + ex.getMessage());
            }
        });

        updateButton.addActionListener(e -> {
            String plate = getFieldValue(plateField, "e.g. ABC-1234");
            String newPlate = getFieldValue(newPlateField, "New plate (for update)");
            if (plate.isEmpty() || newPlate.isEmpty()) {
                showWarning("Please enter both the current and new number plate.");
                return;
            }
            try {
                vehicleManager.updateVehicle(plate, newPlate);
                showSuccess("Number plate updated: " + plate + " → " + newPlate + ".");
                saveAndRefresh();
            } catch (Exception ex) {
                showError("Could not update plate: " + ex.getMessage());
            }
        });

        deleteButton.addActionListener(e -> {
            String plate = getFieldValue(plateField, "e.g. ABC-1234");
            if (plate.isEmpty()) {
                showWarning("Please enter a number plate to delete.");
                return;
            }
            try {
                vehicleManager.deleteVehicle(plate);
                showSuccess("Vehicle " + plate + " deleted successfully.");
                saveAndRefresh();
            } catch (Exception ex) {
                showError("Could not delete vehicle: " + ex.getMessage());
            }
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
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setOpaque(false);

        JPanel formCard = createCard();
        formCard.setLayout(new BorderLayout(0, 12));
        formCard.add(createSectionTitle("Process Payment"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 10);
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

        JButton payButton = createSuccessButton("  Make Payment  ");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 4;
        gbc.insets = new Insets(12, 4, 4, 10);
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
                    showError("Parking record not found for ID " + recordId + ".");
                    return;
                }
                String method = (String) methodCombo.getSelectedItem();
                paymentManager.makePayment(record, method);
                showSuccess("Payment of Rs. " + record.getParkingFee()
                        + " recorded successfully for record #" + recordId
                        + " via " + method + ".");
                saveAndRefresh();
            } catch (NumberFormatException ex) {
                showError("Record ID must be a number.");
            } catch (PaymentFailedException ex) {
                showError("Payment failed: " + ex.getMessage());
            } catch (Exception ex) {
                showError("Could not process payment: " + ex.getMessage());
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

    // ── Shared refresh / save ──────────────────────────────────────────────

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
            totalLabel.setText("  Total Collected: Rs. " + paymentManager.getTotalCollected() + "  ");
        }
    }

    private void saveAndRefresh() {
        try {
            DataStore.saveAll(vehicleManager, customerManager, parkingManager, paymentManager);
        } catch (IOException e) {
            showWarning("Data saved in memory, but could not write to disk: " + e.getMessage());
        }
        refreshAll();
    }
}
