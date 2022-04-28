package cm.protectu.Map.Buildings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.Community.CommunityAdapter;
import cm.protectu.Community.CommunityCard;
import cm.protectu.Community.NewMessageCommunityFragment;
import cm.protectu.Community.SortCommunityCardClass;
import cm.protectu.Map.MapFragment;
import cm.protectu.MissingBoard.MissingBoardFragment;
import cm.protectu.Profile.ProfileFragment;
import cm.protectu.R;


public class BuildingFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    BuildingClass buildingInfo;
    SliderView sliderView;
    ArrayList<String> images;
    SliderAdapter adapter;
    String buildingID;
    ImageView backButton;

    //TAG for debug logs
    private static final String TAG = AuthActivity.class.getName();

    public BuildingFragment(String buildingID){
        this.buildingID = buildingID;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_building, container, false);

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

        sliderView = view.findViewById(R.id.autoImageSlider);
        backButton = view.findViewById(R.id.backButton);

        Log.d(TAG, "Slider info: " + sliderView.toString());

        getData();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapFragment fragment = new MapFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        //Returns the view
        return view;

    }

    public void getData(){
        ProgressDialog mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage("Loading data...");
        mDialog.setCancelable(false);
        mDialog.show();
        firebaseFirestore.collection("map-buildings").document(buildingID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    images = (ArrayList<String>) document.get("images");
                    buildingInfo = new BuildingClass(document.get("buildingID").toString(), document.get("buildingName").toString(), document.get("type").toString(), images);
                    putData();
                    mDialog.dismiss();
                }
            }
        });
    }

    public void putData(){
        adapter = new SliderAdapter(getActivity(), images);
        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        sliderView.setSliderAdapter(adapter);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();
    }
}

