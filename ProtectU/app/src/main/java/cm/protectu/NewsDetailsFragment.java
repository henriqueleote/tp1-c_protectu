package cm.protectu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class NewsDetailsFragment extends Fragment {

    private ImageView imageBack;
    private TextView newsBigTitle, newsDate, newsBigText, publisherID;
    private NewsCard card;

    public NewsDetailsFragment(NewsCard card){
        this.card = card;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details_news, container, false);



        imageBack = view.findViewById(R.id.back_id);
        newsBigTitle = view.findViewById(R.id.newsBigTitle);
        newsDate = view.findViewById(R.id.newsDate);
        newsBigText = view.findViewById(R.id.newsBigText);
        publisherID = view.findViewById(R.id.publisherID);

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


        return view;
    }
}

