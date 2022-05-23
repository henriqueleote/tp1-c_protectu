package cm.protectu.Buildings.War;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.LocationAddress;
import cm.protectu.Map.MapFragment;
import cm.protectu.R;

public class NewWarFragment extends Fragment {

    private static final int PICK_IMAGE = 1;

    //ImageView
    private ImageView backButton, imagesImageView;

    private TextView locationTextView;

    private EditText warNameEditText, warDeadCountEditText, warMissingCountEditText;

    private Button createButton, clearButton;

    private ArrayList<Uri> uriList;

    private ArrayList<String> imagesLinks;

    private List<Address> addressesList;

    //Firebase Authentication
    FirebaseAuth mAuth;

    FirebaseFirestore firebaseFirestore;

    FirebaseStorage firebaseStorage;

    private GeoPoint location;

    private Drawable oldDrawable;

    //TAG for debug logs
    private static final String TAG = AuthActivity.class.getName();

    public NewWarFragment(GeoPoint location){
        this.location = location;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_map_new_war, container, false);

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
        warNameEditText = view.findViewById(R.id.warNameEditText);
        warDeadCountEditText = view.findViewById(R.id.warDeadCountEditText);
        warMissingCountEditText = view.findViewById(R.id.warMissingCountEditText);
        createButton = view.findViewById(R.id.createButton);
        clearButton = view.findViewById(R.id.clearButton);
        oldDrawable = imagesImageView.getDrawable();

        uriList = new ArrayList<>();
        imagesLinks = new ArrayList<>();
        addressesList = new ArrayList<>();

        locationTextView.setText(LocationAddress.getLocation(getActivity(), location));

        //TODO AT LEAST ONE PHOTO
        //On click closes the form sheet
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setMessage("Are you sure you want to return? All progress will be lost")
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
                createWar(warNameEditText.getText().toString(),
                        warDeadCountEditText.getText().toString(),
                        warMissingCountEditText.getText().toString());
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uriList.clear();
                warNameEditText.setText("");
                warDeadCountEditText.setText("");
                warMissingCountEditText.setText("");
                Glide.with(getActivity()).load(R.drawable.ic_camera_black_24dp).into(imagesImageView);;
                //TODO MISSING DESCRIPTION EVERYWHERE
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
                    Toast.makeText(getActivity(), "Images are mandatory", Toast.LENGTH_SHORT).show();
                    return;
                }else if(totalItems > 6){
                    Toast.makeText(getActivity(), "Cant have more than 6 images", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    //TODO NOT PERFECT FIT
                    ViewGroup.LayoutParams params = imagesImageView.getLayoutParams();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;                    clearButton.setVisibility(View.VISIBLE);
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
                //TODO NOT PERFECT FIT
                ViewGroup.LayoutParams params = imagesImageView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                imagesImageView.requestLayout();
                clearButton.setVisibility(View.VISIBLE);
                Glide.with(getActivity())
                        .load(data.getData())
                        
                        .centerCrop()
                        .into(imagesImageView);

                uriList.add(data.getData());

                Log.d(TAG, "Value: " + data.getData());
            }

            ProgressDialog mDialog = new ProgressDialog(getActivity());
            mDialog.setMessage("Loading image(s)...");
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
                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            });

        }
    }

    public void createWar(String warName, String warDeadCount, String warMissingCount){

        // Name field check
        if (TextUtils.isEmpty(warName)) {
            warNameEditText.setError("Name is mandatory");
            warNameEditText.requestFocus();
            return;
        }

        // Death number field check
        if (TextUtils.isEmpty(warDeadCount)) {
            warDeadCountEditText.setError("Death number is mandatory");
            warDeadCountEditText.requestFocus();
            return;
        }

        // Missing number field check
        if (TextUtils.isEmpty(warMissingCount)) {
            warMissingCountEditText.setError("Missing number is mandatory");
            warMissingCountEditText.requestFocus();
            return;
        }

        if(uriList.isEmpty() && imagesLinks.isEmpty()){
            Toast.makeText(getActivity(), "Images are mandatory", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Adding war");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        DocumentReference buildingRef = firebaseFirestore.collection("map-buildings").document();
        Map<String, Object> warData = new HashMap<>();
        warData.put("buildingID", buildingRef.getId());
        warData.put("buildingName", warName);
        warData.put("warDeadCount", warDeadCount);
        warData.put("warMissingCount", warMissingCount);
        warData.put("images", imagesLinks);
        warData.put("location", new GeoPoint(location.getLatitude(), location.getLongitude()));
        warData.put("type", "war");

        buildingRef.set(warData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference pinRef = firebaseFirestore.collection("map-pins").document(buildingRef.getId());
                        Map<String, Object> pinData = new HashMap<>();
                        pinData.put("pinID", pinRef.getId());
                        pinData.put("buildingID", buildingRef.getId());
                        pinData.put("location", new GeoPoint(location.getLatitude(), location.getLongitude()));
                        pinData.put("type", "war");
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