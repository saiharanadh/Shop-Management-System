import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

class Product {
    int serial;
    String name;
    double price;     
    double discount;  
    int quantity;

    public Product(int serial, String name, double price, double discount, int quantity) {
        this.serial = serial;
        this.name = name;
        this.price = price;
        this.discount = discount;
        this.quantity = quantity;
    }

    public double priceAfterDiscount() {
        return price * (1 - discount / 100.0);
    }
}

class Sale {
    Product product;
    int quantitySold;
    double totalPrice;

    public Sale(Product product, int quantitySold, double totalPrice) {
        this.product = product;
        this.quantitySold = quantitySold;
        this.totalPrice = totalPrice;
    }
}

public class ShopManagementSystem {
    static ArrayList<Product> products = new ArrayList<Product>();
    static ArrayList<Sale> dailySales = new ArrayList<Sale>();
    static Sale lastSale = null;

    // Quick Revenue Label
    static JLabel totalRevenueLabel = new JLabel("Total Revenue: 0.00");

    public static void main(String[] args) {
        preloadProducts();
        showLogin();
    }

    // ===== LOGIN =====
    public static void showLogin() {
        JFrame loginFrame = new JFrame("Shop Owner Login");
        loginFrame.setSize(350, 180);
        loginFrame.setLayout(new FlowLayout());
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(12);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(12);
        JButton loginButton = new JButton("Login");

        loginFrame.add(userLabel);
        loginFrame.add(userField);
        loginFrame.add(passLabel);
        loginFrame.add(passField);
        loginFrame.add(loginButton);
        loginFrame.setVisible(true);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String user = userField.getText();
                String pass = new String(passField.getPassword());
                if (user.equals("owner") && pass.equals("shop123")) {
                    loginFrame.dispose();
                    showDashboard();
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Invalid Username or Password!");
                }
            }
        });
    }

    // ===== DASHBOARD =====
    public static void showDashboard() {
        JFrame dash = new JFrame("Shop Management Dashboard");
        dash.setSize(800, 500);
        dash.setLayout(new BorderLayout(10, 10));
        dash.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dash.setLocationRelativeTo(null);

        JLabel title = new JLabel("Shop Management System", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        dash.add(title, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JButton addBtn = new JButton("Add / Update Product");
        JButton sellBtn = new JButton("Sell Product");
        JButton deleteBtn = new JButton("Delete Product");
        JButton undoBtn = new JButton("Undo Last Sale");
        JButton reportBtn = new JButton("Show Daily Report");
        JButton inventoryBtn = new JButton("View Inventory");
        JButton searchBtn = new JButton("Search Product"); // optional button

        JButton[] buttons = { addBtn, sellBtn, deleteBtn, undoBtn, reportBtn, inventoryBtn };

        // Light colored buttons
        Color[] colors = {
            new Color(135, 206, 250), // Light Sky Blue
            new Color(144, 238, 144), // Light Green
            new Color(255, 182, 193), // Light Pink
            new Color(255, 228, 181), // Light Peach
            new Color(221, 160, 221), // Plum
            new Color(240, 230, 140)  // Khaki
        };

        for (int i = 0; i < buttons.length; i++) {
            JButton b = buttons[i];
            b.setFont(new Font("SansSerif", Font.PLAIN, 14));
            b.setPreferredSize(new Dimension(220, 70));
            b.setBackground(colors[i]);
            b.setOpaque(true);
            b.setBorderPainted(false);
            panel.add(b);
        }

        dash.add(panel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // Quick Total Revenue Label
        totalRevenueLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        footer.add(totalRevenueLabel);

        // Search button
        searchBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchBtn.setPreferredSize(new Dimension(220, 50));
        footer.add(searchBtn);

        dash.add(footer, BorderLayout.SOUTH);

        dash.setVisible(true);

        // Actions (lambda -> anonymous inner classes for older Java)
        addBtn.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { addOrUpdateProduct(); } });
        sellBtn.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { sellProduct(); updateTotalRevenue(); } });
        deleteBtn.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { deleteProduct(); } });
        undoBtn.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { undoLastSale(); updateTotalRevenue(); } });
        reportBtn.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { showDailyReport(); } });
        inventoryBtn.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { viewInventory(); } });
        searchBtn.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { searchProduct(); } });
    }

    // ===== PRELOAD PRODUCTS =====
    public static void preloadProducts() {
        for (int i = 1; i <= 30; i++) {
            products.add(new Product(i, "Product" + i, 100 + i * 10, 5, 10));
        }
    }

    // ===== ADD / UPDATE PRODUCT =====
    public static void addOrUpdateProduct() {
        String choice = JOptionPane.showInputDialog(
            null, "Enter 'new' to add new product or 'update' to increase quantity:");
        if (choice == null) return;

        if (choice.equalsIgnoreCase("new")) {
            try {
                int serial = Integer.parseInt(JOptionPane.showInputDialog("Enter Serial Number:"));
                Product existing = findProductBySerial(serial);

                if (existing != null) {
                    int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Serial " + serial + " already exists for " + existing.name +
                        ".\nDo you want to replace it with a new product?",
                        "Confirm Replace",
                        JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.NO_OPTION) return;
                    else products.remove(existing);
                }

                String name = JOptionPane.showInputDialog("Enter Product Name:");
                double price = Double.parseDouble(JOptionPane.showInputDialog("Enter Price (before discount):"));
                double discount = Double.parseDouble(JOptionPane.showInputDialog("Enter Discount %:"));
                int quantity = Integer.parseInt(JOptionPane.showInputDialog("Enter Quantity:"));

                products.add(new Product(serial, name, price, discount, quantity));
                JOptionPane.showMessageDialog(null, "Product added successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Invalid Input!");
            }
        } 
        else if (choice.equalsIgnoreCase("update")) {
            try {
                int serial = Integer.parseInt(JOptionPane.showInputDialog("Enter Serial Number to update:"));
                Product p = findProductBySerial(serial);
                if (p == null) {
                    JOptionPane.showMessageDialog(null, "Product not found!");
                    return;
                }
                int qty = Integer.parseInt(JOptionPane.showInputDialog("Enter quantity to add:"));
                p.quantity += qty;
                JOptionPane.showMessageDialog(null, "Quantity updated! Current qty: " + p.quantity);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Invalid Input!");
            }
        } 
        else {
            JOptionPane.showMessageDialog(null, "Enter 'new' or 'update' only.");
        }
    }

    // ===== SELL PRODUCT =====
    public static void sellProduct() {
        String input = JOptionPane.showInputDialog("Enter Serial Number or Product Name:");
        if (input == null) return;
        Product p = null;
        try { p = findProductBySerial(Integer.parseInt(input.trim())); }
        catch (Exception e) { p = findProductByName(input.trim()); }
        if (p == null) { JOptionPane.showMessageDialog(null, "Product not found!"); return; }
        if (p.quantity <= 0) { JOptionPane.showMessageDialog(null, "Out of stock!"); return; }

        try {
            int qty = Integer.parseInt(JOptionPane.showInputDialog("Enter quantity to sell:"));
            if (qty <= 0 || qty > p.quantity) { JOptionPane.showMessageDialog(null, "Invalid quantity!"); return; }

            p.quantity -= qty;
            double total = qty * p.priceAfterDiscount();
            dailySales.add(new Sale(p, qty, total));
            lastSale = dailySales.get(dailySales.size()-1);

            // Low Stock / Out of Stock alerts
            if (p.quantity == 0) {
                JOptionPane.showMessageDialog(null, "Product is now out of stock!");
            } else if (p.quantity <= 5) {
                JOptionPane.showMessageDialog(null, "Low stock alert! Only " + p.quantity + " remaining.");
            }

            // Invoice
            String invoice = "INVOICE\n\n" +
                             "Product: " + p.name + "\n" +
                             "Quantity: " + qty + "\n" +
                             "Price per unit: " + String.format("%.2f", p.priceAfterDiscount()) + "\n" +
                             "Total: " + String.format("%.2f", total);
            JOptionPane.showMessageDialog(null, invoice, "Sale Complete", JOptionPane.INFORMATION_MESSAGE);
            updateTotalRevenue();

        } catch (Exception e) { JOptionPane.showMessageDialog(null, "Invalid input!"); }
    }

    // ===== DELETE PRODUCT =====
    public static void deleteProduct() {
        try {
            int s = Integer.parseInt(JOptionPane.showInputDialog("Enter Serial Number to delete:"));
            Product p = findProductBySerial(s);
            if (p == null) { JOptionPane.showMessageDialog(null, "Product not found!"); return; }
            int confirm = JOptionPane.showConfirmDialog(null, 
                "Are you sure you want to delete " + p.name + "?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) products.remove(p);
        } catch (Exception e) { JOptionPane.showMessageDialog(null, "Invalid Input!"); }
    }

    // ===== UNDO LAST SALE =====
    public static void undoLastSale() {
        if (lastSale == null) { JOptionPane.showMessageDialog(null, "No sale to undo!"); return; }
        lastSale.product.quantity += lastSale.quantitySold;
        dailySales.remove(lastSale);
        JOptionPane.showMessageDialog(null, "Last sale undone!");
        lastSale = null;
    }

    // ===== DAILY REPORT =====
    public static void showDailyReport() {
        if (dailySales.isEmpty()) { JOptionPane.showMessageDialog(null, "No sales today!"); return; }
        StringBuilder sb = new StringBuilder();
        double total = 0;
        for (int i = 0; i < dailySales.size(); i++) {
            Sale s = dailySales.get(i);
            sb.append(String.format("%d. %s x%d = %.2f\n", 
                i+1, s.product.name, s.quantitySold, s.totalPrice));
            total += s.totalPrice;
        }
        sb.append("\nTotal Revenue: "+String.format("%.2f", total));

        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(720, 380));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JOptionPane.showMessageDialog(null, scroll, "Daily Report", JOptionPane.INFORMATION_MESSAGE);
    }

    // ===== VIEW INVENTORY =====
    public static void viewInventory() {
        if (products.isEmpty()) { JOptionPane.showMessageDialog(null, "No products in inventory."); return; }

        StringBuilder sb = new StringBuilder();
        sb.append("Inventory:\n\n");
        for (Product p : products) {
            sb.append(String.format("%-5d %-20s %-10.2f %-10.2f %-10.2f %d\n",
                p.serial, p.name, p.price, p.discount, p.priceAfterDiscount(), p.quantity));
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setEditable(false);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(720, 380));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JOptionPane.showMessageDialog(null, scroll, "Inventory", JOptionPane.INFORMATION_MESSAGE);
    }

    // ===== SEARCH PRODUCT =====
    public static void searchProduct() {
        String input = JOptionPane.showInputDialog("Enter Serial Number or Product Name:");
        if (input == null) return;
        Product p = null;
        try {
            p = findProductBySerial(Integer.parseInt(input.trim()));
        } catch (Exception e) {
            p = findProductByName(input.trim());
        }
        if (p == null) {
            JOptionPane.showMessageDialog(null, "Product not found!");
            return;
        }

        String info = "Product Found:\n\n" +
                "Serial: "+p.serial+"\n"+
                "Name: "+p.name+"\n"+
                "Price before discount: "+p.price+"\n"+
                "Discount: "+p.discount+"%\n"+
                "Price after discount: "+p.priceAfterDiscount()+"\n"+
                "Quantity: "+p.quantity;

        JOptionPane.showMessageDialog(null, info, "Product Info", JOptionPane.INFORMATION_MESSAGE);
    }

    // ===== UPDATE TOTAL REVENUE =====
    public static void updateTotalRevenue() {
        double total = 0;
        for (Sale s : dailySales) total += s.totalPrice;
        totalRevenueLabel.setText("Total Revenue: " + String.format("%.2f", total));
    }

    // ===== HELPERS =====
    public static Product findProductBySerial(int serial) {
        for (Product p : products) {
            if (p.serial == serial) return p;
        }
        return null;
    }

    public static Product findProductByName(String name) {
        for (Product p : products) {
            if (p.name.equalsIgnoreCase(name)) return p;
        }
        return null;
    }
}
