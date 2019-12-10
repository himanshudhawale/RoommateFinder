package com.example.roommatefinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";

    private EditText email, password;
    private Button buttonLogin, buttonSignup;
    private FirebaseAuth mAuth;
    OkHttpClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = findViewById(R.id.eTemailID);
        password = findViewById(R.id.eTpasswordID);
        buttonLogin = findViewById(R.id.buttonLoginID);
        buttonSignup = findViewById(R.id.buttonSignupID);
        client = new OkHttpClient();

        mAuth = FirebaseAuth.getInstance();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    signIn(email.getText().toString(), password.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "Clicked on signup", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

    }



    private void signIn(final String email, final String password) throws JSONException {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email",email);
        jsonObject.put("password",password);

        String jsonString=jsonObject.toString();

        RequestBody requestBody = RequestBody.create(JSON, jsonString);


        Request request = new Request.Builder()
                .url("http://ec2-3-94-187-73.compute-1.amazonaws.com:5000/users/authenticate")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .post(requestBody)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {

                    String json = response.body().string();
                    JSONObject root = new JSONObject(json);


                    String status= root.getString("email");
                    if(status.equals(email)) {
                        String TOKEN = root.getString("token");
                        String customerID = root.getString("customerID");

                        callThis(email, password, customerID);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        });

        // [START sign_in_with_email]

    }


    private boolean validateForm() {
        boolean valid = true;

        String stremail = email.getText().toString();
        if (TextUtils.isEmpty(stremail)) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }

        String strpassword = password.getText().toString();
        if (TextUtils.isEmpty(strpassword)) {
            password.setError("Required.");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }



    public void callThis(String email, String password, final String customerID)
    {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(MainActivity.this, ForumActivity.class);
                            intent.putExtra("CUSTOMER_ID", customerID);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        if (!task.isSuccessful()) {
//                            Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
