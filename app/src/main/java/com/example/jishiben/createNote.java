package com.example.jishiben;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class createNote extends AppCompatActivity {

    EditText createTitleOfNote, createContentOfNote;
    FloatingActionButton saveNote;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;

    ProgressBar progressBarOfCreateNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        saveNote = findViewById(R.id.saveNote);
        createContentOfNote = findViewById(R.id.createContentOfNote);
        createTitleOfNote = findViewById(R.id.createTileOfNote);
        progressBarOfCreateNote = findViewById(R.id.progressBarOfCreateNote);

        firebaseAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolBarOfCreateNote);
        setSupportActionBar(toolbar);
        //******
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseFirestore = FirebaseFirestore.getInstance();

        //取得现用户信息
        firebaseUser = firebaseAuth.getCurrentUser();


        //保存数据监听事件
        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = createTitleOfNote.getText().toString();
                String content = createContentOfNote.getText().toString();
                if(title.isEmpty() || content.isEmpty()){
                    Toast.makeText(getApplicationContext(), "请输入标题或内容", Toast.LENGTH_SHORT).show();
                }
                //将数据存入firebase数据库
                else {

                    progressBarOfCreateNote.setVisibility(View.VISIBLE);

                    //取出对应用户的数据表项
                    DocumentReference documentReference =
                            firebaseFirestore.collection("notes").
                                    document(firebaseUser.getUid()).
                                    collection("myNotes").document();

                    //以map的形式保存到数据库表
                    Map<String, Object> note = new HashMap<>();

                    note.put("title", title);
                    note.put("content", content);

                    //写入操作
                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressBarOfCreateNote.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "笔记创建成功！", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(createNote.this, notesActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "笔记创建失败！", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }
        });






    }
}