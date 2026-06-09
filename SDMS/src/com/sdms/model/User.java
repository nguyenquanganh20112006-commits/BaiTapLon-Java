package com.sdms.model;

public class User {
    public enum Role { ADMIN, STUDENT }
    private final String username, password, fullName, studentId;
    private final Role role;

    public User(String username, String password, Role role, String fullName, String studentId) {
        this.username = username; this.password = password;
        this.role = role; this.fullName = fullName; this.studentId = studentId;
    }
    public String getUsername()  { return username; }
    public String getPassword()  { return password; }
    public Role   getRole()      { return role; }
    public String getFullName()  { return fullName; }
    public String getStudentId() { return studentId; }

    public static User authenticate(String u, String p) {
        if ("admin".equals(u) && "admin123".equals(p))
            return new User("admin","admin123",Role.ADMIN,"Quản trị viên",null);
        if ("sv001".equals(u) && "sv001".equals(p))
            return new User("sv001","sv001",Role.STUDENT,"Nguyễn Thị Lan Anh","SV001249");
        return null;
    }
}
