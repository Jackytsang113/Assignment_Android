package com.example.assignment_300cem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.assignment_300cem.Adapter.CommentsAdapter;
import com.example.assignment_300cem.Model.Comments;
import com.example.assignment_300cem.Model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentsAdapter commentsAdapter;
    private List<Comments> commentsList;

    ImageView image_profile, backpage;
    EditText write_comment;
    TextView post;

    String postsId;
    String senderId;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        image_profile = (ImageView) findViewById(R.id.image_profile);
        write_comment = (EditText) findViewById(R.id.write_comment);
        post = (TextView) findViewById(R.id.post);
        backpage = (ImageView) findViewById(R.id.backpage);

        Intent intent = getIntent();
        postsId = intent.getStringExtra("postId");
        senderId = intent.getStringExtra("senderId");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentsList = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(this, commentsList, postsId);
        recyclerView.setAdapter(commentsAdapter);

        backpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(write_comment.getText().toString().isEmpty()){
                    Toast.makeText(CommentsActivity.this, getResources().getString(R.string.empty_comment), Toast.LENGTH_SHORT).show();
                }else{
                    addComment();
                }
            }
        });

        getImage();
        readComments();
    }

    private void addComment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments")
                .child(postsId);

        String commentId = reference.push().getKey();
        String comment = write_comment.getText().toString();
        String sender = firebaseUser.getUid();

        Comments comments = new Comments(commentId , comment, sender);

        reference.child(commentId).setValue(comments);
        write_comment.setText("");
    }

    private void getImage(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                Glide.with(getApplicationContext()).load(users.getImage_url()).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readComments(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments")
                .child(postsId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Comments comment = snapshot.getValue(Comments.class);
                    commentsList.add(comment);
                }
                commentsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}