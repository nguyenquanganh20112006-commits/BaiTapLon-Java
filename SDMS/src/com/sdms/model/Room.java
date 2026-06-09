package com.sdms.model;

public class Room {
    public enum Status { AVAILABLE, NEARLY_FULL, FULL, MAINTENANCE }

    private String id, name, type;
    private int floor, capacity, occupied;
    private Status status;

    public Room(String id, String name, String type, int floor, int capacity, int occupied) {
        this.id=id; this.name=name; this.type=type;
        this.floor=floor; this.capacity=capacity; this.occupied=occupied;
        refresh();
    }

    private void refresh() {
        if (occupied >= capacity)        status = Status.FULL;
        else if (occupied >= capacity/2) status = Status.NEARLY_FULL;
        else                             status = Status.AVAILABLE;
    }

    public String getId()          { return id; }
    public String getName()        { return name; }
    public String getType()        { return type; }
    public int    getFloor()       { return floor; }
    public int    getCapacity()    { return capacity; }
    public int    getOccupied()    { return occupied; }
    public Status getStatus()      { return status; }

    public String getStatusText() {
        return switch (status) {
            case AVAILABLE    -> "Còn chỗ";
            case NEARLY_FULL  -> "Gần đầy";
            case FULL         -> "Đã đầy";
            case MAINTENANCE  -> "Bảo trì";
        };
    }

    public void setOccupied(int v) { this.occupied = v; refresh(); }

    public Object[] toRow() {
        return new Object[]{id, name, type, "Tầng "+floor, capacity, occupied, getStatusText()};
    }
}
