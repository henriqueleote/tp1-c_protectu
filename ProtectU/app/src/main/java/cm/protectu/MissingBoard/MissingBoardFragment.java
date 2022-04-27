package cm.protectu.MissingBoard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
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

import cm.protectu.Authentication.AuthActivity;
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


    //Firebase Authentication
    private FirebaseAuth mAuth;



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


        //Link the view objects with the XML
        myAdapter = new MissingBoardAdapter(getActivity(), missingCardClasses,getParentFragmentManager(),mAuth);
        myRecycleView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        myRecycleView.setAdapter(myAdapter);

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

        missingCardsData();

        /*
        searchNames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                    String query = intent.getStringExtra(SearchManager.QUERY);
                    doMySearch(query);
                }
            }
        });
        }*/

        /**
         * Permite ir para o fragment de criar novas publicações
         */
        if (mAuth.getCurrentUser().isAnonymous()) {
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

        MissingBoardFragment fragment = this;

        /**
         * Permite ir para o fragment correspondente às opções de filtro da publicações pelas idades
         */
        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgeFilterMissingFragment ageFilterMissingFragment = new AgeFilterMissingFragment(fragment);
                ageFilterMissingFragment.show(getParentFragmentManager(), ageFilterMissingFragment.getTag());
            }
        });

        //Returns the view
        return view;

    }



    /**
     * Permite verificar se a tarefa de ir buscar os dados na colecao é bem sucessida, depois
     * transforma os dados devolvidos na classe pretendidae e cria as respetivas publicações
     */
    public void missingCardsData() {
        missingCardClasses.clear();
        firebaseFirestore.collection("missing-board")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MissingCardClass missingCardClass = document.toObject(MissingCardClass.class);
                                missingCardClasses.add(missingCardClass);
                                myAdapter = new MissingBoardAdapter(getActivity(), missingCardClasses,getParentFragmentManager(),mAuth);
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
     * Permite trocar as publicações existentes no fragment pelas publicações filtradas pelas idades pretendidas
     * @param mCards
     */
    public void missingFilteredCardsData(ArrayList<MissingCardClass> mCards) {
        refreshCards(mCards);
    }

    public void namesFiltered(String name){
        ArrayList<MissingCardClass> missingCardsFilteredByNameClass;
        missingCardsFilteredByNameClass = new ArrayList<>();
        for(MissingCardClass mCard: missingCardClasses){
            if(mCard.getMissingName().toString() == name){
                missingCardsFilteredByNameClass.add(mCard);
            }
        }
        refreshCards(missingCardsFilteredByNameClass);
    }

    public void refreshCards(ArrayList<MissingCardClass> missCards){
        missingCardClasses.clear();
        missingCardClasses.addAll(missCards);
        myAdapter = new MissingBoardAdapter(getActivity(), missingCardClasses,getParentFragmentManager(),mAuth);
        myRecycleView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
    }


}

