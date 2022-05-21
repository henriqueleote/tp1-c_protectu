package cm.protectu.Buildings.Bunker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cm.protectu.Map.MapPinTypeClass;
import cm.protectu.R;

public class BunkerExtrasAdapter
        extends RecyclerView.Adapter<BunkerExtrasAdapter.MyView> {

    // List with String type
    private List<MapPinTypeClass> buildingExtrasList;
    private Context context;

    public class MyView extends RecyclerView.ViewHolder {

        // Text View
        TextView buildingExtrasTextView;
        ImageView buildingExtrasImageView;

        public MyView(View view){
            super(view);

            buildingExtrasTextView = view.findViewById(R.id.buildingExtrasTextView);
            buildingExtrasImageView = view.findViewById(R.id.buildingExtrasImageView);
        }
    }

    // Constructor for adapter class
    // which takes a list of String type
    public BunkerExtrasAdapter(Context context, List<MapPinTypeClass> buildingExtrasList)
    {
        this.context = context;
        this.buildingExtrasList = buildingExtrasList;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent,int viewType){

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.building_extras_list_view,parent,false);

        // return itemView
        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder,final int position){

        // Set the text of each item of
        // Recycler view with the list items
        holder.buildingExtrasTextView.setText(buildingExtrasList.get(position).getName());
        holder.buildingExtrasImageView.setImageDrawable(context.getDrawable(context.getResources().getIdentifier(buildingExtrasList.get(position).getLogo(), "drawable", context.getPackageName())));
    }

    @Override
    public int getItemCount()
    {
        return buildingExtrasList.size();
    }
}