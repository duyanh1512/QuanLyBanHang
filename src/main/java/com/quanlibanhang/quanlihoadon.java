
package com.quanlibanhang;

import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class quanlihoadon extends JFrame {

    private final Color NAVY = new Color(15, 39, 71);
    private final Color BLUE = new Color(28, 70, 124);
    private final Color BG = new Color(245, 247, 251);

    private JTable tblHoaDon, tblChiTiet;
    private DefaultTableModel modelHD, modelCT;

    private JTextField txtMaHD, txtNgayLap, txtTimKiem;
    private JTextField txtSoLuong, txtTongTien;
    private JComboBox<Item> cboKhachHang, cboSanPham;

    private final ArrayList<ChiTiet> gioHang = new ArrayList<>();

    private final String URL =
        "jdbc:sqlserver://localhost:1433;"
        + "databaseName=quanlibanhang;"
        + "encrypt=true;"
        + "trustServerCertificate=true;";

    private final String USER = "java_app";

    private Connection ketNoi() throws SQLException {
        String password = System.getenv("DB_PASSWORD");
        if (password == null || password.isEmpty()) {
            throw new SQLException("Chưa cấu hình DB_PASSWORD.");
        }
        return DriverManager.getConnection(URL, USER, password);
    }

    static class Item {
        String ma, ten;
        BigDecimal gia;

        Item(String ma, String ten, BigDecimal gia) {
            this.ma = ma;
            this.ten = ten;
            this.gia = gia;
        }

        @Override
        public String toString() {
            return ma + " - " + ten;
        }
    }

    static class ChiTiet {
        String maSP, tenSP;
        int soLuong;
        BigDecimal donGia;

        ChiTiet(String maSP, String tenSP, int soLuong,
                BigDecimal donGia) {
            this.maSP = maSP;
            this.tenSP = tenSP;
            this.soLuong = soLuong;
            this.donGia = donGia;
        }

        BigDecimal thanhTien() {
            return donGia.multiply(BigDecimal.valueOf(soLuong));
        }
    }

    public quanlihoadon() {
        setTitle("Hệ thống quản lý bán hàng - Hóa đơn");
        setSize(1250, 820);
        setMinimumSize(new Dimension(1050, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        taoGiaoDien();
        taiDuLieu();
    }

    private JLabel nhan(String text, int size, boolean bold) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial",
                bold ? Font.BOLD : Font.PLAIN, size));
        return l;
    }

    private JPanel the(String title, Color color, Runnable action) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(color);
        p.setBorder(new EmptyBorder(15, 18, 15, 18));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel label = nhan(title, 16, true);
        p.add(label, BorderLayout.CENTER);

        p.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.run();
            }
        });

        return p;
    }

    private void taoGiaoDien() {
        setLayout(new BorderLayout());

        JPanel sidebar = new JPanel();
        sidebar.setBackground(NAVY);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(new EmptyBorder(30, 15, 20, 15));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = nhan("QL BÁN HÀNG", 23, true);
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(45));

        nutMenu(sidebar, "TRANG CHỦ", () -> dispose());
        nutMenu(sidebar, "QUẢN LÝ SẢN PHẨM",
                () -> new quanlisanpham().setVisible(true));
        nutMenu(sidebar, "QUẢN LÝ KHÁCH HÀNG",
                () -> new quanlikhachhang().setVisible(true));
        nutMenu(sidebar, "QUẢN LÝ HÓA ĐƠN", () -> {});
        nutMenu(sidebar, "THỐNG KÊ DOANH THU",
                () -> JOptionPane.showMessageDialog(this,
                        "Chức năng đang phát triển"));

        sidebar.add(Box.createVerticalGlue());
        nutMenu(sidebar, "ĐÓNG CỬA SỔ", () -> dispose());

        add(sidebar, BorderLayout.WEST);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BG);
        add(content, BorderLayout.CENTER);

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(BLUE);
        header.setBorder(new EmptyBorder(20, 28, 20, 28));

        JLabel title = nhan("QUẢN LÝ HÓA ĐƠN", 28, true);
        title.setForeground(Color.WHITE);
        JLabel sub = nhan(
                "Tạo hóa đơn - Chi tiết sản phẩm - Tổng tiền", 15, false);
        sub.setForeground(Color.WHITE);

        header.add(title);
        header.add(sub);
        content.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(20, 25, 25, 25));
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        JPanel info = new JPanel(new GridLayout(2, 4, 12, 8));
        info.setBackground(BG);
        info.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));

        txtMaHD = new JTextField();
        txtNgayLap = new JTextField(LocalDate.now().toString());
        cboKhachHang = new JComboBox<>();
        txtTongTien = new JTextField("0");
        txtTongTien.setEditable(false);

        info.add(nhan("Mã hóa đơn", 13, true));
        info.add(nhan("Khách hàng", 13, true));
        info.add(nhan("Ngày lập (yyyy-MM-dd)", 13, true));
        info.add(nhan("Tổng tiền", 13, true));

        info.add(txtMaHD);
        info.add(cboKhachHang);
        info.add(txtNgayLap);
        info.add(txtTongTien);

        body.add(info);
        body.add(Box.createVerticalStrut(18));

        JPanel product = new JPanel(new GridLayout(2, 3, 12, 7));
        product.setBackground(BG);
        product.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));

        cboSanPham = new JComboBox<>();
        txtSoLuong = new JTextField("1");

        JButton btnThemSP = new JButton("THÊM SẢN PHẨM");
        btnThemSP.addActionListener(e -> themVaoGio());

        product.add(nhan("Chọn sản phẩm", 13, true));
        product.add(nhan("Số lượng", 13, true));
        product.add(nhan("Thao tác", 13, true));

        product.add(cboSanPham);
        product.add(txtSoLuong);
        product.add(btnThemSP);

        body.add(product);
        body.add(Box.createVerticalStrut(18));

        JPanel actions = new JPanel(new GridLayout(1, 4, 12, 0));
        actions.setBackground(BG);
        actions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        actions.add(the("THÊM HÓA ĐƠN",
                new Color(224, 238, 255), () -> luuHoaDon(false)));
        actions.add(the("SỬA HÓA ĐƠN",
                new Color(225, 246, 232), () -> luuHoaDon(true)));
        actions.add(the("XÓA HÓA ĐƠN",
                new Color(255, 237, 221), () -> xoaHoaDon()));
        actions.add(the("LÀM MỚI",
                new Color(239, 230, 255), () -> lamMoi()));

        body.add(actions);
        body.add(Box.createVerticalStrut(18));

        JPanel search = new JPanel(new BorderLayout(12, 0));
        search.setBackground(BG);
        search.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        txtTimKiem = new JTextField();
        JButton btnTim = new JButton("TÌM KIẾM");
        btnTim.addActionListener(e -> taiHoaDon(txtTimKiem.getText().trim()));

        search.add(nhan("DANH SÁCH HÓA ĐƠN", 15, true),
                BorderLayout.WEST);
        search.add(txtTimKiem, BorderLayout.CENTER);
        search.add(btnTim, BorderLayout.EAST);

        body.add(search);
        body.add(Box.createVerticalStrut(10));

        modelHD = new DefaultTableModel(
                new Object[]{"Mã HĐ", "Mã KH", "Ngày lập", "Tổng tiền"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblHoaDon = new JTable(modelHD);
        tblHoaDon.setRowHeight(30);
        tblHoaDon.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblHoaDon.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) chonHoaDon();
        });

        JScrollPane scrollHD = new JScrollPane(tblHoaDon);
        scrollHD.setPreferredSize(new Dimension(0, 180));
        body.add(scrollHD);
        body.add(Box.createVerticalStrut(15));

        JLabel ctTitle = nhan("CHI TIẾT HÓA ĐƠN", 15, true);
        ctTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(ctTitle);
        body.add(Box.createVerticalStrut(8));

        modelCT = new DefaultTableModel(
                new Object[]{"Mã SP", "Tên sản phẩm", "Số lượng",
                    "Đơn giá", "Thành tiền"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblChiTiet = new JTable(modelCT);
        tblChiTiet.setRowHeight(28);

        JScrollPane scrollCT = new JScrollPane(tblChiTiet);
        scrollCT.setPreferredSize(new Dimension(0, 160));
        body.add(scrollCT);
        body.add(Box.createVerticalStrut(8));

        JButton btnBoSP = new JButton("BỎ SẢN PHẨM ĐÃ CHỌN");
        btnBoSP.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnBoSP.addActionListener(e -> {
            int row = tblChiTiet.getSelectedRow();
            if (row >= 0 && row < gioHang.size()) {
                gioHang.remove(row);
                capNhatChiTiet();
            }
        });
        body.add(btnBoSP);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        content.add(scroll, BorderLayout.CENTER);
    }

    private void nutMenu(JPanel parent, String name, Runnable action) {
        JButton btn = new JButton(name);
        btn.setForeground(Color.WHITE);
        btn.setBackground(NAVY);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(220, 43));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> action.run());
        parent.add(btn);
        parent.add(Box.createVerticalStrut(9));
    }

    private void taiDuLieu() {
        try (Connection c = ketNoi()) {
            cboKhachHang.removeAllItems();
            cboSanPham.removeAllItems();

            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT MaKH, TenKH FROM KhachHang ORDER BY MaKH");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cboKhachHang.addItem(new Item(
                            rs.getString("MaKH"),
                            rs.getString("TenKH"), null));
                }
            }

            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT MaSP, TenSP, DonGia FROM SanPham ORDER BY MaSP");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cboSanPham.addItem(new Item(
                            rs.getString("MaSP"),
                            rs.getString("TenSP"),
                            rs.getBigDecimal("DonGia")));
                }
            }

        } catch (SQLException e) {
            baoLoi(e);
        }

        taiHoaDon("");
    }

    private void taiHoaDon(String keyword) {
        modelHD.setRowCount(0);

        String sql =
                "SELECT MaHD, MaKH, NgayLap, TongTien FROM HoaDon "
                + "WHERE MaHD LIKE ? OR MaKH LIKE ? ORDER BY MaHD";

        try (Connection c = ketNoi();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modelHD.addRow(new Object[]{
                        rs.getString("MaHD"),
                        rs.getString("MaKH"),
                        rs.getDate("NgayLap"),
                        rs.getBigDecimal("TongTien")
                    });
                }
            }
        } catch (SQLException e) {
            baoLoi(e);
        }
    }

    private void chonHoaDon() {
        int row = tblHoaDon.getSelectedRow();
        if (row < 0) return;

        String maHD = modelHD.getValueAt(row, 0).toString();
        String maKH = modelHD.getValueAt(row, 1).toString();

        txtMaHD.setText(maHD);
        txtNgayLap.setText(modelHD.getValueAt(row, 2).toString());

        for (int i = 0; i < cboKhachHang.getItemCount(); i++) {
            if (cboKhachHang.getItemAt(i).ma.equals(maKH)) {
                cboKhachHang.setSelectedIndex(i);
                break;
            }
        }

        gioHang.clear();

        String sql = "SELECT ct.MaSP, sp.TenSP, ct.SoLuong, ct.DonGia "
                + "FROM ChiTietHoaDon ct "
                + "JOIN SanPham sp ON ct.MaSP = sp.MaSP "
                + "WHERE ct.MaHD = ?";

        try (Connection c = ketNoi();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    gioHang.add(new ChiTiet(
                            rs.getString("MaSP"),
                            rs.getString("TenSP"),
                            rs.getInt("SoLuong"),
                            rs.getBigDecimal("DonGia")));
                }
            }
            capNhatChiTiet();
        } catch (SQLException e) {
            baoLoi(e);
        }
    }

    private void themVaoGio() {
        Item sp = (Item) cboSanPham.getSelectedItem();
        if (sp == null) return;

        try {
            int sl = Integer.parseInt(txtSoLuong.getText().trim());
            if (sl <= 0) {
                thongBao("Số lượng phải lớn hơn 0.");
                return;
            }

            for (ChiTiet ct : gioHang) {
                if (ct.maSP.equals(sp.ma)) {
                    ct.soLuong = Math.addExact(ct.soLuong, sl);
                    capNhatChiTiet();
                    return;
                }
            }

            gioHang.add(new ChiTiet(sp.ma, sp.ten, sl, sp.gia));
            capNhatChiTiet();

        } catch (NumberFormatException | ArithmeticException e) {
            thongBao("Số lượng không hợp lệ.");
        }
    }

    private BigDecimal tinhTong() {
        BigDecimal tong = BigDecimal.ZERO;
        for (ChiTiet ct : gioHang) {
            tong = tong.add(ct.thanhTien());
        }
        return tong;
    }

    private void capNhatChiTiet() {
        modelCT.setRowCount(0);
        for (ChiTiet ct : gioHang) {
            modelCT.addRow(new Object[]{
                ct.maSP, ct.tenSP, ct.soLuong,
                ct.donGia, ct.thanhTien()
            });
        }
        txtTongTien.setText(tinhTong().toPlainString());
    }

    private void luuHoaDon(boolean sua) {
        String maHD = txtMaHD.getText().trim();
        Item kh = (Item) cboKhachHang.getSelectedItem();

        if (maHD.isEmpty() || kh == null || gioHang.isEmpty()) {
            thongBao("Nhập mã hóa đơn, chọn khách hàng và thêm sản phẩm.");
            return;
        }

        java.sql.Date ngay;
        try {
            ngay = java.sql.Date.valueOf(txtNgayLap.getText().trim());
        } catch (IllegalArgumentException e) {
            thongBao("Ngày lập phải có dạng yyyy-MM-dd.");
            return;
        }

        Connection c = null;
        try {
            c = ketNoi();
            c.setAutoCommit(false);

            if (sua) {
                try (PreparedStatement ps = c.prepareStatement(
                        "DELETE FROM ChiTietHoaDon WHERE MaHD = ?")) {
                    ps.setString(1, maHD);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE HoaDon SET MaKH=?, NgayLap=?, "
                        + "TongTien=? WHERE MaHD=?")) {
                    ps.setString(1, kh.ma);
                    ps.setDate(2, ngay);
                    ps.setBigDecimal(3, tinhTong());
                    ps.setString(4, maHD);

                    if (ps.executeUpdate() == 0) {
                        throw new SQLException("Không tìm thấy hóa đơn để sửa.");
                    }
                }
            } else {
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO HoaDon(MaHD, MaKH, NgayLap, TongTien) "
                        + "VALUES (?, ?, ?, ?)")) {
                    ps.setString(1, maHD);
                    ps.setString(2, kh.ma);
                    ps.setDate(3, ngay);
                    ps.setBigDecimal(4, tinhTong());
                    ps.executeUpdate();
                }
            }

            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO ChiTietHoaDon"
                    + "(MaHD, MaSP, SoLuong, DonGia) "
                    + "VALUES (?, ?, ?, ?)")) {

                for (ChiTiet ct : gioHang) {
                    ps.setString(1, maHD);
                    ps.setString(2, ct.maSP);
                    ps.setInt(3, ct.soLuong);
                    ps.setBigDecimal(4, ct.donGia);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            c.commit();
            thongBao(sua ? "Sửa hóa đơn thành công!"
                    : "Thêm hóa đơn thành công!");
            lamMoi();

        } catch (SQLException e) {
            if (c != null) {
                try {
                    c.rollback();
                } catch (SQLException ex) {
                    baoLoi(ex);
                }
            }
            baoLoi(e);
        } finally {
            if (c != null) {
                try {
                    c.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    private void xoaHoaDon() {
        String maHD = txtMaHD.getText().trim();

        if (maHD.isEmpty()) {
            thongBao("Hãy chọn hóa đơn cần xóa.");
            return;
        }

        int chon = JOptionPane.showConfirmDialog(
                this, "Xóa hóa đơn " + maHD + " và toàn bộ chi tiết?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (chon != JOptionPane.YES_OPTION) return;

        Connection c = null;
        try {
            c = ketNoi();
            c.setAutoCommit(false);

            try (PreparedStatement ps = c.prepareStatement(
                    "DELETE FROM ChiTietHoaDon WHERE MaHD=?")) {
                ps.setString(1, maHD);
                ps.executeUpdate();
            }

            int count;
            try (PreparedStatement ps = c.prepareStatement(
                    "DELETE FROM HoaDon WHERE MaHD=?")) {
                ps.setString(1, maHD);
                count = ps.executeUpdate();
            }

            if (count == 0) {
                throw new SQLException("Không tìm thấy hóa đơn.");
            }

            c.commit();
            thongBao("Xóa hóa đơn thành công!");
            lamMoi();

        } catch (SQLException e) {
            if (c != null) {
                try {
                    c.rollback();
                } catch (SQLException ex) {
                    baoLoi(ex);
                }
            }
            baoLoi(e);
        } finally {
            if (c != null) {
                try {
                    c.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    private void lamMoi() {
        txtMaHD.setText("");
        txtNgayLap.setText(LocalDate.now().toString());
        txtTimKiem.setText("");
        txtSoLuong.setText("1");
        gioHang.clear();
        capNhatChiTiet();
        tblHoaDon.clearSelection();
        taiDuLieu();
    }

    private void thongBao(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void baoLoi(SQLException e) {
        JOptionPane.showMessageDialog(this,
                "Lỗi SQL: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                () -> new quanlihoadon().setVisible(true));
    }
}
