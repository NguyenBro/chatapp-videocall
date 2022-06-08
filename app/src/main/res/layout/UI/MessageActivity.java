package hcmute.nhom1.chatapp.UI;

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

import net.jitsi.sdktest.UI.MainChatAppActivity;

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
import hcmute.nhom1.chatapp.BottomSheet.BottomSheetMessage;
import hcmute.nhom1.chatapp.Model.Chat;
import hcmute.nhom1.chatapp.Model.User;
import hcmute.nhom1.chatapp.Notifications.Client;
import hcmute.nhom1.chatapp.Notifications.Data;
import hcmute.nhom1.chatapp.Notifications.MyResponse;
import hcmute.nhom1.chatapp.Notifications.Sender;
import hcmute.nhom1.chatapp.Notifications.Token;
import hcmute.nhom1.chatapp.R;
import hcmute.nhom1.chatapp.adapter.MessageAdapter;
import hcmute.nhom1.chatapp.fragment.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    VideoView videoView;
    Intent intent;
    ImageView imgSend,imgInfoUser,imgBack,imgCamera,imgImage,imgTest,imgCancel,imgAudio;
    EditText edtSend;
    MessageAdapter messageAdapter;
    List<Chat> mChat;
    RecyclerView recyclerView;
    ValueEventListener seenListener;
    public static String userid;
    APIService apiService;
    boolean notify = false;
    StorageReference storageReference,storageFile;
    public static final int REQUEST_CODE_CAMERA=1911;
    int REQUEST_FOLDER = 2222;
    int REQUEST_CODE_FILE= 5555;
    int REQUEST_CODE_VIDEO= 1234;
    private Uri imageUri;
    String type="default";
    MediaController mediaController;
    Intent dataIntent;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
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

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

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

        if(firebaseUser.getUid().compareTo(userid) > 0){
            storageReference = FirebaseStorage.getInstance("gs://chatapp-ff2dd.appspot.com").getReference("message_"+firebaseUser.getUid()+"_"+userid);
        }
        else{
            storageReference = FirebaseStorage.getInstance("gs://chatapp-ff2dd.appspot.com").getReference("message_"+userid+"_"+firebaseUser.getUid());
        }

        //requestPermission();


        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                notify = true;
                String msg = edtSend.getText().toString();
                if(msg.equals("") && !type.equals("default"))
                {
                    sendMessage(firebaseUser.getUid(),userid,msg);
                }
                else if(!msg.equals("")){
                    sendMessage(firebaseUser.getUid(),userid,msg);
                }
                else{
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                edtSend.setText("");
//                edtSend.requestFocus();
//                edtSend.setEnabled(true);
//                edtSend.setInputType(InputType.TYPE_CLASS_TEXT);
                edtSend.setFocusableInTouchMode(true);
                edtSend.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                imgCancel.setVisibility(View.GONE);
                type="default";
            }
        });
        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtSend.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                imgTest.setImageResource(0);
                imgCancel.setVisibility(View.GONE);
                type="default";
//                edtSend.requestFocus();
//                edtSend.setEnabled(true);
//                edtSend.setInputType(InputType.TYPE_CLASS_TEXT);
                edtSend.setFocusableInTouchMode(true);
                edtSend.setText("");
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessageActivity.this, MainChatAppActivity.class));
            }
        });

        imgInfoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent_info = new Intent(MessageActivity.this,InformationUserActivity.class);
//                intent_info.putExtra("userid",userid);
//                startActivity(intent_info);
                showPopupMenu();
            }
        });


        imgImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                startActivityForResult(intent,REQUEST_FOLDER);
                showPopupMenuSendFile();
            }
        });

        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(MessageActivity.this,new String[]{Manifest.permission.CAMERA},REQUEST_CODE_CAMERA);
            }
        });

        imgAudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()== MotionEvent.ACTION_DOWN){
                    //Toast.makeText(MessageActivity.this, "cham", Toast.LENGTH_SHORT).show();
                    startRecording();
                }
                else if (motionEvent.getAction()== MotionEvent.ACTION_UP){
                    //Toast.makeText(MessageActivity.this, "nha", Toast.LENGTH_SHORT).show();
                    stopRecording();
                }
                return true;
            }
        });


        reference = FirebaseDatabase.getInstance("https://chatapp-ff2dd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users").child(userid);
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



