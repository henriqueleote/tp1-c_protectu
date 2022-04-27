package cm.protectu;

import android.content.Context;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;


public class MissingPostFragment extends Fragment {

    //Firebase Authentication
    private FirebaseAuth mAuth;
    private ImageView arrowBack, phone, share,profileImage,MissingImage;
    private TextView profileNameID,descriptionPostID,ageMssingPostID,nameMissingPostID;
    private String userID,description,nameMissing,ageMssing,phoneNumber, urlProfile,urlMissing;
    private Context context;
    private FirebaseFirestore firebaseFirestore;

    private static final String TAG =  AuthActivity.class.getName();


    public MissingPostFragment(String userID, String nameMissing , String description, String ageMssing, String phoneNumber, String urlProfile, String urlMissing, Context context) {
        this.userID = userID;
        this.nameMissing = nameMissing;
        this.description = description;
        this.ageMssing = "" + ageMssing;
        this.phoneNumber = "" + phoneNumber;
        this.urlProfile = urlProfile;
        this.urlMissing = urlMissing;
        this.context = context;
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


        Picasso.get()
                .load(urlProfile)
                .centerCrop()
                .fit()
                .transform(new CropCircleTransformation())
                .into(profileImage);

        Picasso.get()
                .load(urlMissing)
                .centerCrop()
                .fit()
                .transform(new CropCircleTransformation())
                .into(MissingImage);

       // Picasso.with(context).load(urlProflie).error(R.drawable.ic_baseline_share_24).into(profileImage);




        /**
         * Permite partilhar a publicação em outras plataformas
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
         * Permite colocar o numero associado à conta de perfil do utilizador da respetiva publicação
         * no telemovel, com o objetivo de realizar chamada para este numero
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

        getData();
        //Returns the view
        return view;

    }



    public void getData(){
        firebaseFirestore.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getId().equals(userID)) {
                                    UserData userData = document.toObject(UserData.class);
                                    profileNameID.setText(userData.getFirstName() + " " + userData.getLastName());
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
