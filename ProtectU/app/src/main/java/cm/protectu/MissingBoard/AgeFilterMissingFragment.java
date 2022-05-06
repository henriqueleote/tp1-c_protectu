package cm.protectu.MissingBoard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.R;


public class AgeFilterMissingFragment extends BottomSheetDialogFragment {

    ArrayList<MissingCardClass> mcards, newCardS;

    //CheckBox
    private CheckBox childrens, adults, seniors;

    //Button
    private Button showResults;

    //Images
    private ImageView closeButton, selectAll,clear ;

    //Firebase Authentication
    private FirebaseAuth mAuth;

    private FirebaseFirestore firebaseFirestore;

    private MissingBoardFragment missingBoardFragment;

    /**
     * Constructor that allows initializing the list with all publications coming from the missing board class
     * @param missingBoardFragment
     * class that contains all publications
     */
    public AgeFilterMissingFragment(MissingBoardFragment missingBoardFragment) {
        this.missingBoardFragment = missingBoardFragment;
        mcards = new ArrayList<>(missingBoardFragment.missingCardClasses);
        newCardS = new ArrayList<>();
    }

    //TAG for debug logs
    private static final String TAG = AuthActivity.class.getName();

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_age_filter_mssing, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        //Link the view objects with the XML
        closeButton = view.findViewById(R.id.closeFilterID);
        showResults = view.findViewById(R.id.createNewMessageButton);
        clear = view.findViewById(R.id.clearAllID);
        selectAll = view.findViewById(R.id.allCheckID);
        childrens = view.findViewById(R.id.childrensFilterID);
        adults = view.findViewById(R.id.adultsFilterID);
        seniors = view.findViewById(R.id.seniorsFilterID);

        firebaseFirestore = FirebaseFirestore.getInstance();

        //On click closes the form sheet
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });

        /**
         * Allows you to clear all checkboxes that are selected
         */
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                childrens.setChecked(false);
                adults.setChecked(false);
                seniors.setChecked(false);
            }
        });

        /**
         *
         * Allows you to select all checkboxes
         */
        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                childrens.setChecked(true);
                adults.setChecked(true);
                seniors.setChecked(true);
            }
        });

        /**
         * Allows you to filter existing publications depending on the selected checkboxes
         */
        showResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Checkbox correspondente a das criancas
                for (MissingCardClass card: mcards){
                    if(childrens.isChecked()){
                        if(Integer.parseInt(card.getMissingAge()) <= 17 && Integer.parseInt(card.getMissingAge()) >= 0){
                            newCardS.add(card);
                        }
                    }
                    //Checkbox correspondente ao dos adultos
                    if(adults.isChecked()){
                        if(Integer.parseInt(card.getMissingAge()) <= 59 && Integer.parseInt(card.getMissingAge()) >= 18){
                            newCardS.add(card);
                        }
                    }
                    //Checkbox correspondente ao dos seniores
                    if(seniors.isChecked()){
                        if(Integer.parseInt(card.getMissingAge())>=59 && Integer.parseInt(card.getMissingAge())<=120){
                            newCardS.add(card);
                        }
                    }
                }
                if(newCardS.isEmpty() || childrens.isChecked() && adults.isChecked() && seniors.isChecked()){
                    getDialog().cancel();
                    Toast.makeText(getActivity(), "Publicações filtradas", Toast.LENGTH_SHORT).show();
                }else {
                    //adiciona os cartoes filtrados a pagina de visualizacao
                    missingBoardFragment.refreshCards(newCardS);
                    getDialog().cancel();
                    Toast.makeText(getActivity(), "Publicações filtradas", Toast.LENGTH_SHORT).show();
                }

            }

    });

        //Returns the view
        return view;
    }
}


