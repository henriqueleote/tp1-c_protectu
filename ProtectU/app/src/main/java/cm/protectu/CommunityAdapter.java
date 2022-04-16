package cm.protectu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.MyViewHolder>{

    Context context;
    ArrayList<CommunityCard> listOfCommunityCards;


    public CommunityAdapter(Context ct, ArrayList<CommunityCard> l){
        context = ct;
        listOfCommunityCards = l;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview_community, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //holder.userName.setText(listOfCommunityCards.get(position).getUserID());
        holder.text.setText(listOfCommunityCards.get(position).getMessageText());
        holder.likeButton.setText(listOfCommunityCards.get(position).getLikes()+"");
        holder.dislikeButton.setText(listOfCommunityCards.get(position).getDislikes()+"");

    }

    @Override
    public int getItemCount() {
        return listOfCommunityCards.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView userName, text, likeButton,dislikeButton;
        ImageView userImage;
        CardView cardCommunity;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            text = itemView.findViewById(R.id.communityText);
            userImage = itemView.findViewById(R.id.userImage);
            likeButton = itemView.findViewById(R.id.numberOfLikes);
            dislikeButton = itemView.findViewById(R.id.numberOfDislikes);
            cardCommunity = itemView.findViewById(R.id.cardCommunity);

        }
    }
}
