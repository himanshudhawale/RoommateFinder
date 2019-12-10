package com.example.roommatefinder;


import android.content.Intent;
import android.graphics.Canvas;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ForumFragment extends Fragment {
    View view;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    StorageReference storageRef;
    DatabaseReference postRef, savedPostRef, userRef;
    SwipeController swipeController = null;

    RecyclerView recyclerView;

    String keyword;

    User user;
    List<Post> postArrayList,filteredList;



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.option_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action1: {
                mAuth = FirebaseAuth.getInstance();
                currentUser = mAuth.getCurrentUser();
                database = FirebaseDatabase.getInstance();
                postRef = database.getReference("posts");
                userRef=database.getReference("users");
                recyclerView = view.findViewById(R.id.frame);
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            User  dummy = dataSnapshot1.getValue(User.class);
                            if(dummy.uID==currentUser.getUid())
                            {
                                user=dummy;
                                break;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                postArrayList=new ArrayList<>();
                postRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        postArrayList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Post post = dataSnapshot1.getValue(Post.class);
                            postArrayList.add(post);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                final Location userLoc=new Location("");
                userLoc.setLatitude(user.latitude);
                userLoc.setLongitude(user.longitude);
                Collections.sort(postArrayList, new Comparator<Post>() {
                    @Override
                    public int compare(Post post, Post t1) {
                        Location a=new Location("");
                        a.setLatitude(post.lat);
                        a.setLongitude(post.lng);
                        Location b=new Location("");
                        b.setLatitude(post.lat);
                        b.setLongitude(post.lng);
                        double distance1= userLoc.distanceTo(a)/1000;
                        double distance2= userLoc.distanceTo(b)/1000;
                        return (int)(distance1-distance2);
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
                            postArrayList.add(post);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                Collections.sort(postArrayList, new Comparator<Post>() {
                    @Override
                    public int compare(Post post1, Post post2) {
                        return post1.date.compareTo(post2.date);
                    }
                });
                final PostAdapter postAdapter = new PostAdapter(view.getContext(), R.layout.post_item, filteredList);
                recyclerView.setAdapter(postAdapter);
                return false;
            }
            case R.id.action3: {
//                AlertDialog.Builder alertDialog = new AlertDialog.Builder();
//                alertDialog.setTitle("Keyword Filter");
//                alertDialog.setMessage("Enter keyword to filter");
//
//                final EditText input = new EditText(getContext());
//                input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
//
//                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        LinearLayout.LayoutParams.MATCH_PARENT);
//                input.setLayoutParams(lp);
//                alertDialog.setView(input);
//                alertDialog.setIcon(R.drawable.money);
//
//                alertDialog.setPositiveButton("OKAY",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                keyword = input.getText().toString();
//                                if (!keyword.equals("")) {
//                                    Toast.makeText(view.getContext(),
//                                            "Selected Keyword is  " + keyword, Toast.LENGTH_SHORT).show();
//
//                                }
//                            }
//                        });
//
//                alertDialog.setNegativeButton("CANCEL",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });
//
//                alertDialog.show();
                Log.d("The array list size is",String.valueOf(postArrayList.size()));
                filteredList= new ArrayList<>();
                for(Post p:postArrayList)
                {
                    if(p.toString().contains("female"))
                        filteredList.add(p);
                }
                final PostAdapter postAdapter = new PostAdapter(view.getContext(), R.layout.post_item, filteredList);
                recyclerView.setAdapter(postAdapter);
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
                final PostAdapter postAdapter = new PostAdapter(view.getContext(), R.layout.post_item, postArrayList);
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
