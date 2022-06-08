package net.jitsi.sdktest.UI;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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

import net.jitsi.sdktest.Model.Chat;
import net.jitsi.sdktest.Model.User;
import net.jitsi.sdktest.R;
import net.jitsi.sdktest.adapter.SearchAdapter;

import java.util.ArrayList;
import java.util.HashMap;



public class SendAgainActivity extends AppCompatActivity {
    ImageView imgBack;
    EditText edtSearch;
    ListView ltvUser;
    ArrayList<User> listUser;
    SearchAdapter adapter;
    Chat chat;
    StorageReference storageReference;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message_again);
        imgBack = findViewById(R.id.imageView14);
        edtSearch =findViewById(R.id.editTextTextPersonName_Again);
        ltvUser = findViewById(R.id.listViewUser);

        listUser =new ArrayList<>();
        Intent intent =getIntent();
        chat = (Chat) intent.getSerializableExtra("chat");

        addUser();


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void addUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (edtSearch.getText().toString().equals("")) {
                    listUser.clear();
                    //listUser.add(new User("11", "default", "default", "default", "default"));
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Log.d("AAA", "a");
                        User user = data.getValue(User.class);
                        Log.d("AAA", user.getUsername());

//                    listUser.add(user);
//                    listUser.add(user);
                        if (!user.getId().equals(firebaseUser.getUid())) {
                            listUser.add(user);

                        }
                        //Log.d("AAA", user.getId());
                        //Log.d("AAA", firebaseUser.getUid());
                        //Log.d("AAA", listUser.size() + "" + listUser.get(0).getUsername());
                    }
