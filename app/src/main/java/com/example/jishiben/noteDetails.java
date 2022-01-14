package com.example.jishiben;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class noteDetails extends AppCompatActivity {

    private TextView titleOfNoteDetail, contentOfNoteDetail;
    FloatingActionButton gotoEditNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        titleOfNoteDetail = findViewById(R.id.titleOfNoteDetail);
        contentOfNoteDetail = findViewById(R.id.contentOfNoteDetail);
        gotoEditNote = findViewById(R.id.gotoEditNote);
        Toolbar toolbar = findViewById(R.id.toolBarOfEditNote);
        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //取出源activity传进来的数据
        Intent data = getIntent();
        gotoEditNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), editNoteActivity.class);
                intent.putExtra("title", data.getStringExtra("title"));
                intent.putExtra("content", data.getStringExtra("content"));
                intent.putExtra("noteId", data.getStringExtra("noteId"));
                startActivity(intent);
            }
        });

        //设置标题和内容
        titleOfNoteDetail.setText(data.getStringExtra("title"));
        contentOfNoteDetail.setText(data.getStringExtra("content"));



    }
}