package cm.protectu.Profile;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.MainActivity;
import cm.protectu.R;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;


public class EditProfileFragment extends Fragment {

    //Firebase Authentication
    private FirebaseAuth mAuth;
    private Button saveBtn;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText contactEditText;
    private CountryCodePicker countryCodePicker;
    private ImageView backButton, profileImageView;
    private FirebaseFirestore firebaseFirestore;
    private String name, surname, phoneNumber, imageURL;
    BottomNavigationView bottomBar;

    FirebaseStorage storage;

    private Uri imguri;

    String firebaseUrl;

    Drawable oldDrawable;

    private File photoFile;
    private String photoPath;
    private static final String TEMP_IMAGE_NAME = "tempImage";
    private static final int PICK_IMAGE_ID = 234;


    private static final String TAG = MainActivity.class.getName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        saveBtn = view.findViewById(R.id.saveButton);
        firstNameEditText = view.findViewById(R.id.firstNameEditText);
        lastNameEditText = view.findViewById(R.id.lastNameEditText);
        contactEditText = view.findViewById(R.id.contactEditText);
        countryCodePicker = view.findViewById(R.id.contactPicker);
        backButton = view.findViewById(R.id.backButton);
        profileImageView = view.findViewById(R.id.profileImageView);

        oldDrawable = profileImageView.getDrawable();

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.pick_image_intent_text)
                        .setPositiveButton(R.string.gallery, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                imageFileChooser();
                            }
                        })
                        .setNegativeButton(R.string.camera, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dispatchTakePictureIntent();
                            }
                        });
                // Create the AlertDialog object and return it
                builder.show();
            }
        });

        String currentString = "Fruit: they taste good";
        String[] separated = MainActivity.sessionUser.getPhoneNumber().split(" ");

        //Initialize Firebase Firestore Database
        firebaseFirestore = FirebaseFirestore.getInstance();


        firebaseUrl = MainActivity.sessionUser.getImageURL();
        firstNameEditText.setText(MainActivity.sessionUser.getFirstName());
        lastNameEditText.setText(MainActivity.sessionUser.getLastName());
        countryCodePicker.setCountryForPhoneCode(Integer.parseInt(separated[0]));
        contactEditText.setText(separated[1]);
        Log.d(TAG, "Value = " + Integer.parseInt(separated[0]));
        Log.d(TAG, "Value = " + separated[1]);

        if(!MainActivity.sessionUser.getImageURL().equals("null")){
            Glide.with(getActivity())
                    .load(MainActivity.sessionUser.getImageURL())
                    .centerCrop()
                    .circleCrop()
                    .into(profileImageView);
        }

        //Checks if there is a session, if not, redirects to the Auth page
        if (mAuth.getCurrentUser() == null) {
            getActivity().finish();
            startActivity(new Intent(getActivity(), AuthActivity.class));
        }


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("uid", mAuth.getCurrentUser().getUid());
                if(!firstNameEditText.equals(name))
                    name = firstNameEditText.getText().toString();
                userData.put("firstName", name);
                if(!lastNameEditText.equals(surname))
                    surname = lastNameEditText.getText().toString();
                userData.put("lastName", surname);
                if(!contactEditText.equals(phoneNumber))
                    phoneNumber = contactEditText.getText().toString();
                userData.put("phoneNumber", countryCodePicker.getSelectedCountryCodeWithPlus() + " " + phoneNumber);
                userData.put("imageURL", firebaseUrl);
                userData.put("email", MainActivity.sessionUser.getEmail());
                userData.put("userType", MainActivity.sessionUser.getUserType());
                Log.d(TAG,userData.toString());
                firebaseFirestore.collection("users").document(mAuth.getCurrentUser().getUid()).set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            ProfileFragment fragment = new ProfileFragment();
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, fragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }else{
                            Toast.makeText(getActivity(), "Error, please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileFragment fragment = new ProfileFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        //Returns the view
        return view;
    }

    public void imageFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {

            if(data.getData() != null){
                imguri = data.getData();
                Log.d(TAG, "Value: " + data.getData());
            }

            Glide.with(getActivity())
                    .load(imguri)
                    .centerCrop()
                    .circleCrop()
                    .into(profileImageView);


            ProgressDialog mDialog = new ProgressDialog(getActivity());
            mDialog.setMessage(getString(R.string.loading_image));
            mDialog.setCancelable(false);
            mDialog.show();

            StorageReference storageReference = storage.getInstance().getReference();
            final StorageReference imageRef = storageReference.child("user-profile/" + mAuth.getCurrentUser().getUid() + "-" + System.currentTimeMillis());
            Log.d(TAG, imageRef.toString());
            UploadTask uploadTask = imageRef.putFile(imguri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Task<Uri> downloadUrl = imageRef.getDownloadUrl();
                    downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            firebaseUrl = uri.toString();
                            Log.d(TAG, "Link: " + firebaseUrl);
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
        }
    }

    /**
     * This method is responsible for creating aN Intent to take the photograph and if successful transform the image file into Uri
     */
    public void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imguri = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imguri);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        photoPath = image.getAbsolutePath();
        return image;
    }
}