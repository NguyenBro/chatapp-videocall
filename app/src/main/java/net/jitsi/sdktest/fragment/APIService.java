package net.jitsi.sdktest.fragment;


import net.jitsi.sdktest.Notifications.MyResponse;
import net.jitsi.sdktest.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
//chưa keyServer của firebase
public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAARV1jM1c:APA91bF4I1-EUDCdh4AGDaRAML4wgH0t8GsXeNHC5FuNpF2hTRS-KcTlRoeCVkS2yye4WFGNk8U9k23ool4LaLpJkqPXjk21Ue4PFTXb4OKrA5TB9PwYUmAXEdIdT3nJPOVy8kuJU8da"
            }

    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
