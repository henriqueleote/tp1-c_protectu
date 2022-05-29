package cm.protectu.Language;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Locale;

import cm.protectu.Map.MapFragment;
import cm.protectu.PrefManager;
import cm.protectu.Profile.ProfileFragment;
import cm.protectu.R;
import cm.protectu.WelcomeScreenActivity;

public class LanguageFragment extends Fragment {

    private CheckBox portugueseCheck, englishCheck, frenchCheck, germanCheck, ukrainianCheck;
    private Button btnSave;
    private ImageView btnBack;
    private PrefManager prefManager;
    String language;
    View view;

    private static final String TAG = WelcomeScreenActivity.class.getName();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_language, container, false);

        //Get elements from view
        portugueseCheck = view.findViewById(R.id.portugueseRadio);
        englishCheck = view.findViewById(R.id.englishRadio);
        frenchCheck = view.findViewById(R.id.frenchRadio);
        germanCheck = view.findViewById(R.id.germanRadio);
        ukrainianCheck = view.findViewById(R.id.ukrainianRadio);


        btnSave = view.findViewById(R.id.btnSave);
        btnBack = view.findViewById(R.id.backID);

        prefManager = new PrefManager(getActivity());

        getChecked();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.question_app_will_restart)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                prefManager.setLanguage(language);
                                setLocale(prefManager.getLanguage());
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

        //Depending on the selected checkbox, it adds to the array list, or removes if it was an uncheck
        portugueseCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    englishCheck.setChecked(false);
                    frenchCheck.setChecked(false);
                    germanCheck.setChecked(false);
                    ukrainianCheck.setChecked(false);
                    language = "pt";
                }
            }
        });

        //Depending on the selected checkbox, it adds to the array list, or removes if it was an uncheck
        englishCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    portugueseCheck.setChecked(false);
                    frenchCheck.setChecked(false);
                    germanCheck.setChecked(false);
                    ukrainianCheck.setChecked(false);
                    language = "us";
                }
            }
        });

        //Depending on the selected checkbox, it adds to the array list, or removes if it was an uncheck
        frenchCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    portugueseCheck.setChecked(false);
                    englishCheck.setChecked(false);
                    germanCheck.setChecked(false);
                    ukrainianCheck.setChecked(false);
                    language = "fr";
                }
            }
        });

        //Depending on the selected checkbox, it adds to the array list, or removes if it was an uncheck
        germanCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    portugueseCheck.setChecked(false);
                    englishCheck.setChecked(false);
                    frenchCheck.setChecked(false);
                    ukrainianCheck.setChecked(false);
                    language = "de";
                }
            }
        });

        //Depending on the selected checkbox, it adds to the array list, or removes if it was an uncheck
        ukrainianCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    portugueseCheck.setChecked(false);
                    englishCheck.setChecked(false);
                    frenchCheck.setChecked(false);
                    germanCheck.setChecked(false);
                    language = "uk";
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new MapFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }


    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    public void getChecked(){
        Log.d(TAG, "Language: " + prefManager.getLanguage());
        if(prefManager.getLanguage().equalsIgnoreCase("pt"))
            portugueseCheck.setChecked(true);
        if(prefManager.getLanguage().equalsIgnoreCase("us"))
            englishCheck.setChecked(true);
        if(prefManager.getLanguage().equalsIgnoreCase("fr"))
            frenchCheck.setChecked(true);
        if(prefManager.getLanguage().equalsIgnoreCase("de"))
            germanCheck.setChecked(true);
        if(prefManager.getLanguage().equalsIgnoreCase("uk"))
            ukrainianCheck.setChecked(true);

    }
}