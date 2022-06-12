package net.jitsi.sdktest.Model;

import java.io.Serializable;
//Các biến để xử lý các sự kiện từ tin nhắn Chat
public class Chat implements Serializable {
    private String id;      //ID tin nhắn, được tạo ngẫu nhiên từ Firebase
    private String sender;  //Người gửi
    private  String receiver;       //Người nhận
    private String message;         //Nội dung tin nhắn text
    private String link;            //Link Uri nếu có
    private boolean isseen;         //Tin nhắn đã được xem hay chưa xem
    private String type;            //Loại tệp đính kèm : Hình ảnh, video, audio, file document

    public Chat(String sender, String receiver, String message,boolean isseen,String image,String type) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isseen = isseen;
        this.link = image;
        this.type = type;

    }

    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }


    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
