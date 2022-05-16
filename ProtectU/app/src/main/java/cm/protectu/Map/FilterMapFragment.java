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
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.Buildings.MapPinTypeClass;
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
    public static ArrayList<MapPinTypeClass> mapPinTypes;

    public static ArrayList<MapPinClass> filteredMapPinClasses;

    //TAG for debug logs
    private static final String TAG = AuthActivity.class.getName();

    MapFragment fragment;

    public FilterMapFragment(MapFragment fragment){
        this.fragment = fragment;
    }

    @SuppressLint("NewApi")
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.map_filter_bottom, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        //Link the view objects with the XML
        closeBtn = view.findViewById(R.id.closeBtn);
        filterBtn = view.findViewById(R.id.filterBtn);
        //checkBoxHospital = view.findViewById(R.id.checkBoxHospital);
        //checkBoxWar = view.findViewById(R.id.checkBoxWar);
        mapPinTypes = MapFragment.mapPinTypes;

        //checkBoxHospital.setChecked(false);
        //checkBoxWar.setChecked(false);

        Log.d(TAG, "Array in filter: -> " + mapPinTypes.size());
        FilterMapAdapter adapter = new FilterMapAdapter(getActivity(), mapPinTypes);
        ListView listView = (ListView) view.findViewById(R.id.checkBoxListView);
        listView.setAdapter(adapter);

        //TODO IF ALREADY IN FILTER, TRUE IN THE CHECKBOX

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
                filter();
            }
        });

        //Returns the view
        return view;
    }

    /*
    @SuppressLint("NewApi")
    public void filterMap(){
        filteredMapPinClasses = new ArrayList<>();
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
    }*/

    @SuppressLint("NewApi")
    public void filter(){
        fragment.clearMap();
        filteredMapPinClasses = new ArrayList<>();
        MapFragment.mapPinClasses.forEach(mapPinClass -> {
            FilterMapAdapter.checkedPositions.forEach(checked -> {
                if(checked.equalsIgnoreCase(mapPinClass.getType()))
                    filteredMapPinClasses.add(mapPinClass);
            });
        });
        if(FilterMapAdapter.checkedPositions.size() == 0)
            filteredMapPinClasses.clear();
        if(FilterMapAdapter.checkedPositions.size() == mapPinTypes.size())
            filteredMapPinClasses.clear();
        getDialog().cancel();
        fragment.refreshMap();
    }
}
