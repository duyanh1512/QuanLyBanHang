/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.quanlibanhang;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
/**
 *
 * @author Duy Anh
 */
public class quanlikhachhang extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(quanlikhachhang.class.getName());

    /**
     * Creates new form quanlikhachhang
     */
   public quanlikhachhang() {
    initComponents();
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    taoGiaoDienKhachHang();
    setLocationRelativeTo(null);
}

private JTable tblKhachHang;
private DefaultTableModel model;
private JTextField txtMaKH, txtTenKH, txtSDT, txtDiaChi, txtTimKiem;

private final Color NAVY = new Color(15, 39, 71);
private final Color BLUE = new Color(25, 70, 125);
private final Color BG = new Color(245, 247, 251);

private Connection ketNoi() throws SQLException {
    String url = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=quanlibanhang;"
            + "encrypt=true;"
            + "trustServerCertificate=true;";

    String password = System.getenv("DB_PASSWORD");

    if (password == null || password.isBlank()) {
        throw new SQLException("Chưa cấu hình DB_PASSWORD");
    }

    return DriverManager.getConnection(url, "java_app", password);
}

private void taoGiaoDienKhachHang() {
    getContentPane().removeAll();
    getContentPane().setLayout(new BorderLayout());

    setTitle("Hệ thống quản lý bán hàng - Khách hàng");
    setSize(1100, 700);
    setMinimumSize(new Dimension(950, 600));

    // MENU BÊN TRÁI
    JPanel menu = new JPanel();
    menu.setBackground(NAVY);
    menu.setPreferredSize(new Dimension(235, 0));
    menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
    menu.setBorder(BorderFactory.createEmptyBorder(25, 12, 20, 12));

    JLabel logo = new JLabel("QL BÁN HÀNG");
    logo.setFont(new Font("Arial", Font.BOLD, 23));
    logo.setForeground(Color.WHITE);
    logo.setAlignmentX(Component.CENTER_ALIGNMENT);

    menu.add(logo);
    menu.add(Box.createVerticalStrut(45));

    themNutMenuKH(menu, "TRANG CHỦ", () -> dispose());
    themNutMenuKH(menu, "QUẢN LÝ SẢN PHẨM",
        () -> new quanlisanpham().setVisible(true));
    themNutMenuKH(menu, "QUẢN LÝ KHÁCH HÀNG", () -> {});
    themNutMenuKH(menu, "QUẢN LÝ HÓA ĐƠN",
        () -> thongBaoChuaCo());
    themNutMenuKH(menu, "THỐNG KÊ DOANH THU",
        () -> thongBaoChuaCo());

    menu.add(Box.createVerticalGlue());
    themNutMenuKH(menu, "THOÁT", () -> dispose());

    add(menu, BorderLayout.WEST);

    // NỘI DUNG CHÍNH
    JPanel content = new JPanel(new BorderLayout());
    content.setBackground(BG);
    add(content, BorderLayout.CENTER);

    // THANH TIÊU ĐỀ
    JPanel header = new JPanel(new GridLayout(2, 1));
    header.setBackground(BLUE);
    header.setBorder(BorderFactory.createEmptyBorder(22, 25, 22, 25));

    JLabel title = new JLabel("QUẢN LÝ KHÁCH HÀNG");
    title.setFont(new Font("Arial", Font.BOLD, 27));
    title.setForeground(Color.WHITE);

    JLabel subtitle = new JLabel(
        "Quản lý thông tin - Thêm - Sửa - Xóa - Tìm kiếm"
    );
    subtitle.setFont(new Font("Arial", Font.PLAIN, 15));
    subtitle.setForeground(Color.WHITE);

    header.add(title);
    header.add(subtitle);
    content.add(header, BorderLayout.NORTH);

    // PHẦN THÂN
    JPanel body = new JPanel(new BorderLayout(15, 18));
    body.setBackground(BG);
    body.setBorder(BorderFactory.createEmptyBorder(30, 30, 25, 30));
    content.add(body, BorderLayout.CENTER);

    // 4 THẺ CHỨC NĂNG
    JPanel cards = new JPanel(new GridLayout(1, 4, 12, 0));
    cards.setBackground(BG);
    cards.setPreferredSize(new Dimension(0, 105));

    cards.add(taoTheKH(
        "THÊM",
        "Khách hàng mới",
        new Color(224, 238, 255),
        () -> themKhachHang()
    ));

    cards.add(taoTheKH(
        "SỬA",
        "Cập nhật thông tin",
        new Color(225, 246, 232),
        () -> suaKhachHang()
    ));

    cards.add(taoTheKH(
        "XÓA",
        "Xóa khách hàng",
        new Color(255, 237, 221),
        () -> xoaKhachHang()
    ));

    cards.add(taoTheKH(
        "LÀM MỚI",
        "Tải lại dữ liệu",
        new Color(239, 230, 255),
        () -> {
            xoaTrang();
            loadKhachHang();
        }
    ));

    body.add(cards, BorderLayout.NORTH);

    // DANH SÁCH KHÁCH HÀNG
    JPanel center = new JPanel(new BorderLayout(0, 12));
    center.setOpaque(false);

    JPanel search = new JPanel(new BorderLayout(12, 0));
    search.setOpaque(false);

    JLabel listTitle = new JLabel("DANH SÁCH KHÁCH HÀNG");
    listTitle.setFont(new Font("Arial", Font.BOLD, 17));
    listTitle.setForeground(NAVY);

    txtTimKiem = new JTextField();
    txtTimKiem.setFont(new Font("Arial", Font.PLAIN, 14));
    txtTimKiem.setToolTipText("Nhập mã, tên hoặc số điện thoại");

    JButton btnTim = new JButton("TÌM KIẾM");
    btnTim.setBackground(BLUE);
    btnTim.setForeground(Color.WHITE);
    btnTim.setFocusPainted(false);
    btnTim.addActionListener(e -> timKiem());

    JPanel searchRight = new JPanel(new BorderLayout(8, 0));
    searchRight.setOpaque(false);
    searchRight.add(txtTimKiem, BorderLayout.CENTER);
    searchRight.add(btnTim, BorderLayout.EAST);

    search.add(listTitle, BorderLayout.WEST);
    search.add(searchRight, BorderLayout.CENTER);

    center.add(search, BorderLayout.NORTH);

    model = new DefaultTableModel(
        new Object[]{"Mã KH", "Tên khách hàng", "SĐT", "Địa chỉ"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };

    tblKhachHang = new JTable(model);
    tblKhachHang.setRowHeight(35);
    tblKhachHang.setFont(new Font("Arial", Font.PLAIN, 14));
    tblKhachHang.setBackground(Color.WHITE);
    tblKhachHang.setForeground(new Color(30, 40, 55));
    tblKhachHang.setSelectionBackground(new Color(210, 230, 250));
    tblKhachHang.setSelectionForeground(Color.BLACK);
    tblKhachHang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JTableHeader tableHeader = tblKhachHang.getTableHeader();
    tableHeader.setFont(new Font("Arial", Font.BOLD, 14));
    tableHeader.setBackground(new Color(224, 238, 255));
    tableHeader.setForeground(NAVY);
    tableHeader.setPreferredSize(new Dimension(0, 40));

    JScrollPane scroll = new JScrollPane(tblKhachHang);
    scroll.getViewport().setBackground(Color.WHITE);
    center.add(scroll, BorderLayout.CENTER);

    body.add(center, BorderLayout.CENTER);

    // CÁC Ô NHẬP THÔNG TIN
    JPanel bottom = new JPanel(new GridLayout(2, 4, 12, 8));
    bottom.setOpaque(false);
    bottom.setPreferredSize(new Dimension(0, 78));

    txtMaKH = new JTextField();
    txtTenKH = new JTextField();
    txtSDT = new JTextField();
    txtDiaChi = new JTextField();

    bottom.add(new JLabel("Mã khách hàng"));
    bottom.add(new JLabel("Tên khách hàng"));
    bottom.add(new JLabel("Số điện thoại"));
    bottom.add(new JLabel("Địa chỉ"));

    bottom.add(txtMaKH);
    bottom.add(txtTenKH);
    bottom.add(txtSDT);
    bottom.add(txtDiaChi);

    body.add(bottom, BorderLayout.SOUTH);

    // CHỌN DÒNG TRONG BẢNG
    tblKhachHang.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            int row = tblKhachHang.getSelectedRow();

            if (row >= 0) {
                txtMaKH.setText(model.getValueAt(row, 0).toString());
                txtTenKH.setText(model.getValueAt(row, 1).toString());
                txtSDT.setText(model.getValueAt(row, 2).toString());
                txtDiaChi.setText(model.getValueAt(row, 3).toString());
            }
        }
    });

    loadKhachHang();
    revalidate();
    repaint();
}

