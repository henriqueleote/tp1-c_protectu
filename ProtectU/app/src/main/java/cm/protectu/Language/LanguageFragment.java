package cm.protectu.Language;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cm.protectu.R;

public class LanguageFragment extends Fragment {

    private TextView auxChosen;
    private List<TextView> tableOptions;
    private Button btnSave;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        tableOptions = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_language, container, false);

        //Get elements from view
        tableOptions.add(view.findViewById(R.id.txtEnglish));
        tableOptions.add(view.findViewById(R.id.txtPortuguese));
        tableOptions.add(view.findViewById(R.id.txtFrench));
        tableOptions.add(view.findViewById(R.id.txtGerman));
        tableOptions.add(view.findViewById(R.id.txtUkrainian));
        btnSave = view.findViewById(R.id.btnSave);

        //Check which language are selected on SO
        whichSelected();

        //For each option program onClick change value of auxChosen
        for (TextView row : tableOptions) {
            row.setOnClickListener(view1 -> chooseOption(row));
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.question_app_will_restart)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String code = setLocateLanguage();
                                LanguageManagerClass.getInstance().updateResource(getResources(),code);
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

        return view;
    }

    private void chooseOption(TextView row) {
        clearBackground();

        // Get Color from Attr file
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(R.attr.backgroundColorSelected, typedValue, true);
        @ColorInt int color = typedValue.data;

        row.setBackgroundColor(color);
        auxChosen = row;
    }

    private void clearBackground() {
        // Get Color from Attr file
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(R.attr.backgroundColor, typedValue, true);
        @ColorInt int color = typedValue.data;

        for (TextView tableOption : tableOptions) {
            tableOption.setBackgroundColor(color);
        }
    }

    private void whichSelected() {
        String language = Locale.getDefault().getDisplayLanguage();
        for (TextView label : tableOptions) {
            if (label.getText().toString().equalsIgnoreCase(language)) {
                chooseOption(label);
                return;
            }
        }
        // Can be position 0 because is the english language
        chooseOption(tableOptions.get(0));
    }

    private String setLocateLanguage() {
        switch (auxChosen.getText().toString().toLowerCase()) {
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