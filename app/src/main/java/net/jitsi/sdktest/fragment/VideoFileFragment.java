package net.jitsi.sdktest.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import net.jitsi.sdktest.R;
import net.jitsi.sdktest.UI.MessageActivity;
import net.jitsi.sdktest.adapter.VideoAdapter;

import java.util.ArrayList;


//Fragment hiện thị các Video trong tin nhắn

public class VideoFileFragment extends Fragment {


    GridView gridViewVideo;
    ArrayList<String> arrayVideo;
    VideoAdapter adapter;
    public VideoFileFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_image_video, container, false);
        gridViewVideo = view.findViewById(R.id.gridViewVideo);
        arrayVideo =new ArrayList<>();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference storage;
        String userid = MessageActivity.userid;
        adapter =new VideoAdapter(getContext(),R.layout.item_video,arrayVideo);

        //Lấy vị trí trên firebase mà ta đã lưu hoặc sẽ lưu acsc Video
        if(firebaseUser.getUid().compareTo(userid) > 0){
            storage = FirebaseStorage.getInstance("gs://chatapp-videocall.appspot.com").getReference("message_"+firebaseUser.getUid()+"_"+userid).child("video");
        }
        else{
            storage = FirebaseStorage.getInstance("gs://chatapp-videocall.appspot.com").getReference("message_"+userid+"_"+firebaseUser.getUid()).child("video");
        }
        //Duyệt danh sách các Video đã lấy được
        storage.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // adding the url in the arraylist
                            //Log.e("zzz1",uri.toString());
                            arrayVideo.add(uri.toString());

                        }
                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            gridViewVideo.setAdapter(adapter);
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


        return view;

    }
}