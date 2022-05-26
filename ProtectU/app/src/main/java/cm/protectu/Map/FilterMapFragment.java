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
import android.widget.Toast;

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
    private FirebaseAuth mAuth;

    //List with all the pins of the map
    public static ArrayList<MapPinTypeClass> mapPinTypes;

    //List with the filtered pins of the map
    public static ArrayList<MapPinClass> filteredMapPinClasses;

    //TAG for debug logs
    private static final String TAG = AuthActivity.class.getName();

    //An object from the MapFragment
    private MapFragment fragment;

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
        ListView listView = (ListView) view.findViewById(R.id.checkBoxListView);

        //Initialize the list with the static value from the previous fragment in order to be "real time"
        mapPinTypes = MapFragment.mapPinTypes;

        //Initializes the adapter with the pins in order to show them as an recycler view
        FilterMapAdapter adapter = new FilterMapAdapter(getActivity(), mapPinTypes);
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


    //This method filters the map.
    //Firstly it clears the map in the background to only show the filtered when the users clicks. Then, for each mapPin in the general list
    //it's gonna check if there is in the checkedPositions from the adapter, if so, it adds that specific pin type to the filtered list
    //After treating the data, it refreshes the map in the background and closes the dialog
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

    @SuppressLint("NewApi")
    public static void filterBunker(){
        filteredMapPinClasses = new ArrayList<>();
        MapFragment.mapPinClasses.forEach(mapPinClass -> {
            if(mapPinClass.getType().equalsIgnoreCase("bunker"))
                filteredMapPinClasses.add(mapPinClass);
        });
        Log.d(TAG, "Size : " + filteredMapPinClasses.size());
        //then redirect to the map maybe
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }
}
