package cm.protectu;

import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class AgeFilterMissingFragment extends BottomSheetDialogFragment {

    ArrayList<MissingCard> mcards, newCardS;

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
     * Construtor que permite inicializar a lista com todos as publicações provenientes da classe missing board
     * @param missingBoardFragment classe que contem todos as publicações
     */
    public AgeFilterMissingFragment(MissingBoardFragment missingBoardFragment) {
        this.missingBoardFragment = missingBoardFragment;
        mcards = new ArrayList<>(missingBoardFragment.missingCards);
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
         * Permite limpar todas as checkbox que estejam selecionadas
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
         * Permite selecionar todas as checkbox
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
         * Permite filtrar as publicações existentes consoante as checkbox selecionadas
         */
        showResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(childrens.isChecked() && adults.isChecked() && seniors.isChecked() || !(childrens.isChecked() && adults.isChecked() && seniors.isChecked())){
                    missingBoardFragment.missingCardsData();
                    getDialog().cancel();
                }
                else{
                    //Checkbox correspondente a das criancas
                    for (MissingCard card: mcards){
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
                }

                //adiciona os cartoes filtrados a pagina de visualizacao
                missingBoardFragment.missingFilteredCardsData(newCardS);
                getDialog().cancel();
            }
        });



        //Returns the view
        return view;
    }

}
