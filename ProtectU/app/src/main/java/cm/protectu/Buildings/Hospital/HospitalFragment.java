package cm.protectu.Buildings.Hospital;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.Buildings.SliderAdapter;
import cm.protectu.Map.LocationAddress;
import cm.protectu.Map.MapFragment;
import cm.protectu.R;


public class HospitalFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    SliderView sliderView;
    ArrayList<String> images;
    SliderAdapter adapter;
    String buildingID;
    RelativeLayout backButton;
    private TextView hospitalNameTextView, hospitalDescriptionTextView, locationTextView, hospitalNumberOfBedsTextView;
    private List<Address> addressesList;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton phoneButton, mapsButton;
    String hospitalContact;
    private GeoPoint location;

    //TAG for debug logs
    private static final String TAG = AuthActivity.class.getName();

    public HospitalFragment() {
    }

    public HospitalFragment(String buildingID){
        this.buildingID = buildingID;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_building_hospital, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        //Checks if there is a session, if not, redirects to the Auth page
        if (mAuth.getCurrentUser() == null) {
            getActivity().finish();
            startActivity(new Intent(getActivity(), AuthActivity.class));
        }

        firebaseFirestore = FirebaseFirestore.getInstance();

        sliderView = view.findViewById(R.id.autoImageSlider);
        backButton = view.findViewById(R.id.backButton);
        hospitalNameTextView = view.findViewById(R.id.hospitalNameTextView);
        locationTextView = view.findViewById(R.id.locationTextView);
        hospitalDescriptionTextView = view.findViewById(R.id.descriptionTextView);
        hospitalNumberOfBedsTextView = view.findViewById(R.id.hospitalNumberOfBedsTextView);
        swipeRefreshLayout = view.findViewById(R.id.swipeToRefresh);
        phoneButton = view.findViewById(R.id.phoneButton);
        mapsButton = view.findViewById(R.id.mapsButton);
        progressDialog = new ProgressDialog(getActivity());

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


        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"+latitude+","+longitude+"?q="+latitude+","+longitude+""));
                startActivity(intent);
            }
        });

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + hospitalContact));
                startActivity(intent);
            }
        });


        //Returns the view
        return view;

    }

    public void getData(){
        firebaseFirestore.collection("map-buildings").document(buildingID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    images = (ArrayList<String>) document.get("images");
                    location = (GeoPoint) document.getGeoPoint("location");
                    hospitalNameTextView.setText(document.get("buildingName").toString());
                    hospitalNumberOfBedsTextView.setText(document.get("hospitalNumberOfBeds").toString() + " " + getString(R.string.beds));
                    hospitalDescriptionTextView.setText(document.get("hospitalDescription").toString());
                    hospitalContact = document.get("buildingContact").toString();
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

