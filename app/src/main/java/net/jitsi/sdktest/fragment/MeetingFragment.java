package net.jitsi.sdktest.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.jitsi.sdktest.Model.Meeting;
import net.jitsi.sdktest.Model.User;
import net.jitsi.sdktest.R;
import net.jitsi.sdktest.adapter.ContactsAdapter;
import net.jitsi.sdktest.adapter.MeetingAdapter;

import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.BroadcastIntentHelper;
import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import timber.log.Timber;

//Fragment Tao Room Meeting

public class MeetingFragment extends Fragment {
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onBroadcastReceived(intent);
        }
    };
    View view;
    EditText edtJoin;
    Button btnJoin, btnCreateRoom;
    ListView ltvMeeting;
    private MeetingAdapter adapter;
    private ArrayList<Meeting> listMeeting;
    FirebaseUser firebaseUser;

    public MeetingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_meeting, container, false);
        edtJoin = view.findViewById(R.id.conferenceNameId);
        btnJoin = view.findViewById(R.id.buttonJoinMeet);
        btnCreateRoom = view.findViewById(R.id.buttonCreateRoom);
        ltvMeeting = view.findViewById(R.id.listViewMeetingRoom);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        listMeeting = new ArrayList<>();
        //addMeetingtoListView();

        //Tao Phòng
        btnCreateRoom.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                //Tao ID Room ngẫu nhiên
                //RandomString gen = new RandomString(8, ThreadLocalRandom.current());
                String text = CreateRandomRoomMeetingName(6);
                Toast.makeText(getContext(),"ID Room:"+ text, Toast.LENGTH_LONG).show();
                //Tạo phòng
                createRoom(text);
                edtJoin.setText("");

            }
        });

        //Tham gia phòng họp đã có sẵn
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String text = edtJoin.getText().toString();
                createRoom(text);

            }
        });


        //kết nối tới URL của Jitsi
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

        return view;
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);

        super.onDestroy();
    }

    //Đăng  kí để tạo Room
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

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, intentFilter);
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
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).sendBroadcast(hangupBroadcastIntent);
    }

    //Tạo ID Room Random
    public String CreateRandomRoomMeetingName(int string) {
        String RamdomRoomName = "ABCDEFGHIJKLMNOPQSTUWZXR";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0;
        while (i < string) {
            stringBuilder.append(RamdomRoomName.
                    charAt(random.nextInt(RamdomRoomName.length())));

            i++;
        }
        return stringBuilder.toString();
    }

    //lưu thông tin cuộc họp vào Firebase
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addMeetingFireBase(String room) {
        String time = java.time.LocalDate.now().toString();
        DatabaseReference reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Meeting").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("roomID", room);
        hashMap.put("time", time);
        hashMap.put("status", "away");
        hashMap.put("isNow", false);
        reference.push().setValue(hashMap);
    }

    private void addMeetingtoListView() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com").getReference("Meeting").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listMeeting.clear();
                //listUser.add(new User("11", "default", "default", "default", "default"));
                for (DataSnapshot data : snapshot.getChildren()) {

                    Meeting meeting = data.getValue(Meeting.class);
                    listMeeting.add(meeting);
                }
//                Log.d("AAA",listUser.size()+""+listUser.get(0).getUsername());
                adapter = new MeetingAdapter(getContext(), R.layout.item_meeting, listMeeting);
                ltvMeeting.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createRoom(String text) {
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
            JitsiMeetActivity.launch(getContext(), options);
            addMeetingFireBase(text);
        }
    }
}