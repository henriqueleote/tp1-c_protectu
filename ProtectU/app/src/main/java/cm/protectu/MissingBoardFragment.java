package cm.protectu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

/**
 * calss of missing board
 */
public class MissingBoardFragment extends Fragment {

    private ArrayList<MissingCard> missingCards;
    private RecyclerView myRecycleView;
    private MissingBoardAdapter myAdapter;
    private FirebaseFirestore firebaseFirestore;
    private FloatingActionButton floatingActionButton;


    //Firebase Authentication
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_missingboard, container, false);

        missingCards = new ArrayList<>();
        myRecycleView = (RecyclerView) view.findViewById(R.id.localCardsID);
        floatingActionButton = view.findViewById(R.id.createMissingBoardButtonID);
        myAdapter = new MissingBoardAdapter(getActivity(), missingCards,getParentFragmentManager(),mAuth);

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

        //Returns the view
        return view;

    }

    /**
     * Permite verificar se a tarefa de ir buiscar os dados na colecao é bem sucessido ou n e dps transforma os dados devolvidos na classe pretendida, cria os respetivos cards
     */
    public void missingCardsData() {
        firebaseFirestore.collection("missing-board")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MissingCard missingCard = document.toObject(MissingCard.class);
                                missingCards.add(missingCard);
                                myAdapter = new MissingBoardAdapter(getActivity(), missingCards,getParentFragmentManager(),mAuth);
                                myRecycleView.setAdapter(myAdapter);
                                myAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(),"erro", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
}
}
