package com.sdms.model;

public class Invoice {
    private String id, studentId, studentName, roomId, month;
    private long roomFee, electricFee, waterFee;
    private boolean paid;

    public Invoice(String id, String studentId, String studentName, String roomId,
                   String month, long roomFee, long electricFee, long waterFee, boolean paid) {
        this.id=id; this.studentId=studentId; this.studentName=studentName;
        this.roomId=roomId; this.month=month;
        this.roomFee=roomFee; this.electricFee=electricFee; this.waterFee=waterFee;
        this.paid=paid;
    }

    public String getId()           { return id; }
    public String getStudentId()    { return studentId; }
    public String getStudentName()  { return studentName; }
    public String getRoomId()       { return roomId; }
    public String getMonth()        { return month; }
    public long   getRoomFee()      { return roomFee; }
    public long   getElectricFee()  { return electricFee; }
    public long   getWaterFee()     { return waterFee; }
    public long   getTotal()        { return roomFee + electricFee + waterFee; }
    public boolean isPaid()         { return paid; }
    public void   setPaid(boolean v){ this.paid = v; }

    public Object[] toRow() {
        return new Object[]{id, studentName, roomId, month,
            String.format("%,d đ", roomFee),
            String.format("%,d đ", electricFee),
            String.format("%,d đ", waterFee),
            String.format("%,d đ", getTotal()),
            paid ? "Đã thanh toán" : "Chưa thanh toán"};
    }
}
