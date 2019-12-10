package com.example.roommatefinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";

    private EditText firstName;
    private EditText lastName;
    private EditText age;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private Place cityP;

    private Button signup,cancel;
    private Spinner myspinner;
    private ImageView imageView;
    private FirebaseAuth mAuth;
    DatabaseReference myref;
    FirebaseUser currentUser;
    FirebaseDatabase mDatabase;
    StorageReference storageRef;
    Bitmap bitmapToSend = null;
    private static final int REQUEST_IMAGE_SELECTOR = 1;

    String getUrl=null;
    User user = new User();

    private OkHttpClient client, client1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        String apiKey = getString(R.string.api_key);
        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
// Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.city_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                cityP=place;
            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });



        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.emailID);
        password = findViewById(R.id.passwordID);
        age = findViewById(R.id.ageID);
        confirmPassword = findViewById(R.id.confirmPasswordID);
//        city = findViewById(R.id.cityID);
        signup = findViewById(R.id.signupID);
        imageView = findViewById(R.id.imageView);
        myspinner = findViewById(R.id.spinnerID);
        cancel = findViewById(R.id.cancelID);
        client = new OkHttpClient();

        String[] arraySpinner = new String[]{ "Select Gender","Male", "Female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myspinner.setSelection(0);
        myspinner.setAdapter(adapter);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


//                image = 1;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_SELECTOR);


            }

        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cancel_intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(cancel_intent);
            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String strfirst = firstName.getText().toString();
                final String strlast = lastName.getText().toString();
                final String stremail = email.getText().toString();
                final String strpassword = password.getText().toString();
                String strconfirm = confirmPassword.getText().toString();
                final String strcity = cityP.getName();
                final String gender = myspinner.getSelectedItem().toString();
                final String strage = age.getText().toString();
                final Double cityLat=cityP.getLatLng().latitude;
                final Double cityLng=cityP.getLatLng().longitude;


                if (stremail.isEmpty() || strpassword.isEmpty() || strconfirm.isEmpty() || strage.isEmpty() || strfirst.isEmpty() || strlast.isEmpty() || strcity.isEmpty() || gender.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please fill all the details", Toast.LENGTH_LONG).show();
                } else if (strpassword.length() < 6) {
                    Toast.makeText(SignupActivity.this, "Password Length", Toast.LENGTH_LONG).show();
                } else if (!(strpassword.equals(strconfirm))) {
                    Toast.makeText(SignupActivity.this, "Password not matched", Toast.LENGTH_SHORT).show();
                }
                else if(bitmapToSend == null){
                    Toast.makeText(SignupActivity.this, "Please attach your profile photo", Toast.LENGTH_SHORT).show();
                }
                else {


                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                    JSONObject jsonObject = new JSONObject();

                    try {
                        jsonObject.put("email",stremail);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        jsonObject.put("password",strpassword);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        jsonObject.put("firstName",strfirst);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        jsonObject.put("lastName",strlast);
                    } catch (JSONException e) {


                    }
                    try {
                        jsonObject.put("address",strcity);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String jsonString=jsonObject.toString();
                    Log.d("Json string", "register: "+jsonString);
                    RequestBody rbody = RequestBody.create(JSON, jsonString);


                    Request request = new Request.Builder()
                            .url("http://ec2-3-94-187-73.compute-1.amazonaws.com:5000/users/register")
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json")
                            .post(rbody)
                            .build();


                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {


                            String json = response.body().string();
                            if(json.equals("{}")) {


                                //                    Log.d("THE", "Well inside else");
                                // [START create_user_with_email]
                                mAuth.createUserWithEmailAndPassword(stremail, strpassword)
                                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {

                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
//                                        Log.d("THE", "I'm here llala");

                                                    // Sign in success, update UI with the signed-in user's information
                                                    Log.d(TAG, "createUserWithEmail:success");
                                                    FirebaseUser fUser = mAuth.getCurrentUser();
                                                    if (fUser != null) {
                                                        uploadImage(bitmapToSend, mAuth.getCurrentUser().getUid());



                                                        String useruid = fUser.getUid();

                                                        user.uID = useruid;
                                                        user.first = strfirst;
                                                        user.last = strlast;
                                                        user.email = stremail;
                                                        user.password = strpassword;
                                                        user.city = strcity;
                                                        user.gender = gender;
                                                        user.age = strage;
                                                        user.status = "none";
                                                        user.customerID="abc";
                                                        user.latitude=cityLat;
                                                        user.longitude=cityLng;



                                                    } else {
//                                            Log.d("THE", "I'm here lolo");

                                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                                        Toast.makeText(SignupActivity.this, "Authentication failed.",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        });

                            }


                        }


                    });




                }
                ////
            }
        });






    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_SELECTOR && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                bitmapToSend = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                flag = true;
                //Drawable d = new BitmapDrawable(getResources(), bitmap);
                imageView.setImageBitmap(bitmapToSend);


            } catch (IOException e) {
                e.printStackTrace();
            }
//            Picasso.with(SignupActivity.this).load(uri).noPlaceholder().centerCrop().fit()
//                            .into((ImageView) findViewById(R.id.imageView));

        }
    }


    private void uploadImage(Bitmap bitmap, String UUID) {
        final StorageReference ref = FirebaseStorage.getInstance().getReference().child(UUID + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();

        final UploadTask uploadTask = ref.putBytes(data);



        Log.d("THE","Uploading image");
/*
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
        */


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

                    user.imageURL = getUrl;




                    mDatabase.getReference("users").child(user.uID).setValue(user);
                    Intent intent = new Intent(SignupActivity.this, ForumActivity.class);
                    startActivity(intent);


                    //Make The bitmap null again
                    bitmapToSend = null;

                } else {
                    // Handle failures
                    // ...
                }
            }
        });



    }


}
