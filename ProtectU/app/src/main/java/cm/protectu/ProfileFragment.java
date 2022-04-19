package cm.protectu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class ProfileFragment extends Fragment {
    //Strings
    String userName, lastName, phoneNumber;

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

        if(mAuth.getCurrentUser().isAnonymous()){

            nameTextView.setVisibility(View.GONE);
            emailTextView.setVisibility(View.GONE);
            contactTextView.setVisibility(View.GONE);
            editImageView.setVisibility(View.GONE);
            optionBtn.setVisibility(View.GONE);
            removeCommunityBtn.setVisibility(View.GONE);
            removeMissingBtn.setVisibility(View.GONE);
            profileImageView.setVisibility(View.GONE);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.want_to_go_to_login)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mAuth.signOut();
                            getActivity().finish();
                            startActivity(new Intent(getActivity(), AuthActivity.class));
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MapFragment fragment = new MapFragment();
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, fragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    });
            // Create the AlertDialog object and return it
            builder.show();
        }

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
                EditProfileFragment fragment = new EditProfileFragment(userName, lastName, phoneNumber);
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

        //TODO way to login
        //If the user is anonymous
        if(mAuth.getCurrentUser().isAnonymous()){

        }


        //Gets the data from Firestore
        getData();

        //Returns the view
        return view;

    }

    //TODO - Add progress bar or splash screen
    //Method to get profile data from Firestore Database
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
                                Log.d(TAG, "Data :" + document.getId() + " => " + document.getData());
                                //Sets the text in the view with the name and surname of the authenticated user
                                userName = document.getString("firstName");
                                lastName = document.getString("lastName");
                                phoneNumber = document.getString("phoneNumber");
                                nameTextView.setText(userName + " " + lastName);
                                emailTextView.setText("E-mail: " + mAuth.getCurrentUser().getEmail());
                                contactTextView.setText("Contact: " + phoneNumber);
                            }
                        } else {
                            //TODO Maybe reload the page or kill the session?
                            //Shows the error
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
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
