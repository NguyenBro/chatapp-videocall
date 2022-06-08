package hcmute.nhom1.chatapp.fragment;

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

import java.util.ArrayList;

import hcmute.nhom1.chatapp.R;
import hcmute.nhom1.chatapp.UI.MessageActivity;
import hcmute.nhom1.chatapp.adapter.VideoAdapter;


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

        if(firebaseUser.getUid().compareTo(userid) > 0){
            storage = FirebaseStorage.getInstance("gs://chatapp-ff2dd.appspot.com").getReference("message_"+firebaseUser.getUid()+"_"+userid).child("video");
        }
        else{
            storage = FirebaseStorage.getInstance("gs://chatapp-ff2dd.appspot.com").getReference("message_"+userid+"_"+firebaseUser.getUid()).child("video");
        }
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
                            Log.e("zzz1",uri.toString());
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