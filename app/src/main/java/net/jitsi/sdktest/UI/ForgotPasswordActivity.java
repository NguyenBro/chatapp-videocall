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
import com.google.firebase.auth.FirebaseAuth;

import net.jitsi.sdktest.R;
//Reset password

public class ForgotPasswordActivity extends AppCompatActivity {
    TextView txtSignUp,txtSignIn;
    EditText edtEmail;      //Điền Gmail
    Button btnReset;        //Button xác nhận reset pass
    private FirebaseAuth auth ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        auth = FirebaseAuth.getInstance();
        AnhXa();
        Event();
    }
    private void AnhXa(){
        txtSignIn = findViewById(R.id.textViewSignIn_Reset);
        txtSignUp = findViewById(R.id.textViewSignUp_Reset);
        edtEmail = findViewById(R.id.editTextEmail_ResetPass);
        btnReset=findViewById(R.id.buttonResetPassWord);
    }
    private void Event() {
        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPasswordActivity.this,MainActivity.class));
            }
        });

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPasswordActivity.this,SignUpActivity.class));
            }
        });
        
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetPassword();
            }
        });
    }

    //Thwujc hiện gửi về gmail đã nhập
    private void ResetPassword(){
        String emailAddress = edtEmail.getText().toString().trim();
        if(!emailAddress.equals("")) {
            //Hàm hỗ trợ của firebase
            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this, getResources().getString(R.string.check_name) + emailAddress, Toast.LENGTH_SHORT).show();
                                edtEmail.setText("");
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}