// TẠO NÚT MENU
private void themNutMenuKH(JPanel menu, String text, Runnable action) {
    JButton btn = new JButton(text);
    btn.setFont(new Font("Arial", Font.BOLD, 13));
    btn.setForeground(Color.WHITE);
    btn.setBackground(NAVY);
    btn.setFocusPainted(false);
    btn.setBorderPainted(false);
    btn.setHorizontalAlignment(SwingConstants.LEFT);
    btn.setMaximumSize(new Dimension(215, 48));
    btn.setAlignmentX(Component.CENTER_ALIGNMENT);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

    btn.addActionListener(e -> action.run());

    btn.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent e) {
            btn.setBackground(new Color(35, 112, 210));
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent e) {
            btn.setBackground(NAVY);
        }
    });

    menu.add(btn);
    menu.add(Box.createVerticalStrut(12));
}

// TẠO THẺ CHỨC NĂNG
private JPanel taoTheKH(
        String title, String desc, Color color, Runnable action) {

    JPanel card = new JPanel(new BorderLayout(0, 8));
    card.setBackground(color);
    card.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
    card.setCursor(new Cursor(Cursor.HAND_CURSOR));

    JLabel lblTitle = new JLabel(title);
    lblTitle.setFont(new Font("Arial", Font.BOLD, 17));
    lblTitle.setForeground(new Color(25, 39, 60));

    JLabel lblDesc = new JLabel(desc);
    lblDesc.setFont(new Font("Arial", Font.PLAIN, 12));

    card.add(lblTitle, BorderLayout.CENTER);
    card.add(lblDesc, BorderLayout.SOUTH);

    card.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {
            action.run();
        }
    });

    return card;
}

