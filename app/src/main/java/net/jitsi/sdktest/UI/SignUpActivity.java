package net.jitsi.sdktest.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.jitsi.sdktest.R;

import java.util.HashMap;



public class SignUpActivity extends AppCompatActivity {
    TextView txtSignIn,txtResetPass;
    EditText edtUser,edtMail,edtPass;
    Button btnSignUp;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://chatapp-videocall-default-rtdb.firebaseio.com/");
        myRef = database.getReference("Users");
        AnhXa();
        Event();
    }

    private void AnhXa(){
        txtSignIn = findViewById(R.id.textViewSignIn);
        txtResetPass =findViewById(R.id.textViewForgotPassword);
        edtUser =findViewById(R.id.editTextUserName_SignUp);
        edtMail =findViewById(R.id.editTextMail_SignUp);
        edtPass =findViewById(R.id.editTextPassword_SignUp);
        btnSignUp =findViewById(R.id.buttonSignUp);
    }

    private void Event(){
        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this,MainActivity.class));
            }
        });

        txtResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this,ForgotPasswordActivity.class));
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DangKi();
            }
        });

    }

    private void DangKi(){
        String userName = edtUser.getText().toString().trim();
        String email = edtMail.getText().toString().trim();
        String password = edtPass.getText().toString().trim();
        if(!userName.equals("") && !email.equals("") && !password.equals("")) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                String userid= firebaseUser.getUid();
                                myRef.child(userid);
                                HashMap<String,String> hashMap =new HashMap<>();
                                hashMap.put("id",userid);
                                hashMap.put("username",userName);
                                hashMap.put("email",email);
                                hashMap.put("phone","default");
                                hashMap.put("imageURL","default");
                                hashMap.put("birthDate","default");
                                hashMap.put("gender","default");
                                hashMap.put("home","default");
                                hashMap.put("local","default");
                                hashMap.put("status","offline");
                                hashMap.put("search",userName.toLowerCase());
                                hashMap.put("isMeet","default");

                                myRef.child(userid).setValue(hashMap);

                                edtMail.setText("");
                                edtPass.setText("");
                                edtUser.setText("");
                                Toast.makeText(SignUpActivity.this, "Sign Up Success", Toast.LENGTH_SHORT).show();
                            } else {
                                if(password.length() < 6) {
                                    Toast.makeText(SignUpActivity.this, "Failed , Password at least 6 characters", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(SignUpActivity.this, "Failed , email is not valid OR Email already exists", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
        }
        else{
            Toast.makeText(SignUpActivity.this, "Please fill out the information completely", Toast.LENGTH_SHORT).show();
        }
    }


}