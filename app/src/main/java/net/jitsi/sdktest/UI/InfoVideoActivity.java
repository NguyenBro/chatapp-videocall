package net.jitsi.sdktest.UI;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.jitsi.sdktest.Model.User;
import net.jitsi.sdktest.R;

import java.util.Calendar;


//Xem Video
public class InfoVideoActivity extends AppCompatActivity {

    ImageView imgBack,imgMore;
    VideoView video;                        //Chạy video từ URI
    MediaController mediaController;        //Hiện thị các điều kiển ,tua nhanh chậm ,dừng lại trên video
    TextView txtName,txtStatus;
    DatabaseReference reference;
    Uri linkVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_video);
        imgBack =findViewById(R.id.imageViewBack_Video);
        video = findViewById(R.id.videoViewDes);
        txtName = findViewById(R.id.textViewName_Video);
        txtStatus = findViewById(R.id.textViewStatus_Video);
        imgMore = findViewById(R.id.imageView111);
        mediaController =new MediaController(this);
        video.setMediaController(mediaController);

        Intent intent =getIntent();
        linkVideo = Uri.parse(intent.getStringExtra("video"));
        video.setVideoURI(linkVideo);
        video.start();

        String userid = intent.getStringExtra("userid");
        //Xet trạng thái hoạt động
        reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user =snapshot.getValue(User.class);
                txtName.setText(user.getUsername());
                txtStatus.setText(user.getStatus());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu();
            }
        });


    }

    private void showPopupMenu() {
        //Toast.makeText(this, "hh", Toast.LENGTH_SHORT).show();
        PopupMenu popupMenu = new PopupMenu(InfoVideoActivity.this, imgMore);
        popupMenu.getMenuInflater().inflate(R.menu.image_setting, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.image_save:
                        //Tải video
                        Toast.makeText(InfoVideoActivity.this, "Save", Toast.LENGTH_SHORT).show();
//                        Uri uri = Uri.parse(url);
                        downloadFile(linkVideo);
                        break;
                    case R.id.image_send:
                        //gửi video
                        Toast.makeText(InfoVideoActivity.this, "Send", Toast.LENGTH_SHORT).show();
                        break;
                }

                return true;
            }
        });
        popupMenu.show();
    }

    //Tải video
    private void downloadFile(Uri uri){
        Calendar calendar = Calendar.getInstance();
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri)
                .setTitle("Video Download-"+calendar.getTimeInMillis())
                .setDescription("This is file from ChatApp")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        downloadManager.enqueue(request);
    }
}