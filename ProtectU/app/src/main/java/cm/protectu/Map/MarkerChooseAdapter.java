package cm.protectu.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import cm.protectu.R;

public class MarkerChooseAdapter extends ArrayAdapter<MapPinTypeClass> {

    public static ArrayList<String> checkedButton;
    private RadioButton radioButton;
    private ImageView radioIcon;
    private TextView radioName;
    public int mSelectedItem = -1;

    public MarkerChooseAdapter(Context context, ArrayList<MapPinTypeClass> mapPinTypes) {
        super(context, 0, mapPinTypes);
        checkedButton = new ArrayList<>();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.button_list_view, parent, false);
        }

        // Get the data item for this position
        MapPinTypeClass mapPinTypeClass = getItem(position);

        // Lookup view for data population
        radioButton = view.findViewById(R.id.radioButtonRadio);
        radioName = view.findViewById(R.id.radioButtonName);
        radioIcon = view.findViewById(R.id.radioButtonIcon);

        radioIcon.setImageDrawable(getContext().getDrawable(getContext().getResources().getIdentifier(mapPinTypeClass.getLogo(), "drawable", getContext().getPackageName())));
        radioName.setText(mapPinTypeClass.getName());

        //https://stackoverflow.com/questions/16092991/only-select-only-one-radio-button-from-listview

        radioButton.setChecked(position == mSelectedItem);
        radioButton.setTag(position);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MainActivity","Position: + " + position + "Valor: " + mapPinTypeClass.getType());
                mSelectedItem = (Integer)view.getTag();
                checkedButton.clear();
                checkedButton.add(mapPinTypeClass.getType());
                notifyDataSetChanged();
            }
        });

        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mSelectedItem = position;
                notifyDataSetChanged();
                Log.d("MainActivity","Position: + " + position + "Valor: " + mapPinTypeClass.getType());
                checkedButton.clear();
                checkedButton.add(mapPinTypeClass.getType());
            }
        });

        /*radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = v.getId();
                //finish this
                checkedButton.clear();
                v.setBackgroundColor(Color.RED);
                radioButton.setBackgroundColor(Color.RED);
                checkedButton.add(mapPinTypeClass.getType());
            }
        });*/

        // Return the completed view to render on screen
        return view;
    }
}