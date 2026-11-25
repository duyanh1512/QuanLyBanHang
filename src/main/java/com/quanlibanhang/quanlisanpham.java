package com.quanlibanhang;

import java.awt.*;
import java.sql.*;
import java.math.BigDecimal;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class quanlisanpham extends JFrame {

    private JTextField txtMaSP = new JTextField();
    private JTextField txtTenSP = new JTextField();
    private JTextField txtDonGia = new JTextField();
    private JTextField txtSoLuong = new JTextField();
    private JTextField txtTimKiem = new JTextField();

    private JTable tblSanPham = new JTable();
    private DefaultTableModel model;

    private final Color NAVY = new Color(15, 39, 71);
    private final Color BLUE = new Color(25, 70, 125);
    private final Color BG = new Color(245, 247, 251);

    public quanlisanpham() {
        taoGiaoDien();
        loadSanPham();
    }

    // Kết nối SQL Server
    private Connection getConnection() throws SQLException {
        String url = "jdbc:sqlserver://localhost:1433;"
                + "databaseName=quanlibanhang;"
                + "encrypt=true;"
                + "trustServerCertificate=true;";

        return DriverManager.getConnection(
                url, "java_app", System.getenv("DB_PASSWORD"));
    }

    // Tạo giao diện
    private void taoGiaoDien() {
        setTitle("Quản lý sản phẩm");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Menu bên trái
        JPanel menu = new JPanel(new GridLayout(8, 1, 0, 10));
        menu.setBackground(NAVY);
        menu.setPreferredSize(new Dimension(210, 0));

        JLabel logo = new JLabel(
                "QUẢN LÝ BÁN HÀNG", SwingConstants.CENTER);

        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Arial", Font.BOLD, 16));

        JButton btnTrangChu = nutMenu("TRANG CHỦ");
        JButton btnSanPham = nutMenu("QUẢN LÝ SẢN PHẨM");
        JButton btnKhachHang = nutMenu("QUẢN LÝ KHÁCH HÀNG");
        JButton btnHoaDon = nutMenu("QUẢN LÝ HÓA ĐƠN");
        JButton btnThoat = nutMenu("THOÁT");

        btnSanPham.setBackground(new Color(35, 112, 210));

        menu.add(logo);
        menu.add(btnTrangChu);
        menu.add(btnSanPham);
        menu.add(btnKhachHang);
        menu.add(btnHoaDon);
        menu.add(btnThoat);

        add(menu, BorderLayout.WEST);

        btnTrangChu.addActionListener(e -> dispose());
        btnThoat.addActionListener(e -> dispose());

        btnKhachHang.addActionListener(e -> {
            new quanlikhachhang().setVisible(true);
            dispose();
        });

        btnHoaDon.addActionListener(e -> {
            new quanlihoadon().setVisible(true);
            dispose();
        });

        // Nội dung chính
        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBackground(BG);
        main.setBorder(BorderFactory.createEmptyBorder(
                15, 15, 15, 15));

        add(main, BorderLayout.CENTER);

        JLabel title = new JLabel("  QUẢN LÝ SẢN PHẨM");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        title.setBackground(BLUE);
        title.setPreferredSize(new Dimension(0, 65));

        main.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(BG);
        main.add(content, BorderLayout.CENTER);

        // Nút chức năng
        JPanel actions = new JPanel(new GridLayout(1, 4, 12, 0));
        actions.setBackground(BG);

        JButton btnThem = nutChucNang(
                "THÊM", new Color(224, 238, 255));

        JButton btnSua = nutChucNang(
                "SỬA", new Color(225, 246, 232));

        JButton btnXoa = nutChucNang(
                "XÓA", new Color(255, 237, 221));

        JButton btnLamMoi = nutChucNang(
                "LÀM MỚI", new Color(239, 230, 255));

        actions.add(btnThem);
        actions.add(btnSua);
        actions.add(btnXoa);
        actions.add(btnLamMoi);

        // Tìm kiếm
        JPanel search = new JPanel(new BorderLayout(10, 0));
        search.setBackground(BG);

        JButton btnTimKiem = new JButton("Tìm kiếm");

        search.add(new JLabel("Tìm sản phẩm:"), BorderLayout.WEST);
        search.add(txtTimKiem, BorderLayout.CENTER);
        search.add(btnTimKiem, BorderLayout.EAST);

        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setBackground(BG);
        top.add(actions, BorderLayout.NORTH);
        top.add(search, BorderLayout.SOUTH);

        content.add(top, BorderLayout.NORTH);

        // Bảng sản phẩm
        model = new DefaultTableModel(
                new String[]{
                    "Mã sản phẩm",
                    "Tên sản phẩm",
                    "Đơn giá",
                    "Số lượng"
                }, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblSanPham.setModel(model);
        tblSanPham.setRowHeight(30);
        tblSanPham.setFont(new Font("Arial", Font.PLAIN, 14));

        tblSanPham.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 14));

        tblSanPham.getTableHeader().setBackground(BLUE);
        tblSanPham.getTableHeader().setForeground(Color.WHITE);

        tblSanPham.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        content.add(new JScrollPane(tblSanPham),
                BorderLayout.CENTER);

        // Form nhập thông tin
        JPanel form = new JPanel(new GridLayout(2, 4, 10, 10));
        form.setBackground(Color.WHITE);

        form.setBorder(BorderFactory.createTitledBorder(
                "Thông tin sản phẩm"));

        form.add(new JLabel("Mã sản phẩm"));
        form.add(new JLabel("Tên sản phẩm"));
        form.add(new JLabel("Đơn giá"));
        form.add(new JLabel("Số lượng"));

        form.add(txtMaSP);
        form.add(txtTenSP);
        form.add(txtDonGia);
        form.add(txtSoLuong);

        form.setPreferredSize(new Dimension(0, 110));

        content.add(form, BorderLayout.SOUTH);

        // Sự kiện nút bấm
        btnThem.addActionListener(e -> themSanPham());
        btnSua.addActionListener(e -> suaSanPham());
        btnXoa.addActionListener(e -> xoaSanPham());
        btnLamMoi.addActionListener(e -> lamMoi());
        btnTimKiem.addActionListener(e -> timKiem());

        txtTimKiem.addActionListener(e -> timKiem());

        tblSanPham.getSelectionModel()
                .addListSelectionListener(e -> {
                    if (!e.getValueIsAdjusting()) {
                        chonSanPham();
                    }
                });
    }

    private JButton nutMenu(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(NAVY);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        return btn;
    }

    private JButton nutChucNang(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(NAVY);
        btn.setFont(new Font("Arial", Font.BOLD, 17));
        btn.setPreferredSize(new Dimension(0, 75));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        return btn;
    }

    // Hiển thị dữ liệu
    private void loadSanPham() {
        hienThi("");
    }

    private void hienThi(String tuKhoa) {
        model.setRowCount(0);

        String sql = "SELECT * FROM SanPham "
                + "WHERE MaSP LIKE ? OR TenSP LIKE ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + tuKhoa + "%");
            ps.setString(2, "%" + tuKhoa + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("MaSP"),
                        rs.getString("TenSP"),
                        rs.getBigDecimal("DonGia"),
                        rs.getInt("SoLuong")
                    });
                }
            }

        } catch (SQLException e) {
            thongBao(e.getMessage());
        }
    }

    // Kiểm tra dữ liệu
    private boolean kiemTra() {
        if (txtMaSP.getText().trim().isEmpty()
                || txtTenSP.getText().trim().isEmpty()) {

            thongBao("Vui lòng nhập đầy đủ thông tin!");
            return false;
        }

        try {
            BigDecimal gia = new BigDecimal(
                    txtDonGia.getText().trim());

            int sl = Integer.parseInt(
                    txtSoLuong.getText().trim());

            if (gia.signum() < 0 || sl < 0) {
                thongBao("Đơn giá và số lượng không được âm!");
                return false;
            }

        } catch (NumberFormatException e) {
            thongBao("Đơn giá hoặc số lượng không hợp lệ!");
            return false;
        }

        return true;
    }

    // Thêm sản phẩm
    private void themSanPham() {
        if (!kiemTra()) {
            return;
        }

        String sql = "INSERT INTO SanPham "
                + "(MaSP, TenSP, DonGia, SoLuong) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, txtMaSP.getText().trim());
            ps.setString(2, txtTenSP.getText().trim());
            ps.setBigDecimal(3, new BigDecimal(
                    txtDonGia.getText().trim()));
            ps.setInt(4, Integer.parseInt(
                    txtSoLuong.getText().trim()));

            ps.executeUpdate();

            thongBao("Thêm sản phẩm thành công!");
            lamMoi();

        } catch (SQLException e) {
            thongBao("Không thể thêm: " + e.getMessage());
        }
    }

    // Sửa sản phẩm
    private void suaSanPham() {
        if (tblSanPham.getSelectedRow() == -1) {
            thongBao("Hãy chọn sản phẩm cần sửa!");
            return;
        }

        if (!kiemTra()) {
            return;
        }

        String sql = "UPDATE SanPham SET "
                + "TenSP = ?, DonGia = ?, SoLuong = ? "
                + "WHERE MaSP = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, txtTenSP.getText().trim());
            ps.setBigDecimal(2, new BigDecimal(
                    txtDonGia.getText().trim()));
            ps.setInt(3, Integer.parseInt(
                    txtSoLuong.getText().trim()));
            ps.setString(4, txtMaSP.getText().trim());

            if (ps.executeUpdate() > 0) {
                thongBao("Sửa sản phẩm thành công!");
                lamMoi();
            } else {
                thongBao("Không tìm thấy sản phẩm!");
            }

        } catch (SQLException e) {
            thongBao(e.getMessage());
        }
    }

    // Xóa sản phẩm
    private void xoaSanPham() {
        if (tblSanPham.getSelectedRow() == -1) {
            thongBao("Hãy chọn sản phẩm cần xóa!");
            return;
        }

        int chon = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa sản phẩm này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (chon != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM SanPham WHERE MaSP = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, txtMaSP.getText().trim());

            if (ps.executeUpdate() > 0) {
                thongBao("Xóa sản phẩm thành công!");
                lamMoi();
            } else {
                thongBao("Không tìm thấy sản phẩm!");
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 547) {
                thongBao("Không thể xóa sản phẩm đã có trong hóa đơn!");
            } else {
                thongBao(e.getMessage());
            }
        }
    }

    // Tìm kiếm
    private void timKiem() {
        hienThi(txtTimKiem.getText().trim());
        xoaThongTin();
    }

    // Chọn sản phẩm trên bảng
    private void chonSanPham() {
        int row = tblSanPham.getSelectedRow();

        if (row >= 0) {
            txtMaSP.setText(model.getValueAt(row, 0).toString());
            txtTenSP.setText(model.getValueAt(row, 1).toString());
            txtDonGia.setText(model.getValueAt(row, 2).toString());
            txtSoLuong.setText(model.getValueAt(row, 3).toString());

            txtMaSP.setEditable(false);
        }
    }

    // Xóa thông tin nhập
    private void xoaThongTin() {
        tblSanPham.clearSelection();

        txtMaSP.setText("");
        txtTenSP.setText("");
        txtDonGia.setText("");
        txtSoLuong.setText("");

        txtMaSP.setEditable(true);
    }

    // Làm mới
    private void lamMoi() {
        xoaThongTin();
        txtTimKiem.setText("");
        loadSanPham();
    }

    private void thongBao(String text) {
        JOptionPane.showMessageDialog(this, text);
    }

    // Chạy thử giao diện
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new quanlisanpham().setVisible(true);
        });
    }
}