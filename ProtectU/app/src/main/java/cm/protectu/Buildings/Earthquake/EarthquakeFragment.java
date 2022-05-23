package cm.protectu.Buildings.Earthquake;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.Buildings.SliderAdapter;
import cm.protectu.LocationAddress;
import cm.protectu.Map.MapFragment;
import cm.protectu.R;


public class EarthquakeFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    SliderView sliderView;
    ArrayList<String> images;
    ArrayList<String> extras;
    SliderAdapter adapter;
    String buildingID;
    RelativeLayout backButton;
    private TextView earthquakeNameTextView, earthquakeRichterTextView, earthquakeDeathCountTextView, earthquakeMissingCountTextView, locationTextView;
    private List<Address> addressesList;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;


    //TAG for debug logs
    private static final String TAG = AuthActivity.class.getName();

    public EarthquakeFragment(String buildingID){
        this.buildingID = buildingID;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_building_earthquake, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        //TODO Check the animation
        //Checks if there is a session, if not, redirects to the Auth page
        if (mAuth.getCurrentUser() == null) {
            getActivity().finish();
            startActivity(new Intent(getActivity(), AuthActivity.class));
        }

        firebaseFirestore = FirebaseFirestore.getInstance();

        sliderView = view.findViewById(R.id.autoImageSlider);
        backButton = view.findViewById(R.id.backButton);
        earthquakeNameTextView = view.findViewById(R.id.earthquakeNameTextView);
        earthquakeRichterTextView = view.findViewById(R.id.earthquakeRichterTextView);
        earthquakeDeathCountTextView = view.findViewById(R.id.fireDeathCountTextView);
        earthquakeMissingCountTextView = view.findViewById(R.id.fireMissingCountTextView);
        locationTextView = view.findViewById(R.id.locationTextView);
        swipeRefreshLayout = view.findViewById(R.id.swipeToRefresh);
        progressDialog = new ProgressDialog(getActivity());
        RecyclerViewLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 800);
            }
        });

        getData();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapFragment fragment = new MapFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        //Returns the view
        return view;

    }

    public void getData(){
        firebaseFirestore.collection("map-buildings").document(buildingID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                GeoPoint location = null;
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    images = (ArrayList<String>) document.get("images");
                    extras = (ArrayList<String>) document.get("extras");
                    location = (GeoPoint) document.getGeoPoint("location");
                    Log.d(TAG, "LOcation - " + location.toString());
                    earthquakeNameTextView.setText(document.get("buildingName").toString());
                    earthquakeRichterTextView.setText(document.get("earthquakeRichter").toString());
                    earthquakeDeathCountTextView.setText(document.get("earthquakeDeathCount").toString());
                    earthquakeMissingCountTextView.setText(document.get("earthquakeMissingCount").toString());
                    //Images Adapter
                    adapter = new SliderAdapter(getActivity(), images);
                    sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
                    sliderView.setSliderAdapter(adapter);
                    sliderView.setAutoCycle(true);
                    sliderView.startAutoCycle();

                    //Extras Adapter
                    progressDialog.dismiss();
                }
                locationTextView.setText(LocationAddress.getLocation(getActivity(), location));
            }
        });
    }
}

