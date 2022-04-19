package cm.protectu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;


public class MissingPostFragment extends Fragment {

    //Firebase Authentication
    private FirebaseAuth mAuth;
    private ImageView arrowBack;
    private TextView profileName;
    private TextView description_post_id;
    private String userName;
    private String description;

    public MissingPostFragment(String userName, String description) {
        this.userName = userName;
        this.description = description;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_missing_post, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        arrowBack = view.findViewById(R.id.backID);
        profileName = view.findViewById(R.id.profileNamePostID);
        profileName.setText(userName);
        description_post_id = view.findViewById(R.id.descriptionPostID);
        description_post_id.setText(description);

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

        //Returns the view
        return view;

    }

}
