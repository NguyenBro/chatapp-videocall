package net.jitsi.sdktest.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import net.jitsi.sdktest.Model.File;
import net.jitsi.sdktest.R;
import net.jitsi.sdktest.UI.MessageActivity;
import net.jitsi.sdktest.adapter.FileAdapter;

import java.util.ArrayList;


//Fragment Hiện thị File tài liệu

public class FileFragment extends ListFragment {

    ArrayList<File> arrayFile;
    FileAdapter adapter;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    StorageReference storage;
    String userid = MessageActivity.userid;
    public FileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        arrayFile =new ArrayList<>();
        adapter =new FileAdapter(getContext(), R.layout.item_file,arrayFile);

        //Tạo Storage giữa 2 User để chứa danh sách file
        if(firebaseUser.getUid().compareTo(userid) > 0){
            storage = FirebaseStorage.getInstance("gs://chatapp-videocall.appspot.com").getReference("message_"+firebaseUser.getUid()+"_"+userid).child("file");
        }
        else{
            storage = FirebaseStorage.getInstance("gs://chatapp-videocall.appspot.com").getReference("message_"+userid+"_"+firebaseUser.getUid()).child("file");
        }

        //Duyệt storage để lấy danh sách File
        storage.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                //Toast.makeText(MessageActivity.this, listResult., Toast.LENGTH_SHORT).show();
//                for (StorageReference item : listResult.getItems())
                for (StorageReference item : listResult.getItems()) {
                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // adding the url in the arraylist
                            //Log.e("zzz1",uri.toString());
                            String name = item.getName();
                            name = name.substring(0,name.length()-13);
                            arrayFile.add(new File(name,uri.toString()));

                        }
                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //setAdapter(adapter);
                            setListAdapter(adapter);
                        }
                    });

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_file, container, false);

        return view;
    }

}