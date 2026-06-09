package com.sdms.ui.user;

import com.sdms.model.User;
import com.sdms.utils.UITheme;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel thông báo hệ thống dành cho sinh viên.
 * Hiển thị danh sách thông báo theo loại, có thể đánh dấu đã đọc.
 */
public class StudentNotificationPanel extends JPanel {

    private final User currentUser;

    // Model thông báo nội bộ (không dùng Notification model để độc lập)
    private static class Notice {
        String id, icon, type, title, content, time;
        boolean read, pinned;
        Notice(String id, String icon, String type, String title,
               String content, String time, boolean read, boolean pinned) {
            this.id=id; this.icon=icon; this.type=type; this.title=title;
            this.content=content; this.time=time; this.read=read; this.pinned=pinned;
        }
    }

    private final List<Notice> notices = new ArrayList<>();
    private JPanel             listPanel;
    private JLabel             lblUnread;
    private String             currentFilter = "Tất cả";

    public StudentNotificationPanel(User currentUser) {
        this.currentUser = currentUser;
        initSampleData();

        setBackground(UITheme.BG_LIGHT);
        setLayout(new BorderLayout());
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    private void initSampleData() {
        notices.add(new Notice("TB001","🔴","Hóa đơn",
            "Hóa đơn tháng 06/2026 đã sẵn sàng",
            "Hóa đơn tháng 06/2026 của bạn đã được tạo. Tổng số tiền: 926,000 đ. "
            + "Vui lòng thanh toán trước ngày 15/06/2026 để tránh phạt trễ hạn.",
            "1 giờ trước", false, true));

        notices.add(new Notice("TB002","📄","Hợp đồng",
            "Hợp đồng sắp hết hạn",
            "Hợp đồng thuê phòng của bạn sẽ hết hạn vào ngày 31/08/2026 (còn 82 ngày). "
            + "Vui lòng liên hệ Ban quản lý để gia hạn trước ngày 01/08/2026.",
            "2 ngày trước", false, false));

        notices.add(new Notice("TB003","🔍","Kiểm tra",
            "Lịch kiểm tra phòng định kỳ tháng 6",
            "Ban quản lý sẽ tiến hành kiểm tra phòng định kỳ vào ngày 10/06/2026, "
            + "bắt đầu từ 09:00. Đề nghị sinh viên có mặt tại phòng và giữ phòng gọn gàng, sạch sẽ.",
            "3 ngày trước", true, false));

        notices.add(new Notice("TB004","🔵","Chung",
            "Nhắc nhở vệ sinh chung cuối tuần",
            "Lịch tổng vệ sinh khu vực hành lang và nhà vệ sinh chung: Thứ 7, ngày 08/06/2026 "
            + "lúc 08:00 – 10:00. Đề nghị các bạn sinh viên phối hợp.",
            "5 ngày trước", true, false));

        notices.add(new Notice("TB005","⚠","Vi phạm",
            "Nhắc nhở về việc tuân thủ giờ giới nghiêm",
            "Ban quản lý ghi nhận một số sinh viên về muộn sau 23:00. "
            + "Đề nghị tất cả sinh viên tuân thủ giờ giới nghiêm. Vi phạm lần 2 sẽ bị lập biên bản.",
            "1 tuần trước", true, false));

        notices.add(new Notice("TB006","🔵","Chung",
            "Thông báo lịch cúp điện bảo trì",
            "Do bảo trì hệ thống điện, khu B sẽ bị cúp điện từ 14:00 – 17:00 ngày 12/06/2026. "
            + "Đề nghị sinh viên chuẩn bị sạc đầy thiết bị trước đó.",
            "1 tuần trước", true, false));

        notices.add(new Notice("TB007","🔵","Chung",
            "Hướng dẫn đăng ký ở lại hè 2026",
            "Sinh viên có nhu cầu ở lại ký túc xá trong hè 2026 vui lòng đăng ký "
            + "tại văn phòng BQL từ ngày 01/07 – 15/07/2026. Mang theo thẻ sinh viên.",
            "2 tuần trước", true, false));
    }

    // ── Header ────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.WHITE);
        p.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
            new EmptyBorder(12, 20, 12, 20)
        ));

        JLabel title = new JLabel("🔔  Thông báo từ hệ thống");
        title.setFont(UITheme.FONT_H2);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Trang chủ / Thông báo");
        sub.setFont(UITheme.FONT_TINY);
        sub.setForeground(UITheme.TEXT_MUTED);

        JPanel left = new JPanel(new BorderLayout(0, 2));
        left.setOpaque(false);
        left.add(title, BorderLayout.NORTH);
        left.add(sub,   BorderLayout.SOUTH);

        long unread = notices.stream().filter(n -> !n.read).count();
        lblUnread = UITheme.badge(unread + " chưa đọc",
            unread > 0 ? UITheme.DANGER_BG : UITheme.BG_SECONDARY,
            unread > 0 ? UITheme.DANGER    : UITheme.TEXT_SECONDARY);

        JButton btnMarkAll = UITheme.outlineBtn("✓ Đánh dấu tất cả đã đọc");
        btnMarkAll.addActionListener(e -> markAllRead());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(lblUnread);
        right.add(btnMarkAll);

        p.add(left,  BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    // ── Nội dung ──────────────────────────────────────────────────
    private JPanel buildContent() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(UITheme.BG_LIGHT);

        // Thanh filter tabs
        p.add(buildFilterBar(), BorderLayout.NORTH);

        // Danh sách thông báo
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(UITheme.BG_LIGHT);
        listPanel.setBorder(new EmptyBorder(12, 20, 20, 20));

        renderList();

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    // ── Thanh filter loại thông báo ───────────────────────────────
    private JPanel buildFilterBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        bar.setBackground(UITheme.WHITE);
        bar.setBorder(new MatteBorder(0, 0, 1, 0, UITheme.BORDER));

        String[] filters = {"Tất cả", "Chưa đọc", "Hóa đơn", "Hợp đồng", "Vi phạm", "Kiểm tra", "Chung"};
        ButtonGroup bg = new ButtonGroup();

        for (String f : filters) {
            JToggleButton btn = filterBtn(f);
            bg.add(btn);
            if (f.equals("Tất cả")) btn.setSelected(true);
            btn.addActionListener(e -> {
                currentFilter = f;
                renderList();
            });
            bar.add(btn);
        }
        return bar;
    }

    private JToggleButton filterBtn(String text) {
        JToggleButton btn = new JToggleButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = isSelected() ? UITheme.PRIMARY : UITheme.WHITE;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(isSelected() ? UITheme.WHITE : UITheme.TEXT_SECONDARY);
                g2.setFont(UITheme.FONT_SMALL);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btn.setFont(UITheme.FONT_SMALL);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setPreferredSize(new Dimension(
            text.length() * 10 + 20, 32));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Render danh sách theo filter ─────────────────────────────
    private void renderList() {
        listPanel.removeAll();

        List<Notice> filtered = notices.stream()
            .filter(n -> {
                return switch (currentFilter) {
                    case "Chưa đọc" -> !n.read;
                    case "Tất cả"   -> true;
                    default         -> n.type.equals(currentFilter);
                };
            })
            .collect(Collectors.toList());

        // Thông báo ghim lên trước
        List<Notice> pinned  = filtered.stream().filter(n -> n.pinned).collect(Collectors.toList());
        List<Notice> regular = filtered.stream().filter(n -> !n.pinned).collect(Collectors.toList());

        if (!pinned.isEmpty()) {
            listPanel.add(sectionLabel("📌  Thông báo quan trọng"));
            listPanel.add(Box.createVerticalStrut(6));
            for (Notice n : pinned) {
                listPanel.add(noticeCard(n));
                listPanel.add(Box.createVerticalStrut(8));
            }
            listPanel.add(Box.createVerticalStrut(6));
        }

        if (!regular.isEmpty()) {
            listPanel.add(sectionLabel("🔔  Tất cả thông báo"));
            listPanel.add(Box.createVerticalStrut(6));
            for (Notice n : regular) {
                listPanel.add(noticeCard(n));
                listPanel.add(Box.createVerticalStrut(8));
            }
        }

        if (filtered.isEmpty()) {
            JLabel empty = new JLabel("Không có thông báo nào.", SwingConstants.CENTER);
            empty.setFont(UITheme.FONT_BODY);
            empty.setForeground(UITheme.TEXT_MUTED);
            empty.setAlignmentX(CENTER_ALIGNMENT);
            listPanel.add(Box.createVerticalStrut(40));
            listPanel.add(empty);
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    // ── Card một thông báo ────────────────────────────────────────
    private JPanel noticeCard(Notice n) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 9999));
        card.setBackground(n.read ? UITheme.WHITE : new Color(0xEFF6FF));
        card.setBorder(new CompoundBorder(
            new LineBorder(n.read ? UITheme.BORDER : new Color(0xBFDBFE), 1, true),
            new EmptyBorder(12, 14, 12, 14)
        ));

        // Icon type
        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(44, 44));

        JLabel lblIcon = new JLabel(n.icon, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        lblIcon.setPreferredSize(new Dimension(44, 44));
        lblIcon.setOpaque(true);
        lblIcon.setBackground(n.read ? UITheme.BG_SECONDARY : new Color(0xDBEAFE));
        lblIcon.setBorder(new EmptyBorder(8, 8, 8, 8));
        iconPanel.add(lblIcon, BorderLayout.CENTER);

        // Nội dung
        JLabel lblTitle = new JLabel(n.title);
        lblTitle.setFont(n.read ? UITheme.FONT_BODY : UITheme.FONT_BOLD);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblContent = new JLabel("<html><body style='width:500px;color:#6B7280'>"
            + n.content + "</body></html>");
        lblContent.setFont(UITheme.FONT_SMALL);

        JLabel lblMeta = new JLabel(n.icon + "  " + n.type + "  ·  " + n.time);
        lblMeta.setFont(UITheme.FONT_TINY);
        lblMeta.setForeground(UITheme.TEXT_MUTED);

        JPanel textPanel = new JPanel(new GridLayout(3, 1, 0, 4));
        textPanel.setOpaque(false);
        textPanel.add(lblTitle);
        textPanel.add(lblContent);
        textPanel.add(lblMeta);

        // Nút đánh dấu đã đọc
        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.setOpaque(false);
        btnPanel.setPreferredSize(new Dimension(110, 40));

        if (!n.read) {
            JButton btnRead = UITheme.outlineBtn("✓ Đã đọc");
            btnRead.setPreferredSize(new Dimension(100, 32));
            btnRead.addActionListener(e -> {
                n.read = true;
                renderList();
                updateUnreadBadge();
            });
            btnPanel.add(btnRead, BorderLayout.NORTH);
        } else {
            JLabel lblRead = new JLabel("✓ Đã đọc");
            lblRead.setFont(UITheme.FONT_TINY);
            lblRead.setForeground(UITheme.TEXT_MUTED);
            lblRead.setHorizontalAlignment(SwingConstants.RIGHT);
            btnPanel.add(lblRead, BorderLayout.NORTH);
        }

        card.add(iconPanel,  BorderLayout.WEST);
        card.add(textPanel,  BorderLayout.CENTER);
        card.add(btnPanel,   BorderLayout.EAST);
        return card;
    }

    /** Đánh dấu tất cả đã đọc */
    private void markAllRead() {
        notices.forEach(n -> n.read = true);
        renderList();
        updateUnreadBadge();
    }

    /** Cập nhật badge số chưa đọc */
    private void updateUnreadBadge() {
        long unread = notices.stream().filter(n -> !n.read).count();
        lblUnread.setText(unread + " chưa đọc");
        lblUnread.setBackground(unread > 0 ? UITheme.DANGER_BG : UITheme.BG_SECONDARY);
        lblUnread.setForeground(unread > 0 ? UITheme.DANGER    : UITheme.TEXT_SECONDARY);
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_LABEL);
        l.setForeground(UITheme.TEXT_SECONDARY);
        l.setAlignmentX(LEFT_ALIGNMENT);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        return l;
    }
}
