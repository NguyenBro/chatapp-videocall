package net.jitsi.sdktest.Model;

public class Meeting {
    private String roomID;
    private String time;
    private String status;
    private boolean isNow;

    public Meeting(String roomID, String time, String status, boolean isNow) {
        this.roomID = roomID;
        this.time = time;
        this.status = status;
        this.isNow = isNow;
    }

    public Meeting() {
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isNow() {
        return isNow;
    }

    public void setNow(boolean now) {
        isNow = now;
    }
}
