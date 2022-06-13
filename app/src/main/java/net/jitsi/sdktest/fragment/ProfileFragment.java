package net.jitsi.sdktest.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import net.jitsi.sdktest.Model.User;
import net.jitsi.sdktest.R;
import net.jitsi.sdktest.UI.PhoneActivity;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

//Fragment hiện thị thông tin cá nhân

public class ProfileFragment extends Fragment {


    CircleImageView image_profile;          //Lưu hình ảnh Avatar
    ImageView imgaddPhone;
    TextView username,email,phone,birth,gender,home,local;      //Text View hiện thị thông tin
    DatabaseReference reference;                                //Lấy data từ Firebase
    FirebaseUser fuser;
    StorageReference storageReference;              //Dùng để lưu Hình ảnh
    private static final int IMAGE_REQUEST =1;      //Hằng số đánh dấu việc chọn hình ảnh từ folder của điện thoại
    private Uri imageUri;                           //uri hình ảnh
    private StorageTask uploadTask;                 //Thực hiện upload hình ảnh lên firrebase

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_profile, container, false);
        //Ánh xạ
        image_profile = view.findViewById(R.id.profile_image_user);
        username = view.findViewById(R.id.username_profile);
        email = view.findViewById(R.id.textViewMail_Profile);
        phone = view.findViewById(R.id.textViewPhone_Profile);
        birth = view.findViewById(R.id.textViewDate_Profile);
        gender = view.findViewById(R.id.textViewGender_Profile);
        home = view.findViewById(R.id.textViewHome_Profile);
        local = view.findViewById(R.id.textViewLocal_Profile);
        imgaddPhone = view.findViewById(R.id.imageViewAddPhone);

        //Storage luuw hình ảnh avatar
        storageReference = FirebaseStorage.getInstance("gs://chatapp-videocall.appspot.com").getReference("uploads");
        //Đên node chưa thông tin cá nhân
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com").getReference("Users").child(fuser.getUid());

        //Load Avatar
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    image_profile.setImageResource(R.drawable.image_avatar);
                }
                else{
                    Picasso.get().load(user.getImageURL()).into(image_profile);
                }
                //==========Hiện thị thông tin
                updateInfoProfile(user);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });

        imgaddPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), PhoneActivity.class));
            }
        });

        addEvent();

        return view;



    }

    //Hiện thị các thông tin cá nhân của Usser
    private void updateInfoProfile(User user){
        if(user.getEmail().equals("default")){
            email.setText("");
        }
        else{
            email.setText(user.getEmail());
        }
        //=======

        if(user.getPhone().equals("default")){
            phone.setText("");
        }
        else{
            phone.setText(user.getPhone());
        }
        //===========
        if(user.getBirthDate().equals("default")){
            birth.setText("");
        }
        else{
            birth.setText(user.getBirthDate());
        }
        //=============
        if(user.getGender().equals("default")){
            gender.setText("");
        }
        else{
            gender.setText(user.getGender());
        }
        //===============
        if(user.getHome().equals("default")){
            home.setText("");
        }
        else{
            home.setText(user.getHome());
        }
        //================
        if(user.getLocal().equals("default")){
            local.setText("");
        }
        else{
            local.setText(user.getLocal());
        }
    }

    //Các Dialog để thay đổi thoogn tin cá nhân
    private void addEvent(){
        local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog =new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_edit_profile);
                dialog.setCanceledOnTouchOutside(false);
                TextView txt = dialog.findViewById(R.id.textViewTitle);
                EditText edt = dialog.findViewById(R.id.editTextBody);
                Button btnClose = dialog.findViewById(R.id.buttonClose);
                Button btnSave =dialog.findViewById(R.id.buttonSave);
                txt.setText(getResources().getString(R.string.current_add));
                edt.setHint(getResources().getString(R.string.current_add));
                edt.setHintTextColor(Color.GRAY);
                if(!local.getText().toString().equals("default")) {
                    edt.setText(local.getText().toString());
                }

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!edt.getText().toString().equals("")) {
                            reference.child("local").setValue(edt.getText().toString());
                            dialog.dismiss();

                        }
                    }
                });
                dialog.show();
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog =new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_edit_profile);
                dialog.setCanceledOnTouchOutside(false);
                TextView txt = dialog.findViewById(R.id.textViewTitle);
                EditText edt = dialog.findViewById(R.id.editTextBody);
                Button btnClose = dialog.findViewById(R.id.buttonClose);
                Button btnSave =dialog.findViewById(R.id.buttonSave);
                txt.setText(getResources().getString(R.string.home));
                edt.setHint(getResources().getString(R.string.home));
                edt.setHintTextColor(Color.GRAY);
                if(!home.getText().toString().equals("default")) {
                    edt.setText(home.getText().toString());
                }

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!edt.getText().toString().equals("")) {
                            reference.child("home").setValue(edt.getText().toString());
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });

        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog =new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_edit_profile);
                dialog.setCanceledOnTouchOutside(false);
                TextView txt = dialog.findViewById(R.id.textViewTitle);
                EditText edt = dialog.findViewById(R.id.editTextBody);
                Button btnClose = dialog.findViewById(R.id.buttonClose);
                Button btnSave =dialog.findViewById(R.id.buttonSave);
                txt.setText(getResources().getString(R.string.gender));
                edt.setHint(getResources().getString(R.string.gender));
                edt.setHintTextColor(Color.GRAY);
                if(!gender.getText().toString().equals("default")) {
                    edt.setText(gender.getText().toString());
                }

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!edt.getText().toString().equals("")) {
                            reference.child("gender").setValue(edt.getText().toString());
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });

        birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog =new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_edit_profile);
                dialog.setCanceledOnTouchOutside(false);
                TextView txt = dialog.findViewById(R.id.textViewTitle);
                EditText edt = dialog.findViewById(R.id.editTextBody);
                Button btnClose = dialog.findViewById(R.id.buttonClose);
                Button btnSave =dialog.findViewById(R.id.buttonSave);
                txt.setText(getResources().getString(R.string.birthdate));
                edt.setHint(getResources().getString(R.string.birthdate));
                edt.setHintTextColor(Color.GRAY);
                if(!birth.getText().toString().equals("default")) {
                    edt.setText(birth.getText().toString());
                }

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!edt.getText().toString().equals("")) {
                            reference.child("birthDate").setValue(edt.getText().toString());
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    //Mở file ảnh trên điện thoại
    private void openImage(){
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

   private String getFileExtension(Uri uri){
       ContentResolver contentResolver =getContext().getContentResolver();
       MimeTypeMap mimeTypeMap =MimeTypeMap.getSingleton();
       return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
   }

   //Upload hình ảnh avatar mới
   private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("uploading");
        pd.show();

        if(imageUri!=null){
            final  StorageReference fileReference =storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask =fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw  task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        //Nếu upload thành công thì thực hiện
                        Uri downloadUri =task.getResult();
                        String mUri = downloadUri.toString();

                        reference =FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com").getReference("Users").child(fuser.getUid());
                        HashMap<String,Object> map =new HashMap<>();
                        map.put("imageURL",mUri);
                        reference.updateChildren(map);
                        pd.dismiss();
                    }
                    else{
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
        else{
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
   }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_REQUEST && resultCode== RESULT_OK && data!= null && data.getData() != null){
            imageUri =data.getData();
            if(uploadTask!= null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "Upload in preopress", Toast.LENGTH_SHORT).show();
            }
            else{
                uploadImage();
            }
        }
    }


}