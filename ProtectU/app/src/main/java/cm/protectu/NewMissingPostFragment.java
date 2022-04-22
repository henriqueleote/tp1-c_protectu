package cm.protectu;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;


public class NewMissingPostFragment extends Fragment {

    //Firebase Authentication
    private FirebaseAuth mAuth;
    private CardView cardView;
    private ImageView arrowBack, imageUploaded, cameraIcon;
    private FirebaseFirestore firebaseFirestore;

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

        //TODO Check the animation
        //Checks if there is a session, if not, redirects to the Auth page
        if (mAuth.getCurrentUser() == null) {
            getActivity().finish();
            //Swipe animation ?? not sure, consult previous code
            //getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            startActivity(new Intent(getActivity(), AuthActivity.class));
        }

        firebaseFirestore = FirebaseFirestore.getInstance();



        //Returns the view
        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data!=null){
            Uri selectedImages = data.getData();
            imageUploaded.setImageURI(selectedImages);
        }
    }
}
