package net.jitsi.sdktest.UI;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import net.jitsi.sdktest.BottomSheet.BottomSheetMessage;
import net.jitsi.sdktest.Model.Chat;
import net.jitsi.sdktest.Model.User;
import net.jitsi.sdktest.Notifications.Client;
import net.jitsi.sdktest.Notifications.Data;
import net.jitsi.sdktest.Notifications.MyResponse;
import net.jitsi.sdktest.Notifications.Sender;
import net.jitsi.sdktest.Notifications.Token;
import net.jitsi.sdktest.R;
import net.jitsi.sdktest.adapter.MessageAdapter;
import net.jitsi.sdktest.fragment.APIService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Activity ch???a n???i dung tin nh???n

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;      //Avatar
    TextView username;                  //T??n
    FirebaseUser firebaseUser;          //L???y t??i kho???n ??ang ????ng nh???p
    DatabaseReference reference;        //L???y c??c data li??n quan t??? firrebase
    VideoView videoView;                //Video
    Intent intent;
    ImageView imgSend,imgInfoUser,imgBack,imgCamera,imgImage,imgTest,imgCancel,imgAudio,imgMeeting;     //M???t s??? image li??n quan
    EditText edtSend;                   //G???i tin nh???n
    MessageAdapter messageAdapter;      //Adapter
    List<Chat> mChat;                   //M???ng c??c tin nh???n
    RecyclerView recyclerView;          //Hi???n th??? danh s??ch tin nh???n
    ValueEventListener seenListener;
    public static String userid;
    APIService apiService;              //API g???i th??ng b??o khi nh???n tin nh???n
    boolean notify = false;
    StorageReference storageReference,storageFile;
    public static final int REQUEST_CODE_CAMERA=1911;       //H???ng s??? ????nh d???u m??? camera
    int REQUEST_FOLDER = 2222;                              //H???ng s??? ????nh d???u m??? folder
    int REQUEST_CODE_FILE= 5555;                            //H???ng s??? ????nh d???u m??? File
    int REQUEST_CODE_VIDEO= 1234;                           //H???ng s??? ????nh d???u m??? Video
    private Uri imageUri;
    String type="default";                                  //C??c lo???i tin nh???n : text, image, video, file, audio
    MediaController mediaController;                        //H??? tr??? vi???c ch???y video
    Intent dataIntent;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;                              //H??? t??? vi???c ch???y audio
    Random random ;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer ;
    boolean checkPermission = false;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar =findViewById(R.id.toolbar);
        imgSend = findViewById(R.id.imageViewSend);
        edtSend = findViewById(R.id.edittextMessage);
        imgInfoUser =findViewById(R.id.imageViewInfoUser);
        imgBack = findViewById(R.id.imageViewBack);
        imgCamera = findViewById(R.id.imageViewCamera);
        imgImage = findViewById(R.id.imageViewImage);
        imgTest = findViewById(R.id.imageView6);
        imgCancel = findViewById(R.id.imageViewCancelImage);
        imgAudio = findViewById(R.id.imageViewAudio);
        imgMeeting = findViewById(R.id.imageViewMeeting);



        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish();
                startActivity(new Intent(MessageActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        //S??? d???ng API Server ????? g???i th??ng b??o khi nh???n tin nh???n
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        //C??c khai b??o
        recyclerView =findViewById(R.id.recycleViewMessage);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.imageView_Avatar_Message);
        username =findViewById(R.id.textViewName_Message);
        intent = getIntent();
        userid= intent.getStringExtra("userid");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        random =new Random();

        //L??a=u storage ????? ch???a c??c file trong cu???c tr?? chuy???n
        if(firebaseUser.getUid().compareTo(userid) > 0){
            storageReference = FirebaseStorage.getInstance("gs://chatapp-videocall.appspot.com").getReference("message_"+firebaseUser.getUid()+"_"+userid);
        }
        else{
            storageReference = FirebaseStorage.getInstance("gs://chatapp-videocall.appspot.com").getReference("message_"+userid+"_"+firebaseUser.getUid());
        }

        //requestPermission();

        //G???i tin nh???n
        imgSend.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                notify = true;
                String msg = edtSend.getText().toString();      //N???i dung tin nh???n
                if(msg.equals("") && !type.equals("default"))
                {
                    sendMessage(firebaseUser.getUid(),userid,msg);  //H??m g???i tin nh???n
                }
                else if(!msg.equals("")){
                    sendMessage(firebaseUser.getUid(),userid,msg);
                }
                else{
                    Toast.makeText(MessageActivity.this, getResources().getString(R.string.cannot), Toast.LENGTH_SHORT).show();
                }
                edtSend.setText("");
                edtSend.setFocusableInTouchMode(true);
                edtSend.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                imgCancel.setVisibility(View.GONE);
                type="default";
            }
        });


        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //H???y file ????nh k??m
                edtSend.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                imgTest.setImageResource(0);
                imgCancel.setVisibility(View.GONE);
                type="default";
                edtSend.setFocusableInTouchMode(true);
                edtSend.setText("");
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessageActivity.this,MainChatAppActivity.class));
            }
        });

        imgInfoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Xem thoogn tin
                showPopupMenu();
            }
        });


        imgImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //L???a ch???n file .image, video ????? g???i
                showPopupMenuSendFile();
            }
        });

        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //M??? camera
                ActivityCompat.requestPermissions(MessageActivity.this,new String[]{Manifest.permission.CAMERA},REQUEST_CODE_CAMERA);
            }
        });

        imgAudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //Th???c hi???n ghi ??m

                if(motionEvent.getAction()== MotionEvent.ACTION_DOWN){
                    //Nh???n v??o image
                    //Toast.makeText(MessageActivity.this, "cham", Toast.LENGTH_SHORT).show();
                    startRecording();
                }
                else if (motionEvent.getAction()== MotionEvent.ACTION_UP){
                    //Toast.makeText(MessageActivity.this, "nha", Toast.LENGTH_SHORT).show();
                    //Th??? ra s??? l??u l???i
                    stopRecording();
                }
                return true;
            }
        });

        imgMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //G???i video

                //Thay ?????i gi?? tr??? isMeet
                reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(firebaseUser.getUid());
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("isMeet","caller");
                reference.updateChildren(hashMap);
            //=====================================
                reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(userid);
                hashMap = new HashMap<>();
                hashMap.put("isMeet","receiver");
                reference.updateChildren(hashMap);
            //======================================
                reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Calling").child(userid);
                hashMap = new HashMap<>();
                hashMap.put("caller",firebaseUser.getUid());

                reference.updateChildren(hashMap);


                Intent intent =new Intent(MessageActivity.this,CallerActivity.class);
                intent.putExtra("userid",userid);
                startActivity(intent);

            }
        });

        //Load image
        reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user =snapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.drawable.image_avatar);
                }
                else{
                    Picasso.get().load(user.getImageURL()).into(profile_image);
                }

                AddMessage(firebaseUser.getUid(),userid,user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        seenMessage(userid);
    }
    //==========================================================================================

    private void showPopupMenu(){
        PopupMenu popupMenu =new PopupMenu(MessageActivity.this,imgInfoUser);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.information_user:
                        Intent intent_info = new Intent(MessageActivity.this,InformationUserActivity.class);
                        intent_info.putExtra("userid",userid);
                        startActivity(intent_info);
                        break;
                    case R.id.image_file:
                        //Toast.makeText(MessageActivity.this, "chualam", Toast.LENGTH_SHORT).show();
                        //showImageFile();
                        Intent intent = new Intent(MessageActivity.this,FileActivity.class);
                        intent .putExtra("userid",userid);
                        startActivity(intent );
                        break;
                }

                return true;
            }
        });

        popupMenu.show();


    }

    //Ch???n file,image,video
    private void showPopupMenuSendFile(){
        PopupMenu popupMenu =new PopupMenu(MessageActivity.this,imgImage);
        popupMenu.getMenuInflater().inflate(R.menu.context_menu_send_file,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.send_file:
                        Intent intentfile = new Intent(Intent.ACTION_GET_CONTENT);
                        intentfile.setType("application/*");
                        startActivityForResult(intentfile,REQUEST_CODE_FILE);
                        break;
                    case R.id.send_image:
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent,REQUEST_FOLDER);
                        break;
                    case R.id.send_video:
                        Intent intentvideo = new Intent(Intent.ACTION_GET_CONTENT);
                        intentvideo.setType("video/*");
                        startActivityForResult(intentvideo,REQUEST_CODE_VIDEO);
                        break;
                }
                return true;
            }
        });

        popupMenu.show();


    }




    //Xem tin nh???n ==> thay ?????i status
    private void seenMessage(String userid){
        reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    Chat chat =data.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("isseen",true);
                        data.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //H??m g???i tin nh???n
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendMessage(String sender, String receiver, String message){

        //T???o li??n k???t firebase
        final DatabaseReference referencee = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference();
        HashMap<String,Object> hashMap = new HashMap<>();
        if(type.equals("default")){
            //N???u tin nh???n kh??ng c?? file ????nh k??m th?? g???i b??nh th?????ng
            //Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
            hashMap.put("sender",sender);
            hashMap.put("receiver",receiver);
            hashMap.put("message",message);
            hashMap.put("isseen",false);
            hashMap.put("type","default");
            hashMap.put("link","default");
            referencee.child("Chats").push().setValue(hashMap);
        }else if(type.equals("image")){
            //Tin nh???n g???m h??nh ???nh
            //L??u h??nh ???nh l??n firebase
            //Giuwx l???i li??n k???t (link ) trong n???i dung tin nh???n
            Calendar calendar = Calendar.getInstance();
            StorageReference mountainsRef = storageReference.child("image").child("image"+calendar.getTimeInMillis()+".png");

            imgTest.setDrawingCacheEnabled(true);
            imgTest.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) imgTest.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = mountainsRef.putBytes(data);
            //Upload firebase
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(MessageActivity.this, getResources().getString(R.string.error_image), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Uri downloadUri = taskSnapshot.getMetadata();
                    //Toast.makeText(MessageActivity.this, "T???i ???nh th??nh c??ng", Toast.LENGTH_SHORT).show();
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();
                    //linkImage[0] = downloadUrl.toString();
                    Log.d("AAA",""+downloadUrl);
                    //Toast.makeText(MessageActivity.this, "khac null", Toast.LENGTH_SHORT).show();
                    hashMap.put("sender",sender);
                    hashMap.put("receiver",receiver);
                    hashMap.put("message",message);
                    hashMap.put("isseen",false);
                    hashMap.put("type","image");
                    hashMap.put("link",downloadUrl.toString());
                    referencee.child("Chats").push().setValue(hashMap);

                }
            });


        }
        else if(type.equals("video")){
            //Tin nh???n c?? ch??a video
            //upload video l??n firebase

            Calendar calendar = Calendar.getInstance();
            StorageReference mountainsRef = storageReference.child("video").child("video"+calendar.getTimeInMillis());
            UploadTask uploadTask =mountainsRef.putFile(dataIntent.getData());
            Task<Uri> urltask =uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return mountainsRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUrl = task.getResult();
                        //Toast.makeText(MessageActivity.this, downloadUrl.toString(), Toast.LENGTH_SHORT).show();
                        //Log.d("BBB",downloadUrl.toString());
                        hashMap.put("sender",sender);
                        hashMap.put("receiver",receiver);
                        hashMap.put("message",message);
                        hashMap.put("isseen",false);
                        hashMap.put("type","video");
                        hashMap.put("link",downloadUrl.toString());     //L??u l???i li??n k???t
                        referencee.child("Chats").push().setValue(hashMap);

                    }

                }
            });
        }else if(type.equals("file")){

            //Tin nh???n g???m file ????nh k??m
            Calendar calendar = Calendar.getInstance();
            Uri name = dataIntent.getData();
            String fileName = getNameFile(name.getLastPathSegment());
            StorageReference mountainsRef = storageReference.child("file").child(fileName+calendar.getTimeInMillis());
            UploadTask uploadTask =mountainsRef.putFile(dataIntent.getData());
            //Upload file l??n firebase == > L??u l???i Uri
            Task<Uri> urltask =uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return mountainsRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUrl = task.getResult();
                        //Toast.makeText(MessageActivity.this, downloadUrl.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("BBB",downloadUrl.toString());
                        hashMap.put("sender",sender);
                        hashMap.put("receiver",receiver);
                        hashMap.put("message", fileName);
                        hashMap.put("isseen",false);
                        hashMap.put("type","file");
                        hashMap.put("link",downloadUrl.toString());
                        referencee.child("Chats").push().setValue(hashMap);

                    }

                }
            });
