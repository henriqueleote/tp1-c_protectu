package cm.protectu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Locale;

import cm.protectu.About.AboutFragment;
import cm.protectu.Alarm.AlarmClass;
import cm.protectu.Alarm.AlarmFragment;
import cm.protectu.Authentication.AuthActivity;
import cm.protectu.Community.ViewPagerFragment;
import cm.protectu.Customization.CustomizationFragment;
import cm.protectu.Customization.CustomizationManager;
import cm.protectu.Language.LanguageFragment;
import cm.protectu.Language.LanguageManagerClass;
import cm.protectu.Map.MapFragment;
import cm.protectu.News.NewsFragment;
import cm.protectu.Panic.PanicFragment;
import cm.protectu.Profile.ProfileFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    //Firebase Authentication
    private FirebaseAuth mAuth;

    //Firebase Firestore
    private FirebaseFirestore firebaseFirestore;

    //Bottom navigation bar
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomBar;
    private NavigationView sideBar;

    //for brightness sensor
    private float floatThreshold = 1;
    private SensorManager sensorManager;
    private Sensor sensorLight;

    public static UserDataClass sessionUser;

    private static final String TAG = MainActivity.class.getName();

    private static Context context;

    public static ViewPagerFragment viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Read and Load Themes
        CustomizationManager.getInstance(this);
//        if (AppCompatDelegate.getDefaultNightMode()== AppCompatDelegate.MODE_NIGHT_YES){
//            setTheme(R.style.Theme_Dark);
//        }else
//            setTheme(R.style.Theme_Light);

        //brightness sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        SensorEventListener sensorEventListenerLight = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float floatSensorValue = event.values[0]; // brightness

                if (floatSensorValue < floatThreshold){
                    //g.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_dark_map));
                }
                else {
                    //This code is when the room is light
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        sensorManager.registerListener(sensorEventListenerLight, sensorLight, SensorManager.SENSOR_DELAY_NORMAL);

        super.onCreate(savedInstanceState);

        MainActivity.context = getApplicationContext();


        String languageToLoad = LanguageManagerClass.getInstance().readLocale(getResources());
        Locale locale;
        locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        this.getBaseContext().getResources().updateConfiguration(config,
                this.getBaseContext().getResources().getDisplayMetrics());

        //Link the layout to the activity
        setContentView(R.layout.activity_main);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();

        //Checks if there is a session, if not, redirects to the Auth page
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(MainActivity.this, AuthActivity.class));
        }
        bottomBar = findViewById(R.id.bottom_menu);
        sideBar = findViewById(R.id.side_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        getUserData(1);

        firebaseFirestore.collection("users").document(mAuth.getCurrentUser().getUid().toString()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    sessionUser = snapshot.toObject(UserDataClass.class);
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
        firebaseFirestore.collection("air-alarm")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        Query q = firebaseFirestore.collection("air-alarm").limit(1);
                        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                    AlarmClass alarmClass = document.toObject(AlarmClass.class);
                                    AlarmFragment g = new AlarmFragment(MainActivity.this, alarmClass);
                                    //g.show();
                                } else {
                                    Log.d(TAG, "Error");
                                }
                            }
                        });
                    }
                });

        bottomBar.setOnNavigationItemSelectedListener(this);
        sideBar.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                //Switch case to load the fragment based on the side menu option
                switch (item.getItemId()) {
                    case R.id.navigation_about:
                        fragment = new AboutFragment();
                        break;

                    case R.id.navigation_language:
                        fragment = new LanguageFragment();
                        break;

                    case R.id.navigation_customize:
                        fragment = new CustomizationFragment();
                        break;

                    case R.id.navigation_logout:
                        logout();
                        break;
                }

                //loads the fragment
                if (fragment == null)
                    return false;
                drawerLayout.closeDrawers();
                return loadFragment(fragment);
            }
        });
    }

    //Method to load Fragments
    public boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }
        return false;
    }

    //Method
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;

        //TODO CHECK WHY THE MAP SHOWS IN THE NEWS WHEN IN LAUNCH
        //Switch case to load the fragment based on the bottom navbar option
        switch (item.getItemId()) {
            case R.id.navigation_map:
                fragment = new MapFragment();
                break;

            case R.id.navigation_profile:
                if(mAuth.getCurrentUser().isAnonymous()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.want_to_go_to_login)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    mAuth.signOut();
                                    startActivity(new Intent(MainActivity.this, AuthActivity.class));
                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    // Create the AlertDialog object and return it
                    builder.show();
                }else{
                    getUserData(-1);
                    fragment = new ProfileFragment();
                }

                break;

            case R.id.navigation_news:
                fragment = new NewsFragment();
                break;

            case R.id.navigation_community:
                fragment = new ViewPagerFragment();
                viewPager = (ViewPagerFragment)fragment;
                break;

            case R.id.navigation_panic:
                PanicFragment bottomPanic = new PanicFragment();
                bottomPanic.show(getSupportFragmentManager(), bottomPanic.getTag());
                break;
        }

        //loads the fragment
        return loadFragment(fragment);
    }

    public void logout() {
        mAuth.signOut();
        Toast.makeText(MainActivity.this, getString(R.string.see_you_next_time), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, AuthActivity.class));
    }

    public void getUserData(int code){
        //Firebase Authentication function get the data from firebase with certain criteria
        firebaseFirestore.collection("users")
                //where the userID is the same as the logged in user
                .whereEqualTo("uid", mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                sessionUser = document.toObject(UserDataClass.class);
                            }
                            if(code == 1)
                                loadFragment(new MapFragment());
                        } else {
                            //TODO Maybe reload the page or kill the session?
                            //Shows the error
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }

}


