package cm.protectu.Panic;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.R;


public class PanicFragment extends BottomSheetDialogFragment{

    private EditText numberOfPeople;

    private Spinner urgencyLevel;

    private String selectedUrgencyLevel;

    private Button sosButton;

    private ImageView closeButton;

    private GeoPoint curLocation;

    //Firebase Authentication
    private FirebaseAuth mAuth;

    private FirebaseFirestore firebaseFirestore;

    //TAG for debug logs
    private static final String TAG = AuthActivity.class.getName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_panic, container, false);


        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        closeButton = view.findViewById(R.id.closePanic);
        numberOfPeople = view.findViewById(R.id.numberOfPeople);
        urgencyLevel = view.findViewById(R.id.urgencyLevel);
        sosButton = view.findViewById(R.id.sosButton);
        firebaseFirestore = FirebaseFirestore.getInstance();


        //On click closes the form sheet
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });

        urgencyLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                selectedUrgencyLevel = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                selectedUrgencyLevel = "Slight";
            }
        });

        Spinner spinner = view.findViewById(R.id.urgencyLevel);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.panic_values, R.layout.color_spinner_panic_layout);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(urgencyLevel.getOnItemSelectedListener());


        sosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curLocation = new GeoPoint(cm.protectu.Map.MapFragment.currentLocation.getLatitude(),cm.protectu.Map.MapFragment.currentLocation.getLongitude());
                createRequest(numberOfPeople.getText().toString().trim()
                        ,mAuth.getUid());
                getDialog().cancel();
            }
        });

        //Returns the view
        return view;

    }
    public void createRequest(String numOfPeople,String userID){
        if (TextUtils.isEmpty(numOfPeople)) {
            numberOfPeople.setError(getString(R.string.error_number_of_people));
            numberOfPeople.requestFocus();
            return;
        }
        firebaseFirestore.collection("sos-requests")
                .add(new PanicRequestClass(userID,"",Integer.parseInt(numOfPeople),selectedUrgencyLevel, new Date(), curLocation))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        firebaseFirestore.collection("sos-requests").document(documentReference.getId())
                                .update("requestID",documentReference.getId());
                        Log.d(TAG, "Document successfully created!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error creating document", e);
                    }
                });
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

}
