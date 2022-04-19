package cm.protectu;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder>{
    Context context;
    ArrayList<NewsCard> listOfNewsCards;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    FragmentManager parentFragment;

    private static final String TAG = MainActivity.class.getName();

    public NewsAdapter(Context context, ArrayList<NewsCard> listOfNewsCards, FirebaseAuth firebaseAuth, FragmentManager fm) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        this.context = context;
        this.listOfNewsCards = listOfNewsCards;
        mAuth = firebaseAuth;
        parentFragment = fm;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview_news, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.newsText.setText(listOfNewsCards.get(position).getNewsText() + "");
        holder.newsTitle.setText(listOfNewsCards.get(position).getNewsTitle() + "");

        holder.cardNews.setOnClickListener(new  View.OnClickListener() {
            public void onClick(View v) {
                NewsDetailsFragment fragment = new NewsDetailsFragment(listOfNewsCards.get(holder.getAdapterPosition()));
                FragmentTransaction transaction = parentFragment.beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

    }

    @Override
    public int getItemCount () {
        return listOfNewsCards.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView newsTitle, newsText;
        ImageView newsImage, newsPublisherImage;
        CardView cardNews;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            newsImage = itemView.findViewById(R.id.newsImage);
            newsText = itemView.findViewById(R.id.newsText);
            newsTitle = itemView.findViewById(R.id.newsTitle);
            newsPublisherImage = itemView.findViewById(R.id.newsPublisherImage);
            cardNews = itemView.findViewById(R.id.cardNews);
        }
    }
}
