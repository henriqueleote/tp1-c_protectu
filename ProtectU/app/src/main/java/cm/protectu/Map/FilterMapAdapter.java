package cm.protectu.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;

import cm.protectu.Map.Buildings.MapPinTypeClass;
import cm.protectu.R;

public class FilterMapAdapter extends ArrayAdapter<MapPinTypeClass> {

    public static ArrayList<String> checkedPositions;
    private TextView checkBoxName;
    private ImageView checkBoxIcon;
    private CheckBox checkBoxCheck;

    public FilterMapAdapter(Context context, ArrayList<MapPinTypeClass> mapPinTypes) {
        super(context, 0, mapPinTypes);
        checkedPositions = new ArrayList<>();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.checkbox_list_view, parent, false);
        }

        // Get the data item for this position
        MapPinTypeClass mapPinTypeClass = getItem(position);

        // Lookup view for data population
        checkBoxName = (TextView) view.findViewById(R.id.checkBoxName);
        checkBoxIcon = (ImageView) view.findViewById(R.id.checkBoxIcon);
        checkBoxCheck = (CheckBox) view.findViewById(R.id.checkBoxCheck);

        // Populate the data into the template view using the data object
        checkBoxName.setText(mapPinTypeClass.getName());
        checkBoxIcon.setImageDrawable(getContext().getDrawable(getContext().getResources().getIdentifier(mapPinTypeClass.getLogo(), "drawable", getContext().getPackageName())));

        //TODO fix this
        if (checkedPositions.contains(mapPinTypeClass.getType()))
            checkBoxCheck.setChecked(true);

        checkBoxCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    checkedPositions.add(mapPinTypeClass.getType());
                else
                    checkedPositions.remove(mapPinTypeClass.getType());
            }
        });

        // Return the completed view to render on screen
        return view;
    }
}