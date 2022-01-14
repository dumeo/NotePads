package com.example.jishiben;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class zhuce extends AppCompatActivity {

    private EditText signupMail, signupPassword;
    private RelativeLayout signup;
    private TextView gotoLogin;
    ProgressBar progressBar;
    //添加firebase验证方式
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhuce);

        getSupportActionBar().hide();
        signupMail = findViewById(R.id.signupemail);
        signupPassword = findViewById(R.id.signuppassword);
        signup = findViewById(R.id.signup);
        gotoLogin = findViewById(R.id.zc_gobacktologin);
        progressBar = findViewById(R.id.signupProgressBar);

        //验证实体，负责在后端与谷歌firebase进行数据交互
        firebaseAuth = FirebaseAuth.getInstance();


        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(zhuce.this, MainActivity.class);
                startActivity(intent);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = signupMail.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();

                //密码或账号为空
                if(mail.isEmpty() || password.isEmpty()){
                    Toast.makeText(getApplicationContext(), "请输入账号和密码", Toast.LENGTH_SHORT).show();;

                }
                //密码小于7位
                else if(password.length() < 7){
                    Toast.makeText(getApplicationContext(), "密码位数必须大于7", Toast.LENGTH_SHORT).show();;

                }

                //连接到firebase注册用户
                else{
                    progressBar.setVisibility(View.VISIBLE);

                    firebaseAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            //成功连接到firebase,用户注册成功
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.INVISIBLE);
                                //验证用户的邮箱
                                sendEmailVerification();
                            }
                            else{
                                Log.e("Note", "onComplete: Failed=" + task.getException().getMessage());
                                Toast.makeText(getApplicationContext(), "注册失败！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
            }
        });







    }

    //发送验证邮箱
    private void sendEmailVerification() {

        //当前用户
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        //数据库中存在当前用户
        if(firebaseUser != null){

            //调用firebaseUser发送邮箱验证
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "验证已发送", Toast.LENGTH_SHORT).show();
                    Log.e("NOTE", "成功注册！");
                    //退出登录，等待用户验证完毕
                    firebaseAuth.signOut();
                    finish();
                }
            });
        }


        else{
            Toast.makeText(getApplicationContext(), "邮箱发送失败！", Toast.LENGTH_SHORT).show();
        }


    }
}