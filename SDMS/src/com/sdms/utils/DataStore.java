package com.sdms.utils;

import com.sdms.model.*;
import java.util.*;



public class DataStore {
    private static final List<Student>        students        = new ArrayList<>();
    private static final List<Room>           rooms           = new ArrayList<>();
    private static final List<Invoice>        invoices        = new ArrayList<>();
    private static final List<PendingAccount> pendingAccounts = new ArrayList<>();

    /** Tài khoản sinh viên đang chờ duyệt (từ form đăng ký) */
    public static class PendingAccount {
        public enum Status { PENDING, APPROVED, REJECTED }

        private final String id;
        private final String username;   // mã SV / tên đăng nhập
        private final String fullName;
        private final String phone;
        private final String dob;
        private final String cccd;
        private final String gender;
        private final String registeredAt;
        private Status status;
        private String note;

        public PendingAccount(String id, String username, String fullName,
                              String phone, String dob, String cccd,
                              String gender, String registeredAt) {
            this.id           = id;
            this.username     = username;
            this.fullName     = fullName;
            this.phone        = phone;
            this.dob          = dob;
            this.cccd         = cccd;
            this.gender       = gender;
            this.registeredAt = registeredAt;
            this.status       = Status.PENDING;
            this.note         = "";
        }

        public String getId()            { return id; }
        public String getUsername()      { return username; }
        public String getFullName()      { return fullName; }
        public String getPhone()         { return phone; }
        public String getDob()           { return dob; }
        public String getCccd()          { return cccd; }
        public String getGender()        { return gender; }
        public String getRegisteredAt()  { return registeredAt; }
        public Status getStatus()        { return status; }
        public String getNote()          { return note; }
        public void setStatus(Status s)  { this.status = s; }
        public void setNote(String n)    { this.note   = n; }

        public String getStatusText() {
            return switch (status) {
                case PENDING  -> "Chờ duyệt";
                case APPROVED -> "Đã duyệt";
                case REJECTED -> "Từ chối";
            };
        }

        public Object[] toRow() {
            return new Object[]{ id, username, fullName, gender, dob, phone, cccd, registeredAt, getStatusText() };
        }
    }

