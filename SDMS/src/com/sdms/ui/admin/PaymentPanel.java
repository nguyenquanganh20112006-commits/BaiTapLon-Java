package com.sdms.ui.admin;

import com.sdms.model.Contract;
import com.sdms.model.Invoice;
import com.sdms.model.Notification;
import com.sdms.utils.DatabaseService;
import com.sdms.utils.UITheme;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class PaymentPanel extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private JTextField tfSearch;
    private JComboBox<String> cbMonth;
    private JComboBox<String> cbStatus;

    private static final String[] COLS = {"Mã HĐ","Sinh viên","Phòng","Tháng",
                                          "Tiền phòng","Tiền điện","Tiền nước","Tổng tiền","Trạng thái"};

    public PaymentPanel() {
        setBackground(UITheme.BG_LIGHT);
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.WHITE);
        p.setBorder(new CompoundBorder(new MatteBorder(0,0,1,0,UITheme.BORDER), new EmptyBorder(12,20,12,20)));
        JLabel title = new JLabel("💳  Quản lý thanh toán");
        title.setFont(UITheme.FONT_H2); title.setForeground(UITheme.TEXT_PRIMARY);
        p.add(title, BorderLayout.WEST);
        return p;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0,10));
        body.setBackground(UITheme.BG_LIGHT);
        body.setBorder(new EmptyBorder(14,16,14,16));

        // Summary cards
        JPanel cards = new JPanel(new GridLayout(1,3,12,0));
        cards.setOpaque(false);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        long totalRev = DatabaseService.getAllInvoices().stream().filter(Invoice::isPaid).mapToLong(Invoice::getTotal).sum();
        long pending  = DatabaseService.getAllInvoices().stream().filter(i -> !i.isPaid()).mapToLong(Invoice::getTotal).sum();
        long count    = DatabaseService.getAllInvoices().stream().filter(i -> !i.isPaid()).count();

        cards.add(summaryCard("💰", "Đã thu tháng này",    String.format("%,d đ", totalRev), UITheme.SUCCESS));
        cards.add(summaryCard("⏳", "Chưa thanh toán",     String.format("%,d đ", pending),  UITheme.WARNING));
        cards.add(summaryCard("📋", "Hóa đơn chờ xử lý",   count + " hóa đơn",              UITheme.DANGER));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
        toolbar.setOpaque(false);
        tfSearch = UITheme.textField("🔍  Tìm theo tên sinh viên, phòng...");
        tfSearch.setPreferredSize(new Dimension(240,36));
        cbMonth = UITheme.comboBox(new String[]{"Tất cả tháng","06/2026","05/2026","04/2026","03/2026"});
        cbMonth.setPreferredSize(new Dimension(110,36));
        cbStatus = UITheme.comboBox(new String[]{"Tất cả","Đã thanh toán","Chưa thanh toán"});
        cbStatus.setPreferredSize(new Dimension(150,36));
        JButton btnPrepay = UITheme.primaryBtn("⚡ Thanh toán trước hạn");
        btnPrepay.addActionListener(e -> openPrepayDialog());
        JButton btnExcel = UITheme.successBtn("📊 Xuất Excel");
        btnExcel.addActionListener(e -> exportToExcel());
        toolbar.add(tfSearch); toolbar.add(cbMonth); toolbar.add(cbStatus); toolbar.add(btnPrepay); toolbar.add(btnExcel);

        // Table
        model = new DefaultTableModel(null, COLS) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        refreshTable();

        // Listeners filter — tìm kiếm/lọc theo tháng và trạng thái
        Runnable doFilter = this::refreshTable;
        tfSearch.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent ev) { doFilter.run(); }
        });
        cbMonth.addActionListener(e -> doFilter.run());
        cbStatus.addActionListener(e -> doFilter.run());

        table = new JTable(model);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(UITheme.BORDER);
        table.setBackground(UITheme.WHITE);
        table.setSelectionBackground(UITheme.PRIMARY_LIGHT);
        table.setRowHeight(38);
        table.getTableHeader().setFont(UITheme.FONT_LABEL);
        table.getTableHeader().setBackground(UITheme.BG_SECONDARY);
        table.getTableHeader().setForeground(UITheme.TEXT_SECONDARY);

        // Status renderer
        table.getColumnModel().getColumn(8).setCellRenderer((t, v, sel, focus, row, col) -> {
            boolean paid = "Đã thanh toán".equals(v.toString());
            JLabel lbl = UITheme.badge(v.toString(), paid ? UITheme.SUCCESS_BG : UITheme.WARNING_BG,
                                       paid ? UITheme.SUCCESS_TEXT : UITheme.WARNING_TEXT);
            lbl.setOpaque(true); lbl.setBackground(sel ? UITheme.PRIMARY_LIGHT : UITheme.WHITE);
            return lbl;
        });

        // Action on row double-click -> mark paid
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row < 0) return;
                    String id = (String) model.getValueAt(row, 0);
                    Invoice inv = DatabaseService.getAllInvoices().stream().filter(i -> i.getId().equals(id)).findFirst().orElse(null);
                    if (inv != null && !inv.isPaid()) {
                        int r = JOptionPane.showConfirmDialog(PaymentPanel.this,
                            "<html>Xác nhận thanh toán hóa đơn <b>"+id+"</b>?<br>Tổng tiền: <b>"+String.format("%,d đ", inv.getTotal())+"</b></html>",
                            "Thanh toán", JOptionPane.YES_NO_OPTION);
                        if (r == JOptionPane.YES_OPTION) {
                            if (DatabaseService.markInvoicePaid(inv.getId(), true)) {
                                inv.setPaid(true);
                                refreshTable();
                                JOptionPane.showMessageDialog(PaymentPanel.this,
                                    "✅ Thanh toán thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(PaymentPanel.this,
                                    "❌ Lưu thất bại! Kiểm tra kết nối database.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1, true));
        scroll.getViewport().setBackground(UITheme.WHITE);

        JLabel hint = new JLabel("💡 Double-click vào hóa đơn chưa thanh toán để xác nhận thanh toán");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(UITheme.TEXT_MUTED);

        JPanel top = new JPanel(new BorderLayout(0,10));
        top.setOpaque(false);
        top.add(cards, BorderLayout.NORTH);
        top.add(toolbar, BorderLayout.SOUTH);

        body.add(top, BorderLayout.NORTH);
        body.add(scroll, BorderLayout.CENTER);
        body.add(hint, BorderLayout.SOUTH);
        return body;
    }

    private JPanel summaryCard(String icon, String label, String value, Color color) {
        JPanel card = UITheme.card();
        card.setLayout(new BorderLayout(10,0));
        JLabel ico = new JLabel(icon);
        ico.setFont(new Font("Segoe UI",Font.PLAIN,28));
        JPanel info = new JPanel(new GridLayout(2,1));
        info.setOpaque(false);
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI",Font.BOLD,18)); val.setForeground(color);
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_SMALL); lbl.setForeground(UITheme.TEXT_SECONDARY);
        info.add(val); info.add(lbl);
        card.add(ico, BorderLayout.WEST);
        card.add(info, BorderLayout.CENTER);
        return card;
    }

    /** Lọc bảng theo từ khóa tìm kiếm, tháng và trạng thái đang chọn */
    private void refreshTable() {
        String search = tfSearch != null ? tfSearch.getText().trim().toLowerCase() : "";
        String month  = cbMonth  != null ? (String) cbMonth.getSelectedItem()  : "Tất cả tháng";
        String status = cbStatus != null ? (String) cbStatus.getSelectedItem() : "Tất cả";

        model.setRowCount(0);
        for (Invoice inv : DatabaseService.getAllInvoices()) {
            if (month != null && !month.equals("Tất cả tháng")) {
                if (inv.getMonth() == null || !inv.getMonth().equals(month)) continue;
            }
            if (status != null && !status.equals("Tất cả")) {
                boolean wantPaid = status.equals("Đã thanh toán");
                if (inv.isPaid() != wantPaid) continue;
            }
            if (!search.isEmpty()) {
                String combined = ((inv.getStudentName() == null ? "" : inv.getStudentName()) + " "
                                  + (inv.getRoomId() == null ? "" : inv.getRoomId())).toLowerCase();
                if (!combined.contains(search)) continue;
            }
            model.addRow(inv.toRow());
        }
    }

    /**
     * Mở dialog "Thanh toán trước hạn": admin chọn sinh viên (đang có hợp đồng hiệu lực)
     * và một tháng (có thể là tháng tương lai chưa tới hạn), hệ thống tạo hóa đơn mới
     * cho tháng đó, đánh dấu đã thanh toán ngay, và gửi thông báo cho sinh viên.
     */
    private void openPrepayDialog() {
        List<Contract> activeContracts = DatabaseService.getAllContracts().stream()
            .filter(c -> c.getStatus() == Contract.Status.ACTIVE)
            .collect(java.util.stream.Collectors.toList());

        if (activeContracts.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "⚠ Không có sinh viên nào đang có hợp đồng hiệu lực để thanh toán trước hạn!",
                "Không có dữ liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Combobox sinh viên: hiển thị "SV001249 - Nguyễn Văn An (P101)"
        String[] studentOptions = activeContracts.stream()
            .map(c -> c.getStudentId() + " - " + c.getStudentName() + " (" + c.getRoomId() + ")")
            .toArray(String[]::new);
        JComboBox<String> cbStudent = new JComboBox<>(studentOptions);

        // Sinh sẵn 6 tháng tới (kể cả tháng hiện tại) để admin chọn nhanh
        java.time.YearMonth now = java.time.YearMonth.now();
        String[] monthOptions = new String[6];
        for (int i = 0; i < 6; i++) {
            java.time.YearMonth ym = now.plusMonths(i);
            monthOptions[i] = String.format("%02d/%d", ym.getMonthValue(), ym.getYear());
        }
        JComboBox<String> cbTargetMonth = new JComboBox<>(monthOptions);
        cbTargetMonth.setEditable(true); // cho phép nhập tháng khác ngoài 6 tháng gợi ý, dạng MM/yyyy

        JPanel dlg = new JPanel(new GridLayout(0, 2, 6, 8));
        dlg.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        dlg.add(new JLabel("Sinh viên:"));      dlg.add(cbStudent);
        dlg.add(new JLabel("Tháng thanh toán:")); dlg.add(cbTargetMonth);

        int confirm = JOptionPane.showConfirmDialog(this, dlg,
            "⚡ Thanh toán trước hạn", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (confirm != JOptionPane.OK_OPTION) return;

        int idx = cbStudent.getSelectedIndex();
        if (idx < 0) return;
        Contract contract = activeContracts.get(idx);

        String month = ((String) cbTargetMonth.getEditor().getItem()).trim();
        if (!month.matches("\\d{2}/\\d{4}")) {
            JOptionPane.showMessageDialog(this,
                "⚠ Tháng không hợp lệ! Vui lòng nhập đúng định dạng MM/yyyy, ví dụ 09/2026.",
                "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Kiểm tra hóa đơn của sinh viên/tháng này đã tồn tại chưa
        boolean exists = DatabaseService.getAllInvoices().stream()
            .anyMatch(inv -> inv.getStudentId().equals(contract.getStudentId()) && inv.getMonth().equals(month));
        if (exists) {
            JOptionPane.showMessageDialog(this,
                "⚠ Sinh viên này đã có hóa đơn tháng " + month + " rồi! Không thể tạo trùng.",
                "Đã tồn tại", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Hóa đơn trả trước: chỉ tính tiền phòng theo hợp đồng (chưa có chỉ số điện nước thực tế tháng đó)
        Invoice inv = new Invoice(
            DatabaseService.nextInvoiceId(),
            contract.getStudentId(),
            contract.getStudentName(),
            contract.getRoomId(),
            month,
            contract.getMonthlyFee(),
            0L, 0L,
            true // đã thanh toán ngay
        );

        if (!DatabaseService.addInvoice(inv)) {
            JOptionPane.showMessageDialog(this,
                "❌ Tạo hóa đơn thất bại! Kiểm tra kết nối database.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Gửi thông báo tới sinh viên
        Notification noti = new Notification(
            DatabaseService.nextNotificationId(),
            "Xác nhận thanh toán trước hạn",
            "Bạn đã thanh toán trước tiền phòng tháng " + month + " (" + String.format("%,d đ", contract.getMonthlyFee()) + "). Cảm ơn bạn!",
            Notification.Type.INVOICE,
            Notification.Target.STUDENT,
            contract.getStudentId(),
            java.time.LocalDateTime.now(),
            "Quản trị viên",
            false, false
        );
        DatabaseService.addNotification(noti);

        refreshTable();
        JOptionPane.showMessageDialog(this,
            "✅ Đã tạo và thanh toán hóa đơn " + inv.getId() + " cho " + contract.getStudentName()
            + " — tháng " + month + ".\n📨 Đã gửi thông báo tới sinh viên.",
            "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Xuất danh sách hóa đơn ra file CSV (mở được bằng Excel) */
    private void exportToExcel() {
        javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        fc.setSelectedFile(new java.io.File("HoaDon_ThanhToan.csv"));
        fc.setDialogTitle("Lưu file xuất Excel");
        if (fc.showSaveDialog(this) != javax.swing.JFileChooser.APPROVE_OPTION) return;

        java.io.File file = fc.getSelectedFile();
        if (!file.getName().endsWith(".csv"))
            file = new java.io.File(file.getPath() + ".csv");

        try (java.io.PrintWriter pw = new java.io.PrintWriter(
                new java.io.OutputStreamWriter(new java.io.FileOutputStream(file), "UTF-8"))) {
            // BOM để Excel nhận UTF-8
            pw.print('\uFEFF');
            pw.println("Mã HĐ,Sinh viên,Phòng,Tháng,Tiền phòng,Tiền điện,Tiền nước,Tổng tiền,Trạng thái");
            for (Invoice inv : DatabaseService.getAllInvoices()) {
                pw.printf("%s,%s,%s,%s,%d,%d,%d,%d,%s%n",
                    inv.getId(), inv.getStudentName(), inv.getRoomId(), inv.getMonth(),
                    inv.getRoomFee(), inv.getElectricFee(), inv.getWaterFee(), inv.getTotal(),
                    inv.isPaid() ? "Đã thanh toán" : "Chưa thanh toán");
            }
            JOptionPane.showMessageDialog(this,
                "✅ Đã xuất " + DatabaseService.getAllInvoices().size() + " hóa đơn ra:\n" + file.getAbsolutePath(),
                "Xuất thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "❌ Xuất thất bại: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}