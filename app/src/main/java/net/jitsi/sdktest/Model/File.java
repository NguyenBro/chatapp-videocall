package net.jitsi.sdktest.Model;

//Lưu file tài liệu
public class File {
    private String name;        //Tên của tập file
    private String urlFile;     //Link Uri từ Firebase

    public File(String name, String urlFile) {
        this.name = name;
        this.urlFile = urlFile;
    }

    public File(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlFile() {
        return urlFile;
    }

    public void setUrlFile(String urlFile) {
        this.urlFile = urlFile;
    }
}
