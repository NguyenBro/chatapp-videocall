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

import hcmute.nhom1.chatapp.Model.ImageFile;
import hcmute.nhom1.chatapp.R;
import hcmute.nhom1.chatapp.UI.MessageActivity;
import hcmute.nhom1.chatapp.adapter.ImageFileAdapter;

public class ImageFileFragment extends Fragment {

    GridView gridViewImage;
    ArrayList<ImageFile> arrayImage;
    ImageFileAdapter adapter;
    public ImageFileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image_file, container, false);
        gridViewImage = view.findViewById(R.id.gridViewImage);
        arrayImage =new ArrayList<>();
        String userid = MessageActivity.userid;
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference storage;
        adapter =new ImageFileAdapter(getContext(),R.layout.item_image,arrayImage);
//        Log.d("aaa",firebaseUser.getUid().toString());
//        Log.d("aaa",userid);
        if(firebaseUser.getUid().compareTo(userid) > 0){
            storage = FirebaseStorage.getInstance("gs://chatapp-ff2dd.appspot.com").getReference("message_"+firebaseUser.getUid()+"_"+userid).child("image");
        }
        else{
            storage = FirebaseStorage.getInstance("gs://chatapp-ff2dd.appspot.com").getReference("message_"+userid+"_"+firebaseUser.getUid()).child("image");
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
                            arrayImage.add(new ImageFile(uri.toString()));

                        }
                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            gridViewImage.setAdapter(adapter);
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
        //adapter =new ImageFileAdapter(getContext(),R.layout.item_image,arrayImage);
        //gridViewImage.setAdapter(adapter);



        return view;
    }


}