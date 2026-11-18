import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ZakatCalculatorGUI extends JFrame {
    private ZakatManager manager;
    private JPanel displayPanel;
    private DecimalFormat df = new DecimalFormat("#,##0.00");

    // Dynamic cross-platform font resolution for Windows and Linux
    public static final String SANS_FONT_FAMILY = resolveSansFont();
    public static final String MONO_FONT_FAMILY = resolveMonoFont();

    public static Font getSansFont(int style, int size) {
        return new Font(SANS_FONT_FAMILY, style, size);
    }

    public static Font getMonoFont(int style, int size) {
        return new Font(MONO_FONT_FAMILY, style, size);
    }

    private static boolean isBilingual(String fontName) {
        try {
            Font f = new Font(fontName, Font.PLAIN, 16);
            return f.canDisplay('A') && f.canDisplay('a') && f.canDisplay('1')
                    && f.canDisplay('ก') && f.canDisplay('้');
        } catch (Exception e) {
            return false;
        }
    }

    private static String resolveSansFont() {
        String[] candidateFonts = {
                // Windows preferred fonts with both English and Thai support
                "Segoe UI", "Tahoma", "Leelawadee UI", "Microsoft Sans Serif",
                // Linux preferred fonts with both English and Thai support
                "Sarabun", "TH Sarabun New", "IBM Plex Sans Thai", "Kanit", "Prompt",
                "Loma", "Waree", "Garuda"
        };
        try {
            Set<String> avail = new HashSet<>(
                    Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
            for (String candidate : candidateFonts) {
                if (avail.contains(candidate) && isBilingual(candidate)) {
                    return candidate;
                }
            }
        } catch (Exception ignored) {
        }
        return Font.SANS_SERIF;
    }

    private static String resolveMonoFont() {
        String[] candidateFonts = {
                "DejaVu Sans Mono", "Consolas", "Courier New"
        };
        try {
            Set<String> avail = new HashSet<>(
                    Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
            for (String candidate : candidateFonts) {
                if (avail.contains(candidate) && isBilingual(candidate)) {
                    return candidate;
                }
            }
            if (isBilingual(Font.MONOSPACED)) {
                return Font.MONOSPACED;
            }
        } catch (Exception ignored) {
        }
        // Fallback to verified bilingual Sans font if no bilingual monospace font
        // exists (e.g. Windows)
        return SANS_FONT_FAMILY;
    }

    public static void applyGlobalFonts() {
        Font defaultFont = getSansFont(Font.PLAIN, 16);
        Font boldFont = getSansFont(Font.BOLD, 16);

        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource || key.toString().endsWith(".font")) {
                UIManager.put(key, defaultFont);
            }
        }

        UIManager.put("Label.font", defaultFont);
        UIManager.put("Button.font", boldFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("ComboBox.font", defaultFont);
        UIManager.put("OptionPane.messageFont", defaultFont);
        UIManager.put("OptionPane.buttonFont", boldFont);
    }

    // จัดเรียงหน้าจอ
    public ZakatCalculatorGUI() {
        applyGlobalFonts();

        manager = new ZakatManager(50);

        setTitle("โปรแกรมคำนวณซะกาตทรัพย์สิน (Zakat Calculator)");
        setSize(920, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout
        setLayout(new BorderLayout(10, 10));

        // Top Panel for Title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(41, 128, 185));

        JLabel titleLabel = new JLabel("โปรแกรมคำนวณซะกาตทรัพย์สิน (Zakat Calculator)");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(getSansFont(Font.BOLD, 26));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        RoundExitButton btnExit = new RoundExitButton("X");
        btnExit.setPreferredSize(new Dimension(45, 45));
        btnExit.setFont(getSansFont(Font.BOLD, 18));
        btnExit.addActionListener(e -> {
            Object[] options = { "ใช่ (Yes)", "ไม่ (No)" };
            int result = JOptionPane.showOptionDialog(this, "คุณต้องการออกจากโปรแกรมหรือไม่?", "ยืนยันการออก",
                    JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (result == 0) {
                System.exit(0);
            }
        });

        JPanel eastPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        eastPanel.setOpaque(false);
        eastPanel.add(btnExit);
        titlePanel.add(eastPanel, BorderLayout.EAST);

        add(titlePanel, BorderLayout.NORTH);

        // Center Panel for Display
        displayPanel = new JPanel();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
        displayPanel.setBackground(Color.WHITE);

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(Color.WHITE);
        wrapperPanel.add(displayPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Left Panel for Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(8, 1, 10, 10)); // 8 rows
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.setPreferredSize(new Dimension(280, 0));

        JButton btnAddCash = createStyledButton("เงินสด");
        JButton btnAddGold = createStyledButton("ทองคำ");
        JButton btnAddTrade = createStyledButton("ทรัพย์สินการค้า");
        JButton btnAddAgri = createStyledButton("ผลผลิตการเกษตร");
        JButton btnShowAll = createStyledButton("แสดงรายการทั้งหมด");
        JButton btnCalculate = createStyledButton("คำนวณยอดซะกาตทั้งหมด");
        JButton btnClearAll = createStyledButton("Clear");

        buttonPanel.add(btnAddCash);
        buttonPanel.add(btnAddGold);
        buttonPanel.add(btnAddTrade);
        buttonPanel.add(btnAddAgri);
        buttonPanel.add(btnShowAll);
        buttonPanel.add(btnCalculate);
        buttonPanel.add(btnClearAll);

        add(buttonPanel, BorderLayout.WEST);

        // Initial Message
        displayPanel.removeAll();
        JTextArea initialArea = new JTextArea(
                "ยินดีต้อนรับสู่โปรแกรมคำนวณซะกาตทรัพย์สิน\nกรุณาเลือกเมนูทางด้านซ้ายเพื่อเริ่มต้นเพิ่มรายการทรัพย์สิน");
        initialArea.setFont(getSansFont(Font.PLAIN, 18));
        initialArea.setEditable(false);
        initialArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        displayPanel.add(initialArea);

        // Event Listeners
        btnAddCash.addActionListener(e -> addCashDialog());
        btnAddGold.addActionListener(e -> addGoldDialog());
        btnAddTrade.addActionListener(e -> addTradeDialog());
        btnAddAgri.addActionListener(e -> addAgricultureDialog());
        btnShowAll.addActionListener(e -> showAllAssets());
        btnCalculate.addActionListener(e -> calculateAndShowTotal());
        btnClearAll.addActionListener(e -> clearAllAssets());
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(getSansFont(Font.BOLD, 16));
        button.setFocusPainted(false);
        return button;
    }

    private void clearAllAssets() {
        if (manager.getCount() == 0) {
            JOptionPane.showMessageDialog(this, "ไม่มีรายการทรัพย์สินให้ลบ", "ข้อความ", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        Object[] options = { "ใช่ (Yes)", "ไม่ (No)" };
        int result = JOptionPane.showOptionDialog(this, "คุณแน่ใจหรือไม่ว่าต้องการลบรายการทั้งหมด?", "ยืนยันการลบ",
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (result == 0) {
            manager.clearAll();
            JOptionPane.showMessageDialog(this, "ล้างรายการทั้งหมดเรียบร้อยแล้ว", "สำเร็จ", JOptionPane.PLAIN_MESSAGE);
            showAllAssets();
        }
    }

    private void addCashDialog() {
        JTextField nameField = new JTextField(15);
        JTextField valueField = new JTextField(15);

        JPanel myPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        myPanel.add(new JLabel("ชื่อรายการ:"));
        myPanel.add(nameField);
        myPanel.add(new JLabel("มูลค่าเงินสด/เงินฝาก (บาท):"));
        myPanel.add(valueField);

        Object[] options = { "ตกลง (OK)", "ยกเลิก (Cancel)" };
        while (true) {
            int result = JOptionPane.showOptionDialog(this, myPanel,
                    "เพิ่มเงินสด / เงินฝากธนาคาร", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                    options, options[0]);
            if (result == 0) {
                try {
                    String name = nameField.getText();
                    if (name.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "กรุณากรอกชื่อรายการ", "ข้อผิดพลาด",
                                JOptionPane.PLAIN_MESSAGE);
                        continue;
                    }
                    double value = Double.parseDouble(valueField.getText());
                    manager.addAsset(new CashAsset(name, value));
                    JOptionPane.showMessageDialog(this, "เพิ่มรายการเรียบร้อยแล้ว", "สำเร็จ",
                            JOptionPane.PLAIN_MESSAGE);
                    showAllAssets();
                    break;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "กรุณากรอกตัวเลขที่ถูกต้อง", "ข้อผิดพลาด",
                            JOptionPane.PLAIN_MESSAGE);
                }
            } else {
                break;
            }
        }
    }

    private void addGoldDialog() {
        JTextField nameField = new JTextField(15);
        JTextField weightField = new JTextField(15);
        JComboBox<String> typeBox = new JComboBox<>(new String[] { "ทองแท่ง", "ทองรูปพรรณ" });

        JPanel myPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        myPanel.add(new JLabel("ชื่อรายการ:"));
        myPanel.add(nameField);
        myPanel.add(new JLabel("ประเภททอง:"));
        myPanel.add(typeBox);
        myPanel.add(new JLabel("น้ำหนักทองคำ (บาททอง):"));
        myPanel.add(weightField);

        Object[] options = { "ตกลง (OK)", "ยกเลิก (Cancel)" };
        while (true) {
            int result = JOptionPane.showOptionDialog(this, myPanel,
                    "เพิ่มทองคำ / เงิน", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options,
                    options[0]);
            if (result == 0) {
                try {
                    String name = nameField.getText();
                    if (name.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "กรุณากรอกชื่อรายการ", "ข้อผิดพลาด",
                                JOptionPane.PLAIN_MESSAGE);
                        continue;
                    }
                    double weight = Double.parseDouble(weightField.getText());
                    String goldType = (String) typeBox.getSelectedItem();
                    manager.addAsset(new GoldAsset(name, weight, goldType));
                    JOptionPane.showMessageDialog(this, "เพิ่มรายการเรียบร้อยแล้ว", "สำเร็จ",
                            JOptionPane.PLAIN_MESSAGE);
                    showAllAssets();
                    break;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "กรุณากรอกตัวเลขที่ถูกต้อง", "ข้อผิดพลาด",
                            JOptionPane.PLAIN_MESSAGE);
                }
            } else {
                break;
            }
        }
    }

    private void addTradeDialog() {
        JTextField nameField = new JTextField(15);
        JTextField valueField = new JTextField(15);
        JTextField debtField = new JTextField(15);
        debtField.setText("0"); // Default value

        JPanel myPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        myPanel.add(new JLabel("ชื่อรายการ:"));
        myPanel.add(nameField);
        myPanel.add(new JLabel("มูลค่ารวมของสินค้า (บาท):"));
        myPanel.add(valueField);
        myPanel.add(new JLabel("หนี้สินที่เกี่ยวข้อง (บาท):"));
        myPanel.add(debtField);

        Object[] options = { "ตกลง (OK)", "ยกเลิก (Cancel)" };
        while (true) {
            int result = JOptionPane.showOptionDialog(this, myPanel,
                    "เพิ่มทรัพย์สินทางการค้า", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options,
                    options[0]);
            if (result == 0) {
                try {
                    String name = nameField.getText();
                    if (name.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "กรุณากรอกชื่อรายการ", "ข้อผิดพลาด",
                                JOptionPane.PLAIN_MESSAGE);
                        continue;
                    }
                    double value = Double.parseDouble(valueField.getText());
                    double debt = Double.parseDouble(debtField.getText());
                    manager.addAsset(new TradeAsset(name, value, debt));
                    JOptionPane.showMessageDialog(this, "เพิ่มรายการเรียบร้อยแล้ว", "สำเร็จ",
                            JOptionPane.PLAIN_MESSAGE);
                    showAllAssets();
                    break;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "กรุณากรอกตัวเลขที่ถูกต้อง", "ข้อผิดพลาด",
                            JOptionPane.PLAIN_MESSAGE);
                }
            } else {
                break;
            }
        }
    }

    private void addAgricultureDialog() {
        JTextField nameField = new JTextField(15);
        JTextField weightField = new JTextField(15);
        JTextField priceField = new JTextField(15);
        JComboBox<String> typeBox = new JComboBox<>(new String[] { "ใช้ต้นทุน/ชลประทาน", "อาศัยน้ำฝนธรรมชาติ" });

        JPanel myPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        myPanel.add(new JLabel("ชื่อรายการ:"));
        myPanel.add(nameField);
        myPanel.add(new JLabel("น้ำหนักผลผลิต (กิโลกรัม):"));
        myPanel.add(weightField);
        myPanel.add(new JLabel("ราคาต่อกิโลกรัม (บาท):"));
        myPanel.add(priceField);
        myPanel.add(new JLabel("รูปแบบการให้น้ำ:"));
        myPanel.add(typeBox);

        Object[] options = { "ตกลง (OK)", "ยกเลิก (Cancel)" };
        while (true) {
            int result = JOptionPane.showOptionDialog(this, myPanel,
                    "เพิ่มผลผลิตทางการเกษตร", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options,
                    options[0]);
            if (result == 0) {
                try {
                    String name = nameField.getText();
                    if (name.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "กรุณากรอกชื่อรายการ", "ข้อผิดพลาด",
                                JOptionPane.PLAIN_MESSAGE);
                        continue;
                    }
                    double weight = Double.parseDouble(weightField.getText());
                    double price = Double.parseDouble(priceField.getText());
                    boolean irrigated = (typeBox.getSelectedIndex() == 0);
                    manager.addAsset(new AgricultureAsset(name, weight, price, irrigated));
                    JOptionPane.showMessageDialog(this, "เพิ่มรายการเรียบร้อยแล้ว", "สำเร็จ",
                            JOptionPane.PLAIN_MESSAGE);
                    showAllAssets();
                    break;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "กรุณากรอกตัวเลขที่ถูกต้อง", "ข้อผิดพลาด",
                            JOptionPane.PLAIN_MESSAGE);
                }
            } else {
                break;
            }
        }
    }

    private void showAllAssets() {
        displayPanel.removeAll();
        if (manager.getCount() == 0) {
            JTextArea emptyArea = new JTextArea("ยังไม่มีรายการทรัพย์สินในระบบ");
            emptyArea.setFont(getSansFont(Font.PLAIN, 18));
            emptyArea.setEditable(false);
            emptyArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            displayPanel.add(emptyArea);
        } else {
            Asset[] assets = manager.getAssets();
            for (int i = 0; i < manager.getCount(); i++) {
                final int index = i;

                JPanel itemPanel = new JPanel(new BorderLayout(10, 10));
                itemPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(12, 15, 12, 15)));
                itemPanel.setBackground(Color.WHITE);

                StringBuilder sb = new StringBuilder();
                sb.append("รายการที่ ").append(i + 1).append("\n");
                sb.append(assets[i].getInfo());

                JTextArea textArea = new JTextArea(sb.toString());
                textArea.setFont(getSansFont(Font.PLAIN, 16));
                textArea.setEditable(false);
                textArea.setOpaque(false);
                itemPanel.add(textArea, BorderLayout.CENTER);

                JButton delBtn = new JButton("Delete");
                delBtn.setBackground(new Color(231, 76, 60));
                delBtn.setForeground(Color.WHITE);
                delBtn.setFocusPainted(false);
                delBtn.setFont(getSansFont(Font.BOLD, 14));
                delBtn.setMargin(new Insets(6, 14, 6, 14));
                delBtn.addActionListener(e -> {
                    Object[] options = { "ใช่ (Yes)", "ไม่ (No)" };
                    int result = JOptionPane.showOptionDialog(this,
                            "คุณต้องการลบรายการที่ " + (index + 1) + " หรือไม่?", "ยืนยันการลบ",
                            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                    if (result == 0) {
                        manager.removeAsset(index);
                        showAllAssets();
                    }
                });

                JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                btnPanel.setOpaque(false);
                btnPanel.add(delBtn);
                itemPanel.add(btnPanel, BorderLayout.EAST);

                displayPanel.add(itemPanel);
            }
        }
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    private void calculateAndShowTotal() {
        displayPanel.removeAll();
        if (manager.getCount() == 0) {
            JTextArea emptyArea = new JTextArea("ยังไม่มีรายการทรัพย์สิน กรุณาเพิ่มรายการก่อน");
            emptyArea.setFont(getSansFont(Font.PLAIN, 18));
            emptyArea.setEditable(false);
            emptyArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            displayPanel.add(emptyArea);
        } else {
            double totalValue = manager.calculateTotalValue();
            double totalZakat = manager.calculateTotalZakat();

            StringBuilder sb = new StringBuilder();
            sb.append("================ สรุปผลการคำนวณซะกาต ================\n");
            sb.append("จำนวนรายการทรัพย์สินทั้งหมด   : ").append(manager.getCount()).append(" รายการ\n");
            sb.append("จำนวนรายการที่ครบเกณฑ์นิศอบ  : ").append(manager.countEligibleAssets()).append(" รายการ\n");
            sb.append("มูลค่าทรัพย์สินรวมทั้งหมด     : ").append(df.format(totalValue)).append(" บาท\n");
            sb.append("ยอดซะกาตที่ต้องชำระทั้งหมด    : ").append(df.format(totalZakat)).append(" บาท\n");
            sb.append("=====================================================\n\n");

            sb.append(">> รายการที่เข้าข่ายต้องจ่ายซะกาต:\n");
            Asset[] assets = manager.getAssets();
            boolean hasEligible = false;
            for (int i = 0; i < manager.getCount(); i++) {
                if (assets[i].isEligible()) {
                    sb.append("- ").append(assets[i].getName())
                            .append(" (").append(assets[i].getType()).append(") : ยอดซะกาต ")
                            .append(df.format(assets[i].calculateZakat())).append(" บาท\n");
                    if (assets[i].getType().equals("สินค้า / ทรัพย์สินทางการค้า")) {
                        sb.append(" (มูลค่าทรัพย์สิน) ")
                                .append(df.format(assets[i].getValue())).append(" บาท\n")
                                .append(" (มูลค่าทรัพย์สินสุทธิ) ")
                                .append(df.format(((TradeAsset) assets[i]).getNetValue())).append(" บาท\n");
                    } else {
                        sb.append(" (มูลค่าทรัพย์สิน) ")
                                .append(df.format(assets[i].getValue())).append(" บาท\n");
                    }
                    hasEligible = true;
                }
            }
            if (!hasEligible) {
                sb.append("- ไม่มีรายการใดถึงเกณฑ์นิศอบ\n");
            }

            JTextArea summaryArea = new JTextArea(sb.toString());
            summaryArea.setFont(getSansFont(Font.PLAIN, 16));
            summaryArea.setEditable(false);
            summaryArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            displayPanel.add(summaryArea);
        }
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    public static void main(String[] args) {
        // Enable anti-aliasing for smooth text rendering on both Linux and Windows
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Set Look and Feel to System default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        applyGlobalFonts();

        SwingUtilities.invokeLater(() -> {
            new ZakatCalculatorGUI().setVisible(true);
        });
    }
}

class RoundExitButton extends JButton {
    public RoundExitButton(String label) {
        super(label);
        setBackground(new Color(231, 76, 60)); // Red
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getModel().isArmed()) {
            g.setColor(new Color(192, 57, 43)); // Darker red
        } else if (getModel().isRollover()) {
            g.setColor(new Color(236, 112, 99)); // Lighter red
        } else {
            g.setColor(getBackground());
        }
        g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);
        super.paintComponent(g);
    }

    Shape shape;

    @Override
    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new java.awt.geom.Ellipse2D.Float(0, 0, getWidth(), getHeight());
        }
        return shape.contains(x, y);
    }
}
