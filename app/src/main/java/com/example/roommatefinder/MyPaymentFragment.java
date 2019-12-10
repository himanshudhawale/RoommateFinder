package com.example.roommatefinder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class MyPaymentFragment extends Fragment implements PaymentInterface{


    View view;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String amountToPay="0";
    FirebaseDatabase database;
    DatabaseReference paymentRef;
    RecyclerView recyclerView;
    List<Payment> listOfPayment;
    Fragment currentFragment;
    OkHttpClient client1;
    String postpostID="";
    String paypaymentID="";

    public MyPaymentFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_my_payment,container,false);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        recyclerView = view.findViewById(R.id.recyclerID);
        paymentRef = database.getReference("mypayment");
        currentFragment = getFragmentManager().findFragmentById(R.id.fragment_container);
        client1 = new OkHttpClient();



        paymentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listOfPayment = new ArrayList<>();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    if(dataSnapshot1.getKey().equals(currentUser.getUid()))
                    {
                        for(DataSnapshot dataSnapshot11 : dataSnapshot1.getChildren())
                        {
                            Payment payment = dataSnapshot11.getValue(Payment.class);
                            listOfPayment.add(payment);

                        }
                    }
                }

                LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(llm);

                PaymentAdapter paymentAdapter = new PaymentAdapter(view.getContext(), R.layout.payment_item, listOfPayment, currentFragment,MyPaymentFragment.this);
                recyclerView.setAdapter(paymentAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return view;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                // use the result to update your UI and send the payment method nonce to your server

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("paymentMethodNonce",result.getPaymentMethodNonce().getNonce());
                    jsonObject.put("amount", amountToPay);
                    String jsonString=jsonObject.toString();
                    RequestBody requestBody = RequestBody.create(JSON, jsonString);
                    String url = "http://ec2-3-94-187-73.compute-1.amazonaws.com:5000/brain/sandbox";
                    final Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();
                    client1.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @SuppressLint("RestrictedApi")
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String json = response.body().string();
                            JSONObject root = null;
                            try {
                                root = new JSONObject(json);
                                Log.d("amount", root.toString());
//                                SelectedItemList.clear();
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
////                                        addToCartRecyclerView.setAdapter(new AddItemToCartAdapter(AddToCartActivity.this,SelectedItemList,AddToCartActivity.this));
//                                        finish();
//
//
//                                    }

                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                        builder.setTitle("Payment was successful");
                                        builder.setMessage("Thank you using Roomster");
                                        builder.setPositiveButton("Okay", new android.content.DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(android.content.DialogInterface dialogInterface, int i) {
                                                FirebaseDatabase.getInstance().getReference("mypayment").child(currentUser.getUid()).child(paypaymentID).child("status").setValue("completed");

                                                FirebaseDatabase.getInstance().getReference("posts").child(postpostID).child("status").setValue("notavailable");


                                            }
                                        });

                                        builder.show();
                                    }
                                });






                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // the user canceled
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
            }
        }

    }




    @Override
    public void sendPayment(String amount, String postpostID, String paypaymentID) {
        this.amountToPay = amount;
        this.postpostID = postpostID;
        this.paypaymentID = paypaymentID;

    }
}
