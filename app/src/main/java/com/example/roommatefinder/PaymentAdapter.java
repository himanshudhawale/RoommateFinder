package com.example.roommatefinder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.google.android.material.tabs.TabLayout;
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
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder>  {


    Context mContext;
    List<Payment> paymentList;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference postRef;
    Post post;
    User user;
    private String clientToken;
    private Fragment fragment;
    OkHttpClient client;
    PaymentInterface paymentInterface;




    public PaymentAdapter(Context context, int post_item, List<Payment> listOfPayment, Fragment fragment, PaymentInterface paymentInterface) {

        this.paymentList = listOfPayment;
        this.mContext = context;
        this.fragment = fragment;
        this.paymentInterface = paymentInterface;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.payment_item, parent,
                false);
        PaymentAdapter.ViewHolder vh = new PaymentAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Payment payment = paymentList.get(position);
        database = FirebaseDatabase.getInstance();
        postRef = database.getReference("posts").child(payment.postID);
        client = new OkHttpClient();

        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                post = dataSnapshot.getValue(Post.class);


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
//                            dots[i] = new ImageView(mContext);

                                    holder.dotLayout.getTabAt(i).setIcon(R.drawable.nonactive_dot);

                                }
                                holder.dotLayout.getTabAt(position).setIcon(R.drawable.active_dot);

//                        Picasso.get().load(post.imageList.get(position)).into(dots[position]);
                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {

                            }
                        });


                        //HEre
                        String url = "http://ec2-3-94-187-73.compute-1.amazonaws.com:5000/brain/token";

                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        JSONObject jsonObject = new JSONObject();

                        try {
                            jsonObject.put("customerID",user.customerID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String jsonString=jsonObject.toString();
                        RequestBody requestBody = RequestBody.create(JSON, jsonString);


                        final Request request = new Request.Builder()
                                .url(url)
                                .post(requestBody)
                                .build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                Log.d("demooo respo", response.toString());
                                String json = response.body().string();
                                JSONObject root = null;
                                try {
                                    root = new JSONObject(json);
                                    clientToken= root.getString("clientToken");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });







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




        holder.payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DropInRequest dropInRequest = new DropInRequest()
                        .clientToken(clientToken);

                //payment amount
                //payment.amount
                paymentInterface.sendPayment(payment.amount, post.pid, post.paymentID);
                Log.d("interface", "onClick: "+payment.amount);



                fragment.startActivityForResult(dropInRequest.getIntent(mContext), 1);
            }
        });


        holder.cancelPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Canceled", Toast.LENGTH_SHORT).show();


            }
        });



    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView city;
        TextView date;
        CircleImageView circular;
        ViewPager mImageViewPager;
        TabLayout dotLayout;
        Button payNow, cancelPay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewNameItem);
            city = itemView.findViewById(R.id.textViewCityItem);
            date = itemView.findViewById(R.id.textViewDateItem);
            circular = itemView.findViewById(R.id.imageViewuserItem);
            mImageViewPager = itemView.findViewById(R.id.pager);
            dotLayout = itemView.findViewById(R.id.sliderDots);
            dotLayout.setupWithViewPager(mImageViewPager);
            payNow =itemView.findViewById(R.id.buttonPayID);
            cancelPay = itemView.findViewById(R.id.buttonCancel);
        }
    }








}
