package com.sdms.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Model đại diện cho một thông báo hệ thống.
 * Thông báo có thể gửi đến toàn bộ sinh viên, một phòng cụ thể,
 * hoặc một sinh viên cụ thể.
 */
public class Notification {

    // ── Loại thông báo ────────────────────────────────────────────
    public enum Type {
        INVOICE,    // Thông báo hóa đơn
        CONTRACT,   // Thông báo hợp đồng
        VIOLATION,  // Thông báo vi phạm
        INSPECTION, // Thông báo kiểm tra phòng
        GENERAL,    // Thông báo chung
        URGENT      // Thông báo khẩn
    }

    // ── Phạm vi gửi ───────────────────────────────────────────────
    public enum Target {
        ALL,      // Toàn bộ sinh viên
        ROOM,     // Một phòng cụ thể
        STUDENT   // Một sinh viên cụ thể
    }

    private static final DateTimeFormatter FMT      = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── Các trường dữ liệu ────────────────────────────────────────
    private String        id;          // Mã thông báo, VD: "TB0001"
    private String        title;       // Tiêu đề
    private String        content;     // Nội dung chi tiết
    private Type          type;        // Loại thông báo
    private Target        target;      // Phạm vi gửi
    private String        targetId;    // ID phòng hoặc sinh viên (nếu target != ALL)
    private LocalDateTime createdAt;   // Thời điểm tạo
    private String        createdBy;   // Người tạo (tên admin)
    private boolean       read;        // Đã đọc chưa (dùng cho giao diện sinh viên)
    private boolean       pinned;      // Ghim thông báo quan trọng

    /**
     * Constructor đầy đủ.
     */
    public Notification(String id, String title, String content,
                        Type type, Target target, String targetId,
                        LocalDateTime createdAt, String createdBy,
                        boolean read, boolean pinned) {
        this.id        = id;
        this.title     = title;
        this.content   = content;
        this.type      = type;
        this.target    = target;
        this.targetId  = targetId;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.read      = read;
        this.pinned    = pinned;
    }

    /**
     * Constructor nhanh — tạo thông báo chung gửi tất cả.
     */
    public Notification(String id, String title, String content, Type type) {
        this(id, title, content, type, Target.ALL, "",
             LocalDateTime.now(), "Quản trị viên", false, false);
    }

    // ── Lấy loại dạng chuỗi tiếng Việt ──────────────────────────
    public String getTypeText() {
        return switch (type) {
            case INVOICE    -> "Hóa đơn";
            case CONTRACT   -> "Hợp đồng";
            case VIOLATION  -> "Vi phạm";
            case INSPECTION -> "Kiểm tra";
            case GENERAL    -> "Chung";
            case URGENT     -> "Khẩn cấp";
        };
    }

    // ── Lấy phạm vi dạng chuỗi tiếng Việt ───────────────────────
    public String getTargetText() {
        return switch (target) {
            case ALL     -> "Tất cả";
            case ROOM    -> "Phòng " + targetId;
            case STUDENT -> "SV " + targetId;
        };
    }

    // ── Icon tương ứng loại thông báo ────────────────────────────
    public String getTypeIcon() {
        return switch (type) {
            case INVOICE    -> "🧾";
            case CONTRACT   -> "📄";
            case VIOLATION  -> "⚠";
            case INSPECTION -> "🔍";
            case URGENT     -> "🔴";
            default         -> "🔵";
        };
    }

    // ── Thời gian tương đối (VD: "2 giờ trước") ─────────────────
    public String getRelativeTime() {
        if (createdAt == null) return "—";
        long minutes = java.time.temporal.ChronoUnit.MINUTES.between(createdAt, LocalDateTime.now());
        if (minutes < 1)   return "Vừa xong";
        if (minutes < 60)  return minutes + " phút trước";
        long hours = minutes / 60;
        if (hours < 24)    return hours + " giờ trước";
        long days = hours / 24;
        if (days < 7)      return days + " ngày trước";
        return createdAt.format(FMT_DATE);
    }

    // ── Lấy thời gian dạng chuỗi đầy đủ ─────────────────────────
    public String getCreatedAtStr() {
        return createdAt != null ? createdAt.format(FMT) : "—";
    }

    // ── Xuất hàng cho JTable (giao diện Admin) ───────────────────
    /**
     * Thứ tự cột:
     * Mã | Tiêu đề | Loại | Phạm vi | Thời gian | Người tạo | Đã ghim
     */
    public Object[] toRow() {
        return new Object[]{
            id,
            title,
            getTypeText(),
            getTargetText(),
            getCreatedAtStr(),
            createdBy,
            pinned ? "📌 Ghim" : ""
        };
    }

    // ── Getters ───────────────────────────────────────────────────
    public String        getId()        { return id; }
    public String        getTitle()     { return title; }
    public String        getContent()   { return content; }
    public Type          getType()      { return type; }
    public Target        getTarget()    { return target; }
    public String        getTargetId()  { return targetId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String        getCreatedBy() { return createdBy; }
    public boolean       isRead()       { return read; }
    public boolean       isPinned()     { return pinned; }

    // ── Setters ───────────────────────────────────────────────────
    public void setTitle(String v)        { this.title     = v; }
    public void setContent(String v)      { this.content   = v; }
    public void setType(Type v)           { this.type      = v; }
    public void setTarget(Target v)       { this.target    = v; }
    public void setTargetId(String v)     { this.targetId  = v; }
    public void setCreatedBy(String v)    { this.createdBy = v; }
    public void setRead(boolean v)        { this.read      = v; }
    public void setPinned(boolean v)      { this.pinned    = v; }

    // ── Tiện ích tĩnh ─────────────────────────────────────────────

    /**
     * Sinh mã thông báo tiếp theo, VD: "TB0001" → "TB0002".
     */
    public static String nextId(String lastId) {
        try {
            int num = Integer.parseInt(lastId.replace("TB", "").trim());
            return String.format("TB%04d", num + 1);
        } catch (Exception e) {
            return "TB0001";
        }
    }
}
