package cm.protectu.News;

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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import cm.protectu.MainActivity;
import cm.protectu.R;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder>{
    Context context;
    ArrayList<NewsCardClass> listOfNewsCardClasses;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    FragmentManager parentFragment;
    String imgURL1, imgURL2;

    private static final String TAG = MainActivity.class.getName();

    public NewsAdapter(Context context, ArrayList<NewsCardClass> listOfNewsCardClasses, FirebaseAuth firebaseAuth, FragmentManager fm) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        this.context = context;
        this.listOfNewsCardClasses = listOfNewsCardClasses;
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
        if(listOfNewsCardClasses.get(position).getNewsText().length() >= 60){
            String substr = listOfNewsCardClasses.get(position).getNewsText().substring(0,57);
            holder.newsText.setText(substr + "...");
        }else {
            holder.newsText.setText(listOfNewsCardClasses.get(position).getNewsText() + "");
        }
        holder.newsTitle.setText(listOfNewsCardClasses.get(position).getNewsTitle() + "");
        imgURL1 = listOfNewsCardClasses.get(position).getImageURL();
        Log.d(TAG,"DATA ->" + listOfNewsCardClasses.get(position).getImageURL());
        if(!imgURL1.equals("")){
            Glide.with(context)
                    .load(imgURL1)
                    .into(holder.newsImage);
        }else{
            holder.newsImage.setVisibility(View.GONE);
        }
        imgURL2 = listOfNewsCardClasses.get(position).getPubImgURL();
        if(!imgURL2.equals("null")){
            Glide.with(context)
                    .load(imgURL2)
                    .centerCrop()
                    .circleCrop()
                    .into(holder.newsPublisherImage);
        }
        holder.cardNews.setOnClickListener(new  View.OnClickListener() {
            public void onClick(View v) {
                NewsDetailsFragment fragment = new NewsDetailsFragment(listOfNewsCardClasses.get(holder.getAdapterPosition()));
                FragmentTransaction transaction = parentFragment.beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

    }

    @Override
    public int getItemCount () {
        return listOfNewsCardClasses.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView newsTitle, newsText, dateText;
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
