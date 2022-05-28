package cm.protectu.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.Authentication.DeleteAccountFragment;
import cm.protectu.Community.CommunityFragment;
import cm.protectu.MainActivity;
import cm.protectu.MissingBoard.MissingBoardFragment;
import cm.protectu.R;


public class ProfileFragment extends Fragment {

    //TextView
    TextView nameTextView, optionBtn, emailTextView, contactTextView;

    //ImageView
    ImageView editImageView, profileImageView, menuImageView;

    //Button
    Button removeCommunityBtn, removeMissingBtn;

    //Firebase Authentication
    FirebaseAuth mAuth;

    //Firebase Firestore
    FirebaseFirestore firebaseFirestore;

    //TAG for debug logs
    private static final String TAG = MainActivity.class.getName();

    public ProfileFragment() {
    }

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
        menuImageView = view.findViewById(R.id.menuImageView);



        registerForContextMenu(optionBtn);

        //On click, opens the menu
        optionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().openContextMenu(v);
            }
        });

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

        removeMissingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MissingBoardFragment fragment = new MissingBoardFragment(mAuth.getCurrentUser().getUid());
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

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

        menuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        //Checks if there is a session, if not, redirects to the Auth page
        if (mAuth.getCurrentUser() == null) {
            getActivity().finish();
            startActivity(new Intent(getActivity(), AuthActivity.class));
        }

        //Gets the data from Firestore
        getData();

        //Returns the view
        return view;

    }

    //Method to get profile data from Firestore Database
    public void getData(){

        ProgressDialog mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage(getString(R.string.loading_user_data));
        mDialog.setCancelable(false);
        mDialog.show();

        nameTextView.setText(MainActivity.sessionUser.getFirstName() + " " + MainActivity.sessionUser.getLastName());
        emailTextView.setText(/*getString(R.string.email) + */MainActivity.sessionUser.getEmail());
        contactTextView.setText(/*getString(R.string.contact) + */MainActivity.sessionUser.getPhoneNumber());
        if(!MainActivity.sessionUser.getImageURL().equals("null")){
            Glide.with(getActivity())
                    .load(MainActivity.sessionUser.getImageURL())
                    .centerCrop()
                    
                    .circleCrop()
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
            case R.id.delete_account:
                DeleteAccountFragment fragment = new DeleteAccountFragment();
                fragment.show(getParentFragmentManager(), fragment.getTag());
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
