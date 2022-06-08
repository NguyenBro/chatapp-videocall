package net.jitsi.sdktest.Notifications;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import net.jitsi.sdktest.R;
import net.jitsi.sdktest.UI.MessageActivity;


public class MyFirebaseIdService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
//        String refreshToken = FirebaseMessaging.getInstance().getToken().getResult();
//        if (refreshToken != null) {
//            updateToken(refreshToken);
//        }
        updateToken(s);
    }

    private void updateToken(String refreshToken) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            DatabaseReference reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Tokens");
            Token token = new Token(refreshToken);
            reference.child(firebaseUser.getUid()).setValue(token);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        String sented = message.getData().get("sented");
        String user = message.getData().get("user");

        SharedPreferences preferences =getSharedPreferences("PREFS",MODE_PRIVATE);
        String currentUser = preferences.getString("currentuser","none");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null && sented.equals(firebaseUser.getUid())){
            MediaPlayer music = MediaPlayer.create(getApplicationContext(), R.raw.music);
            music.start();
            if(!currentUser.equals(user)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Log.d("CCC", "oreo");
                    sendOreoNotification(message);
                } else {
                    Log.d("CCC", "no oreo");
                    sendNotification(message);
                }
            }
        }
    }

    private void sendOreoNotification(RemoteMessage message){
        String user= message.getData().get("user");
        String icon = message.getData().get("icon");
        String title= message.getData().get("title");
        String body = message.getData().get("body");

        RemoteMessage.Notification notification = message.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent =new Intent(this, MessageActivity.class);
        Bundle bundle =new Bundle();
        bundle.putString("userid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent =PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification =new OreoNotification(this);
        Notification.Builder builder =oreoNotification.getOreoNotification(title,body,pendingIntent,defaultSound,icon);

        int i=0;
        if(j>0){
            i=j;
        }

        oreoNotification.getManager().notify(i,builder.build());
    }

    private void sendNotification(RemoteMessage message){
        String user= message.getData().get("user");
        String icon = message.getData().get("icon");
        String title= message.getData().get("title");
        String body = message.getData().get("body");

        RemoteMessage.Notification notification = message.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent =new Intent(this, MessageActivity.class);
        Bundle bundle =new Bundle();
        bundle.putString("userid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent =PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int i=0;
        if(j>0){
            i=j;
        }

        noti.notify(i,builder.build());

    }
}
