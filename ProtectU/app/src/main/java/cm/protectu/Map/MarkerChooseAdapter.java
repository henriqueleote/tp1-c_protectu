package cm.protectu.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import cm.protectu.Map.Buildings.MapPinTypeClass;
import cm.protectu.R;

public class MarkerChooseAdapter extends ArrayAdapter<MapPinTypeClass> {

    public static ArrayList<String> checkedButton;
    private Button selectBtn;

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
        selectBtn = (Button) view.findViewById(R.id.selectBtn);

        selectBtn.setText(mapPinTypeClass.getName());

        //https://stackoverflow.com/questions/16092991/only-select-only-one-radio-button-from-listview

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = v.getId();
                //finish this
                checkedButton.clear();
                v.setBackgroundColor(Color.RED);
                selectBtn.setBackgroundColor(Color.RED);
                checkedButton.add(mapPinTypeClass.getType());
            }
        });

        // Return the completed view to render on screen
        return view;
    }
}