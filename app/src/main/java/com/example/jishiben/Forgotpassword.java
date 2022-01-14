package com.example.jishiben;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forgotpassword extends AppCompatActivity {

    private EditText forgotPassword;
    private Button passwordRecoverButton;
    private TextView goBackToLogin;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        getSupportActionBar().hide();

        forgotPassword = findViewById(R.id.forgotpassword);
        passwordRecoverButton = findViewById(R.id.passwordRecoverButton);
        goBackToLogin = findViewById(R.id.fg_gobacktologin);
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.forgotPasswordProgressBar);


        //返回登录界面
        goBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Forgotpassword.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //重置密码请求
        passwordRecoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = forgotPassword.getText().toString().trim();

                //邮箱为空
                if(mail.isEmpty()){
                    Toast.makeText(getApplicationContext(), "请输入邮箱", Toast.LENGTH_SHORT).show();
                }

                //否则调用firebase重置请求
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "已发送重置邮箱", Toast.LENGTH_SHORT).show();;
                                progressBar.setVisibility(View.INVISIBLE);
                                finish();
                                startActivity(new Intent(Forgotpassword.this, MainActivity.class));

                            }
                            else {
                                Toast.makeText(getApplicationContext(), "发送失败！", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }


            }
        });


    }
}