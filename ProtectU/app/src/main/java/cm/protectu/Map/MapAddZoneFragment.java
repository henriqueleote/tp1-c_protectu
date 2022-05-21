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
import android.graphics.Color;
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
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
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
import cm.protectu.R;


public class MapAddZoneFragment extends Fragment {

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

    //Google Polygon
    private Polygon polygon;

    //List of Locations
    private List<LatLng> latLngList;

    //List of Markers
    private List<Marker> markerList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_map_add_zone, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        //Checks if there is a session, if not, redirects to the Auth page
        if (mAuth.getCurrentUser() == null) {
            getActivity().finish();
            startActivity(new Intent(getActivity(), AuthActivity.class));
        }

        //Variables initialization
        polygon = null;
        latLngList = new ArrayList<>();
        markerList = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        client = LocationServices.getFusedLocationProviderClient(getActivity());

        //Link the view objects with the XML
        resetBtn = view.findViewById(R.id.resetBtn);
        confirmPinBtn = view.findViewById(R.id.confirmPinBtn);
        cancelPinBtn = view.findViewById(R.id.cancelPinBtn);

        //Onclick shows a context box asking the user for confirmation to cancel the zone creating
        cancelPinBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Do you want to cancel?")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                clearZone();
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

        //Onclick checks if there are at least 3 dots to complete a polygon, if there are, runs the method
        confirmPinBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                if(markerList.size() < 3){
                    Toast.makeText(getActivity(), "It must have at least 3 points to make a zone", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    insertZone();
                }
            }
        });

        //Onclick resets the map
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearZone();
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

                            //Loads the map without animation with the device's current location in the map
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12));

                            //when the map is clicked, adds the dots to map and makes a polygon if there are more than 3
                            gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(LatLng latLng) {
                                    MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                                    markerOptions.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_map_add_pin_45dp));
                                    Marker marker = gMap.addMarker(markerOptions);
                                    latLngList.add(latLng);
                                    markerList.add(marker);
                                    if (polygon != null) polygon.remove();
                                    PolygonOptions polygonOptions = new PolygonOptions().addAll(latLngList).clickable(true);
                                    polygon = gMap.addPolygon(polygonOptions);
                                    polygon.setStrokeColor(Color.RED);
                                    polygon.setFillColor(0x3Fb0233d);
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

    //Inserts the zone in the database and redirects to the map with the updates
    public void insertZone() {
        if(markerList.isEmpty()){
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MapFragment())
                    .addToBackStack(null)
                    .commit();
        }else{

            //shows a progress dialog meanwhile
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Adding zone");
            progressDialog.setMessage("Loading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();

            //run the list of markers and adds the locations to a list
            List<GeoPoint> databaseList = new ArrayList<>();
            for (int i = 0; i < markerList.size(); i++) {
                databaseList.add(new GeoPoint(markerList.get(i).getPosition().latitude, markerList.get(i).getPosition().longitude));
            }

            //Generates a reference in the database and adds the map of itens to the firestore
            DocumentReference documentReference = firebaseFirestore.collection("map-zones").document();
            Map<String, Object> markersData = new HashMap<>();
            markersData.put("zoneID", documentReference.getId());
            markersData.put("points", databaseList);
            documentReference.set(markersData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Log.d(TAG, "DocumentSnapshot with the ID: " + documentReference.getId());
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, new MapFragment())
                                    .addToBackStack(null)
                                    .commit();
                        }
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

    //Removes the zone, and all the pins associated
    public void clearZone(){
        if (polygon != null) polygon.remove();
        for (Marker marker : markerList) marker.remove();
        latLngList.clear();
        markerList.clear();
    }

}
