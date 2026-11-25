
package com.quanlibanhang;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MenuChinh extends JFrame {

    private final Color NAVY = new Color(15, 39, 71);
    private final Color BLUE = new Color(35, 112, 210);
    private final Color BG = new Color(245, 247, 251);

    private JPanel content;
    private JLabel lblSanPham;
    private JLabel lblKhachHang;
    private JLabel lblHoaDon;

    public MenuChinh() {
        setTitle("Hệ thống quản lý bán hàng");
        setSize(1100, 700);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        taoMenu();
        taoNoiDung();
    }

    private void taoMenu() {
        JPanel menu = new JPanel();
        menu.setBackground(NAVY);
        menu.setPreferredSize(new Dimension(235, 0));
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(new EmptyBorder(25, 12, 20, 12));

        JLabel logo = new JLabel("QL BÁN HÀNG");
        logo.setFont(new Font("Arial", Font.BOLD, 23));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        menu.add(logo);
        menu.add(Box.createVerticalStrut(45));

        themNutMenu(menu, "TRANG CHỦ", () -> hienTrangChu());
        themNutMenu(menu, "QUẢN LÝ SẢN PHẨM", () -> moSanPham());
        themNutMenu(menu, "QUẢN LÝ KHÁCH HÀNG", () -> {
            new quanlikhachhang().setVisible(true);
        });
        themNutMenu(menu, "QUẢN LÝ HÓA ĐƠN", () -> {
    new quanlihoadon().setVisible(true);
});
        themNutMenu(menu, "THỐNG KÊ DOANH THU", () -> thongBaoChuaCo());

        menu.add(Box.createVerticalGlue());

        themNutMenu(menu, "THOÁT", () -> {
            int chon = JOptionPane.showConfirmDialog(
                this,
                "Bạn có muốn thoát chương trình?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
            );

            if (chon == JOptionPane.YES_OPTION) {
                dispose();
            }
        });

        add(menu, BorderLayout.WEST);
    }

    private void themNutMenu(JPanel menu, String ten, Runnable hanhDong) {
        JButton btn = new JButton(ten);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(NAVY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(215, 48));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> hanhDong.run());

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(BLUE);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(NAVY);
            }
        });

        menu.add(btn);
        menu.add(Box.createVerticalStrut(12));
    }

    private void taoNoiDung() {
        content = new JPanel(new BorderLayout());
        content.setBackground(BG);

        add(content, BorderLayout.CENTER);

        hienTrangChu();
    }

    private void hienTrangChu() {
        content.removeAll();

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(new Color(25, 70, 125));
        header.setBorder(new EmptyBorder(22, 25, 22, 25));

        JLabel title = new JLabel("HỆ THỐNG QUẢN LÝ BÁN HÀNG");
        title.setFont(new Font("Arial", Font.BOLD, 27));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel(
            "Quản lý sản phẩm - Khách hàng - Hóa đơn - Doanh thu"
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 15));
        subtitle.setForeground(Color.WHITE);

        header.add(title);
        header.add(subtitle);

        content.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setBackground(BG);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(35, 30, 25, 30));

        JPanel cards = new JPanel(new GridLayout(2, 2, 20, 20));
        cards.setBackground(BG);
        cards.setMaximumSize(new Dimension(900, 310));

        cards.add(taoThe(
            "QUẢN LÝ SẢN PHẨM",
            "Thêm, sửa, xóa, tìm kiếm sản phẩm",
            new Color(224, 238, 255),
            () -> moSanPham()
        ));

        cards.add(taoThe(
            "QUẢN LÝ KHÁCH HÀNG",
            "Quản lý thông tin khách hàng",
            new Color(225, 246, 232),
            () -> thongBaoChuaCo()
        ));

        cards.add(taoThe(
            "QUẢN LÝ HÓA ĐƠN",
            "Tạo hóa đơn và xem chi tiết",
            new Color(255, 237, 221),
            () -> thongBaoChuaCo()
        ));

        cards.add(taoThe(
            "THỐNG KÊ DOANH THU",
            "Xem doanh thu và báo cáo",
            new Color(239, 230, 255),
            () -> thongBaoChuaCo()
        ));

        body.add(cards);
        body.add(Box.createVerticalStrut(35));

        JPanel stats = new JPanel(new GridLayout(1, 3, 15, 15));
        stats.setBackground(BG);
        stats.setMaximumSize(new Dimension(900, 115));

        lblSanPham = taoThongKe(stats, "TỔNG SẢN PHẨM", "--");
        lblKhachHang = taoThongKe(stats, "TỔNG KHÁCH HÀNG", "--");
        lblHoaDon = taoThongKe(stats, "HÓA ĐƠN HÔM NAY", "--");

        body.add(stats);
        body.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        content.add(scroll, BorderLayout.CENTER);

        
    }

    private JPanel taoThe(
        String ten,
        String moTa,
        Color mau,
        Runnable hanhDong
    ) {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(mau);
        card.setBorder(new EmptyBorder(25, 20, 25, 20));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel title = new JLabel(ten);
        title.setFont(new Font("Arial", Font.BOLD, 17));

        JLabel desc = new JLabel(moTa);
        desc.setFont(new Font("Arial", Font.PLAIN, 13));

        card.add(title, BorderLayout.CENTER);
        card.add(desc, BorderLayout.SOUTH);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                hanhDong.run();
            }
        });

        return card;
    }

    private JLabel taoThongKe(JPanel parent, String ten, String so) {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel(ten);
        title.setFont(new Font("Arial", Font.BOLD, 13));

        JLabel value = new JLabel(so);
        value.setFont(new Font("Arial", Font.BOLD, 28));
        value.setForeground(BLUE);

        panel.add(title);
        panel.add(value);
        parent.add(panel);

        return value;
    }

    private void moSanPham() {
        new quanlisanpham().setVisible(true);
    }

    private void thongBaoChuaCo() {
        JOptionPane.showMessageDialog(
            this,
            "Chức năng này sẽ được xây dựng"
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuChinh().setVisible(true);
        });
    }
}
