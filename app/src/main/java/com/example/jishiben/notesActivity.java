package com.example.jishiben;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class notesActivity extends AppCompatActivity {

    FloatingActionButton createNoteFab;
    FirebaseAuth firebaseAuth;
    //记事本视图的创建
    RecyclerView recyclerView;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder> noteAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        getSupportActionBar().setTitle("所有记事");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();


        createNoteFab = findViewById(R.id.createNoteFab);
        createNoteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(notesActivity.this, createNote.class));
            }
        });

        //拉取数据库数据
        Query query = firebaseFirestore.collection("notes").
                document(firebaseUser.getUid()).collection("myNotes").
                orderBy("title", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<firebasemodel> allusernotes = new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query, firebasemodel.class).build();


        //note适配器
        noteAdapter = new FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder>(allusernotes) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull firebasemodel firebasemodel) {
                noteViewHolder.notetitle.setText(firebasemodel.getTitle());
                noteViewHolder.notecontent.setText(firebasemodel.getContent());

                //设置笔记ID，有利于修改和删除
                String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();

                //菜单选项
                ImageView popupButton = noteViewHolder.itemView.findViewById((R.id.menuPopButton));


                //设置颜色随机
                int colorCode = getRandomColor();
                noteViewHolder.note.setBackgroundColor((noteViewHolder.itemView.getResources().getColor(colorCode, null)));

                //设置每个记事的监听器
                noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //打开记事详情
                        Intent intent = new Intent(view.getContext(), noteDetails.class);

                        //传入ID和主题、内容
                        intent.putExtra("title", firebasemodel.getTitle());
                        intent.putExtra("content", firebasemodel.getContent());
                        intent.putExtra("noteId", docId);

                        view.getContext().startActivity(intent);


                    }
                });

            //设置小菜单监
                popupButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                        popupMenu.setGravity(Gravity.END);
                        
                        //编辑
                        popupMenu.getMenu().add("编辑").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                //编辑记事本的activity
                                Intent intent = new Intent(view.getContext(), editNoteActivity.class);

                                //传入ID和主题、内容
                                intent.putExtra("title", firebasemodel.getTitle());
                                intent.putExtra("content", firebasemodel.getContent());
                                intent.putExtra("noteId", docId);
                                view.getContext().startActivity(intent);

                                return true;
                            }
                        });
                        
                        //删除
                        popupMenu.getMenu().add("删除").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                //删除记事本的activity
                                DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(docId);
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(view.getContext(), "删除失败！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return true;
                            }
                        });

                        popupMenu.show();
                        
                    }
                });








            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes, parent, false);
                return new NoteViewHolder(view);
            }
        };


        //设置recycler view适配器
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(noteAdapter);




    }

    //创建菜单栏，执行退出登录

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    //选择了具体的菜单项


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout:

                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(notesActivity.this, MainActivity.class));

                break;

        }

        return super.onOptionsItemSelected(item);
    }



    public class NoteViewHolder extends RecyclerView.ViewHolder{
        private TextView notetitle;
        private TextView notecontent;
        LinearLayout note;

        public NoteViewHolder(@NonNull View itemView){
            super(itemView);
            notetitle = itemView.findViewById(R.id.noteTitle);
            notecontent = itemView.findViewById(R.id.noteContent);
            note = itemView.findViewById(R.id.note);
        }
    }

    @Override
    protected  void onStart(){
        super.onStart();
        noteAdapter.startListening();

    }

    @Override
    protected  void onStop(){
        super.onStop();
        if(noteAdapter != null){
            Log.e("NOte", "ada not null");
            noteAdapter.stopListening();
        }
    }


    //随机取得颜色
    private int getRandomColor(){
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.white2);
        colorCode.add(R.color.white1);
        colorCode.add(R.color.white3);
        colorCode.add(R.color.white4);
        colorCode.add(R.color.white5);
        colorCode.add(R.color.white6);
        colorCode.add(R.color.white7);
        colorCode.add(R.color.white8);

        Random random = new Random();
        int i = random.nextInt(colorCode.size());
        return colorCode.get(i);



    }








}