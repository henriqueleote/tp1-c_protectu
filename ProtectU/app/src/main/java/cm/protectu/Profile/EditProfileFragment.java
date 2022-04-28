package cm.protectu.Profile;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

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
    private ImageView backButton, profileImageView;
    private FirebaseFirestore firebaseFirestore;
    private String name, surname, phoneNumber, imageURL;
    BottomNavigationView bottomBar;

    FirebaseStorage storage;

    private Uri imguri;

    String firebaseUrl;

    Drawable oldDrawable;


    private static final String TAG = MainActivity.class.getName();

    public EditProfileFragment(String name, String surname, String phoneNumber, String imageURL){
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.imageURL = imageURL;
    }

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
        backButton = view.findViewById(R.id.backButton);
        profileImageView = view.findViewById(R.id.profileImageView);
        BottomNavigationView bottomBar = getActivity().findViewById(R.id.nav_view);


        oldDrawable = profileImageView.getDrawable();


        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageFileChooser();
            }
        });


        //TODO -- It works but it's gone forever, doesn't restore
        //Hide the bottom bar in this page
        //bottomBar.setVisibility(View.INVISIBLE);

        firstNameEditText.setText(name);
        lastNameEditText.setText(surname);
        contactEditText.setText(phoneNumber);
        //TODO CHECK IF THIS IS THE LINE, I'VE A NULL SPACE IN THE REGISTER, SO MAYBE .equals("null") after everyone deletes their accounts
        if(!imageURL.equals("null")){
            //if(document.getString("imageURL") != null){
            Picasso.get()
                    .load(imageURL)
                    .centerCrop()
                    .fit()
                    .transform(new CropCircleTransformation())
                    .into(profileImageView);
        }

        //Initialize Firebase Firestore Database
        firebaseFirestore = FirebaseFirestore.getInstance();

        //TODO Check the animation
        //Checks if there is a session, if not, redirects to the Auth page
        if (mAuth.getCurrentUser() == null) {
            getActivity().finish();
            //Swipe animation ?? not sure, consult previous code
            //getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
                userData.put("phoneNumber", phoneNumber);
                //TODO Check if he changed the image, with the field from the database
                userData.put("imageURL", firebaseUrl);
                Log.d(TAG,userData.toString());
                firebaseFirestore.collection("users").document(mAuth.getCurrentUser().getUid()).set(userData);

                ProfileFragment fragment = new ProfileFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
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
        if (requestCode == 1 && resultCode == RESULT_OK && data != null & data.getData() != null) {

            imguri = data.getData();
            Picasso.get()
                    .load(imguri)
                    .centerCrop()
                    .fit()
                    .transform(new CropCircleTransformation())
                    .into(profileImageView);


            ProgressDialog mDialog = new ProgressDialog(getActivity());
            //TODO UPDATE WITH STATUS

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
                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
