package cm.protectu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.Community.CommunityFragment;
import cm.protectu.Language.LanguageFragment;
import cm.protectu.Map.MapFragment;
import cm.protectu.News.NewsFragment;
import cm.protectu.Panic.PanicFragment;
import cm.protectu.Profile.ProfileFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    //Firebase Authentication
    private FirebaseAuth mAuth;

    //Bottom navigation bar
    BottomNavigationView bottomBar;
    NavigationView sideBar;
    private MenuItem item;

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
        bottomBar = findViewById(R.id.bottom_menu);
        sideBar = findViewById(R.id.side_menu);

        /*
        //If the user is anonymous, removes the profile option
        if(mAuth.getCurrentUser().isAnonymous()){
            navigation.getMenu().removeItem(R.id.navigation_profile);
        }
        */
        bottomBar.setOnNavigationItemSelectedListener(this);
        sideBar.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                //Switch case to load the fragment based on the side menu option
                switch (item.getItemId()) {
                    case R.id.navigation_about:
                        //fragment = new AboutFragment();
                        Toast.makeText(MainActivity.this, "About", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.navigation_language:
                        fragment = new LanguageFragment();
                        break;

                    case R.id.navigation_logout:
                        logout();
                        break;
                }

                //loads the fragment
                if (fragment == null)
                    return false;
                return loadFragment(fragment);
            }
        });

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
                    fragment = new ProfileFragment();
                }

                break;

            case R.id.navigation_news:
                fragment = new NewsFragment();
                break;

            case R.id.navigation_community:
                fragment = new CommunityFragment();
                break;

            case R.id.navigation_panic:
                //Toast.makeText(this, "Change to the panic fragment", Toast.LENGTH_SHORT).show();
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

}


