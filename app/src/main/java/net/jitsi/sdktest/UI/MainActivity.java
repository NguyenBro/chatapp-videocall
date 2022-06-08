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
    EditText edtMail,edtPass;
    Button btnSignIn;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onStart() {
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

        //showContacts();

//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
//                == PackageManager.PERMISSION_GRANTED) {
//            getAllContacts();
//        } else {
//            requestPermission();
//        }

//        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
//        while (phones.moveToNext())
//        {
//            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//            Log.d("AAA", name + phoneNumber);
//
//        }
//        phones.close();



    }



    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            getContactNames();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getContactNames(){
        ContentResolver cr = getContentResolver();
        // Get the Cursor of all the contacts
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        // Move the cursor to first. Also check whether the cursor is empty or not.
        if (cursor.moveToFirst()) {
            // Iterate through the cursor
            do {
                // Get the contacts name
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                @SuppressLint("Range") String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                if(Integer.parseInt(phone) >=1){
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        @SuppressLint("Range") String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.d("AAA"," name :" + name + " phone: "+ phoneNo);
                    }
                    pCur.close();
                }

            } while (cursor.moveToNext());
        }
        // Close the curosor
        cursor.close();

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
                            Toast.makeText(MainActivity.this, "Successfull", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this,MainChatAppActivity.class));
                            //startActivity(new Intent(MainActivity.this,MainChatAppActivity.class));
                        } else {
                            Toast.makeText(MainActivity.this, "Email OR Password is not correct", Toast.LENGTH_SHORT).show();
                            edtPass.setText("");
                        }
                    }
                });
    }



}