//    private void showImageFile(){
//        StorageReference storage;
//        if(firebaseUser.getUid().compareTo(userid) > 0){
//            storage = FirebaseStorage.getInstance("gs://chatapp-ff2dd.appspot.com").getReference("message_"+firebaseUser.getUid()+"_"+userid);
//        }
//        else{
//            storage = FirebaseStorage.getInstance("gs://chatapp-ff2dd.appspot.com").getReference("message_"+userid+"_"+firebaseUser.getUid());
//        }
//        storage.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
//            @Override
//            public void onSuccess(ListResult listResult) {
//                //Toast.makeText(MessageActivity.this, listResult., Toast.LENGTH_SHORT).show();
//                for (StorageReference item : listResult.getItems()) {
//                    Toast.makeText(MessageActivity.this, "1", Toast.LENGTH_SHORT).show();
//                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            // adding the url in the arraylist
//                            Log.e("zzz1",uri.toString());
//                        }
//                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//
//                        }
//                    });
//                }
//
////                for (StorageReference prefix : listResult.getPrefixes()) {
////                    Log.d("ZZZ",prefix.getDownloadUrl().toString());
////                    // You may call listAll() recursively on them.
////                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }


    private void seenMessage(String userid){
        reference = FirebaseDatabase.getInstance("https://chatapp-ff2dd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Chats");
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

    private void sendMessage(String sender,String receiver,String message){

        final DatabaseReference referencee = FirebaseDatabase.getInstance("https://chatapp-ff2dd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
        HashMap<String,Object> hashMap = new HashMap<>();
        if(type.equals("default")){
            //Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
            hashMap.put("sender",sender);
            hashMap.put("receiver",receiver);
            hashMap.put("message",message);
            hashMap.put("isseen",false);
            hashMap.put("type","default");
            hashMap.put("link","default");
            referencee.child("Chats").push().setValue(hashMap);
        }else if(type.equals("image")){

            Calendar calendar = Calendar.getInstance();
            StorageReference mountainsRef = storageReference.child("image").child("image"+calendar.getTimeInMillis()+".png");

            imgTest.setDrawingCacheEnabled(true);
            imgTest.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) imgTest.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = mountainsRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(MessageActivity.this, "Lỗi Lưu ảnh", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Uri downloadUri = taskSnapshot.getMetadata();
                    //Toast.makeText(MessageActivity.this, "Tải ảnh thành công", Toast.LENGTH_SHORT).show();
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
                        Log.d("BBB",downloadUrl.toString());
                        hashMap.put("sender",sender);
                        hashMap.put("receiver",receiver);
                        hashMap.put("message",message);
                        hashMap.put("isseen",false);
                        hashMap.put("type","video");
                        hashMap.put("link",downloadUrl.toString());
                        referencee.child("Chats").push().setValue(hashMap);

                    }

                }
            });
        }else if(type.equals("file")){
            Calendar calendar = Calendar.getInstance();
            Uri name = dataIntent.getData();
            String fileName = getNameFile(name.getLastPathSegment());
            StorageReference mountainsRef = storageReference.child("file").child(fileName+calendar.getTimeInMillis());
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


        Log.d("TIME",java.time.LocalDate.now().toString() + java.time.LocalTime.now().toString());

        //=======
        DatabaseReference chatRef = FirebaseDatabase.getInstance("https://chatapp-ff2dd-default-rtdb.asia-southeast1.firebasedatabase.app")
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

        DatabaseReference chatReff = FirebaseDatabase.getInstance("https://chatapp-ff2dd-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Chatlist").child(userid).child(firebaseUser.getUid());

        chatReff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    //chatReff.child("id").setValue(firebaseUser.getUid());
//                    HashMap<String,Object> hashMap = new HashMap<>();
//                    hashMap.put("id",firebaseUser.getUid());
//                    hashMap.put("time",time);
//                    chatReff.push().setValue(hashMap);

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

        final String msg = message;
        reference = FirebaseDatabase.getInstance("https://chatapp-ff2dd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users").child(firebaseUser.getUid());
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

    private void sendNotification(String receiver,String username,String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance("https://chatapp-ff2dd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Tokens");
        Query query =tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("CCC","111");
                for(DataSnapshot data :snapshot.getChildren()){
                    Token token = data.getValue(Token.class);
                    Data dataSend = new Data(firebaseUser.getUid(),R.mipmap.ic_launcher,username+": "+message,"New Message",userid);
                    Sender sender =new Sender(dataSend,token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code()==200){
                                        if(response.body().success!=1){
                                            //Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void AddMessage(final String myid,final String userid,final String imageurl){
        mChat =new ArrayList<>();
        reference =FirebaseDatabase.getInstance("https://chatapp-ff2dd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Chats");
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
        currentUser(userid);

    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
        currentUser("none");
    }

    //==================================
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if(requestCode==REQUEST_CODE_CAMERA && grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(intent,REQUEST_CODE_CAMERA);
//        }
//        else{
//            Toast.makeText(this, "Lỗi Camera", Toast.LENGTH_SHORT).show();
//        }

        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(MessageActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MessageActivity.this, "Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case REQUEST_CODE_CAMERA:
                if( grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,REQUEST_CODE_CAMERA);
                }
                else{
                    Toast.makeText(this, "Lỗi Camera", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode ==REQUEST_CODE_CAMERA && resultCode==RESULT_OK && data!=null){
            Bitmap bit = (Bitmap) data.getExtras().get("data");
            imgTest.setImageBitmap(bit);
            edtSend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_image_brown24, 0, 0, 0);
            imgCancel.setVisibility(View.VISIBLE);
            type = "image";
        }

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

//            Uri name = data.getData();
//            Toast.makeText(this, name.getLastPathSegment(), Toast.LENGTH_SHORT).show();
//            Log.d("BBB",name.getLastPathSegment());
//            Log.d("BBB",getNameFile(name.getLastPathSegment()));
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

    public void downloadFile(Uri uri,String filename){
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri)
                .setTitle(filename)
                .setDescription("This is file from ChatApp")
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

            Toast.makeText(MessageActivity.this, "Recording started",
                    Toast.LENGTH_LONG).show();
        } else {
            requestPermission();
        }
    }

    private void uploadFirebase(){
        final DatabaseReference referencee = FirebaseDatabase.getInstance("https://chatapp-ff2dd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
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
                    Toast.makeText(MessageActivity.this, "Done", Toast.LENGTH_SHORT).show();
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