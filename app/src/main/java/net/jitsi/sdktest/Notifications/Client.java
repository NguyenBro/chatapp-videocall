package net.jitsi.sdktest.Notifications;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
//Client nơi nhận thông báo
//Xử dụng thư viện Retrofit
public class Client {
    private static Retrofit retrofit =null;
    public static Retrofit getClient(String uri){
        //Các bước kiểm tra Uri
        if(retrofit==null){
            retrofit =new Retrofit.Builder()
                    .baseUrl(uri)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
