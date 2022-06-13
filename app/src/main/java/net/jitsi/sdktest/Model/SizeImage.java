package net.jitsi.sdktest.Model;
//Lưu kích thước hình ảnh
public class SizeImage {
    private int width;
    private int height;

    public SizeImage(){

    }

    public SizeImage(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
