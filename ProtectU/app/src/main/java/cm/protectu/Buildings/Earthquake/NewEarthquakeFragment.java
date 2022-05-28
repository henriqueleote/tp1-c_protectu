package cm.protectu.Buildings.Earthquake;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.Map.LocationAddress;
import cm.protectu.Map.MapFragment;
import cm.protectu.R;

public class NewEarthquakeFragment extends Fragment {

    private static final int PICK_IMAGE = 1;

    //ImageView
    private ImageView backButton, imagesImageView;

    private TextView locationTextView;

    private EditText earthquakeNameEditText, earthquakeRichterEditText, earthquakeDeathCountEditText, earthquakeMissingCountEditText, earthquakeContactEditText;

    private CountryCodePicker countryCodePicker;

    private Button createButton;

    private ArrayList<Uri> uriList;

    private ArrayList<String> imagesLinks;

    private List<Address> addressesList;

    //Firebase Authentication
    FirebaseAuth mAuth;

    FirebaseFirestore firebaseFirestore;

    FirebaseStorage firebaseStorage;

    private GeoPoint location;

    //TAG for debug logs
    private static final String TAG = AuthActivity.class.getName();

    public NewEarthquakeFragment(GeoPoint location){
        this.location = location;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_map_new_earthquake, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        //Initialize Firebase Firestore
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseStorage = FirebaseStorage.getInstance();

        //Checks if there is a session, if not, redirects to the Auth page
        if (mAuth.getCurrentUser() == null) {
            getActivity().finish();
            startActivity(new Intent(getActivity(), AuthActivity.class));
        }

        //Link the view objects with the XML
        backButton = view.findViewById(R.id.backButton);
        imagesImageView = view.findViewById(R.id.imagesImageView);
        locationTextView = view.findViewById(R.id.locationTextView);
        earthquakeNameEditText = view.findViewById(R.id.earthquakeNameEditText);
        earthquakeRichterEditText = view.findViewById(R.id.earthquakeRichterEditText);
        earthquakeDeathCountEditText = view.findViewById(R.id.earthquakeDeathCountEditText);
        earthquakeMissingCountEditText = view.findViewById(R.id.earthquakeMissingCountEditText);
        createButton = view.findViewById(R.id.createButton);
        earthquakeContactEditText = view.findViewById(R.id.contactEditText);
        countryCodePicker = view.findViewById(R.id.contactPicker);

        uriList = new ArrayList<>();
        imagesLinks = new ArrayList<>();
        addressesList = new ArrayList<>();

        locationTextView.setText(LocationAddress.getLocation(getActivity(), location));

        //On click closes the form sheet
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setMessage(getString(R.string.areYouSureProgressLost))
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, new MapFragment())
                                        .addToBackStack(null)
                                        .commit();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                // Create the AlertDialog object and return it
                builder.show();
            }
        });

        imagesImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEarthquake(earthquakeNameEditText.getText().toString(),
                        earthquakeRichterEditText.getText().toString(),
                        earthquakeDeathCountEditText.getText().toString(),
                        earthquakeMissingCountEditText.getText().toString(),
                        earthquakeContactEditText.getText().toString());
            }
        });

        //Returns the view
        return view;
    }

    public void uploadImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && (data.getData() != null || data.getClipData() != null)) {
            uriList.clear();
            imagesLinks.clear();
            if(data.getClipData() != null){
                int totalItems = data.getClipData().getItemCount();

                if(totalItems < 1){
                    Toast.makeText(getActivity(), getString(R.string.images_mandatory), Toast.LENGTH_SHORT).show();
                    return;
                }else if(totalItems > 6){
                    Toast.makeText(getActivity(), getString(R.string.limit_six_images), Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    Glide.with(getActivity())
                            .load(data.getClipData().getItemAt(totalItems-1).getUri())
                            .centerCrop()
                            
                            .into(imagesImageView);

                    Log.d(TAG, "Value: " + data.getClipData().getItemAt(totalItems-1).getUri());

                    for(int i = 0; i < totalItems; i++){
                        uriList.add(data.getClipData().getItemAt(i).getUri());
                    }
                }

            }
            else if(data.getData() != null){
                Glide.with(getActivity())
                        .load(data.getData())
                        .centerCrop()
                        
                        .into(imagesImageView);

                uriList.add(data.getData());

                Log.d(TAG, "Value: " + data.getData());
            }

            ProgressDialog mDialog = new ProgressDialog(getActivity());
            mDialog.setMessage(getString(R.string.loading_images));
            mDialog.setCancelable(false);
            mDialog.show();

            StorageReference storageReference = firebaseStorage.getInstance().getReference();
            uriList.forEach(uri -> {
                final StorageReference imageRef = storageReference.child("buildings/" + mAuth.getCurrentUser().getUid() + "-" + System.currentTimeMillis());
                Log.d(TAG, imageRef.toString());
                UploadTask uploadTask = imageRef.putFile(uri);

                uploadTask
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mDialog.dismiss();
                        Task<Uri> downloadUrl = imageRef.getDownloadUrl();
                        downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imagesLinks.add(uri.toString());
                                Log.d(TAG, "Link: " + uri.toString());
                            }
                        });
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        mDialog.dismiss();
                        Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }
                });
            });

        }
    }

    public void createEarthquake(String earthquakeName, String earthquakeRichter, String earthquakeDeathCount, String earthquakeMissingCount, String contact){

        // Name field check
        if (TextUtils.isEmpty(earthquakeName)) {
            earthquakeNameEditText.setError(getString(R.string.name_mandatory));
            earthquakeNameEditText.requestFocus();
            return;
        }

        // Richter field check
        if (TextUtils.isEmpty(earthquakeRichter)) {
            earthquakeRichterEditText.setError(getString(R.string.richter_mandatory));
            earthquakeRichterEditText.requestFocus();
            return;
        }

        // Death Count field check
        if (TextUtils.isEmpty(earthquakeDeathCount)) {
            earthquakeDeathCountEditText.setError(getString(R.string.death_count_mandatory));
            earthquakeDeathCountEditText.requestFocus();
            return;
        }

        // Missing Count field check
        if (TextUtils.isEmpty(earthquakeMissingCount)) {
            earthquakeMissingCountEditText.setError(getString(R.string.missing_count_mandatory));
            earthquakeMissingCountEditText.requestFocus();
            return;
        }

        // Contact's field check
        if (TextUtils.isEmpty(contact)) {
            earthquakeContactEditText.setError(getResources().getString(R.string.error_invalid_contact));
            earthquakeContactEditText.requestFocus();
            return;
        }

        if(uriList.isEmpty() && imagesLinks.isEmpty()){
            Toast.makeText(getActivity(), getString(R.string.images_mandatory), Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(getString(R.string.adding_earthquake));
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        DocumentReference buildingRef = firebaseFirestore.collection("map-buildings").document();
        Map<String, Object> earthquakeData = new HashMap<>();
        earthquakeData.put("buildingID", buildingRef.getId());
        earthquakeData.put("buildingName", earthquakeName);
        earthquakeData.put("buildingContact", countryCodePicker.getSelectedCountryCodeWithPlus() + " " + contact);
        earthquakeData.put("earthquakeRichter", earthquakeRichter);
        earthquakeData.put("earthquakeDeathCount", earthquakeDeathCount);
        earthquakeData.put("earthquakeMissingCount", earthquakeMissingCount);
        earthquakeData.put("images", imagesLinks);
        earthquakeData.put("location", new GeoPoint(location.getLatitude(), location.getLongitude()));
        earthquakeData.put("type", "earthquake");

        buildingRef.set(earthquakeData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference pinRef = firebaseFirestore.collection("map-pins").document(buildingRef.getId());
                        Map<String, Object> pinData = new HashMap<>();
                        pinData.put("pinID", pinRef.getId());
                        pinData.put("buildingID", buildingRef.getId());
                        pinData.put("location", new GeoPoint(location.getLatitude(), location.getLongitude()));
                        pinData.put("type", "earthquake");
                        pinRef.set(pinData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot with the ID: " + pinRef.getId());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });

                        progressDialog.dismiss();
                        Log.d(TAG, "DocumentSnapshot with the ID: " + buildingRef.getId());
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new MapFragment())
                                .addToBackStack(null)
                                .commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }
}