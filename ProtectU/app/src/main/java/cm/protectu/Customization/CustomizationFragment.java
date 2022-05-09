package cm.protectu.Customization;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.Language.LanguageManagerClass;
import cm.protectu.MainActivity;
import cm.protectu.Map.MapFragment;
import cm.protectu.R;


public class CustomizationFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    //Firebase Authentication
    private FirebaseAuth mAuth;

    //Image witch we can go to the back fragment(Map Fragment)
    private ImageView arrowBack;

    private Spinner spinner;

    private CustomizationFragment thisFragment;

    private BottomNavigationView bottomBar;

    public CustomizationFragment(BottomNavigationView bottomBar) {
        this.bottomBar = bottomBar;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_costumization, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        arrowBack = view.findViewById(R.id.backID);


        spinner = view.findViewById(R.id.spinner_theme);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.theme_values, R.layout.color_spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        thisFragment = this;

//        whichSelected();

        //go back to the map frame
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapFragment fragment = new MapFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });


        //TODO Check the animation
        //Checks if there is a session, if not, redirects to the Auth page
        if (mAuth.getCurrentUser() == null) {
            getActivity().finish();
            //Swipe animation ?? not sure, consult previous code
            //getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            startActivity(new Intent(getActivity(), AuthActivity.class));
        }

        //Returns the view
        return view;

    }

    public void whichSelected() {
        String selectedTheme = CustomizationManager.getInstance(getActivity()).getSelectedTheme();

        switch (selectedTheme.toLowerCase(Locale.ROOT)) {
            case "dark":
                spinner.setSelection(2); //Position in String-array
                break;
            case "light":
                spinner.setSelection(1); //Position in String-array
                break;
            default:
                spinner.setSelection(0); //Position in String-array
                break;
        }
    }


    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if (position != 0) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setMessage(R.string.question_app_will_restart)
//                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
                            switch (position) {
                                case 1: //Light Mode
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                    getActivity().setTheme(R.style.Theme_Light);
                                    CustomizationManager.getInstance(getActivity()).saveTheme("light");
//                                    bottomBar.setBackgroundColor(R.color.backgroundColorSelected);
//                                    getActivity().recreate();
                                    break;
                                case 2: // Dark Mode
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                    getActivity().setTheme(R.style.Theme_Dark);
                                    CustomizationManager.getInstance(getActivity()).saveTheme("dark");

//                                    getActivity().recreate();
                                    break;
                                case 3: // Blue Mode
                                    break;
                                default: // System Default
//                getActivity().setTheme(R.style.Theme_Light);
                            }
//                            getActivity().recreate();
                            updateFragmentColor();
                        }
//                    })
//                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.dismiss();
//                            return;
//                        }
//                    });
            // Create the AlertDialog object and return it
//            builder.show();
//        }


    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @SuppressLint("ResourceAsColor")
    private void updateFragmentColor(){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new CustomizationFragment(bottomBar))
                .commit();
        System.out.println("Color: "+ R.attr.backgroundColorSelected);
        getActivity().findViewById(R.id.side_menu).setBackgroundColor(R.attr.backgroundColorSelected);
        bottomBar.setBackgroundColor(R.color.black);
    }
}