// HIỂN THỊ DANH SÁCH KHÁCH HÀNG
private void loadKhachHang() {
    model.setRowCount(0);

    String sql = "SELECT MaKH, TenKH, SDT, DiaChi FROM KhachHang";

    try (Connection con = ketNoi();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("MaKH"),
                rs.getString("TenKH"),
                rs.getString("SDT"),
                rs.getString("DiaChi")
            });
        }

    } catch (SQLException e) {
        baoLoi(e);
    }
}

// KIỂM TRA THÔNG TIN
private boolean kiemTraNhap() {
    if (txtMaKH.getText().trim().isEmpty()
            || txtTenKH.getText().trim().isEmpty()
            || txtSDT.getText().trim().isEmpty()
            || txtDiaChi.getText().trim().isEmpty()) {

        JOptionPane.showMessageDialog(
            this, "Vui lòng nhập đầy đủ thông tin!"
        );
        return false;
    }

    return true;
}

// THÊM KHÁCH HÀNG
private void themKhachHang() {
    if (!kiemTraNhap()) return;

    String sql = "INSERT INTO KhachHang (MaKH, TenKH, SDT, DiaChi) "
               + "VALUES (?, ?, ?, ?)";

    try (Connection con = ketNoi();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, txtMaKH.getText().trim());
        ps.setString(2, txtTenKH.getText().trim());
        ps.setString(3, txtSDT.getText().trim());
        ps.setString(4, txtDiaChi.getText().trim());

        ps.executeUpdate();

        JOptionPane.showMessageDialog(
            this, "Thêm khách hàng thành công!"
        );

        xoaTrang();
        loadKhachHang();

    } catch (SQLException e) {
        baoLoi(e);
    }
}

