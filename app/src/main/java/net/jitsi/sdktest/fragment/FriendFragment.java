package net.jitsi.sdktest.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.jitsi.sdktest.Model.User;
import net.jitsi.sdktest.R;
import net.jitsi.sdktest.adapter.UserAdapter;

import java.util.ArrayList;



//Fragment hiện thị danh sách Các bạn bè
public class FriendFragment extends Fragment {


    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private ArrayList<User> listUser;
    EditText search_user;
    public FriendFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_friend,container,false);
        recyclerView = view.findViewById(R.id.recyclerFriend);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listUser =new ArrayList<>();
        addUser();
        search_user = view.findViewById(R.id.editTextSearchUser);
        search_user.addTextChangedListener(new TextWatcher() {
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
        return view;
    }

    private void searchUsers(String s) {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com").getReference("Users").orderByChild("search").startAt(s).endAt(s+"\uf8ff");
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

                adapter = new UserAdapter(getContext(),listUser,false);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com").getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (search_user.getText().toString().equals("")) {
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
                    adapter = new UserAdapter(getActivity(), listUser, false);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}