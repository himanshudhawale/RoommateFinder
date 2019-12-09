package com.example.roommatefinder;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
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
        final Bitmap[] bitmaps = new Bitmap[4];
        final int dotCounts=post.imageList.size();
//        final ImageView[] dots= new ImageView[4];

        FirebaseDatabase.getInstance().getReference("users").child(post.userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                holder.name.setText(user.first + " " + user.last);
                holder.city.setText(post.city);
                holder.date.setText(post.date);
                Picasso.get()
                        .load(user.imageURL)
                        .into(holder.circular);


                ViewPagerAdapter adapter = new ViewPagerAdapter(mContext, post.imageList);
                holder.mImageViewPager.setAdapter(adapter);
                holder.mImageViewPager.setOffscreenPageLimit(4);



                for(int i=0; i<dotCounts; i++)
                {
                    holder.dotLayout.getTabAt(i).setIcon(R.drawable.nonactive_dot);
                }
                holder.dotLayout.getTabAt(0).setIcon(R.drawable.active_dot);


                holder.dotLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                    }
                });


                holder.mImageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        for(int i=0; i< dotCounts; i++)
                        {
//                          dots[i] = new ImageView(mContext);
                            holder.dotLayout.getTabAt(i).setIcon(R.drawable.nonactive_dot);
                        }
                        holder.dotLayout.getTabAt(position).setIcon(R.drawable.active_dot);
//                      Picasso.get().load(post.imageList.get(position)).into(dots[position]);

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });

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
        ViewPager mImageViewPager;
        TabLayout dotLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewNameItem);
            city = itemView.findViewById(R.id.textViewCityItem);
            date = itemView.findViewById(R.id.textViewDateItem);
            circular = itemView.findViewById(R.id.imageViewuserItem);
            mImageViewPager = itemView.findViewById(R.id.pager);
            dotLayout = itemView.findViewById(R.id.sliderDots);
            dotLayout.setupWithViewPager(mImageViewPager);

        }
    }
}
