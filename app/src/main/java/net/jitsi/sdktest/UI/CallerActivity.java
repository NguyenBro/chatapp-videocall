package net.jitsi.sdktest.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
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

//Activity cho người gọi
public class CallerActivity extends AppCompatActivity {
    TextView txtName;
    CircleImageView imageAvatar,imageDecline;
    ImageView imageBack;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    CountDownTimer time;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onBroadcastReceived(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caller);
        txtName = findViewById(R.id.textViewNameCaller);
        imageAvatar = findViewById(R.id.caller_avatar);
        imageDecline = findViewById(R.id.caller_decline);
        imageBack = findViewById(R.id.imageViewCallerBack);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent =getIntent();
        String userid = intent.getStringExtra("userid");    //Lấy userid mà người ta muốn gọi

        //Load avatar và tên người mà ta cần gọi
        reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user =snapshot.getValue(User.class);
                txtName.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    imageAvatar.setImageResource(R.drawable.image_avatar);
                }
                else{
                    Picasso.get().load(user.getImageURL()).into(imageAvatar);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //====================
        //Kiểm tra trạng thái gọi real time từ firebase
        //Nghĩa là đang gọi hay đang rảnh
        //Phụ thuộc vào các tham số 'isMeet'
        reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user =snapshot.getValue(User.class);
                //nếu giá trị isMeet == default thì 2 bên không gọi được ( không chấp nhận cuộc gọi hoặc hủy cuộc gọi)
                if(user.getIsMeet().equals("default")){
                    time.cancel();
                    finish();
                }
                else if(user.getIsMeet().equals("calling")){
                    //Chấp nhận cuộc gọi từ người nhận
                    //Chuyển 2 user vào phòng họp
                    //Toast.makeText(CallerActivity.this, "Chấp nhận", Toast.LENGTH_SHORT).show();
                    time.cancel();

                    //Sau khi vào phòng họp, ta set lại cái giá trị default
                    reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(firebaseUser.getUid());
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("isMeet","default");
                    reference.updateChildren(hashMap);

                    reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(userid);
                    hashMap = new HashMap<>();
                    hashMap.put("isMeet","default");

                    reference.updateChildren(hashMap);
                    finish();
                    //Vào phòng họp
                    addRoomMeeting();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Time : thời gian để chờ cuộc gọi là 30s
        //Nếu hết 30s thì ttuwj động tắt máy
        time = new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                //Kết thúc thời gian, set lại isMeet thành default
                reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(firebaseUser.getUid());
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("isMeet","default");
                reference.updateChildren(hashMap);

                reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(userid);
                hashMap = new HashMap<>();
                hashMap.put("isMeet","default");

                reference.updateChildren(hashMap);
                finish();
            }
        }.start();


        //Hủy cuộc gọi từ người gọi
        imageDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time.cancel();
                reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(firebaseUser.getUid());
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("isMeet","default");
                reference.updateChildren(hashMap);

                reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(userid);
                hashMap = new HashMap<>();
                hashMap.put("isMeet","default");

                reference.updateChildren(hashMap);
                finish();
            }
        });

        //Kết nối tới sserverr của Jitsi
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
                // When using JaaS, set the obtained JWT here
                //.setToken("MyJWT")
                // Different features flags can be set
                // .setFeatureFlag("toolbox.enabled", false)
                // .setFeatureFlag("filmstrip.enabled", false)
                .setFeatureFlag("welcomepage.enabled", false)
                .build();
        JitsiMeet.setDefaultConferenceOptions(defaultOptions);

        registerForBroadcastMessages();
    }
    //Add Usser vào phòng, nếu như người nhận chấp nhập cuộc gọi
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

        /* This registers for every possible event sent from JitsiMeetSDK
           If only some of the events are needed, the for loop can be replaced
           with individual statements:
           ex:  intentFilter.addAction(BroadcastEvent.Type.AUDIO_MUTED_CHANGED.getAction());
                intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_TERMINATED.getAction());
                ... other events
         */
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