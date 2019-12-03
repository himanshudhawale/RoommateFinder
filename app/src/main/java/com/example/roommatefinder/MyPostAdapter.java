package com.example.roommatefinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.ViewHolder>{

    Context mContext;
    List<Post> postList;
    String userId;
    User user;

    public MyPostAdapter(Context context, int post_item, List<Post> postArrayList) {

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
        MyPostAdapter.ViewHolder vh = new MyPostAdapter.ViewHolder(v);
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
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView city;
        TextView date;
        CircleImageView circular;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewNameItem);
            city = itemView.findViewById(R.id.textViewCityItem);
            date = itemView.findViewById(R.id.textViewDateItem);
            circular = itemView.findViewById(R.id.imageViewuserItem);

        }
    }
}
