package cm.protectu.Map.Buildings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.R;

public class MapBuildingFragment extends BottomSheetDialogFragment {

    //ImageView
    private ImageView closeBtn, buildingImage;

    //TextView
    private TextView buildingNameTextView;

    //Button
    private Button seeMoreButton;

    String buildingName;
    ArrayList<String> images;

    //Firebase Authentication
    FirebaseAuth mAuth;

    //TAG for debug logs
    private static final String TAG = AuthActivity.class.getName();

    public MapBuildingFragment(String buildingName, ArrayList<String> images){
        this.buildingName = buildingName;
        this.images = images;
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
