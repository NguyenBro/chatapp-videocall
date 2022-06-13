package net.jitsi.sdktest.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import net.jitsi.sdktest.fragment.MeetingFragment;
import net.jitsi.sdktest.fragment.MessageFragment;
import net.jitsi.sdktest.fragment.ProfileFragment;
import net.jitsi.sdktest.fragment.UserFragment;

import java.util.Date;
import java.util.HashMap;

//Giao diện chính
//Gồm Bottom Navigation

public class MainChatAppActivity extends AppCompatActivity {
    Button btnLogout,btnTest;
    ImageView imgAvatar,imgSetting;
    TextView txtName;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    BottomNavigationView bottomNavigationView;
    public static int flag =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat_app);
        //Các khái báo và ánh xạ
        firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(firebaseUser.getUid());
        bottomNavigationView = findViewById(R.id.navigationBar);
        imgSetting = findViewById(R.id.imageSettingMain);
        AnhXa();
        Event();
        LoadImageAvatar();
        loadFragment(new MessageFragment());
        meetingReceive();

        //======Các event tren bottom Navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.navigation_messeger:
                        //Chứa tin nhắn
                        fragment = new MessageFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.navigation_users:
                        //Chứa các user
                        fragment = new UserFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.navigation_profile:
                        //Profile cá nhân
                        fragment = new ProfileFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.navigation_setting:
                        //Tạo phòng họp
                        fragment = new MeetingFragment();
                        loadFragment(fragment);
                        return true;

                }
                return false;
            }
        });


    }

    private void AnhXa(){
        btnLogout =findViewById(R.id.buttonLogout);
        imgAvatar = findViewById(R.id.imageView_Avatar);
        txtName = findViewById(R.id.textViewName_Main);
    }
    private void Event(){
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag =1;
                status("offline");
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainChatAppActivity.this,MainActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenuSetting();
            }
        });


    }

    //Show Popup Menu khi chon Setting
    private void showPopupMenuSetting(){
        PopupMenu popupMenu =new PopupMenu(MainChatAppActivity.this,imgSetting);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_setting,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.logout:
                        flag =1;
                        //Log.d("TTT","3");
                        status("offline");
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(MainChatAppActivity.this,MainActivity.class);
                        startActivity(intent);
                        break;
                }

                return true;
            }
        });

        popupMenu.show();


    }

    //Load Avatar Usser
    private void LoadImageAvatar(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                txtName.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    imgAvatar.setImageResource(R.drawable.image_avatar);

                }
                else{


                    Picasso.get().load(user.getImageURL()).into(imgAvatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //Cập nhập trạng thái Usser : online hay offline
    private void status(String status){
        reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);

        reference.updateChildren(hashMap);
    }

    //Tham gia cuộc gọi, khi chấp nhận yêu cầu
    private void meetingReceive(){
        reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user =snapshot.getValue(User.class);
                String isMeet = user.getIsMeet();
                if(isMeet.equals("receiver")){
                    //Toast.makeText(MainChatAppActivity.this, "Có người gọi", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainChatAppActivity.this,ReceiverActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //Cập nhập trạng thái online
    @Override
    protected void onResume() {
        super.onResume();
        status("online");

    }

    //Cập nhập trạng thái offline
    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}