package cm.protectu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class EditProfileFragment extends Fragment {

    //Firebase Authentication
    private FirebaseAuth mAuth;
    private Button saveBtn;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText contactEditText;
    private ImageView backButton;
    private FirebaseFirestore firebaseFirestore;
    private String name, surname, phoneNumber;
    BottomNavigationView bottomBar;


    private static final String TAG = MainActivity.class.getName();

    public EditProfileFragment(String name, String surname, String phoneNumber){
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        saveBtn = view.findViewById(R.id.saveButton);
        firstNameEditText = view.findViewById(R.id.firstNameEditText);
        lastNameEditText = view.findViewById(R.id.lastNameEditText);
        contactEditText = view.findViewById(R.id.contactEditText);
        backButton = view.findViewById(R.id.backButton);
        BottomNavigationView bottomBar = getActivity().findViewById(R.id.nav_view);

        //TODO -- It works but it's gone forever, doesn't restore
        //Hide the bottom bar in this page
        //bottomBar.setVisibility(View.INVISIBLE);

        firstNameEditText.setText(name);
        lastNameEditText.setText(surname);
        contactEditText.setText(phoneNumber);

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
}
