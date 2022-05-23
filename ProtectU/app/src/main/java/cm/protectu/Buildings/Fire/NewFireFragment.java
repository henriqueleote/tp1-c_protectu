package cm.protectu.Buildings.Fire;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.LocationAddress;
import cm.protectu.Map.MapFragment;
import cm.protectu.R;

public class NewFireFragment extends Fragment {

    private static final int PICK_IMAGE = 1;

    //ImageView
    private ImageView backButton, imagesImageView;

    private TextView locationTextView;

    private EditText fireNameEditText, fireDescriptionEditText, fireDeathCountEditText, fireMissingCountEditText;

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

    public NewFireFragment(GeoPoint location){
        this.location = location;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_map_new_fire, container, false);

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
        fireNameEditText = view.findViewById(R.id.fireNameEditText);
        fireDescriptionEditText = view.findViewById(R.id.fireDescriptionEditText);
        fireMissingCountEditText = view.findViewById(R.id.fireMissingCountEditText);
        fireDeathCountEditText = view.findViewById(R.id.fireDeathCountEditText);
        createButton = view.findViewById(R.id.createButton);

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
                createFire(fireNameEditText.getText().toString(),
                        fireDescriptionEditText.getText().toString(),
                        fireDeathCountEditText.getText().toString(),
                        fireMissingCountEditText.getText().toString());
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
                    Picasso.get()
                            .load(data.getClipData().getItemAt(totalItems-1).getUri())
                            .centerCrop()
                            .fit()
                            .into(imagesImageView);

                    Log.d(TAG, "Value: " + data.getClipData().getItemAt(totalItems-1).getUri());

                    for(int i = 0; i < totalItems; i++){
                        uriList.add(data.getClipData().getItemAt(i).getUri());
                    }
                }

            }
            else if(data.getData() != null){
                Picasso.get()
                        .load(data.getData())
                        .centerCrop()
                        .fit()
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

    public void createFire(String fireName, String fireDescription, String fireDeathCount, String fireMissingCount){

        // Name field check
        if (TextUtils.isEmpty(fireName)) {
            fireNameEditText.setError("Name is mandatory");
            fireNameEditText.requestFocus();
            return;
        }

        // Description field check
        if (TextUtils.isEmpty(fireDescription)) {
            fireDescriptionEditText.setError("Description is mandatory");
            fireDescriptionEditText.requestFocus();
            return;
        }

        // Death Count field check
        if (TextUtils.isEmpty(fireDeathCount)) {
            fireDeathCountEditText.setError("Death count is mandatory");
            fireDeathCountEditText.requestFocus();
            return;
        }

        // Missing Count field check
        if (TextUtils.isEmpty(fireMissingCount)) {
            fireMissingCountEditText.setError("Missing count is mandatory");
            fireMissingCountEditText.requestFocus();
            return;
        }

        if(uriList.isEmpty() && imagesLinks.isEmpty()){
            Toast.makeText(getActivity(), "Images are mandatory", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Adding fire");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        DocumentReference buildingRef = firebaseFirestore.collection("map-buildings").document();
        Map<String, Object> fireData = new HashMap<>();
        fireData.put("buildingID", buildingRef.getId());
        fireData.put("buildingName", fireName);
        fireData.put("buildingDescription", fireDescription);
        fireData.put("fireDeathCount", fireDeathCount);
        fireData.put("fireMissingCount", fireMissingCount);
        fireData.put("images", imagesLinks);
        fireData.put("location", new GeoPoint(location.getLatitude(), location.getLongitude()));
        fireData.put("type", "fire");

        buildingRef.set(fireData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference pinRef = firebaseFirestore.collection("map-pins").document(buildingRef.getId());
                        Map<String, Object> pinData = new HashMap<>();
                        pinData.put("pinID", pinRef.getId());
                        pinData.put("buildingID", buildingRef.getId());
                        pinData.put("location", new GeoPoint(location.getLatitude(), location.getLongitude()));
                        pinData.put("type", "fire");
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