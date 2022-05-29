package cm.protectu.MissingBoard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import cm.protectu.MainActivity;
import cm.protectu.R;

/**
 * calss of missing board
 */
public class MissingBoardFragment extends Fragment {

    public  ArrayList<MissingCardClass> missingCardClasses;
    private RecyclerView myRecycleView;
    private MissingBoardAdapter myAdapter;
    private FirebaseFirestore firebaseFirestore;
    private FloatingActionButton floatingActionButton;
    private CardView age;
    private SearchView searchNames;
    private SwipeRefreshLayout swipeToRefresh;
    private ImageView menuImageView;
    private String userID;
    private MissingBoardFragment fragment;


    //Firebase Authentication
    private FirebaseAuth mAuth;

    public MissingBoardFragment(String userID) {
        this.userID = userID;
    }

    public MissingBoardFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_missingboard, container, false);

        missingCardClasses = new ArrayList<>();

        myRecycleView = (RecyclerView) view.findViewById(R.id.localCardsID);
        floatingActionButton = view.findViewById(R.id.createMissingBoardButtonID);
        age = view.findViewById(R.id.ageFilterButtonID);
        searchNames = view.findViewById(R.id.searchID);
        swipeToRefresh = view.findViewById(R.id.swipeToRefreshMissing);
        menuImageView = view.findViewById(R.id.menuImageView);

        int searchCloseButtonId = searchNames.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = this.searchNames.findViewById(searchCloseButtonId);

        fragment = this;

        //Link the view objects with the XML
        myAdapter = new MissingBoardAdapter(getActivity(), missingCardClasses,getParentFragmentManager(),mAuth, fragment,userID);
        myRecycleView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        myRecycleView.setAdapter(myAdapter);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        //Checks if there is a session, if not, redirects to the Auth page
        if (mAuth.getCurrentUser() == null) {
            getActivity().finish();
            //Swipe animation ?? not sure, consult previous code
            //getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            startActivity(new Intent(getActivity(), AuthActivity.class));
        }

        firebaseFirestore = FirebaseFirestore.getInstance();

        missingCardsData(userID);

        /**
         *
         * Allows you to refresh the page by loading data back to the page
         */
        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                missingCardsData(userID);
                searchNames.setQuery("", false);
                searchNames.setIconified(true);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeToRefresh.isRefreshing()) {
                            swipeToRefresh.setRefreshing(false);
                        }
                    }
                }, 500);
            }
        });

        /**
         *
         * Allows you to exit the name filter and put all posts back
         */
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                missingCardsData(userID);
                searchNames.setQuery("", false);
                searchNames.setIconified(true);
            }
        });

        /**
         *
         * Lets you go to the fragment to create new posts
         */
        if (mAuth.getCurrentUser().isAnonymous() || userID != null) {
            floatingActionButton.setVisibility(View.GONE);
        } else {
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mAuth.getCurrentUser().isAnonymous()) {
                        NewMissingPostFragment fragment = new NewMissingPostFragment();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }
            });
        }



        /**
         * Allows you to go to the fragment corresponding to the filter options of publications by age
         */
        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgeFilterMissingFragment ageFilterMissingFragment = new AgeFilterMissingFragment(fragment);
                ageFilterMissingFragment.show(getParentFragmentManager(), ageFilterMissingFragment.getTag());
            }
        });

        searchNames.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String nameText) {
                namesFiltered(nameText);
                return true;
            }
        });

        menuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        //Returns the view
        return view;

    }



    /**
     * Allows you to check if the task of fetching the data in the collection is successful, then
     * transforms the returned data into the desired class and creates the respective publications
     */
    public void missingCardsData(String id) {
        missingCardClasses.clear();
        firebaseFirestore.collection("missing-board")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MissingCardClass missingCardClass = document.toObject(MissingCardClass.class);
                                if (id == null) {
                                    missingCardClasses.add(missingCardClass);
                                } else {
                                    if (missingCardClass.getUserID().equals(id)) {
                                        missingCardClasses.add(missingCardClass);
                                    }
                                }
                                Collections.sort(missingCardClasses, new SortMissingBoardCardClass());
                                myAdapter = new MissingBoardAdapter(getActivity(), missingCardClasses,getParentFragmentManager(),mAuth,fragment,id);
                                myRecycleView.setAdapter(myAdapter);
                                myAdapter.notifyDataSetChanged();

                            }

                        } else {
                            Toast.makeText(getActivity(),"erro", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    /**
     *
     * Allows you to save publications that have the name of the person to be searched for
     * @param name
     */
    public void namesFiltered(String name){
        ArrayList<MissingCardClass> missingCardsFilteredByNameClass;
        missingCardsFilteredByNameClass = new ArrayList<>();
        for(MissingCardClass mCard: missingCardClasses){
            if(mCard.getMissingName().contains(name)){
                missingCardsFilteredByNameClass.add(mCard);
            }
        }
        refreshCards(missingCardsFilteredByNameClass);
    }


    public void refreshCards(ArrayList<MissingCardClass> missCards){
        if(missCards.isEmpty()){
            Toast.makeText(getActivity(), R.string.no_posts, Toast.LENGTH_SHORT).show();
        }else{
            missingCardClasses.clear();
            missingCardClasses.addAll(missCards);
            myAdapter = new MissingBoardAdapter(getActivity(), missingCardClasses,getParentFragmentManager(),mAuth,fragment,userID);
            myRecycleView.setAdapter(myAdapter);
            myAdapter.notifyDataSetChanged();
        }
    }
}

