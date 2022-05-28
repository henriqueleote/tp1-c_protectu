package cm.protectu.Customization;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.Map.MapFragment;
import cm.protectu.R;


public class CustomizationFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    //Firebase Authentication
    private FirebaseAuth mAuth;

    //Image witch we can go to the back fragment(Map Fragment)
    private ImageView arrowBack;

    private Spinner spinner;
    private Button btnSave;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_customization, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        arrowBack = view.findViewById(R.id.backID);
        btnSave = view.findViewById(R.id.btnSave);


        spinner = view.findViewById(R.id.spinner_theme);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.theme_values, R.layout.color_spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.question_app_will_restart)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getActivity().recreate();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                // Create the AlertDialog object and return it
                builder.show();
            }
        });


        //Checks if there is a session, if not, redirects to the Auth page
        if (mAuth.getCurrentUser() == null) {
            getActivity().finish();
            startActivity(new Intent(getActivity(), AuthActivity.class));
        }

        //Returns the view
        return view;

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (position) {
            case 1: //Light Mode
                CustomizationManager.getInstance().saveTheme("light");
                break;
            case 2: // Dark Mode
                CustomizationManager.getInstance().saveTheme("dark");
                break;
            default: // System Default
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
