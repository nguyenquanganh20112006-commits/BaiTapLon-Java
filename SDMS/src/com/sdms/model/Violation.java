package com.sdms.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Model đại diện cho một lần vi phạm nội quy của sinh viên.
 * Lưu thông tin người vi phạm, loại vi phạm, mức phạt và trạng thái xử lý.
 */
public class Violation {

    // ── Mức độ vi phạm ────────────────────────────────────────────
    public enum Severity {
        LOW,      // Nhẹ   — nhắc nhở
        MEDIUM,   // Trung bình — cảnh cáo + phạt tiền
        HIGH,     // Nặng  — đình chỉ tạm thời
        CRITICAL  // Rất nặng — chấm dứt hợp đồng
    }

    // ── Trạng thái xử lý ─────────────────────────────────────────
    public enum Status {
        PENDING,   // Chưa xử lý
        PROCESSED, // Đã xử lý
        APPEALING  // Đang khiếu nại
    }

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── Các trường dữ liệu ────────────────────────────────────────
    private String    id;           // Mã vi phạm, VD: "VP0001"
    private String    studentId;    // Mã sinh viên vi phạm
    private String    studentName;  // Tên sinh viên (cache hiển thị)
    private String    roomId;       // Phòng xảy ra vi phạm
    private LocalDate date;         // Ngày vi phạm
    private String    type;         // Loại vi phạm (VD: "Gây tiếng ồn")
    private String    description;  // Mô tả chi tiết
    private Severity  severity;     // Mức độ
    private long      fine;         // Tiền phạt (đồng), 0 nếu không phạt tiền
    private String    handledBy;    // Tên người xử lý (admin)
    private Status    status;       // Trạng thái xử lý
    private String    note;         // Ghi chú thêm

    /**
     * Constructor đầy đủ.
     */
    public Violation(String id, String studentId, String studentName,
                     String roomId, LocalDate date, String type,
                     String description, Severity severity, long fine,
                     String handledBy, Status status, String note) {
        this.id          = id;
        this.studentId   = studentId;
        this.studentName = studentName;
        this.roomId      = roomId;
        this.date        = date;
        this.type        = type;
        this.description = description;
        this.severity    = severity;
        this.fine        = fine;
        this.handledBy   = handledBy;
        this.status      = status;
        this.note        = note;
    }

    // ── Lấy mức độ dạng chuỗi tiếng Việt ────────────────────────
    public String getSeverityText() {
        return switch (severity) {
            case LOW      -> "Nhẹ";
            case MEDIUM   -> "Trung bình";
            case HIGH     -> "Nặng";
            case CRITICAL -> "Rất nặng";
        };
    }

    // ── Lấy trạng thái dạng chuỗi tiếng Việt ────────────────────
    public String getStatusText() {
        return switch (status) {
            case PENDING   -> "Chưa xử lý";
            case PROCESSED -> "Đã xử lý";
            case APPEALING -> "Đang khiếu nại";
        };
    }

    // ── Lấy ngày dạng chuỗi dd/MM/yyyy ───────────────────────────
    public String getDateStr() {
        return date != null ? date.format(FMT) : "—";
    }

    // ── Xuất hàng cho JTable ─────────────────────────────────────
    /**
     * Thứ tự cột:
     * Mã VP | Mã SV | Tên SV | Phòng | Ngày | Loại VP | Mức độ | Tiền phạt | Trạng thái
     */
    public Object[] toRow() {
        return new Object[]{
            id,
            studentId,
            studentName,
            roomId,
            getDateStr(),
            type,
            getSeverityText(),
            fine > 0 ? String.format("%,d đ", fine) : "Không",
            getStatusText()
        };
    }

    // ── Getters ───────────────────────────────────────────────────
    public String    getId()          { return id; }
    public String    getStudentId()   { return studentId; }
    public String    getStudentName() { return studentName; }
    public String    getRoomId()      { return roomId; }
    public LocalDate getDate()        { return date; }
    public String    getType()        { return type; }
    public String    getDescription() { return description; }
    public Severity  getSeverity()    { return severity; }
    public long      getFine()        { return fine; }
    public String    getHandledBy()   { return handledBy; }
    public Status    getStatus()      { return status; }
    public String    getNote()        { return note; }

    // ── Setters ───────────────────────────────────────────────────
    public void setStudentId(String v)   { this.studentId   = v; }
    public void setStudentName(String v) { this.studentName = v; }
    public void setRoomId(String v)      { this.roomId      = v; }
    public void setDate(LocalDate v)     { this.date        = v; }
    public void setType(String v)        { this.type        = v; }
    public void setDescription(String v) { this.description = v; }
    public void setSeverity(Severity v)  { this.severity    = v; }
    public void setFine(long v)          { this.fine        = v; }
    public void setHandledBy(String v)   { this.handledBy   = v; }
    public void setStatus(Status v)      { this.status      = v; }
    public void setNote(String v)        { this.note        = v; }

    // ── Tiện ích tĩnh ─────────────────────────────────────────────

    /** Các loại vi phạm phổ biến để đưa vào JComboBox */
    public static final String[] VIOLATION_TYPES = {
        "Gây tiếng ồn",
        "Hút thuốc trong phòng",
        "Mang khách lạ vào khu nội trú",
        "Vi phạm giờ giấc",
        "Gây mất trật tự",
        "Phá hỏng tài sản",
        "Sử dụng thiết bị điện công suất lớn",
        "Vi phạm vệ sinh chung",
        "Tụ tập gây rối",
        "Vi phạm khác"
    };

    /**
     * Sinh mã vi phạm tiếp theo, VD: "VP0001" → "VP0002".
     */
    public static String nextId(String lastId) {
        try {
            int num = Integer.parseInt(lastId.replace("VP", "").trim());
            return String.format("VP%04d", num + 1);
        } catch (Exception e) {
            return "VP0001";
        }
    }

    /**
     * Parse ngày từ chuỗi dd/MM/yyyy.
     * @return LocalDate hoặc null nếu không hợp lệ
     */
    public static LocalDate parseDate(String str) {
        try {
            return LocalDate.parse(str.trim(), FMT);
        } catch (Exception e) {
            return null;
        }
    }
}
