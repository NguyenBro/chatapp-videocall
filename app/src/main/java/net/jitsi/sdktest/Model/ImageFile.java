package net.jitsi.sdktest.Model;

public class ImageFile {
    private String imageUrl;        //Link Uri hình ảnh

    public ImageFile() {
    }

    public ImageFile(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
