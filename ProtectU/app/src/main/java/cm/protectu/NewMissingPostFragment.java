package cm.protectu;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;


public class NewMissingPostFragment extends Fragment {

    //Firebase Authentication
    private FirebaseAuth mAuth;
    private CardView cardView;
    private ImageView arrowBack, imageUploaded, cameraIcon,profileImage,phone;
    private FirebaseFirestore firebaseFirestore;
    private EditText name,age,description;
    //private TextView phone;
    private Button create;

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
        imageUploaded = view.findViewById(R.id.imageUplaodedID);
        cameraIcon = view.findViewById(R.id.uploadImageIconID);
        name = view.findViewById(R.id.nameNewMissingPostID);
        age = view.findViewById(R.id.ageNewMissingPostID);
        description = view.findViewById(R.id.descriptionNewMissingPostID);

        profileImage = view.findViewById(R.id.foto);
        create = view.findViewById(R.id.createButtonNewMissingID);


        //TODO Check the animation
        //Checks if there is a session, if not, redirects to the Auth page
        if (mAuth.getCurrentUser() == null) {
            getActivity().finish();
            //Swipe animation ?? not sure, consult previous code
            //getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            startActivity(new Intent(getActivity(), AuthActivity.class));
        }

        firebaseFirestore = FirebaseFirestore.getInstance();

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,3);
                cameraIcon.setVisibility(View.INVISIBLE);
            }
        });

        //go back to the map frame
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MissingBoardFragment fragment = new MissingBoardFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        // IN PROGRESS THIS SITUACION
        /*
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createMissingPost(mAuth.getUid(),description.getText().toString().trim(),name.getText().toString().trim(),mAuth.getCurrentUser().getPhoneNumber(),age.getText().toString(),"fff","fff",getActivity());
            }
        });*/



        //Returns the view
        return view;

    }

    // IN PROGRESS THIS SITUACION
    /*
    public void createMissingPost(String userID, String description, String missingName, String phoneNumber, String missingAge, String foto, String fotoMissing,Context context){
        // Message's field check
        if (TextUtils.isEmpty(missingName) || TextUtils.isEmpty(String.valueOf(missingAge)) || TextUtils.isEmpty(description)) {
            name.setError(getString(R.string.error_enter_your_mail));
            name.requestFocus();
            age.setError(getString(R.string.error_enter_your_mail));
            age.requestFocus();
            this.description.setError(getString(R.string.error_enter_your_mail));
            this.description.requestFocus();
            return;
        }

        firebaseFirestore.collection("missing-board")
                .add(new MissingPostFragment(userID,missingName,description,missingAge,phoneNumber,foto,fotoMissing,context))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        firebaseFirestore.collection("missing-board").document(documentReference.getId())
                                .update("missingID",documentReference.getId());
                        Log.d(TAG, "Document successfully created!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error creating document", e);
                    }
                });
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data!=null){
            Uri selectedImages = data.getData();
            imageUploaded.setImageURI(selectedImages);
        }
    }
}
