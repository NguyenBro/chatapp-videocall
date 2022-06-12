package net.jitsi.sdktest.Model;

//Chứa Id của người gọi
public class Caller {
    private String caller; //ID Người gọi

    public Caller() {
    }

    public Caller(String caller) {
        this.caller = caller;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }
}
