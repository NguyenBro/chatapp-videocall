package net.jitsi.sdktest.Notifications;

//Thông tin thông báo được gửi
public class Data {
    private  String user;       //Id người nhận thông báo
    private int icon;           //Iocn cho thông báo
    private String body;           //Nội dung thông báo
    private String title;       //Tiêu đề của thông báo
    private String sented;      //Người đã gửi tin nhắn

    public Data(String user, int icon, String body, String title, String sented) {
        this.user = user;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.sented = sented;
    }

    public Data() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSented() {
        return sented;
    }

    public void setSented(String sented) {
        this.sented = sented;
    }
}
