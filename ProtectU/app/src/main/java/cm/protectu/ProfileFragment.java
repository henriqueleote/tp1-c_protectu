package cm.protectu;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class ProfileFragment extends Fragment {

    //TextView
    TextView nameTextView, optionBtn;

    //Button
    Button logoutBtn;

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
        logoutBtn = view.findViewById(R.id.logoutButton);
        optionBtn = view.findViewById(R.id.options);
        registerForContextMenu(optionBtn);

        //On click, opens the menu
        optionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().openContextMenu(v);
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
        if (mAuth.getCurrentUser().getEmail().equals(null)) {
            getActivity().finish();
            MainActivity main = new MainActivity();
            main.loadFragment(new MapFragment());
        }

        //Gets the data from Firestore
        getData();

        //On click signs out the session and redirects to the Auth page
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

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
                                nameTextView.setText(document.getString("firstName") + " " + document.getString("lastName"));
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
