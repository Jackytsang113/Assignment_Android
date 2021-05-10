package com.example.assignment_300cem.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.assignment_300cem.Model.Users;
import com.example.assignment_300cem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class navHeaderAdapter extends RecyclerView.Adapter<navHeaderAdapter.ViewHolder> {
    private Context mContext;
    private List<Users> mUsers;

    private FirebaseUser firebaseUser;
    public navHeaderAdapter(Context mContext, List<Users> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public navHeaderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.navheader_layout, parent, false);
        return new navHeaderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull navHeaderAdapter.ViewHolder holder, int position) {
        System.out.println("Hello");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Users users = mUsers.get(position);
        Glide.with(mContext).load(users.getImage_url()).into(holder.image_profile);
        holder.username.setText(users.getUserId());
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView image_profile;
        public TextView username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
        }
    }
}
