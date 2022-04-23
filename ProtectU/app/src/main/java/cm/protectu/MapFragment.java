package cm.protectu;

import android.Manifest;
import android.annotation.SuppressLint;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
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


public class MapFragment extends Fragment {

    //Firebase Authentication
    private FirebaseAuth mAuth;

    //Firebase Firestore
    private FirebaseFirestore firebaseFirestore;

    //FusedLocationProviderClient
    private FusedLocationProviderClient client;

    //Map Fragment
    private SupportMapFragment supportMapFragment;

    //Floating Action Button
    private FloatingActionButton resetLocation;

    //List with the pins of the map
    private ArrayList<MapPin> mapPins;

    //List with the zones of the map
    private ArrayList<MapZone> mapZones;

    //TAG for debug logs
    private static final String TAG =  AuthActivity.class.getName();

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
        resetLocation = view.findViewById(R.id.resetLocationBtn);

        mapPins = new ArrayList<>();
        mapZones = new ArrayList<>();

        //mapPins.add(new MapPin("idsodka", new GeoPoint(37.47370592890489,-122.13161490149692),"war"));
        //mapPins.add(new MapPin("hdklsad", new GeoPoint(37.53871235343273, -122.05999266014223), "hospital"));
        //mapZones.add(new MapZone("jdosad2", new GeoPoint(37.35562280859825, -122.03126224500416),new GeoPoint(37.322159536785755, -122.06340285010553),new GeoPoint(37.30085696557495, -122.09707396021173),new GeoPoint(37.30937871839392, -122.16441618042415)));

        getPinsFromDatabase();
        getZoneFromDatabase();

        //On click resets the location and goes back showing where the user is in the map
        resetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @SuppressLint({"NewApi", "MissingPermission"})
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            //TODO CHECK IF IT ROTATES WHEN ON MOBILE
                            googleMap.setMyLocationEnabled(true);
                            placeZoneMarker(googleMap);
                            //TODO - FIX THIS
                            //places the pins from the database
                            placePins(googleMap);
                            //TODO It would be nice instead of a marker, put those blue dots from google
                            // and apple maps with the compass sensor telling where it's turned to
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                            }
                    });
                }
            }
        });
    }

    //TODO - COMMENT
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults){
        if(requestCode == 44){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }
    }

    //TODO - Add the filter with the method where equals before the get, would be nice if it was possible to do without duplicate code.
    //TODO CHECK IF IT WORKS PROPERLY
    //Gets the map's pins from firebase to an arraylist
    public void getPinsFromDatabase(){
        firebaseFirestore.collection("map-pins")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "\nPin Object Data (Database) => " + document.getData() + "\n");
                                GeoPoint location = document.getGeoPoint("location");
                                MapPin pin = new MapPin(document.getId(), location, document.get("type").toString());
                                Log.d(TAG, "\nPin Object Data (Object) => " + pin + "\n");
                                mapPins.add(pin);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    //TODO - SOMETIMES DOESN'T WORK, I THINK ITS BECAUSE OF THE INTERNET ACCESS, WE HAVE TO PUT THE LOAD AS AN ASYNC FUNCTION
    //Gets the map's zones from firebase to an arraylist
    public void getZoneFromDatabase(){
        firebaseFirestore.collection("map-zones")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "\nZone Object Data (Database) => " + document.getData() + "\n");

                                MapZone zone = new MapZone(document.getId(),
                                        new GeoPoint(document.getGeoPoint("top1").getLatitude(),document.getGeoPoint("top1").getLongitude()),
                                        new GeoPoint(document.getGeoPoint("top2").getLatitude(),document.getGeoPoint("top2").getLongitude()),
                                                new GeoPoint(document.getGeoPoint("bottom1").getLatitude(),document.getGeoPoint("bottom1").getLongitude()),
                                                        new GeoPoint(document.getGeoPoint("bottom2").getLatitude(),document.getGeoPoint("bottom2").getLongitude()));
                                Log.d(TAG, "\nZone Object Data (Object) => " + zone + "\n");
                                mapZones.add(zone);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
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

    @SuppressLint("NewApi")
    //Places the pins from the arraylist in the map
    public void placePins(GoogleMap googleMap){
        Log.d(TAG, "Numero: " + mapPins.size());
        Log.d(TAG, "MapPin: " + mapPins.toString());
        //TODO FIX THE INTERNET PROBLEM, IF THATS THE PROBLEM
        //TODO FIX THE ICON PROBLEM, FROM DRAWABLE VECTOR TO BITMAP
        //TODO ADD THE IFS WITH THE TYPE
        //TODO ADD THE DANGER ZONE
        //TODO ADD THE WINDOW ON CLICK
        mapPins.forEach(mapPin -> {
            BitmapDescriptor icon = null;
            LatLng latLng = new LatLng(mapPin.getLocation().getLatitude(),mapPin.getLocation().getLongitude());
            if(mapPin.getType().trim().equals("war")){
                Log.d(TAG, "oi 1");
                icon = bitmapDescriptorFromVector(getActivity(), R.drawable.ic_map_war_pin_45dp);
            }
            if(mapPin.getType().trim().equals("hospital")){
                Log.d(TAG, "oi 2");
                icon = bitmapDescriptorFromVector(getActivity(), R.drawable.ic_map_hospital_pin_45dp);
            }
            MarkerOptions options = new MarkerOptions().position(latLng).title(mapPin.getType()).icon(icon);
            googleMap.addMarker(options);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    //Places the pins from the arraylist in the map
    public void placeZoneMarker(GoogleMap googleMap){
        mapZones.forEach(mapZone -> {
            Log.d(TAG, "Latitude: " + mapZone.getTopOneLocation().getLatitude());
            PolygonOptions polygonOptions = new PolygonOptions().add(
                    new LatLng(mapZone.getTopOneLocation().getLatitude(), mapZone.getTopOneLocation().getLongitude()),
                    new LatLng(mapZone.getTopTwoLocation().getLatitude(), mapZone.getTopTwoLocation().getLongitude()),
                    new LatLng(mapZone.getBottomOneLocation().getLatitude(), mapZone.getBottomOneLocation().getLongitude()),
                    new LatLng(mapZone.getBottomTwoLocation().getLatitude(), mapZone.getBottomTwoLocation().getLongitude()))
                    .strokeColor(Color.RED)
                    .fillColor(0x3Fb0233d);
            polygonOptions.isClickable();
            googleMap.addPolygon(polygonOptions);
        });
    }

    //TODO - Finish this, i dont know if it works because it needs a bottom to see the onclick and the hard part is to refresh the map
    public void removePins(GoogleMap googleMap){
        mapPins.clear();
        placePins(googleMap);
    }
}
