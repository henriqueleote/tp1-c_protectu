package cm.protectu.News;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.squareup.picasso.Picasso;

import cm.protectu.R;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class NewsDetailsFragment extends Fragment {

    private ImageView imageBack, newsBigImage, pubImage;
    private TextView newsBigTitle, newsDate, newsBigText, publisherID;
    private NewsCardClass card;
    String imgURL1, imgURL2;

    public NewsDetailsFragment(NewsCardClass card){
        this.card = card;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details_news, container, false);
        imageBack = view.findViewById(R.id.back_id);
        newsBigTitle = view.findViewById(R.id.newsBigTitle);
        newsDate = view.findViewById(R.id.newsDate);
        newsBigText = view.findViewById(R.id.newsBigText);
        publisherID = view.findViewById(R.id.publisherID);
        newsBigImage = view.findViewById(R.id.newsBigImage);
        pubImage = view.findViewById(R.id.publisherImg);

        newsBigTitle.setText(card.getNewsTitle());
        newsBigText.setText(card.getNewsText());
        publisherID.setText(card.getPubID());

        //go back to the news frame
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewsFragment fragment = new NewsFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
        imgURL1 = card.getImageURL();
        if(imgURL1 != null){
            Picasso.get()
                    .load(imgURL1)
                    .into(newsBigImage);
        }
        imgURL2 = card.getPubImgURL();
        if(imgURL2 != null){
            Picasso.get()
                    .load(imgURL2)
                    .centerCrop()
                    .fit()
                    .transform(new CropCircleTransformation())
                    .into(pubImage);
        }

        return view;
    }
}