// SỬA KHÁCH HÀNG
private void suaKhachHang() {
    int row = tblKhachHang.getSelectedRow();

    if (row < 0) {
        JOptionPane.showMessageDialog(
            this, "Hãy chọn khách hàng cần sửa!"
        );
        return;
    }

    if (!kiemTraNhap()) return;

    String maCu = model.getValueAt(row, 0).toString();

    String sql = "UPDATE KhachHang "
               + "SET TenKH=?, SDT=?, DiaChi=? WHERE MaKH=?";

    try (Connection con = ketNoi();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, txtTenKH.getText().trim());
        ps.setString(2, txtSDT.getText().trim());
        ps.setString(3, txtDiaChi.getText().trim());
        ps.setString(4, maCu);

        ps.executeUpdate();

        JOptionPane.showMessageDialog(this, "Sửa thành công!");

        xoaTrang();
        loadKhachHang();

    } catch (SQLException e) {
        baoLoi(e);
    }
}

// XÓA KHÁCH HÀNG
private void xoaKhachHang() {
    int row = tblKhachHang.getSelectedRow();

    if (row < 0) {
        JOptionPane.showMessageDialog(
            this, "Hãy chọn khách hàng cần xóa!"
        );
        return;
    }

    String ma = model.getValueAt(row, 0).toString();

    int chon = JOptionPane.showConfirmDialog(
        this,
        "Bạn có muốn xóa khách hàng " + ma + "?",
        "Xác nhận",
        JOptionPane.YES_NO_OPTION
    );

    if (chon != JOptionPane.YES_OPTION) return;

    String sql = "DELETE FROM KhachHang WHERE MaKH=?";

    try (Connection con = ketNoi();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, ma);
        ps.executeUpdate();

        JOptionPane.showMessageDialog(this, "Xóa thành công!");

        xoaTrang();
        loadKhachHang();

    } catch (SQLException e) {
        baoLoi(e);
    }
}

// TÌM KIẾM KHÁCH HÀNG
private void timKiem() {
    String tuKhoa = txtTimKiem.getText().trim();

    if (tuKhoa.isEmpty()) {
        loadKhachHang();
        return;
    }

    model.setRowCount(0);

    String sql = "SELECT MaKH, TenKH, SDT, DiaChi FROM KhachHang "
               + "WHERE MaKH LIKE ? OR TenKH LIKE ? OR SDT LIKE ?";

    try (Connection con = ketNoi();
         PreparedStatement ps = con.prepareStatement(sql)) {

        String key = "%" + tuKhoa + "%";

        ps.setString(1, key);
        ps.setString(2, key);
        ps.setString(3, key);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("MaKH"),
                    rs.getString("TenKH"),
                    rs.getString("SDT"),
                    rs.getString("DiaChi")
                });
            }
        }

    } catch (SQLException e) {
        baoLoi(e);
    }
}

// LÀM MỚI
private void xoaTrang() {
    tblKhachHang.clearSelection();
    txtMaKH.setText("");
    txtTenKH.setText("");
    txtSDT.setText("");
    txtDiaChi.setText("");
    txtTimKiem.setText("");
}

// THÔNG BÁO
private void thongBaoChuaCo() {
    JOptionPane.showMessageDialog(
        this, "Chức năng này sẽ được xây dựng!"
    );
}

// HIỂN THỊ LỖI SQL
private void baoLoi(SQLException e) {
    JOptionPane.showMessageDialog(
        this,
        "Lỗi SQL: " + e.getMessage(),
        "Lỗi",
        JOptionPane.ERROR_MESSAGE
    );
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new quanlikhachhang().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
