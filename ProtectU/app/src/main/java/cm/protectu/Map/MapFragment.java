package cm.protectu.Map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.Authentication.ForgotPasswordFragment;
import cm.protectu.MainActivity;
import cm.protectu.Map.Buildings.BuildingClass;
import cm.protectu.Map.Buildings.MapBuildingFragment;
import cm.protectu.MissingBoard.MissingBoardFragment;
import cm.protectu.R;


public class MapFragment extends Fragment {

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

    //Floating Action Button
    private FloatingActionButton createBtn, createZoneBtn, createMarkerBtn, changeMapTypeBtn, mapFilterBtn;

    //LinearLayout
    private LinearLayout containerMarkerBtn, containerZoneBtn;

    boolean areAllButtonVisible;

    private GoogleMap gMap;

    public static Location currentLocation;

    //List with the pins of the map
    public static ArrayList<MapPinClass> mapPinClasses;

    //List with the zones of the map
    public static ArrayList<List<Object>> mapZoneClasses;

    //List with the buildings of the map
    public static ArrayList<BuildingClass> buildingsList;

    MapFragment fragment = this;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        //TODO Check the animation
        //Checks if there is a session, if not, redirects to the Auth page
        if (mAuth.getCurrentUser() == null) {
            getActivity().finish();
            //Swipe animation ?? not sure, consult previous code
            //getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            startActivity(new Intent(getActivity(), AuthActivity.class));
        }

        firebaseFirestore = FirebaseFirestore.getInstance();
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        //resetLocation = view.findViewById(R.id.resetLocationBtn);
        createMarkerBtn = view.findViewById(R.id.createMarkerBtn);
        createZoneBtn = view.findViewById(R.id.createZoneBtn);
        createBtn = view.findViewById(R.id.createBtn);
        mapFilterBtn = view.findViewById(R.id.mapFilterBtn);
        changeMapTypeBtn = view.findViewById(R.id.changeMapTypeBtn);
        containerZoneBtn = view.findViewById(R.id.containerZoneBtn);
        containerMarkerBtn = view.findViewById(R.id.containerMarkerBtn);

        containerZoneBtn.setVisibility(View.GONE);
        containerMarkerBtn.setVisibility(View.GONE);
        areAllButtonVisible = false;

