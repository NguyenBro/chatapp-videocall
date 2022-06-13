package net.jitsi.sdktest.Model;

//Dùng để lưu tin nhắn cuối cùng , với thời gian được lưu
public class ChatList {
    public String id;       //Id
    public String time;     //Thời gian tin nhắn

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
