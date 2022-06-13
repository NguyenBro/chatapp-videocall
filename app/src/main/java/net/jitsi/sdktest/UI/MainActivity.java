package net.jitsi.sdktest.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
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

import net.jitsi.sdktest.R;
//Activity Login

public class MainActivity extends AppCompatActivity {
    TextView txtSignUp,txtResetPass;
    EditText edtMail,edtPass;       //Gồm Email và password
    Button btnSignIn;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onStart() {
        //Kiểm tra đăng nhập , nếu đã có tài khoản thì k cần đăng nhập lại
        super.onStart();
        //Log.d("TTT","5");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //Log.d("TTT","6");

        if (firebaseUser != null) {
            Intent intent = new Intent(MainActivity.this, MainChatAppActivity.class);
            startActivity(intent);
            finish();
            //Log.d("TTT","7");
        }

        //Log.d("TTT","8");


    }

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        AnhXa();
        Event();



    }

    private void AnhXa(){

        txtSignUp = findViewById(R.id.textViewSignUp);
        edtMail=findViewById(R.id.editTextMail_SignIn);
        edtPass =findViewById(R.id.editTextPassword_SignIn);
        btnSignIn =findViewById(R.id.buttonSignIn);
        txtResetPass = findViewById(R.id.textViewResetPass_SignIn);
    }
    private void Event(){
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SignUpActivity.class));
                //Toast.makeText(MainActivity.this, "gfg", Toast.LENGTH_SHORT).show();
            }
        });

        txtResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ForgotPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DangNhap();
            }
        });
    }

    //Thực hiện đăng nhập và xác thực theo firebase
    private void DangNhap(){
        String email = edtMail.getText().toString();
        String password = edtPass.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.success_in), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this,MainChatAppActivity.class));
                            //startActivity(new Intent(MainActivity.this,MainChatAppActivity.class));
                        } else {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.email_password), Toast.LENGTH_SHORT).show();
                            edtPass.setText("");
                        }
                    }
                });
    }



}