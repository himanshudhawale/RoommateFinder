package com.example.roommatefinder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    Context mContext;
    List<Post> postList;
    String userId;
    User user;

    public PostAdapter(Context context, int post_item, List<Post> postArrayList) {

        this.postList = postArrayList;
        this.mContext = context;
//        this.userId = uid;
//        Log.d("demooooo", String.valueOf(postList.size()));
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent,
                        false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Post post = postList.get(position);

        FirebaseDatabase.getInstance().getReference("users").child(post.userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                holder.name.setText(user.first + " " + user.last);
                holder.city.setText(post.city);
                holder.date.setText(post.date);

//                Log.d("demoooo", post.city);
//                Log.d("demooooo",user.first);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
//        Log.d("demooooool", String.valueOf(postList.size()));
        return postList.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView city;
        TextView date;
        CircleImageView circular;
        ViewPager mImageViewPager;
        TabLayout tabLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewNameItem);
            city = itemView.findViewById(R.id.textViewCityItem);
            date = itemView.findViewById(R.id.textViewDateItem);
            circular = itemView.findViewById(R.id.imageViewuserItem);
            mImageViewPager = itemView.findViewById(R.id.pager);
            tabLayout =itemView.findViewById(R.id.tabDots);
            tabLayout.setupWithViewPager(mImageViewPager, true);



        }
    }
}
