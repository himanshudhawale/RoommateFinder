package com.example.roommatefinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    String postID = "";

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String getUrl;
    FirebaseDatabase database;
    StorageReference storageRef;
    DatabaseReference postRef, myRefUser, myRefToChat, myRefToChat2,  myRefToMyChats, myRefToMyChats2, myRefToPayment;


    Messages lastMessage;

    ArrayAdapter<Messages> adapter;
    ArrayList<Messages> messageList = new ArrayList<>();

    public static Context contextOfApplication;

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }

    EditText messageToSend;
    ListView listViewChats;
    User userValue, globalUser;
    Post post;
    ImageView imageViewsend, imageAdd;
    private static final int REQUEST_IMAGE_SELECTOR = 1;
    int image = 0;
    boolean flag = false, newFlag = false;

    String secondUserID="";
    Bitmap bitmapToSend = null;
    String numberAmount="0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();


        messageToSend = findViewById(R.id.editTextMessageId);
        listViewChats = findViewById(R.id.listViewIdchat);
        imageViewsend = findViewById(R.id.imageViewSend);
        imageAdd = findViewById(R.id.imageAdd);

        listViewChats.setLongClickable(true);

        myRefUser = database.getReference("users").child(currentUser.getUid());





        if (getIntent().getExtras() != null) {
            if(getIntent().getExtras().containsKey("FORUM_FRAGMENT_KEY")) {
                postID = getIntent().getExtras().getString("FORUM_FRAGMENT_KEY");
                postRef = database.getReference("posts").child(postID);



                postRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        post = dataSnapshot.getValue(Post.class);


                        myRefToChat = database.getReference("chats").child(currentUser.getUid()).child(post.userID);

                        myRefToChat.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ArrayList<Messages> getMessageList = new ArrayList<>();
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    {
                                        Messages message = child.getValue(Messages.class);
                                        getMessageList.add(message);
                                    }
                                    messageList = getMessageList;
                                    adapter = new MessageAdapter(ChatActivity.this, R.layout.message_item, messageList, currentUser.getUid(), post.userID, adapter);
                                    listViewChats.setAdapter(adapter);

                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        myRefToChat2 = database.getReference("chats").child(post.userID).child(currentUser.getUid());

                        myRefToMyChats = database.getReference("mychats").child(currentUser.getUid()).child(post.userID);
                        myRefToMyChats2 = database.getReference("mychats").child(post.userID).child(currentUser.getUid());


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }else{
                secondUserID = getIntent().getExtras().getString("CHAT_FRAGMENT_KEY");
                myRefToChat = database.getReference("chats").child(currentUser.getUid()).child(secondUserID);

                myRefToChat.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<Messages> getMessageList = new ArrayList<>();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            {
                                Messages message = child.getValue(Messages.class);
                                getMessageList.add(message);
                            }
                            messageList = getMessageList;
                            adapter = new MessageAdapter(ChatActivity.this, R.layout.message_item, messageList, currentUser.getUid(),secondUserID, adapter);
                            listViewChats.setAdapter(adapter);

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                myRefToChat2 = database.getReference("chats").child(secondUserID).child(currentUser.getUid());

                myRefToMyChats = database.getReference("mychats").child(currentUser.getUid()).child(secondUserID);
                myRefToMyChats2 = database.getReference("mychats").child(secondUserID).child(currentUser.getUid());




            }
        }





        myRefUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userValue = dataSnapshot.getValue(User.class);
//                Log.d("demoM", "Value is: " + userValue);
                globalUser = userValue;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("demo", "Failed to read value.", error.toException());
            }

        });




        imageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });





        imageViewsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageAdd.setImageResource(R.drawable.myprofile);


                Messages message = new Messages();

                if (bitmapToSend != null) {

                    uploadImage(bitmapToSend);


                } else {

                    message.url = "abc";
                    String mess = messageToSend.getText().toString();
                    message.message = mess;
                    Date date = new Date();
                    message.timeStamp = String.valueOf(date);
                    message.userID = currentUser.getUid();
                    message.userName = userValue.first;

                    String key = myRefToChat.push().getKey();
                    message.messageID = key;

                    //Make The bitmap null again
                    bitmapToSend = null;

                    myRefToChat.child(key).setValue(message);
                    myRefToChat2.child(key).setValue(message);
                    myRefToMyChats.setValue(key);
                    myRefToMyChats.child(key).setValue(message);
                    myRefToMyChats2.setValue(key);
                    myRefToMyChats2.child(key).setValue(message);

                    messageList.add(message);
                    messageToSend.setText("");

                }

            }
        });

        if (newFlag) {
            newFlag = false;
            flag = false;
        }






    }


    public void getMessages() {
        myRefToChat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Messages> getMessageList = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    {
                        Messages message = child.getValue(Messages.class);
                        getMessageList.add(message);
                    }
                    messageList = getMessageList;
                    adapter = new MessageAdapter(ChatActivity.this, R.layout.message_item, messageList, currentUser.getUid(), post.userID, adapter);
                    listViewChats.setAdapter(adapter);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void uploadImage(Bitmap bitmap) {
        final StorageReference ref = FirebaseStorage.getInstance().getReference().child(UUID.randomUUID() + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();

        final UploadTask uploadTask = ref.putBytes(data);


        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    getUrl = downloadUri.toString();

                    Messages message=new Messages();
                    message.url=downloadUri.toString();

                    String mess = messageToSend.getText().toString();
                    message.message = mess;
                    Date date = new Date();
                    message.timeStamp = String.valueOf(date);
                    message.userID = currentUser.getUid();
                    message.userName = userValue.first;

                    String key = myRefToChat.push().getKey();
                    message.messageID = key;


                    //Make The bitmap null again
                    bitmapToSend = null;

                    myRefToChat.child(key).setValue(message);

                    messageList.add(message);
                    messageToSend.setText("");
                } else {
                    // Handle failures
                    // ...
                }
            }
        });

        // return getUrl;

    }


    public User fetchUserDetails(String userID)
    {
        final User[] user = new User[1];
        FirebaseDatabase.getInstance().getReference("users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user[0] =dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return user[0];
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_SELECTOR && resultCode == RESULT_OK && data != null && data.getData() != null && image == 1) {
            image = 0;
            Uri uri = data.getData();
            try {
                bitmapToSend = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                flag = true;
                //Drawable d = new BitmapDrawable(getResources(), bitmap);
                imageAdd.setImageBitmap(bitmapToSend);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    public boolean showMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.menu_message_room, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.requestID) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChatActivity.this);
                    alertDialog.setTitle("Request Amount");
                    alertDialog.setMessage("Enter amount to be paid");

                    final EditText input = new EditText(ChatActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setLayoutParams(lp);
                    alertDialog.setView(input);
                    alertDialog.setIcon(R.drawable.money);

                    alertDialog.setPositiveButton("OKAY",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    numberAmount = input.getText().toString();
                                    if (!numberAmount.equals("")) {
                                        Toast.makeText(getApplicationContext(),
                                                "Requested Amount is " + numberAmount, Toast.LENGTH_SHORT).show();

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


                    return false;
                }


                else if (item.getItemId() == R.id.attachID)
                {
                    image = 1;
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_SELECTOR);
                    return false;
                }

                return false;
            }
        });
        return true;
    }
}


