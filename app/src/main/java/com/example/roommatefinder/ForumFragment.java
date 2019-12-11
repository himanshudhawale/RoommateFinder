package com.example.roommatefinder;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ForumFragment extends Fragment {
    View view;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    StorageReference storageRef;
    DatabaseReference postRef, savedPostRef, userRef;
    SwipeController swipeController = null;

    PostAdapter postAdapter;
    RecyclerView recyclerView;

    String keyword;
    User user;
    List<Post> postArrayList,filteredList;
     List<Double> list;


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.option_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);

    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action1: {
                mAuth = FirebaseAuth.getInstance();
                currentUser = mAuth.getCurrentUser();
                database = FirebaseDatabase.getInstance();
                postRef = database.getReference("posts");
                userRef=database.getReference("users").child(currentUser.getUid());
                recyclerView = view.findViewById(R.id.frame);
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            user = dataSnapshot.getValue(User.class);

                        final Location userLoc=new Location("");
                        userLoc.setLatitude(user.latitude);
                        userLoc.setLongitude(user.longitude);


//                        postArrayList=new ArrayList<>();
                        postRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                postArrayList = new ArrayList<>();
                                list = new ArrayList<>();
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    Post post = dataSnapshot1.getValue(Post.class);
                                    if(post.status.equals("available")) {
                                        postArrayList.add(post);

                                    }
                                }
                                Collections.sort(postArrayList, new Comparator<Post>() {
                                    @Override
                                    public int compare(Post post, Post t1) {
                                        Location a=new Location("");
                                        a.setLatitude(post.lat);
                                        a.setLongitude(post.lng);
                                        Location b=new Location("");
                                        b.setLatitude(post.lat);
                                        b.setLongitude(post.lng);
                                        double distanceToPlace1 = distance(user.latitude, user.longitude, post.lat, post.lng);
                                        double distanceToPlace2 = distance(user.latitude, user.longitude, t1.lat, t1.lng);



                                        return (int) (distanceToPlace1 - distanceToPlace2);

                                    }
                                });




                                Log.d("inside menu", postArrayList.toString());
                                Log.d("inside menu", list.toString());
                                postAdapter = new PostAdapter(getContext(), R.layout.post_item, postArrayList);
                                recyclerView.setAdapter(postAdapter);
                                postAdapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });



                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });




                return false;
            }
            case R.id.action2: {
                database = FirebaseDatabase.getInstance();
                postRef = database.getReference("posts");
                recyclerView = view.findViewById(R.id.frame);
                postArrayList=new ArrayList<>();
                postRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        postArrayList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Post post = dataSnapshot1.getValue(Post.class);
                            if(post.status.equals("available"))
                            {
                                postArrayList.add(post);
                            }
                        }
                        Collections.sort(postArrayList, new Comparator<Post>() {
                            public int compare(Post o1, Post o2) {
                                DateFormat format = new SimpleDateFormat("MM/dd/yyyy");

                                Date date1 = null;
                                Date date2 = null;
                                try {
                                    date1=format.parse(o1.date);
                                    date2=format.parse(o2.date);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                return date1.compareTo(date2);
                            }
                        });

                        postAdapter = new PostAdapter(getContext(), R.layout.post_item, postArrayList);
                        recyclerView.setAdapter(postAdapter);
                        postAdapter.notifyDataSetChanged();



                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                return false;
            }
            case R.id.action3: {

                database = FirebaseDatabase.getInstance();
                postRef = database.getReference("posts");
                recyclerView = view.findViewById(R.id.frame);
                postArrayList=new ArrayList<>();

                postRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Post post = dataSnapshot1.getValue(Post.class);
                            if(post.status.equals("available"))
                            {
                                postArrayList.add(post);
                            }
                        }



                        ///here
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                        alertDialog.setTitle("Keyword Filter");
                        alertDialog.setMessage("Enter keyword to filter");

                        final EditText input = new EditText(getContext());
                        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);

                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                        input.setLayoutParams(lp);
                        alertDialog.setView(input);
                        alertDialog.setIcon(R.drawable.money);

                        alertDialog.setPositiveButton("OKAY",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        keyword = input.getText().toString();
                                        if (!keyword.equals("")) {
                                            Toast.makeText(view.getContext(),
                                                    "Selected Keyword is  " + keyword, Toast.LENGTH_SHORT).show();

                                            Log.d("The array list size is",String.valueOf(postArrayList.size()));
                                            filteredList= new ArrayList<>();
                                            for(Post p:postArrayList)
                                            {
                                                if(p.gender.toLowerCase().equals(keyword.toLowerCase()))
                                                    filteredList.add(p);
                                            }
                                            postAdapter = new PostAdapter(view.getContext(), R.layout.post_item, filteredList);
                                            recyclerView.setAdapter(postAdapter);
                                            postAdapter.notifyDataSetChanged();

                                        }
                                    }
                                });

                        alertDialog.setNegativeButton("CANCEL",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                        alertDialog.show();




                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //filetr post
                return false;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_forum,container,false);
        setHasOptionsMenu(true);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        postRef = database.getReference("posts");
        savedPostRef = database.getReference("savedposts").child(currentUser.getUid());
        recyclerView = view.findViewById(R.id.frame);



        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postArrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Post post = dataSnapshot1.getValue(Post.class);
                    if(post==null)
                    {
                        //
                    }
                    else if(post.status.equals("available"))
                    {
                        postArrayList.add(post);
                    }

                }
                LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(llm);
                postAdapter = new PostAdapter(view.getContext(), R.layout.post_item, postArrayList);
                recyclerView.setAdapter(postAdapter);




                swipeController = new SwipeController(new SwipeControllerActions() {
                    @Override
                    public void onRightClicked(int position) {
                        Post post = postAdapter.postList.get(position);

                        savedPostRef.child(post.pid).setValue(post);

                        // ***********************
                        // ***********************
                        // ***********************
                        // *******IMPORTANT*******
                        // ***********************
                        // ***********************
                        // ***********************
                        //once saved do not let the user save this post again




                    }
                    @Override
                    public void onLeftClicked(int position){
                        // ***********************
                        // ***********************
                        // ***********************
                        // *******IMPORTANT*******
                        // ***********************
                        // ***********************
                        // ***********************
                        // Start chatting but also send the post object
                        Post post = postAdapter.postList.get(position);

                        if(post.userID.equals(currentUser.getUid()))
                        {
                            Toast.makeText(getContext().getApplicationContext(), "Disabled for post owner", Toast.LENGTH_SHORT).show();
                        } else {

//                        FragmentTransaction t = getFragmentManager().beginTransaction();
//                        Fragment mFrag = new MyFragment();
//                        t.replace(R.id.content_frame, mFrag);
//                        t.commit();
                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.putExtra("FORUM_FRAGMENT_KEY", post.pid);
                            startActivity(intent);
                        }

                    }

                });

                ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
                itemTouchhelper.attachToRecyclerView(recyclerView);

                recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                    @Override
                    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                        swipeController.onDraw(c);
                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }


}
