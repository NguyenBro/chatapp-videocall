package net.jitsi.sdktest.Model;

//Lưu thông tin User
public class User {
    private String id;          //Id audto do FireBase tạo
    private String username;    //Tên
    private String email;       //Email tạo
    private String phone;       //Số ddiejn thoại
    private String imageURL;       //Link Uri hình ảnh Avatar
    private String birthDate;       //Ngày Sinh
    private String gender;      //Giới tính
    private String home;        //Địa chỉ
    private String local;       //Sống tại
    private String status;      //Trạng thái hoạt động
    private String search;      //Viết thường username để search
    private String isMeet;      //Trạng thái tham gia cuộc họp

    public User() {
    }

    public User(String id, String username, String email, String phone, String imageURL, String birthDate, String gender, String home, String local, String status, String search,String meet) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.imageURL = imageURL;
        this.birthDate = birthDate;
        this.gender = gender;
        this.home = home;
        this.local = local;
        this.status = status;
        this.search = search;
        this.isMeet = meet;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIsMeet() {
        return isMeet;
    }

    public void setIsMeet(String isMeet) {
        this.isMeet = isMeet;
    }
}
