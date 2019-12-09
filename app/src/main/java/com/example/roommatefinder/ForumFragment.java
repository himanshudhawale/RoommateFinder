package com.example.roommatefinder;


import android.content.Intent;
import android.graphics.Canvas;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
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
import java.util.List;


public class ForumFragment extends Fragment {
    View view;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    StorageReference storageRef;
    DatabaseReference postRef, savedPostRef;
    SwipeController swipeController = null;

    List<Post> postArrayList;
    RecyclerView recyclerView;


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
                    postArrayList.add(post);


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