        mapPinClasses = new ArrayList<>();
        mapZoneClasses = new ArrayList<>();
        buildingsList = new ArrayList<>();

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!areAllButtonVisible) {
                    containerZoneBtn.setVisibility(View.VISIBLE);
                    containerMarkerBtn.setVisibility(View.VISIBLE);
                    createBtn.animate().rotation(45).setDuration(200).start();
                    areAllButtonVisible = true;
                }else{
                    containerZoneBtn.setVisibility(View.GONE);
                    containerMarkerBtn.setVisibility(View.GONE);
                    createBtn.animate().rotation(0).setDuration(200).start();
                    areAllButtonVisible = false;
                }
            }
        });

        //TODO Comment
        changeMapTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(gMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL){
                    gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }else{
                    gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                getCurrentLocation();
            }
        });

        createZoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new MapAddZoneFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        createMarkerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new MapAddMarkerFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        mapFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterMapFragment bottomFilter = new FilterMapFragment(fragment);
                bottomFilter.show(getParentFragmentManager(), bottomFilter.getTag());
            }
        });

        //Initialize the map fragment
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        //Check map permissions
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //If the app has permission
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        //Returns the view
        return view;

    }

    //TODO - Check if the users location is enabled
    //Gets the user current location and displays in the map
    private void getCurrentLocation() {
        @SuppressLint("MissingPermission") Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @SuppressLint({"NewApi", "MissingPermission"})
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            //Only shows the button if its a admin or authority
                            if(!mAuth.getCurrentUser().isAnonymous())
                                if(!MainActivity.sessionUser.getUserType().equals("user"))
                                    createBtn.setVisibility(View.VISIBLE);

                            currentLocation = location;

                            gMap = googleMap;

                            googleMap.setMyLocationEnabled(true);

                            gMap.clear();


                            if(FilterMapFragment.filteredMapPinClasses == null || FilterMapFragment.filteredMapPinClasses.isEmpty()){
                                ProgressDialog mDialog = new ProgressDialog(getActivity());
                                mDialog.setMessage("Loading markers and zones...");
                                mDialog.setCancelable(false);
                                mDialog.show();

                                /*  THESE THREE LISTENERS UNDER CAN'T BE IN OUTSIDE METHODS OR THEY WONT BE CALLED  */
                                //get the pin data on map load
                                firebaseFirestore.collection("map-pins").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d(TAG, "\nPin Object Data (Database) => " + document.getData() + "\n");
                                                GeoPoint location = document.getGeoPoint("location");
                                                MapPinClass mapPin = new MapPinClass(document.getId(), location, document.get("type").toString());
                                                mapPinClasses.add(mapPin);

                                                BitmapDescriptor icon = null;
                                                LatLng latLng = new LatLng(mapPin.getLocation().getLatitude(), mapPin.getLocation().getLongitude());
                                                if (mapPin.getType().trim().equals("war")) {
                                                    icon = bitmapDescriptorFromVector(getActivity(), R.drawable.ic_map_war_pin_45dp);
                                                }
                                                if (mapPin.getType().trim().equals("hospital")) {
                                                    icon = bitmapDescriptorFromVector(getActivity(), R.drawable.ic_map_hospital_pin_45dp);
                                                }
                                                MarkerOptions options = new MarkerOptions().position(latLng).title(mapPin.getType()).snippet(mapPin.getPinID()).icon(icon);
                                                googleMap.addMarker(options);
                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });

                                //get the marker data on map load
                                firebaseFirestore.collection("map-zones").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d(TAG, "\nZone Object Data (Database) => " + document.getData() + "\n");
                                                List<Object> mapZone = (List<Object>) document.get("points");
                                                mapZoneClasses.add(mapZone);
                                                PolygonOptions poly = new PolygonOptions().strokeColor(Color.RED).fillColor(0x3Fb0233d).clickable(true);
                                                for (int i = 0; i < mapZone.size(); i++) {
                                                    GeoPoint polyGeo = (GeoPoint) mapZone.get(i);
                                                    double lat = polyGeo.getLatitude();
                                                    double lng = polyGeo.getLongitude();
                                                    LatLng latLng = new LatLng(lat, lng);
                                                    poly.add(latLng);
                                                }
                                                googleMap.addPolygon(poly);
                                            }
                                            Log.d(TAG, "CODE: " + mapZoneClasses.toString());
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });

                                //get the marker data on map load
                                firebaseFirestore.collection("map-buildings").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                //Log.d(TAG, "\nBuilding Object Data (Database) => " + document.getData() + "\n");
                                                ArrayList<String> images = (ArrayList<String>) document.get("images");
                                                BuildingClass building = new BuildingClass(document.get("buildingID").toString(), document.get("buildingName").toString(), document.get("type").toString(), images);
                                                buildingsList.add(building);
                                            }
                                            mDialog.dismiss();
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                            }

                            else{
                                loadFilteredPins();
                            }

                            /*  THESE THREE LISTENERS ABOVE CAN'T BE IN OUTSIDE METHODS OR THEY WONT BE CALLED  */

                            //TODO CHECK IF IT ROTATES WHEN ON MOBILE

                            //TODO It would be nice instead of a marker, put those blue dots from google
                            // and apple maps with the compass sensor telling where it's turned to

                            //Loads the map without animation with the device's current location in the map
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12));

                            gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    BuildingClass building = null;
                                    marker.hideInfoWindow();
                                    String buildingID = marker.getSnippet();
                                    for(int i = 0; i < buildingsList.size(); i++)
                                        if (buildingsList.get(i).getBuildingID().equals(buildingID))
                                            building = buildingsList.get(i);
                                    if(building == null)
                                        Toast.makeText(getActivity(), "Error getting building", Toast.LENGTH_SHORT).show();
                                    else{
                                        //Opens the bottom sheet fragment
                                        MapBuildingFragment bottomBuilding = new MapBuildingFragment(building.getBuildingID(), building.getBuildingName(), building.getImages());
                                        bottomBuilding.show(getActivity().getSupportFragmentManager(), bottomBuilding.getTag());
                                    }
                                    return true;
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    //TODO - Add the filter with the method where equals before the get, would be nice if it was possible to do without duplicate code.
    //TODO CHECK IF IT WORKS PROPERLY
    //Gets the map's pins from firebase to an arraylist
    public void getPinsFromDatabase() {
        firebaseFirestore.collection("map-pins")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "\nPin Object Data (Database) => " + document.getData() + "\n");
                                GeoPoint location = document.getGeoPoint("location");
                                MapPinClass pin = new MapPinClass(document.getId(), location, document.get("type").toString());
                                Log.d(TAG, "\nPin Object Data (Object) => " + pin + "\n");
                                mapPinClasses.add(pin);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    //TODO - IT WORKS WITH THE ARRAY
    //TODO - SOMETIMES DOESN'T WORK, I THINK ITS BECAUSE OF THE INTERNET ACCESS, WE HAVE TO PUT THE LOAD AS AN ASYNC FUNCTION
    //Gets the map's zones from firebase to an arraylist
    public void getZoneFromDatabase() {
        firebaseFirestore.collection("map-zones")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "\nZone Object Data (Database) => " + document.getData() + "\n");
                                List<Object> polyPoint = (List<Object>) document.get("points");
                                mapZoneClasses.add(polyPoint);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    //TODO ERROR WHEN THE APP ITS OPENED AND WE GO DIRECTLY TO THE PROFILE
    //TODO FIX THIS ERROR
    //    java.lang.NullPointerException: Attempt to invoke virtual method 'android.graphics.drawable.Drawable android.content.Context.getDrawable(int)' on a null object reference
    //Converts the drawable to a bitmap
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int drawableType) {
        Drawable background = ContextCompat.getDrawable(context, drawableType);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @SuppressLint("NewApi")
    //Places the pins from the arraylist in the map
    public void placePins(GoogleMap googleMap) {
        Log.d(TAG, "Numero: " + mapPinClasses.size());
        Log.d(TAG, "MapPin: " + mapPinClasses.toString());
        //TODO FIX THE INTERNET PROBLEM, IF THATS THE PROBLEM
        //TODO FIX THE ICON PROBLEM, FROM DRAWABLE VECTOR TO BITMAP
        //TODO ADD THE IFS WITH THE TYPE
        //TODO ADD THE DANGER ZONE
        //TODO ADD THE WINDOW ON CLICK
        mapPinClasses.forEach(mapPinClass -> {
            BitmapDescriptor icon = null;
            LatLng latLng = new LatLng(mapPinClass.getLocation().getLatitude(), mapPinClass.getLocation().getLongitude());
            if (mapPinClass.getType().trim().equals("war")) {
                icon = bitmapDescriptorFromVector(getActivity(), R.drawable.ic_map_war_pin_45dp);
            }
            if (mapPinClass.getType().trim().equals("hospital")) {
                icon = bitmapDescriptorFromVector(getActivity(), R.drawable.ic_map_hospital_pin_45dp);
            }
            MarkerOptions options = new MarkerOptions().position(latLng).title(mapPinClass.getType()).icon(icon);
            googleMap.addMarker(options);
        });
    }

    @SuppressLint("NewApi")
    public void placeZoneMarker(GoogleMap googleMap) {
        mapZoneClasses.forEach(mapZone -> {
            PolygonOptions poly = new PolygonOptions().strokeColor(Color.RED).fillColor(0x3Fb0233d).clickable(true);
            for (int i = 0; i < mapZone.size(); i++) {
                GeoPoint polyGeo = (GeoPoint) mapZone.get(i);
                double lat = polyGeo.getLatitude();
                double lng = polyGeo.getLongitude();
                LatLng latLng = new LatLng(lat, lng);
                poly.add(latLng);
            }
            googleMap.addPolygon(poly);
        });
    }

    @SuppressLint("NewApi")
    public void loadFilteredPins(){
        FilterMapFragment.filteredMapPinClasses.forEach(mapPin -> {
            BitmapDescriptor icon = null;
            LatLng latLng = new LatLng(mapPin.getLocation().getLatitude(), mapPin.getLocation().getLongitude());
            if (mapPin.getType().trim().equals("war")) {
                icon = bitmapDescriptorFromVector(getActivity(), R.drawable.ic_map_war_pin_45dp);
            }
            if (mapPin.getType().trim().equals("hospital")) {
                icon = bitmapDescriptorFromVector(getActivity(), R.drawable.ic_map_hospital_pin_45dp);
            }
            MarkerOptions options = new MarkerOptions().position(latLng).title(mapPin.getType()).snippet(mapPin.getPinID()).icon(icon);
            gMap.addMarker(options);
        });
    }

    @SuppressLint("NewApi")
    public void refreshMap(){
        gMap.clear();
        placeZoneMarker(gMap);
        loadFilteredPins();
    }
}