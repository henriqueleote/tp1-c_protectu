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
import cm.protectu.Buildings.MapPinTypeClass;
import cm.protectu.Buildings.NewBunkerFragment;
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
        if(MarkerChooseAdapter.checkedButton.get(MarkerChooseAdapter.checkedButton.size()-1).equalsIgnoreCase("bunker")) {
            getDialog().dismiss();
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new NewBunkerFragment(new GeoPoint(markerList.get(markerList.size()-1).getPosition().latitude, markerList.get(markerList.size()-1).getPosition().longitude)))
                    .addToBackStack(null)
                    .commit();
        }else{
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Adding marker");
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            DocumentReference documentReference = firebaseFirestore.collection("map-pins").document();
            Map<String, Object> markersData = new HashMap<>();
            markersData.put("pinID", documentReference.getId());
            markersData.put("location", new GeoPoint(markerList.get(markerList.size()-1).getPosition().latitude, markerList.get(markerList.size()-1).getPosition().longitude));
            markersData.put("type", MarkerChooseAdapter.checkedButton.get(MarkerChooseAdapter.checkedButton.size()-1));
            documentReference.set(markersData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Log.d(TAG, "DocumentSnapshot with the ID: " + documentReference.getId());
                            getDialog().dismiss();
                            getFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, new MapFragment())
                                    .addToBackStack(null)
                                    .commit();}
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Log.w(TAG, "Error writing document", e);
                        }
                    });
        }

    }
}
