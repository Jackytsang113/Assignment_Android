package com.example.assignment_300cem.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.assignment_300cem.CommentsActivity;
import com.example.assignment_300cem.GoogleMap.GoogleMapActivity;
import com.example.assignment_300cem.GoogleMap.ViewPostMapActivity;
import com.example.assignment_300cem.Model.Posts;
import com.example.assignment_300cem.Model.Users;
import com.example.assignment_300cem.PostsActivity;
import com.example.assignment_300cem.R;
import com.example.assignment_300cem.ViewPostActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PostsAdapter extends  RecyclerView.Adapter<PostsAdapter.ViewHolder>{

    public Context mContext;
    public List<Posts> mPosts;

    private FirebaseUser firebaseUser;

    public PostsAdapter(Context mContext, List<Posts> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public PostsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Posts posts = mPosts.get(position);

        Glide.with(mContext).load(posts.getPostImage()).into(holder.post_image);
        holder.location_name.setText(posts.getLocationName());
        holder.description.setText(posts.getDescription());

        isLiked(posts.getPostId(), holder.favorite);
        countLikes(posts.getPostId(), holder.likes);
        readSender(posts.getSender(), holder.sender);

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postId", posts.getPostId());
                intent.putExtra("senderId", posts.getSender());
                mContext.startActivity(intent);
            }
        });

        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.favorite.getTag().equals("Like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(posts.getPostId())
                            .child(firebaseUser.getUid()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Favorite").child(firebaseUser.getUid())
                            .child(posts.getPostId()).setValue(true);
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(posts.getPostId())
                            .child(firebaseUser.getUid()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Favorite").child(firebaseUser.getUid())
                            .child(posts.getPostId()).removeValue();
                }
            }
        });

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ViewPostMapActivity.class);
                intent.putExtra("LocationName", posts.getLocationName()); //put Location Name value to ViewPostMapActivity
                intent.putExtra("Latitude", posts.getMapLatitude()); //put Latitude value to ViewPostMapActivity
                intent.putExtra("Longitude", posts.getMapLongitude()); //put Longitude value to ViewPostMapActivity
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView post_image, favorite, comments;
        TextView location_name, description, sender, likes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            post_image = itemView.findViewById(R.id.post_image);
            favorite = itemView.findViewById(R.id.favorite);
            comments = itemView.findViewById(R.id.comment);

            location_name = itemView.findViewById(R.id.location_name);
            description = itemView.findViewById(R.id.description);
            sender = itemView.findViewById(R.id.sender);
            likes = itemView.findViewById(R.id.likes);

        }
    }

    private void isLiked(String postId, ImageView imageView){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(firebaseUser.getUid()).exists()){
                    //imageView.setColorFilter(imageView.getContext().getResources().getColor(R.color.colorLiked));
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("Liked");
                }else{
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void countLikes(String postId, final TextView likes){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount() + " Likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void readSender(String senderId, TextView sender){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(senderId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

                sender.setText(users.getUsername());
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }
}
