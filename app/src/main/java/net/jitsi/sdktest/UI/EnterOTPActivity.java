package net.jitsi.sdktest.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.jitsi.sdktest.R;
//Nhập mã OTP khi xác nhận số điện thoại

public class EnterOTPActivity extends AppCompatActivity {
    ImageView imgBack;              //Nút Back về màn hình trước
    Button btnCheck;                //Nút xác nhận mã OTP
    EditText edtOTP;                //Nhập OTP
    TextView txtPhone;
    String vertifyId,numberPhone;       //mã otp xac nhận và số điện thoại người dùng
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);
        txtPhone = findViewById(R.id.textView20);
        imgBack =findViewById(R.id.imageView188);
        btnCheck = findViewById(R.id.buttonCheckOTP);
        edtOTP =findViewById(R.id.editTextNumber);
        mAuth =FirebaseAuth.getInstance();
        firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
        String id = firebaseUser.getUid();
        reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(id);
        //Log.d("ID",id);


        Intent intent =getIntent();
        numberPhone  = intent.getStringExtra("numberphone");
        vertifyId = intent.getStringExtra("otp");
        txtPhone.setText(numberPhone);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = edtOTP.getText().toString();
                vertifyOtp(otp);
            }
        });
    }

    private void vertifyOtp(String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(vertifyId, otp);
        Log.d("AAA",credential.toString());
        signInWithPhoneAuthCredential(credential);
        //Toast.makeText(this, vertifyId, Toast.LENGTH_SHORT).show();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(EnterOTPActivity.this, "Sign In Again", Toast.LENGTH_SHORT).show();
                            reference.child("phone").setValue("+84"+numberPhone);
//                            Log.d("TAG", "signInWithCredential:success");
//                            Log.d("ID",FirebaseAuth.getInstance().getCurrentUser().getUid());
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(EnterOTPActivity.this,MainActivity.class);
                            startActivity(intent);
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(EnterOTPActivity.this, "that bai", Toast.LENGTH_SHORT).show();
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
}