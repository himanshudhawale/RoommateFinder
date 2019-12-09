package com.example.roommatefinder;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends ArrayAdapter<Messages> {


    List<Messages> messagesList;

    String userId;

    DatabaseReference myref, myReff, myRefUser;


    String secondUserID;
    Context mContext;
    FirebaseUser firebaseUser;
    User user;
    Messages mtr;
    User goodUser;  //current User
    Adapter adapter;


    public MessageAdapter(@NonNull Context context, int resource, @NonNull List<Messages> objects, String id, String secondUserID, Adapter adapter) {
        super(context, resource, objects);
        this.messagesList = objects;
        this.userId = id;
        this.mContext=context;
        this.secondUserID = secondUserID;
        this.adapter = adapter;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        final Messages message = getItem(position);


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item, parent, false);

        }

        TextView textViewMsg = convertView.findViewById(R.id.textViewc1);
        TextView textViewName = convertView.findViewById(R.id.textViewc2);
        TextView textViewTime = convertView.findViewById(R.id.textViewc3);
        ImageView imageDelete = convertView.findViewById(R.id.imageViewdelete);
        final CircleImageView circular = convertView.findViewById(R.id.circularImageNavID);
        CardView cardView = convertView.findViewById(R.id.card_view);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(message.userID.equals(firebaseUser.getUid()))
        {
            cardView.setBackgroundColor(Color.parseColor("#B4E6F5"));
        }
        else
        {
            cardView.setBackgroundColor(Color.parseColor("#A7EDD6"));
        }

        myReff = FirebaseDatabase.getInstance().getReference("users").child(message.userID);

        myReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                Picasso.get()
                        .load(user.imageURL)
                        .into(circular);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        myRefUser= FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        myRefUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                goodUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        ImageView imageToSend = convertView.findViewById(R.id.imageView);

        imageToSend.setVisibility(View.GONE);


        if (!("abc".equals(message.url))) {
            imageToSend.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(message.url)
                    .into(imageToSend);


        }



        imageDelete.setVisibility(View.GONE);
        try {

            if (userId.equals(message.userID)) {
                imageDelete.setVisibility(View.VISIBLE);


                imageDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            // final Messages message = getItem(position);

                            String s = message.messageID;
                            String url = message.url;


                            delete(s, url);

                            messagesList.remove(position);
                            notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        } catch (Exception e) {
            //Log.d
        }


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

        Date date = new Date();
        try {
            date = simpleDateFormat.parse(messagesList.get(position).timeStamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(Calendar.HOUR, 0);
        Date updatedDate = gc.getTime();

        String prettyTime = new PrettyTime().format(updatedDate);

        textViewTime.setText(prettyTime);


        textViewMsg.setText(message.message);
        textViewName.setText(message.userName);


        return convertView;


    }

    public void delete(final String id, final String url) {
        myref = FirebaseDatabase.getInstance().getReference("chats").child(userId).child(secondUserID);
//
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if(!("abc".equals(url)))
                {

                    StorageReference mreference= FirebaseStorage.getInstance().getReferenceFromUrl(url);
                    mreference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("demo","Failed");
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dataSnapshot.child(id).getRef().removeValue();

                        }
                    });
                }
                else {
                    dataSnapshot.child(id).getRef().removeValue();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }






}