package cm.protectu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


public class MissingBoardFragment extends Fragment {

    private List<Card> cards;
    RecyclerView myrv;
    RecyclerViewAdapter myAdapter;

    //Firebase Authentication
    private FirebaseAuth mAuth;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_missingboard, container, false);

        cards = new ArrayList<>();
        cards.add(new Card("Tommy", "blablalbalblalbalbalblbalblalbalblalbal", 56, 91919919,R.drawable.tommy));
        cards.add(new Card("Tommy", "blablalbalblalbalbalblbalblalbalblalbal", 56, 91919919,R.drawable.tommy));
        cards.add(new Card("Tommy", "blablalbalblalbalbalblbalblalbalblalbal", 56, 91919919,R.drawable.tommy));
        cards.add(new Card("Tommy", "blablalbalblalbalbalblbalblalbalblalbal", 56, 91919919,R.drawable.tommy));
        cards.add(new Card("Tommy", "blablalbalblalbalbalblbalblalbalblalbal", 56, 91919919,R.drawable.tommy));
        cards.add(new Card("Tommy", "blablalbalblalbalbalblbalblalbalblalbal", 56, 91919919,R.drawable.tommy));

        myrv = (RecyclerView) view.findViewById(R.id.recyclerview_id);
        myAdapter = new RecyclerViewAdapter(getActivity(),cards);
        myrv.setLayoutManager(new GridLayoutManager(getActivity(),3));
        myrv.setAdapter(myAdapter);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

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
