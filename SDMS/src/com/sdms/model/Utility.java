package com.sdms.model;

/**
 * Model đại diện cho chỉ số điện nước hàng tháng của một phòng.
 * Mỗi bản ghi tương ứng với một phòng trong một tháng cụ thể.
 */
public class Utility {

    // ── Đơn giá mặc định (có thể chỉnh trong SettingsPanel) ──────
    public static final long DEFAULT_ELECTRIC_UNIT_PRICE = 2_000L; // đồng/kWh
    public static final long DEFAULT_WATER_UNIT_PRICE    = 6_000L; // đồng/m³

    // ── Các trường dữ liệu ────────────────────────────────────────
    private String id;            // Mã bản ghi, VD: "UT0001"
    private String roomId;        // Mã phòng
    private String month;         // Tháng ghi nhận, VD: "06/2026"

    // Chỉ số điện (kWh)
    private double electricPrev;  // Chỉ số điện đầu kỳ
    private double electricCurr;  // Chỉ số điện cuối kỳ

    // Chỉ số nước (m³)
    private double waterPrev;     // Chỉ số nước đầu kỳ
    private double waterCurr;     // Chỉ số nước cuối kỳ

    // Đơn giá riêng (có thể khác mặc định theo từng kỳ)
    private long electricUnitPrice; // đồng/kWh
    private long waterUnitPrice;    // đồng/m³

    private String note;           // Ghi chú (VD: "Kiểm tra lại đồng hồ")
    private boolean confirmed;     // Đã xác nhận chốt chỉ số chưa

    /**
     * Constructor đầy đủ.
     */
    public Utility(String id, String roomId, String month,
                   double electricPrev, double electricCurr,
                   double waterPrev,    double waterCurr,
                   long electricUnitPrice, long waterUnitPrice,
                   String note, boolean confirmed) {
        this.id                 = id;
        this.roomId             = roomId;
        this.month              = month;
        this.electricPrev       = electricPrev;
        this.electricCurr       = electricCurr;
        this.waterPrev          = waterPrev;
        this.waterCurr          = waterCurr;
        this.electricUnitPrice  = electricUnitPrice;
        this.waterUnitPrice     = waterUnitPrice;
        this.note               = note;
        this.confirmed          = confirmed;
    }

    /**
     * Constructor đơn giản — dùng đơn giá mặc định.
     */
    public Utility(String id, String roomId, String month,
                   double electricPrev, double electricCurr,
                   double waterPrev,    double waterCurr) {
        this(id, roomId, month,
             electricPrev, electricCurr,
             waterPrev,    waterCurr,
             DEFAULT_ELECTRIC_UNIT_PRICE,
             DEFAULT_WATER_UNIT_PRICE,
             "", false);
    }

    // ── Tính lượng tiêu thụ ───────────────────────────────────────

    /** Số kWh điện tiêu thụ trong kỳ */
    public double getElectricUsage() {
        return Math.max(0, electricCurr - electricPrev);
    }

    /** Số m³ nước tiêu thụ trong kỳ */
    public double getWaterUsage() {
        return Math.max(0, waterCurr - waterPrev);
    }

    // ── Tính tiền ────────────────────────────────────────────────

    /** Tiền điện = lượng tiêu thụ × đơn giá điện */
    public long getElectricFee() {
        return Math.round(getElectricUsage() * electricUnitPrice);
    }

    /** Tiền nước = lượng tiêu thụ × đơn giá nước */
    public long getWaterFee() {
        return Math.round(getWaterUsage() * waterUnitPrice);
    }

    /** Tổng tiền điện + nước */
    public long getTotalFee() {
        return getElectricFee() + getWaterFee();
    }

    // ── Xuất hàng cho JTable ─────────────────────────────────────
    /**
     * Thứ tự cột:
     * Mã | Phòng | Tháng | Điện cũ | Điện mới | Dùng (kWh) | Tiền điện
     *                    | Nước cũ | Nước mới | Dùng (m³)  | Tiền nước | Tổng | Xác nhận
     */
    public Object[] toRow() {
        return new Object[]{
            id,
            roomId,
            month,
            String.format("%.1f", electricPrev),
            String.format("%.1f", electricCurr),
            String.format("%.1f", getElectricUsage()),
            String.format("%,d đ", getElectricFee()),
            String.format("%.1f", waterPrev),
            String.format("%.1f", waterCurr),
            String.format("%.1f", getWaterUsage()),
            String.format("%,d đ", getWaterFee()),
            String.format("%,d đ", getTotalFee()),
            confirmed ? "✓ Đã chốt" : "⏳ Chưa chốt"
        };
    }

    // ── Getters ───────────────────────────────────────────────────
    public String  getId()                { return id; }
    public String  getRoomId()            { return roomId; }
    public String  getMonth()             { return month; }
    public double  getElectricPrev()      { return electricPrev; }
    public double  getElectricCurr()      { return electricCurr; }
    public double  getWaterPrev()         { return waterPrev; }
    public double  getWaterCurr()         { return waterCurr; }
    public long    getElectricUnitPrice() { return electricUnitPrice; }
    public long    getWaterUnitPrice()    { return waterUnitPrice; }
    public String  getNote()              { return note; }
    public boolean isConfirmed()          { return confirmed; }

    // ── Setters ───────────────────────────────────────────────────
    public void setRoomId(String v)            { this.roomId            = v; }
    public void setMonth(String v)             { this.month             = v; }
    public void setElectricPrev(double v)      { this.electricPrev      = v; }
    public void setElectricCurr(double v)      { this.electricCurr      = v; }
    public void setWaterPrev(double v)         { this.waterPrev         = v; }
    public void setWaterCurr(double v)         { this.waterCurr         = v; }
    public void setElectricUnitPrice(long v)   { this.electricUnitPrice = v; }
    public void setWaterUnitPrice(long v)      { this.waterUnitPrice    = v; }
    public void setNote(String v)              { this.note              = v; }
    public void setConfirmed(boolean v)        { this.confirmed         = v; }

    // ── Tiện ích tĩnh ─────────────────────────────────────────────

    /**
     * Sinh mã bản ghi tiếp theo, VD: "UT0001" → "UT0002".
     */
    public static String nextId(String lastId) {
        try {
            int num = Integer.parseInt(lastId.replace("UT", "").trim());
            return String.format("UT%04d", num + 1);
        } catch (Exception e) {
            return "UT0001";
        }
    }
}
