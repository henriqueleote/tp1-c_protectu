package cm.protectu.Map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.Authentication.LoginFragment;
import cm.protectu.News.NewsDetailsFragment;
import cm.protectu.R;


public class MapAddMarkerFragment extends Fragment {

    //TAG for debug logs
    private static final String TAG = AuthActivity.class.getName();

    //Firebase Authentication
    private FirebaseAuth mAuth;

    //Firebase Firestore
    private FirebaseFirestore firebaseFirestore;

    //FusedLocationProviderClient
    private FusedLocationProviderClient client;

    //Map Fragment
    private SupportMapFragment supportMapFragment;

    //Floating Action Buttons
    private FloatingActionButton resetBtn, confirmPinBtn, cancelPinBtn;

    //Google Map
    private GoogleMap gMap;

    //ArrayList with markers
    private List<Marker> markerList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_map_add_zone, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        //Initialize Firebase Firestore
        firebaseFirestore = FirebaseFirestore.getInstance();

        //Checks if there is a session, if not, redirects to the Auth page
        if (mAuth.getCurrentUser() == null) {
            getActivity().finish();
            startActivity(new Intent(getActivity(), AuthActivity.class));
        }

        //Initializes the access to the database
        firebaseFirestore = FirebaseFirestore.getInstance();

        //Gets the device's current location if the permission was granted in the AuthActivity
        client = LocationServices.getFusedLocationProviderClient(getActivity());

        //Initializes the list of markers
        markerList = new ArrayList<>();

        //Link the view objects with the XML
        resetBtn = view.findViewById(R.id.resetBtn);
        confirmPinBtn = view.findViewById(R.id.confirmPinBtn);
        cancelPinBtn = view.findViewById(R.id.cancelPinBtn);

        //If the user clicks to cancel, it will ask through a dialog for certain, if so, clears the list and goes back to the map
        cancelPinBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Do you want to cancel?")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                clearMarker();
                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, new MapFragment())
                                        .addToBackStack(null)
                                        .commit();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Doesn't do nothing
                            }
                        });
                // Create the AlertDialog object and return it
                builder.show();
            }
        });

        //Onclick, it runs the method to insert the marker
        confirmPinBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                insertMarker();
            }
        });

        //Onclick, it runs the method to reset the marker
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearMarker();
            }
        });

        //Check map permissions
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //When permission granted, calls the method
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        //Initialize the map fragment
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        //Returns the view
        return view;

    }

    //TODO - Check if the users location is enabled
    //Gets the user current location and displays in the map
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //Get the users current location
        Task<Location> task = client.getLastLocation();

        //Listener in case the location is successful
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                //if the location isn't null means that it was successful
                if (location != null) {
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @SuppressLint({"NewApi", "MissingPermission"})
                        @Override
                        public void onMapReady(GoogleMap googleMap) {

                            //indirectly turns the variable public for all the call
                            gMap = googleMap;

                            //shows a blue dot with the current location
                            googleMap.setMyLocationEnabled(true);

                            //After the map is loaded, moves the map to the current location
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12));

                            //when the map is clicked
                            gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(LatLng latLng) {

                                    //if the map is empty means that the user hasnt clicked in the map in order to add a pin
                                    if(markerList.isEmpty()){
                                        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                                        markerOptions.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_map_add_pin_45dp));
                                        Marker marker = gMap.addMarker(markerOptions);
                                        markerList.add(marker);
                                    }

                                    //if the list isn't empty means that the user alright picked a location for the pin so it
                                    // replaces the new one automatically
                                    else{
                                        Marker oldMarker = markerList.get(markerList.size()-1);
                                        oldMarker.remove();
                                        markerList.clear();
                                        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                                        markerOptions.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_map_add_pin_45dp));
                                        Marker marker = gMap.addMarker(markerOptions);
                                        markerList.add(marker);
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    //Checks if the permissions are granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    //Converts the drawable to a bitmap
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int drawableType) {
        Drawable background = ContextCompat.getDrawable(context, drawableType);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //Redirects to the page to choose the type if there is a marker chosen
    public void insertMarker() {
        if(markerList.isEmpty()){
            Toast.makeText(getActivity(), "You have to choose a location", Toast.LENGTH_SHORT).show();
        }else{
            MarkerChooseFragment bottomLogin = new MarkerChooseFragment(getActivity(), markerList);
            bottomLogin.show(getActivity().getSupportFragmentManager(), bottomLogin.getTag());
        }
    }

    //Removes the marker
    public void clearMarker(){
        for (Marker marker : markerList) marker.remove();
        markerList.clear();
    }

}
