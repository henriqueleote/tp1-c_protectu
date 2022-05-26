package cm.protectu.Map;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.Buildings.Bunker.BunkerFragment;
import cm.protectu.Buildings.Bunker.NewBunkerFragment;
import cm.protectu.Buildings.Earthquake.NewEarthquakeFragment;
import cm.protectu.Buildings.Fire.NewFireFragment;
import cm.protectu.Buildings.Hospital.NewHospitalFragment;
import cm.protectu.Buildings.War.NewWarFragment;
import cm.protectu.R;

public class MarkerChooseFragment extends BottomSheetDialogFragment {


    //ImageView
    private ImageView closeBtn;

    //Button
    private Button createPinBtn;

    //Firebase Authentication
    FirebaseAuth mAuth;

    FirebaseFirestore firebaseFirestore;

    //List with the pins of the map
    public static ArrayList<MapPinTypeClass> mapPinTypes;

    private List<Marker> markerList;

    //TAG for debug logs
    private static final String TAG = AuthActivity.class.getName();

    private FragmentActivity fragment;

    public MarkerChooseFragment(FragmentActivity fragment, List<Marker> markerList){
        this.markerList = markerList;
        this.fragment = fragment;
    }

    @SuppressLint("NewApi")
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.marker_choose_bottom, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();

        //Link the view objects with the XML
        closeBtn = view.findViewById(R.id.closeBtn);
        createPinBtn = view.findViewById(R.id.createPinBtn);
        mapPinTypes = MapFragment.mapPinTypes;

        Log.d(TAG, "Array in filter: -> " + mapPinTypes.size());
        MarkerChooseAdapter adapter = new MarkerChooseAdapter(getActivity(), mapPinTypes);
        ListView listView = (ListView) view.findViewById(R.id.buttonsListView);
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
        createPinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertMarker();
            }
        });

        //Returns the view
        return view;
    }


    public void insertMarker() {
        Fragment fragment = null;
        if(MarkerChooseAdapter.checkedButton.get(MarkerChooseAdapter.checkedButton.size()-1).equalsIgnoreCase("bunker"))
            fragment = new NewBunkerFragment(new GeoPoint(markerList.get(markerList.size()-1).getPosition().latitude, markerList.get(markerList.size()-1).getPosition().longitude));
        if(MarkerChooseAdapter.checkedButton.get(MarkerChooseAdapter.checkedButton.size()-1).equalsIgnoreCase("hospital"))
            fragment = new NewHospitalFragment(new GeoPoint(markerList.get(markerList.size()-1).getPosition().latitude, markerList.get(markerList.size()-1).getPosition().longitude));
        if(MarkerChooseAdapter.checkedButton.get(MarkerChooseAdapter.checkedButton.size()-1).equalsIgnoreCase("fire"))
            fragment = new NewFireFragment(new GeoPoint(markerList.get(markerList.size()-1).getPosition().latitude, markerList.get(markerList.size()-1).getPosition().longitude));
        if(MarkerChooseAdapter.checkedButton.get(MarkerChooseAdapter.checkedButton.size()-1).equalsIgnoreCase("earthquake"))
            fragment = new NewEarthquakeFragment(new GeoPoint(markerList.get(markerList.size()-1).getPosition().latitude, markerList.get(markerList.size()-1).getPosition().longitude));
        if(MarkerChooseAdapter.checkedButton.get(MarkerChooseAdapter.checkedButton.size()-1).equalsIgnoreCase("war"))
            fragment = new NewWarFragment(new GeoPoint(markerList.get(markerList.size()-1).getPosition().latitude, markerList.get(markerList.size()-1).getPosition().longitude));

        getDialog().dismiss();
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }
}
