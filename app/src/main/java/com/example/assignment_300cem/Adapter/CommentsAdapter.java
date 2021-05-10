package com.example.assignment_300cem.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.assignment_300cem.Model.Comments;
import com.example.assignment_300cem.Model.Users;
import com.example.assignment_300cem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CommentsAdapter extends  RecyclerView.Adapter<CommentsAdapter.ViewHolder>{
    private Context mContext;
    private List<Comments> mComment;
    private String postId;

    private FirebaseUser firebaseUser;

    public CommentsAdapter(Context mContext, List<Comments> mComment, String postId) {
        this.mContext = mContext;
        this.mComment = mComment;
        this.postId = postId;
    }

    @NonNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comments_item, parent, false);
        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Comments comments = mComment.get(position);

        holder.comment.setText(comments.getComment());

        readUser(holder.username, holder.image_profile, comments.getSender());

        // Delete Comment
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(comments.getSender().equals(firebaseUser.getUid())){
                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle(mContext.getResources().getString(R.string.delect_comment));
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, mContext.getResources().getString(R.string.no),
                            new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i){
                                    dialogInterface.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getResources().getString(R.string.yes),
                            new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i){
                                    deleteCommand(postId, comments.getCommentId());
                                    dialogInterface.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image_profile;
        public TextView username, comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
        }
    }

    private void readUser(TextView username, ImageView image_profile, String senderId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(senderId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class) ;
                Glide.with(mContext).load(users.getImage_url()).into(image_profile);
                username.setText(users.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteCommand(String postId, String commendId){
        FirebaseDatabase.getInstance().getReference("Comments")
                .child(postId)
                .child(commendId)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.comment_deleted), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
