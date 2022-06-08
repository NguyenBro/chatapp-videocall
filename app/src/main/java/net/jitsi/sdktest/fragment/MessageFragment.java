package net.jitsi.sdktest.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import net.jitsi.sdktest.Model.ChatList;
import net.jitsi.sdktest.Model.User;
import net.jitsi.sdktest.Notifications.Token;
import net.jitsi.sdktest.R;
import net.jitsi.sdktest.adapter.UserAdapter;
import net.jitsi.sdktest.adapter.UserStatusAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


//Fragment hiện thị danh sách tin nhắn
public class MessageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    private RecyclerView recyclerView,recyclerViewStatus;
    private UserAdapter userAdapter;
    private UserStatusAdapter userStatusAdapter;
    private List<User> mUsers,mStatusUsers;
    FirebaseUser fuser;
    DatabaseReference reference;
    private List<ChatList>  usersList;

    public MessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message,container,false);
        recyclerView = view.findViewById(R.id.recycler_view_chat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewStatus = view.findViewById(R.id.recycler_view_status);
        recyclerViewStatus.setHasFixedSize(true);
        recyclerViewStatus.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        usersList =new ArrayList<>();

        setRecyclerViewStatus();

     //==========================
        //Lấy danh sách các User mà tài khoản đã nhắn tin
        reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com").getReference("Chatlist").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Log.d("BBB",fuser.getUid());
                usersList.clear();
                for(DataSnapshot data : snapshot.getChildren()){
                    ChatList chatList = data.getValue(ChatList.class);
                    //Log.d("BBB",chatList.getId());
                    usersList.add(chatList);
                }

                //Log.d("MANG",usersList.size()+"");

                //=============Sap Xep Các danh sách tin nhắn theo thứ tự
                if(usersList.size() > 1 && checkArrayNull(usersList) ) {
                    //Log.d("MANG",usersList.get(0).getTime());
                    Collections.sort(usersList, new Comparator<ChatList>() {
                        @Override
                        public int compare(ChatList chatList, ChatList t1) {
                            if (chatList.getTime().compareTo(t1.getTime()) < 0) {
                                return 1;
                            } else {
                                if (chatList.getTime().compareTo(t1.getTime()) == 0) {
                                    return 0;
                                } else {
                                    return -1;
                                }
                            }
                        }
                    });
                    //Log.d("MANG", usersList.get(0).getTime());
                }
                //======================

                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Token
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                if(s!=null){
                    updateToken(s);
                }
            }
        });
        //updateToken(FirebaseMessaging.getInstance().getToken().getResult());


        return view;
    }

    //Kiểm tra Arrray Null
    private  boolean checkArrayNull(List<ChatList> a){
        for(int i=0;i<a.size();i++){
            if(a.get(i).getTime() == null || a.get(i).getId() ==null){
                return false;
            }
        }

        return true;
    }

    private void chatList(){
        mUsers = new ArrayList<>();
        reference =FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com").getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
//                for(DataSnapshot data :snapshot.getChildren()){
//                    User user =data.getValue(User.class);
//                    for(ChatList chatList :usersList){
//                        if(user.getId().equals(chatList.getId())){
//                            mUsers.add(user);
//                        }
//                    }
//                }

                for(ChatList chatList : usersList){
                    for(DataSnapshot data :snapshot.getChildren()){
                        User user =data.getValue(User.class);
                        if(user.getId().equals(chatList.getId())){
                            mUsers.add(user);
                        }
                    }
                }

                userAdapter = new UserAdapter(getContext(),mUsers,true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //Update Token  phục vụ cho việc gửi thông báo khi nhận tin nhắn mới
    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com").getReference("Tokens").child(fuser.getUid());
        Token tokenl = new Token(token);
        //reference.child(fuser.getUid()).setValue(tokenl);
        reference.setValue(tokenl);

    }

    private void setRecyclerViewStatus(){
        mStatusUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com").getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mStatusUsers.clear();
                for(DataSnapshot data: snapshot.getChildren()){
                    User user = data.getValue(User.class);
                    if(user.getStatus().equals("online") && !user.getId().equals(fuser.getUid())){
                        mStatusUsers.add(user);
                    }
                }

                userStatusAdapter = new UserStatusAdapter(getContext(),mStatusUsers);
                recyclerViewStatus.setAdapter(userStatusAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}