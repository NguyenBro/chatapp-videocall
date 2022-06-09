package net.jitsi.sdktest.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.jitsi.sdktest.R;

import java.util.concurrent.TimeUnit;



public class PhoneActivity extends AppCompatActivity {

    ImageView imgBack;
    Button btnCountinue;
    EditText edtPhone;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        imgBack = findViewById(R.id.imageView187);
        btnCountinue = findViewById(R.id.buttonCountinue);
        edtPhone =findViewById(R.id.editTextInputPhone);
        mAuth =FirebaseAuth.getInstance();
        firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
        String id = firebaseUser.getUid();
        reference = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/").getReference("Users").child(id);


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnCountinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtPhone.getText().toString().equals("")){
                    Toast.makeText(PhoneActivity.this, "Enter Phone", Toast.LENGTH_SHORT).show();
                }
                else{
                    reference.child("phone").setValue("+84"+edtPhone.getText().toString());
                    finish();
                    //VertifyPhone("+84"+edtPhone.getText().toString());
//                    Intent intent = new Intent(PhoneActivity.this,EnterOTPActivity.class);
//                    intent.putExtra("numberphone",edtPhone.getText().toString());
//                    startActivity(intent);

                }

            }
        });
    }

    private void VertifyPhone(String phone) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                //signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(PhoneActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                Log.d("ERROR",e.toString());
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                Intent intent =new Intent(PhoneActivity.this,EnterOTPActivity.class);
                                intent.putExtra("numberphone",edtPhone.getText().toString());
                                intent.putExtra("otp",s);
                                startActivity(intent);

                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
}