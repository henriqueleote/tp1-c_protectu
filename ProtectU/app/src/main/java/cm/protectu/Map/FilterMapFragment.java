package cm.protectu.Map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.R;

public class FilterMapFragment extends BottomSheetDialogFragment {

    //ImageView
    private ImageView closeBtn;

    //Button
    private Button filterBtn;

    //CheckBox
    private CheckBox checkBoxHospital, checkBoxWar;

    //Firebase Authentication
    FirebaseAuth mAuth;

    //List with the pins of the map
    public static ArrayList<MapPinClass> mapPinClasses;

    public static ArrayList<MapPinClass> filteredMapPinClasses;

    //TAG for debug logs
    private static final String TAG = AuthActivity.class.getName();

    MapFragment fragment;

    public FilterMapFragment(MapFragment fragment){
        this.fragment = fragment;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.map_filter_bottom, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        //Link the view objects with the XML
        closeBtn = view.findViewById(R.id.close);
        filterBtn = view.findViewById(R.id.filterBtn);
        checkBoxHospital = view.findViewById(R.id.checkBoxHospital);
        checkBoxWar = view.findViewById(R.id.checkBoxWar);

        checkBoxHospital.setChecked(false);
        checkBoxWar.setChecked(false);

        filteredMapPinClasses = new ArrayList<>();
        mapPinClasses = MapFragment.mapPinClasses;


        //On click closes the form sheet
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });


        //On click goes to the detailed page
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterMap();
            }
        });

        //Returns the view
        return view;
    }

    @SuppressLint("NewApi")
    public void filterMap(){
        mapPinClasses.forEach(mapPinClass -> {
            if (checkBoxWar.isChecked()) {
                if(mapPinClass.getType().equals("war"))
                    filteredMapPinClasses.add(mapPinClass);
            }
            if(checkBoxHospital.isChecked()){
                if(mapPinClass.getType().equals("hospital"))
                    filteredMapPinClasses.add(mapPinClass);
            }
        });
        getDialog().cancel();
        fragment.refreshMap();
    }
}
