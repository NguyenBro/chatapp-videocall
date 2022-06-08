package hcmute.nhom1.chatapp.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.HashMap;

import hcmute.nhom1.chatapp.Model.User;
import hcmute.nhom1.chatapp.R;
import hcmute.nhom1.chatapp.fragment.MessageFragment;
import hcmute.nhom1.chatapp.fragment.ProfileFragment;
import hcmute.nhom1.chatapp.fragment.UserFragment;

public class MainChatAppActivity extends AppCompatActivity {
    Button btnLogout,btnTest;
    ImageView imgAvatar;
    TextView txtName;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    BottomNavigationView bottomNavigationView;
    public static int flag =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat_app);
        firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://chatapp-ff2dd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users").child(firebaseUser.getUid());
        bottomNavigationView = findViewById(R.id.navigationBar);
        AnhXa();
        Event();
        LoadImageAvatar();
        loadFragment(new MessageFragment());

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.navigation_messeger:
                        fragment = new MessageFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.navigation_users:
                        fragment = new UserFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.navigation_profile:
                        fragment = new ProfileFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.navigation_setting:
//                        fragment = new UserFragment();
//                        loadFragment(fragment);
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
                Log.d("TTT","3");
                status("offline");
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainChatAppActivity.this,MainActivity.class);
                startActivity(intent);
                Log.d("TTT","4");
                //finish();
            }
        });


    }

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

    private void status(String status){
        reference = FirebaseDatabase.getInstance("https://chatapp-ff2dd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");

    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}