//
        }


        //Log.d("TIME",java.time.LocalDate.now().toString() + java.time.LocalTime.now().toString());

        //=======
        DatabaseReference chatRef = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/")
                .getReference("Chatlist").child(firebaseUser.getUid()).child(userid);

        String time = java.time.LocalDate.now().toString() +"-"+ java.time.LocalTime.now().toString();

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    //chatRef.child("id").setValue(userid);
                    chatRef.child("id").setValue(userid);
                    chatRef.child("time").setValue(time);

                }
                else{
                    chatRef.child("time").setValue(time);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //==========
        //L??u Usser v??o danh s??ch l???ch s??? nh???n tin == > ????? hi???n th???
        DatabaseReference chatReff = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/")
                .getReference("Chatlist").child(userid).child(firebaseUser.getUid());

        chatReff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    chatReff.child("id").setValue(firebaseUser.getUid());
                    chatReff.child("time").setValue(time);
                }
                else {
                    chatReff.child("time").setValue(time);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //G???i th??ng b??o khi c?? tin nh???n m???i
        final String msg = message;
        reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(notify) {
                    sendNotification(receiver, user.getUsername(), msg);
                }
//                MediaPlayer music = MediaPlayer.create(MessageActivity.this, R.raw.music);
//                music.start();
                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    //G???i th??ng b??o, s??? d???ng Token
    private void sendNotification(String receiver,String username,String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Tokens");
        Query query =tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Log.d("CCC","111");
                for(DataSnapshot data :snapshot.getChildren()){
                    Token token = data.getValue(Token.class);
                    Data dataSend = new Data(firebaseUser.getUid(),R.drawable.logo,username+": "+message,"New Message",userid);
                    Sender sender =new Sender(dataSend,token.getToken());

                    //?????c API
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code()==200){
                                        if(response.body().success!=1){
                                            Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Toast.makeText(MessageActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //th??m tin nh???n v?? hi???n th??? danh s??ch tin nh???n
    private void AddMessage(final String myid,final String userid,final String imageurl){
        mChat =new ArrayList<>();
        reference =FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for(DataSnapshot data :snapshot.getChildren()){
                    Chat chat =data.getValue(Chat.class);
                    if((chat.getReceiver().equals(myid) && chat.getSender().equals(userid)) || (chat.getReceiver().equals(userid) && chat.getSender().equals(myid))){
                        mChat.add(chat);
                    }
                    chat.setId(data.getKey());

                    messageAdapter = new MessageAdapter(MessageActivity.this,mChat,imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void currentUser(String userid){
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor =getSharedPreferences("PREFS",MODE_PRIVATE).edit();
        editor.putString("currentuser",userid);
        editor.apply();
    }

    //C???p nh???p tr???ng th??i ng?????i d??ng
    private void status(String status){
        reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);

        reference.updateChildren(hashMap);
    }



    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(userid);

    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
        currentUser("none");
    }

    //==================================Nh???n l???i Resquest khi m??? camera ho???c file, video
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(MessageActivity.this, getResources().getString(R.string.granted),
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MessageActivity.this, getResources().getString(R.string.denied),Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case REQUEST_CODE_CAMERA:
                if( grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,REQUEST_CODE_CAMERA);
                }
                else{
                    Toast.makeText(this, getResources().getString(R.string.error_camera), Toast.LENGTH_SHORT).show();
                }
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //L???y k???t qu???
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //K???t qu??? khi m??? camera
        if(requestCode ==REQUEST_CODE_CAMERA && resultCode==RESULT_OK && data!=null){
            Bitmap bit = (Bitmap) data.getExtras().get("data");
            imgTest.setImageBitmap(bit);
            edtSend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_image_brown24, 0, 0, 0);
            imgCancel.setVisibility(View.VISIBLE);
            type = "image";
        }
        //K???t qu??? khi m??? folder ch??a file
        if(requestCode==REQUEST_FOLDER && resultCode==RESULT_OK && data!=null){
            Uri uri =data.getData();
            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imgTest.setImageBitmap(bitmap);
            edtSend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_image_brown24, 0, 0, 0);
            imgCancel.setVisibility(View.VISIBLE);
            type = "image";
        }

        if(requestCode ==REQUEST_CODE_FILE && resultCode==RESULT_OK && data!=null){
            dataIntent = data;
            //sendFile(data);
            type = "file";
            edtSend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_upload_file_24, 0, 0, 0);
            imgCancel.setVisibility(View.VISIBLE);
            edtSend.setText(getNameFile(data.getData().getLastPathSegment()));
            edtSend.setFocusable(false);

        }

        if(requestCode ==REQUEST_CODE_VIDEO && resultCode==RESULT_OK && data!=null){
            dataIntent = data;
            //sendVideo(dataIntent);
            edtSend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_ondemand_video_24, 0, 0, 0);
            imgCancel.setVisibility(View.VISIBLE);
            type = "video";
        }



        super.onActivityResult(requestCode, resultCode, data);
    }



    public void showBottomSheet(String msg,String id_chat,Chat chat){
        BottomSheetMessage bottomSheetMessage =new BottomSheetMessage(msg,id_chat,chat);
        bottomSheetMessage.show(getSupportFragmentManager(), "TAG");
    }

    //Download file khi ch???n
    public void downloadFile(Uri uri,String filename){
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri)
                .setTitle(filename)
                .setDescription(getResources().getString(R.string.this_is_file))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        downloadManager.enqueue(request);
    }

    private String getNameFile(String s){
        String[] ary = s.split("");
        String[] res;
        String result="";
        int i=ary.length-1;
        while (!ary[i].equals("/")){
            result=result+ary[i];
            i--;
        }
        StringBuilder str = new StringBuilder(result);
        return str.reverse().toString();

    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(MessageActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }


    //Ki???m tra quy???n truy c???p c?? cho ph??p hay kkhong
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    public String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder( string );
        int i = 0 ;
        while(i < string ) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++ ;
        }
        return stringBuilder.toString();
    }

    private void stopRecording(){
        if(checkPermission) {
            mediaRecorder.stop();
            mediaRecorder.release();

//            Toast.makeText(MessageActivity.this, "Recording Completed",
//                    Toast.LENGTH_LONG).show();
            uploadFirebase();
            checkPermission = false;
        }
    }

    //B???t ?????u th???c hi???n vi???c record audio
    private void startRecording() {
        if(checkPermission()) {
            checkPermission = true;
            AudioSavePathInDevice =
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                            CreateRandomAudioFileName(5) + "AudioRecording.3gp";

            MediaRecorderReady();

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Toast.makeText(MessageActivity.this, getResources().getString(R.string.start_audio),
                    Toast.LENGTH_LONG).show();
        } else {
            requestPermission();
        }
    }

    //Upload audio l??n firebase
    private void uploadFirebase(){
        final DatabaseReference referencee = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference();
        HashMap<String,Object> hashMap = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        StorageReference mountainsRef = storageReference.child("audio").child("audio"+calendar.getTimeInMillis()+".png");
        Uri uri = Uri.fromFile(new File(AudioSavePathInDevice));
        UploadTask uploadTask =mountainsRef.putFile(uri);
        Task<Uri> urltask =uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                return mountainsRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri downloadUrl = task.getResult();
                    Toast.makeText(MessageActivity.this, getResources().getString(R.string.done), Toast.LENGTH_SHORT).show();
                    //firebaseUser.getUid(),userid
                    Log.d("BBB",downloadUrl.toString());
                    hashMap.put("sender",firebaseUser.getUid());
                    hashMap.put("receiver",userid);
                    hashMap.put("message", "");
                    hashMap.put("isseen",false);
                    hashMap.put("type","audio");
                    hashMap.put("link",downloadUrl.toString());
                    referencee.child("Chats").push().setValue(hashMap);

                }

            }
        });

    }

    private void sendAudio(){

    }

}