package cm.protectu.Community;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.MissingBoard.MissingBoardFragment;
import cm.protectu.R;


public class CommunityFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private CommunityAdapter communityAdapter;
    private ArrayList<CommunityCard> listOfCommunityCards;
    private FloatingActionButton floatingActionButton;
    private ImageView missingPeopleButton,backProfile;
    private SwipeRefreshLayout swipeToRefresh;
    private CommunityFragment fragment;
    private String userID;

    public CommunityFragment(String userID) {
        this.userID = userID;
    }

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

        fragment = this;

        listOfCommunityCards = new ArrayList<>();

        recyclerView = view.findViewById(R.id.communityRecyclerView);
        swipeToRefresh = view.findViewById(R.id.swipeToRefresh);
        backProfile = view.findViewById(R.id.back_id);

        communityAdapter = new CommunityAdapter(getActivity(), listOfCommunityCards, mAuth, fragment);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.setAdapter(communityAdapter);

        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                communityCardsData(userID);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeToRefresh.isRefreshing()) {
                            swipeToRefresh.setRefreshing(false);
                        }
                    }
                }, 800);
            }
        });
        communityCardsData(userID);

        floatingActionButton = view.findViewById(R.id.createMessageButton);
        missingPeopleButton = view.findViewById(R.id.missingPeopleButton);


        //TODO: DISABLE BUTTON
        /**
         *if the user is not logged in the add button disappears,
         * otherwise the button appears and if clicked it opens a bottom sheet that allows the creation of a new message in the community
         */
        if (mAuth.getCurrentUser().isAnonymous() || userID != null) {
            floatingActionButton.setVisibility(View.GONE);
        } else {
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            if (!mAuth.getCurrentUser().isAnonymous()) {
                                                                NewMessageCommunityFragment newMessageCommunityFragment = new NewMessageCommunityFragment(fragment);
                                                                newMessageCommunityFragment.show(getParentFragmentManager(), newMessageCommunityFragment.getTag());
                                                            }
                                                        }
                                                    }
            );
        }

        /**
         * This button changes to the missing fragment, replacing the community fragment with this one
         */
        if (userID == null) {
            missingPeopleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragmentToChange = new MissingBoardFragment();
                    getFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_in,  // enter
                                    R.anim.fade_out,  // exit
                                    R.anim.fade_in,   // popEnter
                                    R.anim.slide_out  // popExit
                            )
                            .replace(R.id.fragment_container, fragmentToChange)
                            .addToBackStack(null)
                            .commit();
                    /**
                     MissingBoardFragment fragment = new MissingBoardFragment();
                     FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                     transaction.replace(R.id.fragment_container, fragment);
                     transaction.addToBackStack(null);
                     transaction.commit();**/
                }
            });
        }
        else{
            missingPeopleButton.setVisibility(View.GONE);
            backProfile.setVisibility(View.VISIBLE);
        }


        //Returns the view
        return view;

    }

    /**
     * This method will fetch the messages from the database and place them in to a array list then sort them by date,
     * that is, from the newest to the oldest and then place them inside the community adapter,
     * + if fails sends an error message.
     */
    public void communityCardsData(String userId) {
        ProgressDialog cDialog = new ProgressDialog(getActivity());
        cDialog.setMessage("Loading...");
        cDialog.setCancelable(false);
        cDialog.show();
        listOfCommunityCards.clear();
        firebaseFirestore.collection("community-chat")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CommunityCard communityCard = document.toObject(CommunityCard.class);
                                if (userId == null) {
                                    listOfCommunityCards.add(communityCard);
                                } else {
                                    if (communityCard.getUserID().equals(userId)) {
                                        listOfCommunityCards.add(communityCard);
                                    }
                                }
                            }

                            Collections.sort(listOfCommunityCards, new SortCommunityCardClass());

                            communityAdapter = new CommunityAdapter(getActivity(), listOfCommunityCards, mAuth, fragment);
                            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                            recyclerView.setAdapter(communityAdapter);
                            communityAdapter.notifyDataSetChanged();


                            cDialog.dismiss();

                        } else {
                            Toast.makeText(getActivity(), "erro", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

}

