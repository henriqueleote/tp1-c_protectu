package cm.protectu.Language;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cm.protectu.R;

public class LanguageFragment extends Fragment {

    private TableRow auxChosen;
    private List<TableRow> tableOptions;
    private Button btnSave;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        LanguageManagerClass lang = new LanguageManagerClass(this);
        tableOptions = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_language, container, false);

        //Get elements from view
        tableOptions.add(view.findViewById(R.id.tbOpEnglish));
        tableOptions.add(view.findViewById(R.id.tbOpPortuguese));
        tableOptions.add(view.findViewById(R.id.tbOpFrench));
        tableOptions.add(view.findViewById(R.id.tbOpGerman));
        tableOptions.add(view.findViewById(R.id.tbOpUkraine));
        btnSave = view.findViewById(R.id.btnSave);

        //Check which language are selected on SO
        WhichSelected();

        //For each option program onClick change value of auxChosen
        for (TableRow row : tableOptions) {
            row.setOnClickListener(view1 -> ChooseOption(row));
        }

        btnSave.setOnClickListener(view12 -> {
            String code = setLocateLanguage();
            lang.updateResorce(code);
            getActivity().recreate();
        });

        return view;
    }

    private void ChooseOption(TableRow row) {
        ClearBackground();
        row.setBackgroundColor(getResources().getColor(R.color.white));
        auxChosen = row;
    }

    private void ClearBackground() {
        for (TableRow tableOption : tableOptions) {
            tableOption.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
        }
    }

    private void WhichSelected() {
        String language = Locale.getDefault().getDisplayLanguage();
        for (TableRow tableOption : tableOptions) {
            TextView label = (TextView) tableOption.getChildAt(0);
            System.out.println(label.getText());
            if (label.getText().toString().equalsIgnoreCase(language)) {
                ChooseOption(tableOption);
                return;
            }
        }
        // Can be position 0 because is the english language
        ChooseOption(tableOptions.get(0));
    }

    private String setLocateLanguage() {
        switch (((TextView) auxChosen.getChildAt(0)).getText().toString().toLowerCase()) {
            case "português":
                return "pt";
            case "deutsch":
                return "de";
            case "français":
                return "fr";
            case "Українська":
                return "ua";
            default:
                return "en";
        }

    }
}