//                Log.d("AAA",listUser.size()+""+listUser.get(0).getUsername());
                    adapter = new SearchAdapter(SendAgainActivity.this, R.layout.item_friend_search, listUser);
                    ltvUser.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchUsers(String s) {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").orderByChild("search").startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUser.clear();
                //listUser.add(new User("11", "default", "default", "default", "default"));
                for(DataSnapshot data :snapshot.getChildren()){
                    User user = data.getValue(User.class);
                    assert user != null;
                    assert fuser != null;
                    if(!user.getId().equals(fuser.getUid())){
                        listUser.add(user);
                    }
                }

                adapter = new SearchAdapter(SendAgainActivity.this, R.layout.item_friend_search, listUser);
                ltvUser.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendMessage(String userid){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference referencee = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference();
        if(firebaseUser.getUid().compareTo(userid) > 0){
            storageReference = FirebaseStorage.getInstance("gs://chatapp-ff2dd.appspot.com").getReference("message_"+firebaseUser.getUid()+"_"+userid);
        }
        else{
            storageReference = FirebaseStorage.getInstance("gs://chatapp-ff2dd.appspot.com").getReference("message_"+userid+"_"+firebaseUser.getUid());
        }

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",firebaseUser.getUid());
        hashMap.put("receiver",userid);
        hashMap.put("message",chat.getMessage());
        hashMap.put("isseen",false);
        hashMap.put("type",chat.getType());
        hashMap.put("link",chat.getLink());
        referencee.child("Chats").push().setValue(hashMap);
//        if(chat.getType().equals("default")){
//            //Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
//            hashMap.put("sender",firebaseUser.getUid());
//            hashMap.put("receiver",userid);
//            hashMap.put("message",chat.getMessage());
//            hashMap.put("isseen",false);
//            hashMap.put("type",chat.getType());
//            hashMap.put("link",chat.getLink());
//            referencee.child("Chats").push().setValue(hashMap);
//        }else if(chat.getType().equals("image")){
//
//            Calendar calendar = Calendar.getInstance();
//            StorageReference mountainsRef = storageReference.child("image").child("image"+calendar.getTimeInMillis()+".png");
//
////            imgTest.setDrawingCacheEnabled(true);
////            imgTest.buildDrawingCache();
////            Bitmap bitmap = ((BitmapDrawable) imgTest.getDrawable()).getBitmap();
////            ByteArrayOutputStream baos = new ByteArrayOutputStream();
////            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
////            byte[] data = baos.toByteArray();
//            UploadTask uploadTask = mountainsRef.putFile(Uri.parse(chat.getLink()));
//            uploadTask.addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    Toast.makeText(SendAgainActivity.this, "Lỗi Lưu ảnh", Toast.LENGTH_SHORT).show();
//                }
//            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    //Uri downloadUri = taskSnapshot.getMetadata();
//                    //Toast.makeText(MessageActivity.this, "Tải ảnh thành công", Toast.LENGTH_SHORT).show();
//                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
//                    while (!urlTask.isSuccessful());
//                    Uri downloadUrl = urlTask.getResult();
//                    //linkImage[0] = downloadUrl.toString();
//                    Log.d("AAA",""+downloadUrl);
//                    //Toast.makeText(MessageActivity.this, "khac null", Toast.LENGTH_SHORT).show();
//                    hashMap.put("sender",firebaseUser.getUid());
//                    hashMap.put("receiver",userid);
//                    hashMap.put("message",chat.getMessage());
//                    hashMap.put("isseen",false);
//                    hashMap.put("type","image");
//                    hashMap.put("link",downloadUrl.toString());
//                    referencee.child("Chats").push().setValue(hashMap);
//
//                }
//            });
//
//
//        }
//        else if(chat.getType().equals("video")){
//            Calendar calendar = Calendar.getInstance();
//            StorageReference mountainsRef = storageReference.child("video").child("video"+calendar.getTimeInMillis());
//            UploadTask uploadTask =mountainsRef.putFile(Uri.parse(chat.getLink()));
//            Task<Uri> urltask =uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                @Override
//                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                    if(!task.isSuccessful()){
//                        throw task.getException();
//                    }
//                    return mountainsRef.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    if(task.isSuccessful()){
//                        Uri downloadUrl = task.getResult();
//                        //Toast.makeText(MessageActivity.this, downloadUrl.toString(), Toast.LENGTH_SHORT).show();
//                        Log.d("BBB",downloadUrl.toString());
//                        hashMap.put("sender",firebaseUser.getUid());
//                        hashMap.put("receiver",userid);
//                        hashMap.put("message",chat.getMessage());
//                        hashMap.put("isseen",false);
//                        hashMap.put("type","video");
//                        hashMap.put("link",downloadUrl.toString());
//                        referencee.child("Chats").push().setValue(hashMap);
//
//                    }
//
//                }
//            });
//        }else if(chat.getType().equals("file")){
//            Toast.makeText(this, "No", Toast.LENGTH_SHORT).show();
////            Calendar calendar = Calendar.getInstance();
////            Uri name = dataIntent.getData();
////            String fileName = getNameFile(name.getLastPathSegment());
////            StorageReference mountainsRef = storageReference.child("file").child(fileName+calendar.getTimeInMillis());
////            UploadTask uploadTask =mountainsRef.putFile(dataIntent.getData());
////
////            Task<Uri> urltask =uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
////                @Override
////                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
////                    if(!task.isSuccessful()){
////                        throw task.getException();
////                    }
////                    return mountainsRef.getDownloadUrl();
////                }
////            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
////                @Override
////                public void onComplete(@NonNull Task<Uri> task) {
////                    if(task.isSuccessful()){
////                        Uri downloadUrl = task.getResult();
////                        //Toast.makeText(MessageActivity.this, downloadUrl.toString(), Toast.LENGTH_SHORT).show();
////                        Log.d("BBB",downloadUrl.toString());
////                        hashMap.put("sender",sender);
////                        hashMap.put("receiver",receiver);
////                        hashMap.put("message", fileName);
////                        hashMap.put("isseen",false);
////                        hashMap.put("type","file");
////                        hashMap.put("link",downloadUrl.toString());
////                        referencee.child("Chats").push().setValue(hashMap);
////
////                    }
////
////                }
////            });
////
//        }


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

        DatabaseReference chatReff = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/")
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

//        final String msg = message;
//        reference = FirebaseDatabase.getInstance("https://chatapp-ff2dd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users").child(firebaseUser.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User user = snapshot.getValue(User.class);
//                if(notify) {
//                    sendNotification(receiver, user.getUsername(), msg);
//                }
//                notify=false;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }
}