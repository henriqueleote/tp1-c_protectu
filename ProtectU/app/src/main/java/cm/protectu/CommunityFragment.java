package cm.protectu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class CommunityFragment extends Fragment {

    //Firebase Authentication
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private CommunityAdapter communityAdapter;
    private ArrayList<CommunityCard> listOfCommunityCards;
    private FloatingActionButton floatingActionButton;
    private ImageView missingPeopleButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_community, container, false);

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

        firebaseFirestore = FirebaseFirestore.getInstance();

        listOfCommunityCards = new ArrayList<>();
        recyclerView = view.findViewById(R.id.communityRecyclerView);
        communityAdapter = new CommunityAdapter(getActivity(), listOfCommunityCards, mAuth);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.setAdapter(communityAdapter);

        communityCardsData();

        CommunityFragment fragment = this;

        floatingActionButton = view.findViewById(R.id.createMessageButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mAuth.getCurrentUser().isAnonymous()) {
                    NewMessageCommunity newMessageCommunity = new NewMessageCommunity(fragment);
                    newMessageCommunity.show(getParentFragmentManager(), newMessageCommunity.getTag());
                }
            }
        }
        );

        missingPeopleButton = view.findViewById(R.id.missingPeopleButton);

        missingPeopleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MissingBoardFragment fragment = new MissingBoardFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        //Returns the view
        return view;

    }

    public void communityCardsData() {
        listOfCommunityCards.clear();
        firebaseFirestore.collection("community-chat")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CommunityCard communityCard = document.toObject(CommunityCard.class);
                                listOfCommunityCards.add(communityCard);
                                communityAdapter = new CommunityAdapter(getActivity(), listOfCommunityCards, mAuth);
                                recyclerView.setAdapter(communityAdapter);
                                communityAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), "erro", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}

