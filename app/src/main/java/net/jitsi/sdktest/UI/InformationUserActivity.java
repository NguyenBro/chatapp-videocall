package net.jitsi.sdktest.UI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import net.jitsi.sdktest.Model.User;
import net.jitsi.sdktest.R;

import de.hdodenhof.circleimageview.CircleImageView;

//Xem thông tin của bạn bè

public class InformationUserActivity extends AppCompatActivity {

    CircleImageView image_profile;              //Hình ảnh
    TextView username,email,phone,birth,gender,home,local;      //Các thông tin liên quan
    DatabaseReference reference;                //Lấy thông tin từ database
    FirebaseUser fuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_user);
        AnhXa();
        Intent intent =getIntent();
        String userid = intent.getStringExtra("userid");
        //Load ten User và Avatar
        reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    image_profile.setImageResource(R.drawable.image_avatar);
                }
                else{
                    Picasso.get().load(user.getImageURL()).into(image_profile);
                }
                //==========
                updateInfoProfile(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    //Hiện thị các thông tin khác của user
    private void updateInfoProfile(User user){
        if(user.getEmail().equals("default")){
            email.setText("");
        }
        else{
            email.setText(user.getEmail());
        }
        //=======

        if(user.getPhone().equals("default")){
            phone.setText("");
        }
        else{
            phone.setText(user.getPhone());
        }
        //===========
        if(user.getBirthDate().equals("default")){
            birth.setText("");
        }
        else{
            birth.setText(user.getBirthDate());
        }
        //=============
        if(user.getGender().equals("default")){
            gender.setText("");
        }
        else{
            gender.setText(user.getGender());
        }
        //===============
        if(user.getHome().equals("default")){
            home.setText("");
        }
        else{
            home.setText(user.getHome());
        }
        //================
        if(user.getLocal().equals("default")){
            local.setText("");
        }
        else{
            local.setText(user.getLocal());
        }
    }

    private void AnhXa(){
        image_profile = findViewById(R.id.profile_image_user_info);
        username = findViewById(R.id.username_profile_info);
        email = findViewById(R.id.textViewMail_Info);
        phone = findViewById(R.id.textViewPhone_Info);
        birth = findViewById(R.id.textViewDate_Info);
        gender = findViewById(R.id.textViewGender_Info);
        home = findViewById(R.id.textViewHome_Info);
        local = findViewById(R.id.textViewLocal_Info);
    }
}