    static {
        // Students
        students.add(new Student("SV001248","Nguyễn Văn An",     "01/05/2002","Nam","012345678901","0912345678","an.nv@email.com",      "ĐHBK Hà Nội","CNTT",        "20221001","Hà Nội",       "A301","Đang ở"));
        students.add(new Student("SV001247","Trần Thị Bình",     "12/03/2003","Nữ", "012345678902","0923456789","binh.tt@email.com",    "ĐH Kinh tế", "Kế toán",     "20221002","Nam Định",     "B204","Đang ở"));
        students.add(new Student("SV001246","Lê Quang Minh",     "22/07/2002","Nam","012345678903","0934567890","minh.lq@email.com",    "ĐHQGHN",     "Vật lý",      "20221003","Hải Phòng",   "C112","Mới đăng ký"));
        students.add(new Student("SV001245","Phạm Thu Hà",       "08/11/2003","Nữ", "012345678904","0945678901","ha.pt@email.com",      "HV Tài chính","Tài chính",  "20221004","Thái Bình",   "",    "Chờ duyệt"));
        students.add(new Student("SV001244","Hoàng Văn Nam",     "15/06/2002","Nam","012345678905","0956789012","nam.hv@email.com",     "ĐH Kinh tế", "Quản trị KD", "20221005","Bắc Ninh",    "D405","Đã rời"));
        students.add(new Student("SV001243","Vũ Thị Lan",        "30/09/2003","Nữ", "012345678906","0967890123","lan.vt@email.com",     "ĐHBK Hà Nội","Cơ khí",      "20221006","Hưng Yên",    "A204","Đang ở"));
        students.add(new Student("SV001249","Nguyễn Thị Lan Anh","15/03/2003","Nữ", "012345678907","0912345670","lananh@email.com",     "ĐHBK Hà Nội","CNTT",        "20221234","Hà Nội",       "A301","Đang ở"));
        students.add(new Student("SV001242","Đặng Minh Tuấn",    "04/04/2002","Nam","012345678908","0978901234","tuan.dm@email.com",    "ĐHQGHN",     "Toán học",    "20221007","Hà Nam",       "B102","Đang ở"));
        students.add(new Student("SV001241","Bùi Thị Mai",       "18/08/2003","Nữ", "012345678909","0989012345","mai.bt@email.com",     "HV Tài chính","Ngân hàng",   "20221008","Ninh Bình",   "C305","Đang ở"));
        students.add(new Student("SV001240","Ngô Quốc Hùng",     "25/01/2002","Nam","012345678910","0990123456","hung.nq@email.com",    "ĐHBK Hà Nội","Điện tử",     "20221009","Vĩnh Phúc",   "D201","Mới đăng ký"));

        // Rooms (4 floors x 8 rooms)
        int[][] data = {
            {8,8},{8,4},{4,0},{4,3},{8,8},{4,2},{4,4},{8,5},
            {8,5},{8,3},{4,4},{4,2},{8,7},{4,0},{4,4},{8,6},
            {8,8},{8,8},{4,2},{4,4},{8,5},{4,3},{4,1},{8,8},
            {8,8},{8,6},{4,4},{4,2},{8,5},{4,0},{4,4},{8,7}
        };
        String[] floors = {"1","2","3","4"};
        for (int f = 0; f < 4; f++) {
            for (int r = 0; r < 8; r++) {
                int idx = f * 8 + r;
                String rid = "A" + floors[f] + "0" + (r+1);
                rooms.add(new Room(rid, "Phòng "+rid, data[idx][0]+" người", f+1, data[idx][0], data[idx][1]));
            }
        }

        // Invoices
        invoices.add(new Invoice("HD001","SV001248","Nguyễn Văn An",    "A301","06/2026",850000,76000,24000,false));
        invoices.add(new Invoice("HD002","SV001247","Trần Thị Bình",    "B204","06/2026",850000,62000,18000,false));
        invoices.add(new Invoice("HD003","SV001243","Vũ Thị Lan",       "A204","06/2026",850000,55000,20000,true));
        invoices.add(new Invoice("HD004","SV001242","Đặng Minh Tuấn",   "B102","06/2026",850000,80000,28000,false));
        invoices.add(new Invoice("HD005","SV001241","Bùi Thị Mai",      "C305","06/2026",850000,70000,22000,true));
        invoices.add(new Invoice("HD006","SV001249","Nguyễn Thị Lan Anh","A301","06/2026",850000,76000,24000,false));

        // Pending accounts (đơn đăng ký chờ duyệt)
        pendingAccounts.add(new PendingAccount("PA001","22IT1001","Nguyễn Minh Đức",   "0911222333","15/04/2004","035123456701","Nam","08/06/2026 09:15"));
        pendingAccounts.add(new PendingAccount("PA002","22KT2002","Trần Thị Thu Hương","0922333444","22/08/2004","035123456702","Nữ", "08/06/2026 10:30"));
        pendingAccounts.add(new PendingAccount("PA003","21VL3003","Lê Hoàng Phúc",     "0933444555","05/01/2003","035123456703","Nam","08/06/2026 11:00"));
        pendingAccounts.add(new PendingAccount("PA004","23TC4004","Phạm Ngọc Linh",    "0944555666","30/12/2004","035123456704","Nữ", "07/06/2026 14:20"));
        pendingAccounts.add(new PendingAccount("PA005","22DT5005","Hoàng Văn Thắng",   "0955666777","12/07/2003","035123456705","Nam","07/06/2026 16:45"));
    }

    public static List<Student> getStudents()         { return students; }
    public static List<Room>    getRooms()            { return rooms; }
    public static List<Invoice> getInvoices()         { return invoices; }
    public static List<PendingAccount> getPendingAccounts() { return pendingAccounts; }
    public static void addPendingAccount(PendingAccount a) { if (a != null) pendingAccounts.add(a); }
    public static long pendingCount() { return pendingAccounts.stream().filter(a -> a.getStatus()== PendingAccount.Status.PENDING).count(); }

    public static int   totalStudents()   { return students.size(); }
    public static int   totalRooms()      { return rooms.size(); }
    public static long  emptyRooms()      { return rooms.stream().filter(r -> r.getStatus()==Room.Status.AVAILABLE).count(); }
    public static long  activeStudents()  { return students.stream().filter(s -> "Đang ở".equals(s.getStatus())).count(); }
    public static long  monthRevenue()    { return invoices.stream().filter(Invoice::isPaid).mapToLong(Invoice::getTotal).sum(); }

    public static String nextStudentId()  {
        int max = students.stream().mapToInt(s -> {
            try { return Integer.parseInt(s.getId().replace("SV00","").trim()); } catch(Exception e){return 0;}
        }).max().orElse(1248);
        return String.format("SV%06d", max+1);
    }

    // ========== THÊM 2 PHƯƠNG THỨC CÒN THIẾU ==========
    
    // Thêm sinh viên mới
    public static void addStudent(Student student) {
        if (student != null) {
            students.add(student);
        }
    }
    
    // Xóa sinh viên
    public static void removeStudent(Student student) {
        if (student != null) {
            students.remove(student);
        }
    }
}