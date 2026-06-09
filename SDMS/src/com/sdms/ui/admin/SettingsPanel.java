package com.sdms.ui.admin;

import com.sdms.utils.UITheme;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Panel cài đặt hệ thống ký túc xá.
 * Bao gồm: Thông tin trường/KTX | Đơn giá điện nước | Quy định nội trú | Tài khoản Admin.
 */
public class SettingsPanel extends JPanel {

    // ── Các trường cài đặt chung ─────────────────────────────────
    private JTextField tfDormName, tfAddress, tfPhone, tfEmail, tfDirector;
    private JTextField tfAcademicYear, tfSemester;

    // ── Đơn giá điện nước ────────────────────────────────────────
    private JTextField tfElecPrice, tfWaterPrice, tfRoomFee4, tfRoomFee6;

    // ── Quy định nội trú ─────────────────────────────────────────
    private JTextField tfCurfew, tfGuestPolicy;
    private JSpinner   spMaxWarning;
    private JCheckBox  chkAutoNotify, chkAutoInvoice, chkAutoContract;

    // ── Tài khoản admin ──────────────────────────────────────────
    private JTextField     tfAdminName, tfAdminEmail;
    private JPasswordField pfCurrentPwd, pfNewPwd, pfConfirmPwd;

    public SettingsPanel() {
        setBackground(UITheme.BG_LIGHT);
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(buildContent());
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        add(scroll, BorderLayout.CENTER);
    }

