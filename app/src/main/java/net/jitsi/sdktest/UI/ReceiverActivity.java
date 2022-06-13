package net.jitsi.sdktest.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import net.jitsi.sdktest.Model.Caller;
import net.jitsi.sdktest.Model.User;
import net.jitsi.sdktest.R;

import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.BroadcastIntentHelper;
import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

//Người nhận khi được gọi đến

public class ReceiverActivity extends AppCompatActivity {

    CircleImageView imageDecline,imageAccept,imageAvatar;       //Từ chối gọi, chấp nhận cuộc gọi và Avatar
    TextView txtName;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    String userid="";
    MediaPlayer music ;                 //Chạy nhạc chuông khi có người gọi
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onBroadcastReceived(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);
        imageAccept = findViewById(R.id.receiver_call);
        imageDecline = findViewById(R.id.receiver_decline);
        txtName = findViewById(R.id.textView24);
        imageAvatar  =findViewById(R.id.receiver_avatar);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        music = MediaPlayer.create(getApplicationContext(), R.raw.music_call);
        music.start();

        //Lấy user id của người gọi , và load các thông tin người gọi lên màn hình
        reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Calling").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Caller caller = snapshot.getValue(Caller.class);
                userid = caller.getCaller();  //Láy Userid
                if(!userid.equals("")) {
                    //Load thông tin lên
                    reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(userid);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                txtName.setText(user.getUsername());
                                if (user.getImageURL().equals("default")) {
                                    imageAvatar.setImageResource(R.drawable.image_avatar);
                                } else {
                                    Picasso.get().load(user.getImageURL()).into(imageAvatar);
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
       // ==========================================

        //==========================================
        //Kiểm tra trạng thái cuộc gọi
        reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user =snapshot.getValue(User.class);

                if(user.getIsMeet().equals("default")){
                    //Trajng thái không ai gọi
                    finish();
                    music.stop();
                }
                else if(user.getIsMeet().equals("calling")){
                    //Chấp nhập cuộc gọi
                    //Toast.makeText(ReceiverActivity.this, "Chấp nhận", Toast.LENGTH_SHORT).show();
                    reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(firebaseUser.getUid());
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("isMeet","default");
                    reference.updateChildren(hashMap);

                    reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(userid);
                    hashMap = new HashMap<>();
                    hashMap.put("isMeet","default");

                    reference.updateChildren(hashMap);
                    finish();
                    music.stop();

                    addRoomMeeting();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imageDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //kHÔNG CHẤP nhập cuộc gọi
                reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(firebaseUser.getUid());
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("isMeet","default");
                reference.updateChildren(hashMap);

                reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(userid);
                hashMap = new HashMap<>();
                hashMap.put("isMeet","default");

                reference.updateChildren(hashMap);
                finish();
                music.stop();
            }
        });

        imageAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chấp nhận cuộc gọi
                reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(firebaseUser.getUid());
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("isMeet","calling");
                reference.updateChildren(hashMap);

                reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(userid);
                hashMap = new HashMap<>();
                hashMap.put("isMeet","calling");

                reference.updateChildren(hashMap);
                finish();
                music.stop();
                //
                //addRoomMeeting();
            }
        });

        //Xử dụng Server của Jitsi - Meeting để tạo cuộc họp
        URL serverURL;
        try {
            // When using JaaS, replace "https://meet.jit.si" with the proper serverURL
            serverURL = new URL("https://meet.jit.si");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid server URL!");
        }
        JitsiMeetConferenceOptions defaultOptions
                = new JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                .setFeatureFlag("welcomepage.enabled", false)
                .build();
        JitsiMeet.setDefaultConferenceOptions(defaultOptions);

        registerForBroadcastMessages();
    }

    private void addRoomMeeting(){
        Date date=java.util.Calendar.getInstance().getTime();
        //Log.d("DATE",date.toString() + "   "+ date.toString().length());
        String text = date.toString().substring(0,16).trim();

        if (text.length() > 0) {
            // Build options object for joining the conference. The SDK will merge the default
            // one we set earlier and this one when joining.
            JitsiMeetConferenceOptions options
                    = new JitsiMeetConferenceOptions.Builder()
                    .setRoom(text)
                    // Settings for audio and video
                    //.setAudioMuted(true)
                    //.setVideoMuted(true)
                    .build();
            // Launch the new activity with the given options. The launch() method takes care
            // of creating the required Intent and passing the options.
            JitsiMeetActivity.launch(this, options);
        }
    }

    private void registerForBroadcastMessages() {
        IntentFilter intentFilter = new IntentFilter();

        for (BroadcastEvent.Type type : BroadcastEvent.Type.values()) {
            intentFilter.addAction(type.getAction());
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    // Example for handling different JitsiMeetSDK events
    private void onBroadcastReceived(Intent intent) {
        if (intent != null) {
            BroadcastEvent event = new BroadcastEvent(intent);

            switch (event.getType()) {
                case CONFERENCE_JOINED:
                    Timber.i("Conference Joined with url%s", event.getData().get("url"));
                    break;
                case PARTICIPANT_JOINED:
                    Timber.i("Participant joined%s", event.getData().get("name"));
                    break;
            }
        }
    }

    // Example for sending actions to JitsiMeetSDK
    private void hangUp() {
        Intent hangupBroadcastIntent = BroadcastIntentHelper.buildHangUpIntent();
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(hangupBroadcastIntent);
    }
}