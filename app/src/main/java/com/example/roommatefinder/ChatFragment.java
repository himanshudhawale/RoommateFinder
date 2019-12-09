package com.example.roommatefinder;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import de.hdodenhof.circleimageview.CircleImageView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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


public class ChatFragment extends Fragment {


    public ChatFragment() {
        // Required empty public constructor
    }


    FirebaseUser currentUser;
    FirebaseDatabase database;
    FirebaseAuth mAuth;

    ArrayAdapter<Messages> adapter;
    ArrayList<Messages> messageList = new ArrayList<>();
    ListView listViewChats;
    User userValue, globalUser;
    DatabaseReference myRef;

    View view;
    List<String> userIDS;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_chat,container,false);
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        listViewChats = view.findViewById(R.id.listViewOfChats);

        myRef = database.getReference("mychats").child(currentUser.getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList = new ArrayList<>();
                userIDS = new ArrayList<>();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    userIDS.add(dataSnapshot1.getKey());
                    for(DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren())
                    {
                        Messages message = dataSnapshot2.getValue(Messages.class);
                        messageList.add(message);
                    }
                }



                LastMessageAdapter lastMessageAdapter = new LastMessageAdapter(view.getContext(), R.layout.message_item, messageList, currentUser.getUid(), adapter);
                listViewChats.setAdapter(lastMessageAdapter);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listViewChats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String strID = userIDS.get(i);

                Intent intent = new Intent(view.getContext(), ChatActivity.class);
                intent.putExtra("CHAT_FRAGMENT_KEY", strID);
                startActivity(intent);
            }
        });



        return view;
    }




}
