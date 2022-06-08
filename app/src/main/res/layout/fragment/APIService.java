package hcmute.nhom1.chatapp.fragment;

import hcmute.nhom1.chatapp.Notifications.MyResponse;
import hcmute.nhom1.chatapp.Notifications.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAQ4huTYM:APA91bF1OaeL8ngFxrJrVFK_L5LaQWHKS3YZOIVvG96y67zB_PI3Gf3I6G5rkeo1Gx-HeFVIGbRngfepBt1_puZ9cpQJ9HOovKKbaU6_uU1GSvNQBsi4dFveSlM2EWRTM4kQZHvPCIAr"
            }

    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
