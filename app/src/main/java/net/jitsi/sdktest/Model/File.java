package net.jitsi.sdktest.Model;

public class File {
    private String name;
    private String urlFile;

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
