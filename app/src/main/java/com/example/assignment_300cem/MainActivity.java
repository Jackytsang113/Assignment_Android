package com.example.assignment_300cem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.TestLooperManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.assignment_300cem.Adapter.navHeaderAdapter;
import com.example.assignment_300cem.Model.Users;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{

    ImageView menu_icon ;
    RoundedImageView navImage_profile;
    NavigationView nav_view;
    TextView navUsername,navEmail;

    Fragment selectedFragment = null;
    NavController navController;

    private FirebaseUser firebaseUser;
    private List<Users> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get currently logged-in in user data
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final DrawerLayout  drawerLayout = findViewById(R.id.drawer_layout);
        menu_icon = (ImageView) findViewById(R.id.menu_icon);
        nav_view = (NavigationView) findViewById(R.id.navigation_view);

        // change the menu icon color
        nav_view.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));

        View headerView = nav_view.getHeaderView(0);
        navUsername = (TextView) headerView.findViewById(R.id.username);
        navEmail = (TextView) headerView.findViewById(R.id.email);
        navImage_profile = (RoundedImageView) headerView.findViewById(R.id.image_profile);

        readUser(); // run readUser

        //setNavigationItemSelectedListener in onCreate of Activity
        if(nav_view != null){
            System.out.println("Test");
//            nav_view.setNavigationItemSelectedListener(this);
        }

        // switch fragment page
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(nav_view, navController);


        menu_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    //get the currently logged-in user data in firebase
    private void readUser(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid()); // get the current data in firebase

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

                //put the current users profile image in to navImage_profile
                Glide.with(MainActivity.this).load(users.getImage_url()).into(navImage_profile);
                navUsername.setText(users.getUsername());
                navEmail.setText(users.getEmail());
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    //NavigationView click event to item
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        if(id == R.id.menuLogout){ //check menu login to Login account
            FirebaseAuth.getInstance().signOut(); // logout account in firebase
            startActivity(new Intent(MainActivity.this, LoginActivity.class)); //back to login page
            return true;
        }

        return true;
    }
}