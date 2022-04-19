package cm.protectu;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class MissingBoardAdapter extends RecyclerView.Adapter<MissingBoardAdapter.MyViewHolder> {

    //TODO - Change variables names and comment

    private Context mContext;
    private List<MissingCard> mData;

    public MissingBoardAdapter(Context mContext, List<MissingCard> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_missing,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.description.setText(mData.get(position).getDescription());
        holder.userName.setText(mData.get(position).getProfileName());
        holder.missingName.setText(mData.get(position).getMissingName());
        holder.age.setText(String.valueOf(mData.get(position).getMissingAge()));
        //falta buscar imagens

        //set click listener
        /*
        holder.cardMissing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(mContext,CardActivity.class);
                //intent.putExtra("ProfileName",mData.get(position).getProfileName());
                //mContext.startActivity(intent);
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView description, userName, missingName, age;
        ImageView userImage, missingImage;
        CardView cardMissing;


        //View Holder: View Holder Class is the java class that stores the reference to the UI Elements in the Card Layout and they can be modified dynamically during the execution of the program by the list of data.
        public MyViewHolder(View itemView){
            super(itemView);
            description = itemView.findViewById(R.id.description_id);
            userName = itemView.findViewById(R.id.profile_name_id);
            missingName = itemView.findViewById(R.id.name_of_the_missing_id);
            age = itemView.findViewById(R.id.age_id);
            userImage = itemView.findViewById(R.id.image_profile_id);
            missingImage = itemView.findViewById(R.id.image_missing_id);
            cardMissing = itemView.findViewById(R.id.cardMissing_id);

        }

    }
}
