package cm.protectu.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.Community.CommunityFragment;
import cm.protectu.MainActivity;
import cm.protectu.R;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;


public class ProfileFragment extends Fragment {
    //Strings
    String userName, lastName, phoneNumber, imageURL;

    //TextView
    TextView nameTextView, optionBtn, emailTextView, contactTextView;

    //ImageView
    ImageView editImageView, profileImageView;

    //Button
    Button removeCommunityBtn, removeMissingBtn;

    //Firebase Authentication
    FirebaseAuth mAuth;

    //Firebase Firestore
    FirebaseFirestore firebaseFirestore;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int RESULT_OK = 1;

    //TAG for debug logs
    private static final String TAG = MainActivity.class.getName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        //Initialize Firebase Firestore Database
        firebaseFirestore = FirebaseFirestore.getInstance();

        //Link the view objects with the XML
        nameTextView = view.findViewById(R.id.nameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        contactTextView = view.findViewById(R.id.contactTextView);
        editImageView = view.findViewById(R.id.editImageView);
        optionBtn = view.findViewById(R.id.options);
        removeCommunityBtn = view.findViewById(R.id.removeCommunityButton);
        removeMissingBtn = view.findViewById(R.id.removeMissingButton);
        profileImageView = view.findViewById(R.id.profileImageView);


        registerForContextMenu(optionBtn);

        //On click, opens the menu
        optionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().openContextMenu(v);
            }
        });

        //TODO Open edit profile page
        //On click opens edit page
        editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileFragment fragment = new EditProfileFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        //TODO remove missing publication
        removeMissingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });

        //TODO remove community publication
        removeCommunityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommunityFragment fragment = new CommunityFragment(mAuth.getCurrentUser().getUid());
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

        //TODO Check the animation
        //If the user is anonymous
        /*if (mAuth.getCurrentUser().getEmail().equals(null)) {
            getActivity().finish();
            MainActivity main = new MainActivity();
            main.loadFragment(new MapFragment());
        }*/

        //Gets the data from Firestore
        getData();

        //Returns the view
        return view;

    }

    //TODO - Add progress bar or splash screen
    //Method to get profile data from Firestore Database
    public void getData(){

        ProgressDialog mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage(getString(R.string.loading_user_data));
        mDialog.setCancelable(false);
        mDialog.show();

        nameTextView.setText(MainActivity.sessionUser.getFirstName() + " " + MainActivity.sessionUser.getLastName());
        emailTextView.setText(getString(R.string.email) + MainActivity.sessionUser.getEmail());
        contactTextView.setText(getString(R.string.contact) + MainActivity.sessionUser.getPhoneNumber());
        if(!MainActivity.sessionUser.getImageURL().equals("null")){
            Picasso.get()
                    .load(MainActivity.sessionUser.getImageURL())
                    .centerCrop()
                    .fit()
                    .transform(new CropCircleTransformation())
                    .into(profileImageView);
        }

        mDialog.dismiss();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.options_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Handle item click
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                break;
            default:
                break;

        }
        return super.onContextItemSelected(item);
    }

    public void logout(){
        mAuth.signOut();
        Toast.makeText(getActivity(), getString(R.string.see_you_next_time), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getActivity(), AuthActivity.class));
    }

}
