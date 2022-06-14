package net.jitsi.sdktest.UI;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import net.jitsi.sdktest.Model.User;
import net.jitsi.sdktest.R;

import java.util.Calendar;

//Xem ảnh

public class InfoImageActivity extends AppCompatActivity {

    ImageView imgBack,imgBody,imgMore;
    TextView txtName,txtStatus;
    String userid = MessageActivity.userid;
    public static String start ="";
    String url;                                             //Link ảnh trên firebase
    DatabaseReference reference;
    private ScaleGestureDetector scaleGestureDetector;      //Dùng để zoom ảnh
    private float mScaleFactor = 1.0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_image);
        imgBack = findViewById(R.id.imageViewBack_Image);
        imgBody = findViewById(R.id.imageView10);
        imgMore = findViewById(R.id.imageView11);
        txtName = findViewById(R.id.textViewName_Image);
        txtStatus = findViewById(R.id.textViewStatus_Image);
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        url = getIntent().getStringExtra("url");
        Picasso.get().load(url).into(imgBody);

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
        imgBody.post(new Runnable() {
            @Override
            public void run() {


            }
        });


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    //Zoom out - zoom in hình ảnh
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
            imgBody.setScaleX(mScaleFactor);
            imgBody.setScaleY(mScaleFactor);
            return true;
        }
    }

    //Lựa chọn tải ảnh hoặc gửi ảnh cho người khác
    private void showPopupMenu() {
        //Toast.makeText(this, "hh", Toast.LENGTH_SHORT).show();
        PopupMenu popupMenu = new PopupMenu(InfoImageActivity.this, imgMore);
        popupMenu.getMenuInflater().inflate(R.menu.image_setting, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.image_save:
                        Toast.makeText(InfoImageActivity.this, "Save", Toast.LENGTH_SHORT).show();
                        Uri uri = Uri.parse(url);
                        downloadFile(uri);
                        break;
                    case R.id.image_send:
                        Toast.makeText(InfoImageActivity.this, "Send", Toast.LENGTH_SHORT).show();
                        break;
                }

                return true;
            }
        });
        popupMenu.show();
    }

    //Download ảnh về
    private void downloadFile(Uri uri){
        Calendar calendar = Calendar.getInstance();
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri)
                .setTitle("File Download-"+calendar.getTimeInMillis())
                .setDescription(getResources().getString(R.string.this_is_file))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        downloadManager.enqueue(request);
    }
}