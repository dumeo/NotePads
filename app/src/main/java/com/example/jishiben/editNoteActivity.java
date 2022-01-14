package com.example.jishiben;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class editNoteActivity extends AppCompatActivity {

    Intent data;
    EditText editTitleOfNote, editContentOfNote;
    FloatingActionButton saveEditNote;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        editTitleOfNote = findViewById(R.id.editTileOfNote);
        editContentOfNote = findViewById(R.id.editContentOfNote);
        saveEditNote = findViewById(R.id.saveEditNote);
//        Toolbar toolbar = findViewById(R.id.toolBarOfEditNote);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();



        data = getIntent();////////////////////////////////
        String title = data.getStringExtra("title");
        String content = data.getStringExtra("content");
        editTitleOfNote.setText(title);
        editContentOfNote.setText(content);
        saveEditNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTitle = editTitleOfNote.getText().toString();
                String newContent = editContentOfNote.getText().toString();
                Log.e("edit", newContent);

                if(newTitle.isEmpty()){
                    Toast.makeText(getApplicationContext(), "请输入标题!", Toast.LENGTH_SHORT).show();
                }
                else {
                    DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(data.getStringExtra("noteId"));
                    Map<String, Object> note = new HashMap<>();
                    note.put("title", newTitle);
                    note.put("content", newContent);

                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //Toast.makeText(getApplicationContext(), "更新成功！", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(view.getContext(), notesActivity.class));

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "更新失败", Toast.LENGTH_SHORT).show();
                        }
                    });

                }



            }
        });



    }
}