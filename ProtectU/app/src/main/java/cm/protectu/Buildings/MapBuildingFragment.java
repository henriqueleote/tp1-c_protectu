package cm.protectu.Buildings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.Buildings.Bunker.BunkerFragment;
import cm.protectu.Buildings.Earthquake.EarthquakeFragment;
import cm.protectu.Buildings.Fire.FireFragment;
import cm.protectu.Buildings.Hospital.HospitalFragment;
import cm.protectu.Buildings.War.WarFragment;
import cm.protectu.R;

public class MapBuildingFragment extends BottomSheetDialogFragment {

    //ImageView
    private ImageView closeBtn, buildingImage;

    //TextView
    private TextView buildingNameTextView;

    //Button
    private Button seeMoreButton;

    String buildingID, buildingName;
    ArrayList<String> images;

    //Firebase Authentication
    FirebaseAuth mAuth;

    String type;

    //TAG for debug logs
    private static final String TAG = AuthActivity.class.getName();

    public MapBuildingFragment(String buildingID, String buildingName, ArrayList<String> images, String type){
        this.buildingID = buildingID;
        this.buildingName = buildingName;
        this.images = images;
        this.type = type;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.map_building_bottom, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        //Link the view objects with the XML
        closeBtn = view.findViewById(R.id.close);
        buildingImage = view.findViewById(R.id.buildingImage);
        buildingNameTextView = view.findViewById(R.id.buildingName);
        seeMoreButton = view.findViewById(R.id.seeMoreButton);


        //On click closes the form sheet
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });


        //On click goes to the detailed page
        seeMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().cancel();
                Fragment fragment = null;
                if(type.equalsIgnoreCase("bunker"))
                    fragment = new BunkerFragment(buildingID);
                if(type.equalsIgnoreCase("earthquake"))
                    fragment = new EarthquakeFragment(buildingID);
                if(type.equalsIgnoreCase("fire"))
                    fragment = new FireFragment(buildingID);
                if(type.equalsIgnoreCase("hospital"))
                    fragment = new HospitalFragment(buildingID);
                if(type.equalsIgnoreCase("war"))
                    fragment = new WarFragment(buildingID);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        putData();

        //Returns the view
        return view;
    }

    public void putData(){
        buildingNameTextView.setText(buildingName);
        Picasso.get().load(images.get(0)).into(buildingImage);
    }

}
