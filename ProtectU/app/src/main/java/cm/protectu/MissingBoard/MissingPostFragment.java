package cm.protectu.MissingBoard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.Community.ViewPagerFragment;
import cm.protectu.R;
import cm.protectu.UserDataClass;


public class MissingPostFragment extends Fragment {

    //Firebase Authentication
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private ImageView arrowBack, phone, share,profileImage,MissingImage;
    private TextView profileNameID,descriptionPostID,ageMssingPostID,nameMissingPostID;
    private String userID,description,nameMissing,ageMssing,phoneNumber, urlProfile,urlMissing;

    private static final String TAG =  AuthActivity.class.getName();


    public MissingPostFragment(String userID, String nameMissing , String description, String ageMssing, String phoneNumber, String urlProfile, String urlMissing) {
        this.userID = userID;
        this.nameMissing = nameMissing;
        this.description = description;
        this.ageMssing = "" + ageMssing;
        this.phoneNumber = "" + phoneNumber;
        this.urlProfile = urlProfile;
        this.urlMissing = urlMissing;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_missing_post, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        //Link the view objects with the XML and put the information in the elements
        arrowBack = view.findViewById(R.id.backID);
        descriptionPostID = view.findViewById(R.id.descriptionPostID);
        descriptionPostID.setText(description);
        ageMssingPostID = view.findViewById(R.id.ageMissingPostID);
        ageMssingPostID.setText(ageMssing);
        nameMissingPostID = view.findViewById(R.id.nameMissingPostID);
        nameMissingPostID.setText(nameMissing);
        profileNameID = view.findViewById(R.id.profileNamePostID);
        phone = view.findViewById(R.id.phoneNumberIconID);
        share = view.findViewById(R.id.sharePostID);
        profileImage = view.findViewById(R.id.foto);
        MissingImage = view.findViewById(R.id.imageMissingID);

        //coloca a imagem do utilizador
        if(!urlProfile.equals("null")){
            Glide.with(getActivity())
                    .load(urlProfile)
                    .centerCrop()
                    
                    .circleCrop()
                    .into(profileImage);
        }


        //coloca a imagem da pessoa desaparecida
        Glide.with(getActivity())
                .load(urlMissing)
                .centerCrop()
                
                .into(MissingImage);

        /**
         *
         * Allows you to share your post on other platforms
         */
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareBody = "Your Body here";
                String shareSub = "Your Subject here";
                intent.putExtra(Intent.EXTRA_SUBJECT,shareBody);
                intent.putExtra(Intent.EXTRA_TEXT,shareSub);
                startActivity(Intent.createChooser(intent,"Share this Post"));
            }
        });

        /**
         *
         * Allows placing the number associated with the user profile account of the respective publication
         * on the cell phone, in order to make a call to this number
         */
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
            }
        });

        /**
         * go back to the main missing board frag
         */
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //MissingBoardFragment fragment = new MissingBoardFragment(null);
                ViewPagerFragment fragment = new ViewPagerFragment(true);
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

        getData();
        //Returns the view
        return view;

    }


    /**
     *
     * Allows you to fetch the username from the database
     */
    public void getData(){
        firebaseFirestore.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getId().equals(userID)) {
                                    UserDataClass userDataClass = document.toObject(UserDataClass.class);
                                    profileNameID.setText(userDataClass.getFirstName() + " " + userDataClass.getLastName());
                                    break;
                                } else {
                                    // voltar para tras com mensagem de erro --holder.userName.setText("Not Found");
                                }
                            }
                        } else {
                            Log.d(TAG, "Insucess");
                        }
                    }
                });
    }

}
