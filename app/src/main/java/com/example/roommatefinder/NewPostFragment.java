package com.example.roommatefinder;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import com.google.android.gms.common.api.Status;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import java.util.Arrays;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import static com.android.volley.VolleyLog.TAG;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class NewPostFragment extends Fragment {

    EditText mDateEditText, cityEditText, additionalEditText;
    static int year, month, day;
    Button buttonPost, buttonClear;
    View view1;
    int REQUEST_CODE=1100001;
    Spinner myspinner;
    Post post;

    FirebaseAuth mAuth;
    private Place addressP;
    FirebaseUser currentUser;
    FirebaseDatabase database;
//    StorageReference storageRef;
//    DatabaseReference myRef;

    Bitmap[] bitmaps = new Bitmap[4];

    //    String genderString, dateString, cityString, additionalString;
    ImageView img1, img2, img3, img4;

    int REQUEST_IMAGE_SELECTOR1=11, REQUEST_IMAGE_SELECTOR2=12, REQUEST_IMAGE_SELECTOR3=13, REQUEST_IMAGE_SELECTOR4=14;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view1=inflater.inflate(R.layout.fragment_new_post,container,false);
        post = new Post();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();




        String apiKey = getString(R.string.api_key);
        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(view1.getContext(), apiKey);
        }
// Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(view1.getContext());
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)getChildFragmentManager().findFragmentById(R.id.city_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                addressP=place;
            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        mDateEditText = view1.findViewById(R.id.editTextDateID);
        mDateEditText.setInputType(InputType.TYPE_NULL);
        myspinner = view1.findViewById(R.id.spinnerID2);
        buttonClear = view1.findViewById(R.id.buttonClearID);
        buttonPost = view1.findViewById(R.id.buttonPostID);
        additionalEditText = view1.findViewById(R.id.editTextAdditionalID);
        img1 = view1.findViewById(R.id.imageViewRoom1);
        img2 = view1.findViewById(R.id.imageViewRoom2);
        img3 = view1.findViewById(R.id.imageViewRoom3);
        img4 = view1.findViewById(R.id.imageViewRoom4);


        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_SELECTOR1);
            }
        });


        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_SELECTOR2);
            }
        });

        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_SELECTOR3);
            }
        });

        img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_SELECTOR4);
            }
        });


        String[] arraySpinner = new String[]{"Available for:","Male", "Female", "Gender Neutral"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view1.getContext(),
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myspinner.setAdapter(adapter);


        final FragmentManager fm = getActivity().getSupportFragmentManager();

        mDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatDialogFragment newFragment = new DatePickerFragment();
                // set the targetFragment to receive the results, specifying the request code
                newFragment.setTargetFragment(NewPostFragment.this, REQUEST_CODE);
                // show the datePicker
                newFragment.show(fm, "datePicker");
            }
        });




        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String genderString = myspinner.getSelectedItem().toString();
                String dateString = mDateEditText.getText().toString();
//                String cityString = cityEditText.getText().toString();
                String additionalString = additionalEditText.getText().toString();

                // ------ ******* --------
                // ------ ******* --------
                // Validations required
                // ------ ******* --------
                // ------ ******* --------

                post.gender = genderString;
                post.pid = UUID.randomUUID().toString();
                post.date = dateString;
                post.city = addressP.getName();;
                post.additional = additionalString;
                post.userID = currentUser.getUid();
                post.status="available";
                post.lat=addressP.getLatLng().latitude;
                post.lng=addressP.getLatLng().longitude;

                post.paymentID=UUID.randomUUID().toString();

                for(int i=0;i<4; i++){
                    uploadImage(bitmaps[i], i);
                }




            }
        });



        return view1;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check for the results
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // get date from string
            String selectedDate = data.getStringExtra("selectedDate");
            // set the value of the editText

            Log.d("demooooo", selectedDate);
            mDateEditText.setText(selectedDate);
//            post.date = selectedDate;

        }

        else if (requestCode == REQUEST_IMAGE_SELECTOR1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                bitmaps[0] = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                img1.setImageBitmap(bitmaps[0]);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else if (requestCode == REQUEST_IMAGE_SELECTOR2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                bitmaps[1] = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                img2.setImageBitmap(bitmaps[1]);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else if (requestCode == REQUEST_IMAGE_SELECTOR3 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                bitmaps[2] = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                img3.setImageBitmap(bitmaps[2]);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else if (requestCode == REQUEST_IMAGE_SELECTOR4 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                bitmaps[3] = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                img4.setImageBitmap(bitmaps[3]);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public static class DatePickerFragment extends AppCompatDialogFragment implements DatePickerDialog.OnDateSetListener {
        private static final String TAG = "DatePickerFragment";
        final Calendar c = Calendar.getInstance();

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Set the current date as the default date
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Return a new instance of DatePickerDialog
            return new DatePickerDialog(getActivity(), DatePickerFragment.this, year, month, day);
        }

        // called when a date has been selected
        public void onDateSet(DatePicker view, int year, int month, int day) {
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);
            String selectedDate = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).format(c.getTime());

            Log.d(TAG, "onDateSet: " + selectedDate);
            // send date back to the target fragment
            getTargetFragment().onActivityResult(
                    getTargetRequestCode(),
                    RESULT_OK,
                    new Intent().putExtra("selectedDate", selectedDate)
            );
        }
    }



    private void uploadImage(Bitmap bitmap, final int index) {
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
                    String getUrl = downloadUri.toString();

                    post.imageList.add(getUrl);

                    if(index==3)
                    {

                        database.getReference("posts").child(post.pid).setValue(post);
                        Toast.makeText(getActivity(), "Successfully posted", Toast.LENGTH_SHORT).show();

                    }

                } else {

                }
            }
        });

        // return getUrl;

    }



}
