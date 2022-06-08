package net.jitsi.sdktest.Model;

public class ChatList {
    public String id;
    public String time;

    public ChatList(String id,String time) {
        this.id = id;
        this.time =time;
    }

    public ChatList() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
