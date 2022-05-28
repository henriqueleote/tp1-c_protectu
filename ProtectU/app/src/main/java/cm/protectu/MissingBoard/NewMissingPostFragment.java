package cm.protectu.MissingBoard;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.Community.ViewPagerFragment;
import cm.protectu.R;


public class NewMissingPostFragment extends Fragment {

    //Firebase Authentication
    private FirebaseAuth mAuth;
    private CardView cardView;
    private ImageView arrowBack, imageUploaded, cameraIcon,profileImage;
    private FirebaseFirestore firebaseFirestore;
    private EditText name,age,description;
    private String phone, urlProfile, urlMissing;
    private Button create;
    private FirebaseStorage storage;
    private Uri imguri;
    private String firebaseUrl;

    private static final String TAG =  AuthActivity.class.getName();

    public NewMissingPostFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_missing_new_post, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        arrowBack = view.findViewById(R.id.backID);
        cardView = view.findViewById(R.id.uploadImageID);
        imageUploaded = view.findViewById(R.id.imagesImageView);
        cameraIcon = view.findViewById(R.id.imagesImageView);
        name = view.findViewById(R.id.nameNewMissingPostID);
        age = view.findViewById(R.id.ageNewMissingPostID);
        description = view.findViewById(R.id.descriptionNewMissingPostID);
        profileImage = view.findViewById(R.id.foto);
        create = view.findViewById(R.id.createButtonNewMissingID);



        //Checks if there is a session, if not, redirects to the Auth page
        if (mAuth.getCurrentUser() == null) {
            getActivity().finish();
            //Swipe animation ?? not sure, consult previous code
            //getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            startActivity(new Intent(getActivity(), AuthActivity.class));
        }

        firebaseFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        getData();

        /**
         *
         * put the present icon on the invisible card
         */
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageFileChooser();
            }
        });

        /**
         * go back to the map frame
         */
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewPagerFragment fragment = new ViewPagerFragment(true);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        /**
         *
         * creates the new publication and returns to the publications page
         */
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createMissingPost(name.getText().toString().trim(),description.getText().toString().trim(),age.getText().toString(),phone,urlProfile,mAuth.getUid(),firebaseUrl,"");
            }
        });

        //Returns the view
        return view;

    }

    /**
     * Allows you to create a new publication with the data received
     */
    public void createMissingPost(String missingName, String description, String missingAge, String phoneNumber, String foto,String userID,String fotoMissing,String missingID) {

        // Permite verificar se os campos prenchidos estão corretos
        if (TextUtils.isEmpty(missingName)) {
            name.setError(getString(R.string.error_name_not_valid));
            name.requestFocus();
            return;

        }
        if (TextUtils.isEmpty(String.valueOf(missingAge))) {
            age.setError(getString(R.string.error_age_not_valid));
            age.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(description)) {
            this.description.setError(getString(R.string.error_description_not_valid));
            this.description.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(fotoMissing)) {

            ProgressDialog mDialog = new ProgressDialog(getActivity());
            mDialog.setMessage("Creating post...");
            mDialog.setCancelable(false);
            mDialog.show();
            //Allows placing in the database the data received when creating a new publication
            firebaseFirestore.collection("missing-board")
                    .add(new MissingCardClass(missingName, description, missingAge, phoneNumber, foto, userID, fotoMissing, missingID, new Date()))
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            firebaseFirestore.collection("missing-board").document(documentReference.getId())
                                    .update("missingID", documentReference.getId());
                            Log.d(TAG, "Document successfully created!");
                            mDialog.dismiss();
                            //getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MissingBoardFragment(null)).addToBackStack(null).commit();

                            ViewPagerFragment fragment = new ViewPagerFragment(true);
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, fragment);
                            transaction.addToBackStack(null);
                            transaction.commit();

                            Toast.makeText(getActivity(), "Publicação criada", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error creating document", e);
                            mDialog.dismiss();
                        }
                    });
        }else {
            Toast.makeText(getActivity(), "Terá que colocar uma imagem", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Permite buscar as informações do utilizador da base de dados
     */
    public void getData(){
        //Firebase Authentication function get the data from firebase with certain criteria
        firebaseFirestore.collection("users")
                //where the userID is the same as the logged in user
                .whereEqualTo("uid", mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Prints in debug the data object
                                Log.d(TAG, "Data: " + document.getId() + " => " + document.getData());
                                //Sets the text in the view with the name and surname of the authenticated user
                                phone = document.getString("phoneNumber");
                                urlProfile = document.getString("imageURL");
                                urlMissing = document.getString("fotoMissing");
                            }
                        }
                        else{
                            //Shows the error
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    /**
     *
     * Allows you to go to user files
     */
    public void imageFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    /**
     *
     * Allows you to place an image from the user's gallery
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null & data.getData() != null) {

            imguri = data.getData();
            Glide.with(getActivity())
                    .load(imguri)
                    .centerCrop()
                    .into(imageUploaded);
            cameraIcon.setVisibility(View.INVISIBLE);



            ProgressDialog mDialog = new ProgressDialog(getActivity());
            //TODO UPDATE WITH STATUS
            mDialog.setMessage("Loading image...");
            mDialog.setCancelable(false);
            mDialog.show();

            StorageReference storageReference = storage.getInstance().getReference();
            final StorageReference imageRef = storageReference.child("missing-board/" + mAuth.getCurrentUser().getUid() + "-" + System.currentTimeMillis());
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


}
