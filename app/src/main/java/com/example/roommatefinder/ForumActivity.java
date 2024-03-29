package com.example.roommatefinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ForumActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    FirebaseAuth mAuth;
    TextView userName;
    FirebaseUser currentUser;
    DatabaseReference myRef;
    User userValue;
    private OkHttpClient client1;

    ImageView userImage;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout drawerLayout;
    boolean mToolBarNavigationListenerIsRegistered= true;
    String customerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout= findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        userName =  navigationView.getHeaderView(0).findViewById(R.id.texViewuser);
        userImage = navigationView.getHeaderView(0).findViewById(R.id.imageViewuserItem);
        mAuth = FirebaseAuth.getInstance();
        currentUser =mAuth.getCurrentUser();

        client1 = new OkHttpClient();
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if(getIntent().getExtras()!=null)
        {
            customerID = getIntent().getExtras().getString("CUSTOMER_ID");
            FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid()).child("customerID").setValue(customerID);
        }

        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name)
            {

                public void onDrawerClosed(View view)
                {
                    supportInvalidateOptionsMenu();
//                    mToolBarNavigationListenerIsRegistered = false;

                }

                public void onDrawerOpened(View drawerView)
                {
                    supportInvalidateOptionsMenu();
//                    mToolBarNavigationListenerIsRegistered = true;

                }
            };
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            drawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ForumFragment()).commit();

            navigationView.setCheckedItem(R.id.nav_forum);
        }


        myRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userValue = dataSnapshot.getValue(User.class);
//                Log.d("demoM", "Value is: " + userValue);

                String name = userValue.first + " " + userValue.last;
//                Log.d("demoM", "Name is: " + name);
//                Log.d("demoM", "URL is: " + userValue.imageURL);

                ;
                userName.setText(name);
                Picasso.get()
                        .load(userValue.imageURL)
                        .into(userImage);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("demo", "Failed to read value.", error.toException());
            }

        });


    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }



    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId())
        {
            case R.id.nav_forum:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ForumFragment()).commit();
                break;

            case R.id.nav_mypost:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyPostFragment()).commit();
                break;

            case R.id.nav_savedposts:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SavedPostsFragment()).commit();
                break;

//            case R.id.nav_myprofile:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyProfileFragment()).commit();
//                break;
            //nav_newpost


            case R.id.nav_payment:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyPaymentFragment()).commit();
                break;


            case R.id.nav_chats:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatFragment()).commit();
                break;

//
            case R.id.nav_signout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ForumActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.nav_newpost:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NewPostFragment()).commit();
                break;



        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
