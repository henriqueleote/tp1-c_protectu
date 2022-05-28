package cm.protectu.Buildings.Bunker;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.Map.LocationAddress;
import cm.protectu.Map.MapPinTypeClass;
import cm.protectu.Buildings.SliderAdapter;
import cm.protectu.Map.MapFragment;
import cm.protectu.R;


public class BunkerFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    SliderView sliderView;
    ArrayList<String> images;
    ArrayList<String> extras;
    SliderAdapter adapter;
    String buildingID;
    RelativeLayout backButton;
    private TextView bunkerNameTextView, locationTextView, descriptionTextView;
    private ArrayList<MapPinTypeClass> buildingAllExtrasList;
    private ArrayList<MapPinTypeClass> finalExtrasList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    BunkerExtrasAdapter extrasAdapter;
    LinearLayoutManager HorizontalLayout;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout buildingExtrasContainer;
    private FloatingActionButton phoneButton, mapsButton;
    String bunkerContact;
    private GeoPoint location;

    public BunkerFragment(String buildingID){
        this.buildingID = buildingID;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_building_bunker, container, false);

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
        recyclerView = view.findViewById(R.id.buildingExtrasRecyclerView);
        bunkerNameTextView = view.findViewById(R.id.bunkerNameTextView);
        locationTextView = view.findViewById(R.id.locationTextView);
        descriptionTextView = view.findViewById(R.id.descriptionTextView);
        swipeRefreshLayout = view.findViewById(R.id.swipeToRefresh);
        buildingExtrasContainer = view.findViewById(R.id.buildingExtrasContainer);
        progressDialog = new ProgressDialog(getActivity());
        RecyclerViewLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        phoneButton = view.findViewById(R.id.phoneButton);
        mapsButton = view.findViewById(R.id.mapsButton);
        buildingAllExtrasList = MapFragment.buildingAllExtrasList;
        extras = new ArrayList<>();
        finalExtrasList = new ArrayList<>();

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
                intent.setData(Uri.parse("tel:" + bunkerContact));
                startActivity(intent);
            }
        });


        //Returns the view
        return view;

    }

    @SuppressLint("NewApi")
    public void getExtras(){
        extras.forEach(extra -> {
            buildingAllExtrasList.forEach(allExtras -> {
                if(extra.equals(allExtras.getType())){
                    finalExtrasList.add(allExtras);
                }
            });
        });
        extrasAdapter = new BunkerExtrasAdapter(getActivity(), finalExtrasList);
        HorizontalLayout = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(RecyclerViewLayoutManager);
        recyclerView.setLayoutManager(HorizontalLayout);
        recyclerView.setAdapter(extrasAdapter);
    }

    public void getData(){
        firebaseFirestore.collection("map-buildings").document(buildingID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    images = (ArrayList<String>) document.get("images");
                    extras = (ArrayList<String>) document.get("extras");
                    location = (GeoPoint) document.getGeoPoint("location");
                    bunkerNameTextView.setText(document.get("buildingName").toString());
                    descriptionTextView.setText(document.get("buildingDescription").toString());
                    bunkerContact = document.get("buildingContact").toString();
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
                if(extras == null)
                    buildingExtrasContainer.setVisibility(View.GONE);
                else
                    getExtras();
            }
        });
    }
}