    // ── Header ────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.WHITE);
        p.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
            new EmptyBorder(12, 20, 12, 20)
        ));

        JLabel title = new JLabel("⚙  Cài đặt hệ thống");
        title.setFont(UITheme.FONT_H2);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel breadcrumb = new JLabel("Dashboard / Cài đặt hệ thống");
        breadcrumb.setFont(UITheme.FONT_TINY);
        breadcrumb.setForeground(UITheme.TEXT_MUTED);

        JPanel left = new JPanel(new BorderLayout(0, 2));
        left.setOpaque(false);
        left.add(title, BorderLayout.NORTH);
        left.add(breadcrumb, BorderLayout.SOUTH);

        // Nút lưu toàn bộ
        JButton btnSaveAll = UITheme.primaryBtn("💾 Lưu tất cả cài đặt");
        btnSaveAll.addActionListener(e -> saveAllSettings());

        p.add(left,      BorderLayout.WEST);
        p.add(btnSaveAll,BorderLayout.EAST);
        return p;
    }

    // ── Nội dung chính (scroll) ───────────────────────────────────
    private JPanel buildContent() {
        JPanel content = new JPanel();
        content.setBackground(UITheme.BG_LIGHT);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 24, 24, 24));

        content.add(buildDormInfoSection());
        content.add(Box.createVerticalStrut(16));
        content.add(buildPricingSection());
        content.add(Box.createVerticalStrut(16));
        content.add(buildRulesSection());
        content.add(Box.createVerticalStrut(16));
        content.add(buildAutomationSection());
        content.add(Box.createVerticalStrut(16));
        content.add(buildAccountSection());

        return content;
    }

    // ──────────────────────────────────────────────────────────────
    // SECTION 1: THÔNG TIN KÝ TÚC XÁ
    // ──────────────────────────────────────────────────────────────
    private JPanel buildDormInfoSection() {
        JPanel card = sectionCard("🏫  Thông tin Ký túc xá");

        tfDormName    = UITheme.textField("Ký túc xá Đại học Bách Khoa Hà Nội");
        tfAddress     = UITheme.textField("01 Đại Cồ Việt, Hai Bà Trưng, Hà Nội");
        tfPhone       = UITheme.textField("024 3869 2222");
        tfEmail       = UITheme.textField("ktx@hust.edu.vn");
        tfDirector    = UITheme.textField("Nguyễn Văn Quản");
        tfAcademicYear= UITheme.textField("2025–2026");
        tfSemester    = UITheme.textField("Học kỳ 2");

        // Giá trị mặc định
        tfDormName.setText("Ký túc xá Đại học SDMS");
        tfAddress.setText("01 Đường Đại học, Quận 1, TP. Hà Nội");
        tfPhone.setText("024 1234 5678");
        tfEmail.setText("ktx@sdms.edu.vn");
        tfDirector.setText("Trần Thị Hương");
        tfAcademicYear.setText("2025–2026");
        tfSemester.setText("Học kỳ 2");

        JPanel grid = formGrid();
        addField(grid, "Tên ký túc xá",    tfDormName);
        addField(grid, "Năm học",          tfAcademicYear);
        addField(grid, "Địa chỉ",          tfAddress);
        addField(grid, "Học kỳ",           tfSemester);
        addField(grid, "Số điện thoại",    tfPhone);
        addField(grid, "Email liên hệ",    tfEmail);
        addField(grid, "Giám đốc / Quản lý",tfDirector);

        JPanel btnRow = actionRow();
        JButton btn = UITheme.primaryBtn("💾 Lưu thông tin");
        btn.addActionListener(e -> showSuccess("Đã lưu thông tin ký túc xá!"));
        btnRow.add(btn);

        card.add(grid);
        card.add(Box.createVerticalStrut(12));
        card.add(btnRow);
        return wrapCard(card);
    }

    // ──────────────────────────────────────────────────────────────
    // SECTION 2: ĐƠN GIÁ ĐIỆN NƯỚC & TIỀN PHÒNG
    // ──────────────────────────────────────────────────────────────
    private JPanel buildPricingSection() {
        JPanel card = sectionCard("💰  Đơn giá & Tiền phòng");

        tfElecPrice = UITheme.textField("2000");
        tfWaterPrice= UITheme.textField("6000");
        tfRoomFee4  = UITheme.textField("850000");
        tfRoomFee6  = UITheme.textField("650000");

        tfElecPrice.setText("2,000");
        tfWaterPrice.setText("6,000");
        tfRoomFee4.setText("850,000");
        tfRoomFee6.setText("650,000");

        JPanel grid = formGrid();
        addFieldWithUnit(grid, "Đơn giá điện (đ/kWh)",  tfElecPrice,  "đồng/kWh");
        addFieldWithUnit(grid, "Đơn giá nước (đ/m³)",   tfWaterPrice, "đồng/m³");
        addFieldWithUnit(grid, "Tiền phòng 4 người (đ/tháng)", tfRoomFee4, "đồng/tháng");
        addFieldWithUnit(grid, "Tiền phòng 6 người (đ/tháng)", tfRoomFee6, "đồng/tháng");

        // Ghi chú cảnh báo
        JLabel warn = new JLabel(
            "⚠  Thay đổi đơn giá sẽ áp dụng từ kỳ ghi nhận tiếp theo. Các kỳ đã chốt không bị ảnh hưởng.");
        warn.setFont(UITheme.FONT_TINY);
        warn.setForeground(UITheme.WARNING_TEXT);
        warn.setBorder(new EmptyBorder(4, 0, 0, 0));
        warn.setAlignmentX(LEFT_ALIGNMENT);

        JPanel btnRow = actionRow();
        JButton btn = UITheme.primaryBtn("💾 Lưu đơn giá");
        btn.addActionListener(e -> showSuccess("Đã cập nhật đơn giá thành công!"));
        JButton btnHistory = UITheme.outlineBtn("📋 Lịch sử thay đổi");
        btnHistory.addActionListener(e -> showPriceHistory());
        btnRow.add(btn);
        btnRow.add(btnHistory);

        card.add(grid);
        card.add(Box.createVerticalStrut(6));
        card.add(warn);
        card.add(Box.createVerticalStrut(12));
        card.add(btnRow);
        return wrapCard(card);
    }

    /** Hiển thị lịch sử thay đổi giá */
    private void showPriceHistory() {
        String[] cols = {"Ngày", "Loại", "Giá cũ", "Giá mới", "Người thay đổi"};
        Object[][] rows = {
            {"01/09/2025", "Tiền phòng 4 người", "800,000 đ", "850,000 đ", "Admin"},
            {"01/09/2025", "Đơn giá điện",        "1,800 đ",   "2,000 đ",   "Admin"},
            {"01/01/2025", "Đơn giá nước",        "5,000 đ",   "6,000 đ",   "Admin"},
        };
        JTable tbl = new JTable(rows, cols);
        tbl.setRowHeight(32);
        tbl.setFont(UITheme.FONT_BODY);
        tbl.getTableHeader().setFont(UITheme.FONT_LABEL);
        JOptionPane.showMessageDialog(this,
            new JScrollPane(tbl), "Lịch sử thay đổi giá",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // ──────────────────────────────────────────────────────────────
    // SECTION 3: QUY ĐỊNH NỘI TRÚ
    // ──────────────────────────────────────────────────────────────
    private JPanel buildRulesSection() {
        JPanel card = sectionCard("📋  Quy định nội trú");

        tfCurfew     = UITheme.textField("23:00");
        tfGuestPolicy= UITheme.textField("Phải đăng ký trước 20:00");
        spMaxWarning = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        spMaxWarning.setFont(UITheme.FONT_BODY);
        ((JSpinner.DefaultEditor) spMaxWarning.getEditor()).getTextField()
            .setBackground(UITheme.BG_SECONDARY);

        tfCurfew.setText("23:00");
        tfGuestPolicy.setText("Phải đăng ký trước 20:00 cùng ngày");

        JPanel grid = formGrid();
        addField(grid, "Giờ giới nghiêm (curfew)", tfCurfew);
        addSpinnerField(grid, "Số lần cảnh cáo tối đa trước khi xử lý", spMaxWarning);
        addField(grid, "Quy định khách thăm", tfGuestPolicy);

        JPanel btnRow = actionRow();
        JButton btn = UITheme.primaryBtn("💾 Lưu quy định");
        btn.addActionListener(e -> showSuccess("Đã lưu quy định nội trú!"));
        btnRow.add(btn);

        card.add(grid);
        card.add(Box.createVerticalStrut(12));
        card.add(btnRow);
        return wrapCard(card);
    }

    // ──────────────────────────────────────────────────────────────
    // SECTION 4: TỰ ĐỘNG HÓA
    // ──────────────────────────────────────────────────────────────
    private JPanel buildAutomationSection() {
        JPanel card = sectionCard("🤖  Tự động hóa & Thông báo");

        chkAutoNotify   = styledCheckBox("Tự động gửi thông báo hóa đơn vào đầu mỗi tháng");
        chkAutoInvoice  = styledCheckBox("Tự động tạo hóa đơn tháng cho tất cả sinh viên");
        chkAutoContract = styledCheckBox("Tự động nhắc nhở hợp đồng sắp hết hạn (trước 30 ngày)");

        chkAutoNotify.setSelected(true);
        chkAutoInvoice.setSelected(true);
        chkAutoContract.setSelected(true);

        JPanel checks = new JPanel(new GridLayout(3, 1, 0, 10));
        checks.setOpaque(false);
        checks.setAlignmentX(LEFT_ALIGNMENT);
        checks.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        checks.add(chkAutoNotify);
        checks.add(chkAutoInvoice);
        checks.add(chkAutoContract);

        JPanel btnRow = actionRow();
        JButton btn = UITheme.primaryBtn("💾 Lưu cài đặt tự động");
        btn.addActionListener(e -> showSuccess("Đã lưu cài đặt tự động hóa!"));
        JButton btnTest = UITheme.outlineBtn("▶ Chạy thử ngay");
        btnTest.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "✅ Đã chạy thử tác vụ tự động thành công!", "Thông báo",
            JOptionPane.INFORMATION_MESSAGE));
        btnRow.add(btn);
        btnRow.add(btnTest);

        card.add(checks);
        card.add(Box.createVerticalStrut(12));
        card.add(btnRow);
        return wrapCard(card);
    }

    // ──────────────────────────────────────────────────────────────
    // SECTION 5: TÀI KHOẢN ADMIN
    // ──────────────────────────────────────────────────────────────
    private JPanel buildAccountSection() {
        JPanel card = sectionCard("👤  Tài khoản quản trị viên");

        tfAdminName  = UITheme.textField("Quản trị viên");
        tfAdminEmail = UITheme.textField("admin@sdms.edu.vn");
        pfCurrentPwd = new JPasswordField();
        pfNewPwd     = new JPasswordField();
        pfConfirmPwd = new JPasswordField();

        stylePasswordField(pfCurrentPwd);
        stylePasswordField(pfNewPwd);
        stylePasswordField(pfConfirmPwd);

        tfAdminName.setText("Admin SDMS");
        tfAdminEmail.setText("admin@sdms.edu.vn");

        JPanel infoGrid = formGrid();
        addField(infoGrid, "Tên hiển thị", tfAdminName);
        addField(infoGrid, "Email",         tfAdminEmail);

        JLabel pwdSec = new JLabel("Đổi mật khẩu");
        pwdSec.setFont(UITheme.FONT_LABEL);
        pwdSec.setForeground(UITheme.TEXT_SECONDARY);
        pwdSec.setAlignmentX(LEFT_ALIGNMENT);
        pwdSec.setBorder(new EmptyBorder(8, 0, 4, 0));

        JPanel pwdGrid = formGrid();
        addField(pwdGrid, "Mật khẩu hiện tại",  pfCurrentPwd);
        addField(pwdGrid, "Mật khẩu mới",       pfNewPwd);
        addField(pwdGrid, "Xác nhận mật khẩu mới", pfConfirmPwd);

        JPanel btnRow = actionRow();
        JButton btnInfo = UITheme.primaryBtn("💾 Lưu thông tin");
        JButton btnPwd  = UITheme.warningBtn("🔒 Đổi mật khẩu");
        btnInfo.addActionListener(e -> showSuccess("Đã cập nhật thông tin tài khoản!"));
        btnPwd.addActionListener(e  -> changePassword());
        btnRow.add(btnInfo);
        btnRow.add(btnPwd);

        card.add(infoGrid);
        card.add(Box.createVerticalStrut(4));
        card.add(pwdSec);
        card.add(pwdGrid);
        card.add(Box.createVerticalStrut(12));
        card.add(btnRow);
        return wrapCard(card);
    }

    /** Xử lý đổi mật khẩu */
    private void changePassword() {
        char[] newPwd     = pfNewPwd.getPassword();
        char[] confirmPwd = pfConfirmPwd.getPassword();
        if (pfCurrentPwd.getPassword().length == 0) {
            showWarn("Vui lòng nhập mật khẩu hiện tại!"); return;
        }
        if (newPwd.length < 6) {
            showWarn("Mật khẩu mới phải có ít nhất 6 ký tự!"); return;
        }
        if (!java.util.Arrays.equals(newPwd, confirmPwd)) {
            showWarn("Mật khẩu xác nhận không khớp!"); return;
        }
        pfCurrentPwd.setText("");
        pfNewPwd.setText("");
        pfConfirmPwd.setText("");
        showSuccess("Đã đổi mật khẩu thành công!");
    }

    /** Lưu toàn bộ cài đặt */
    private void saveAllSettings() {
        showSuccess("Đã lưu toàn bộ cài đặt hệ thống!");
    }

    // ── Builder helpers ───────────────────────────────────────────

    /** Tạo panel card chứa một section */
    private JPanel sectionCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);

        JLabel lbl = new JLabel(title);
        lbl.setFont(UITheme.FONT_H3);
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(0, 0, 12, 0));
        card.add(lbl);
        return card;
    }

    /** Bọc card vào panel trắng có border */
    private JPanel wrapCard(JPanel inner) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.WHITE);
        wrapper.setBorder(new CompoundBorder(
            new LineBorder(UITheme.BORDER, 1, true),
            new EmptyBorder(18, 20, 18, 20)
        ));
        wrapper.setAlignmentX(LEFT_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 9999));
        wrapper.add(inner, BorderLayout.CENTER);
        return wrapper;
    }

    /** Grid 2 cột cho form */
    private JPanel formGrid() {
        JPanel g = new JPanel(new GridLayout(0, 2, 12, 10));
        g.setOpaque(false);
        g.setAlignmentX(LEFT_ALIGNMENT);
        g.setMaximumSize(new Dimension(Integer.MAX_VALUE, 9999));
        return g;
    }

    /** Hàng nút thao tác */
    private JPanel actionRow() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        return p;
    }

    /** Thêm field label+input vào grid */
    private void addField(JPanel grid, String label, JComponent field) {
        JPanel cell = new JPanel(new BorderLayout(0, 4));
        cell.setOpaque(false);
        cell.add(UITheme.formLabel(label), BorderLayout.NORTH);
        cell.add(field, BorderLayout.CENTER);
        grid.add(cell);
    }

    /** Thêm field với đơn vị bên phải */
    private void addFieldWithUnit(JPanel grid, String label, JTextField field, String unit) {
        JPanel cell = new JPanel(new BorderLayout(0, 4));
        cell.setOpaque(false);

        JPanel inputRow = new JPanel(new BorderLayout(6, 0));
        inputRow.setOpaque(false);
        JLabel unitLbl = new JLabel(unit);
        unitLbl.setFont(UITheme.FONT_TINY);
        unitLbl.setForeground(UITheme.TEXT_MUTED);
        inputRow.add(field,   BorderLayout.CENTER);
        inputRow.add(unitLbl, BorderLayout.EAST);

        cell.add(UITheme.formLabel(label), BorderLayout.NORTH);
        cell.add(inputRow, BorderLayout.CENTER);
        grid.add(cell);
    }

    /** Thêm JSpinner vào grid */
    private void addSpinnerField(JPanel grid, String label, JSpinner spinner) {
        JPanel cell = new JPanel(new BorderLayout(0, 4));
        cell.setOpaque(false);
        cell.add(UITheme.formLabel(label), BorderLayout.NORTH);
        cell.add(spinner, BorderLayout.CENTER);
        grid.add(cell);
    }

    /** Checkbox có style */
    private JCheckBox styledCheckBox(String text) {
        JCheckBox cb = new JCheckBox(text);
        cb.setFont(UITheme.FONT_BODY);
        cb.setForeground(UITheme.TEXT_PRIMARY);
        cb.setOpaque(false);
        cb.setFocusPainted(false);
        return cb;
    }

    /** Áp style cho JPasswordField */
    private void stylePasswordField(JPasswordField pf) {
        pf.setFont(UITheme.FONT_BODY);
        pf.setBackground(UITheme.BG_SECONDARY);
        pf.setForeground(UITheme.TEXT_PRIMARY);
        pf.setBorder(new CompoundBorder(
            new LineBorder(UITheme.BORDER, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        pf.setPreferredSize(new Dimension(0, 36));
    }

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, "⚠ " + msg, "Lưu ý", JOptionPane.WARNING_MESSAGE);
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, "✅ " + msg, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
}
