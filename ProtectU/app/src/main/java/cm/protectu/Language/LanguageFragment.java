package cm.protectu.Language;

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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

import cm.protectu.PrefManager;
import cm.protectu.R;
import cm.protectu.WelcomeScreenActivity;

public class LanguageFragment extends Fragment {

    private CheckBox portugueseCheck, englishCheck;
    private Button btnSave;
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
        btnSave = view.findViewById(R.id.btnSave);

        prefManager = new PrefManager(getActivity());

        getChecked();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefManager.setLanguage(language);
                setLocale(prefManager.getLanguage());
            }
        });

        //Depending on the selected checkbox, it adds to the array list, or removes if it was an uncheck
        portugueseCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    englishCheck.setChecked(false);
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
                    language = "us";
                }

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
        getActivity().recreate();
    }

    public void getChecked(){
        Log.d(TAG, "Language: " + prefManager.getLanguage());
        if(prefManager.getLanguage().equalsIgnoreCase("pt"))
            portugueseCheck.setChecked(true);
        if(prefManager.getLanguage().equalsIgnoreCase("us"))
            englishCheck.setChecked(true);
    }
}