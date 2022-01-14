package com.example.jishiben;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText loginMail, loginPassword;
    private RelativeLayout login, gotoSignup;
    private TextView gotoForgotPassword;
    ProgressBar progressBarOfMainActivity;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginMail = findViewById(R.id.loginemail);
        loginPassword = findViewById(R.id.loginpassword);
        login = findViewById(R.id.login);
        gotoForgotPassword = findViewById(R.id.gotoForgotPassword);
        gotoSignup = findViewById(R.id.gotosignup);
        progressBarOfMainActivity = findViewById(R.id.progressBarOfMainActivity);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseuser = firebaseAuth.getCurrentUser();

//        {
//            finish();
//            startActivity(new Intent(MainActivity.this, createNote.class));
//
//        }

        //如果用户已经登录，直接跳转到主事件
        //后面的代码不执行
        if(firebaseuser != null) {
            finish();
            startActivity(new Intent(MainActivity.this, notesActivity.class));
        }



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = loginMail.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();

                if(mail.isEmpty() || password.isEmpty()){
                    Toast.makeText(getApplicationContext(), "请输入账号和密码", Toast.LENGTH_SHORT).show();
                }

                //登录到firebase
                else {
                        progressBarOfMainActivity.setVisibility(View.VISIBLE);
                        firebaseAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBarOfMainActivity.setVisibility(View.INVISIBLE);
                                if(task.isSuccessful()){
                                    checkMailVerification();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "账号或密码错误！", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                }

            }
        });

        gotoSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, zhuce.class);
                startActivity(intent);
            }
        });

        gotoForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Forgotpassword.class);
                startActivity(intent);
            }
        });

    }

    //登录之后检查邮箱是否验证
    private void checkMailVerification() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser.isEmailVerified() == true){
            Toast.makeText(getApplicationContext(), "登录成功！", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(MainActivity.this, notesActivity.class));
        }
        else {
            //progressBarOfMainActivity.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "请先验证邮箱", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }

    }
}