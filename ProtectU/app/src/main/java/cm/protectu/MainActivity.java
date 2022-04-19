package cm.protectu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    //Firebase Authentication
    private FirebaseAuth mAuth;

    //Bottom navigation bar
    BottomNavigationView bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Link the layout to the activity
        setContentView(R.layout.activity_main);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();


        //Checks if there is a session, if not, redirects to the Auth page
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(MainActivity.this, AuthActivity.class));
        }
        bottomBar = findViewById(R.id.nav_view);

        /*
        //If the user is anonymous, removes the profile option
        if(mAuth.getCurrentUser().isAnonymous()){
            navigation.getMenu().removeItem(R.id.navigation_profile);
        }
        */
        bottomBar.setOnNavigationItemSelectedListener(this);

        //Loads the Profile fragment as default when onStart
        loadFragment(new MapFragment());

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
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;

        //Switch case to load the fragment based on the bottom navbar option
        switch (item.getItemId()) {
            case R.id.navigation_map:
                fragment = new MapFragment();
                break;

            case R.id.navigation_profile:
                fragment = new ProfileFragment();
                break;

            case R.id.navigation_news:
                fragment = new NewsFragment();
                break;

            case R.id.navigation_community:
                fragment = new CommunityFragment();
                break;

            case R.id.navigation_panic:
                fragment = new MissingBoardFragment();
                break;
        }

        //loads the fragment
        return loadFragment(fragment);
    }

}


