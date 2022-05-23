package cm.protectu.News;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.Community.CommunityFragment;
import cm.protectu.Community.NewMessageCommunityFragment;
import cm.protectu.MainActivity;
import cm.protectu.R;


public class NewsFragment extends Fragment {

    //Firebase Authentication
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private ArrayList<NewsCardClass> listOfNewsCardClasses;
    private FloatingActionButton floatingActionButton;
    private SwipeRefreshLayout swipeToRefresh;
    private NewsFragment fragment;
    private ImageView menuImageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        //TODO Check the animation
        //Checks if there is a session, if not, redirects to the Auth page
        if (mAuth.getCurrentUser() == null) {
            getActivity().finish();
            //Swipe animation ?? not sure, consult previous code
            //getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            startActivity(new Intent(getActivity(), AuthActivity.class));
        }

        firebaseFirestore = FirebaseFirestore.getInstance();

        listOfNewsCardClasses = new ArrayList<>();
        recyclerView = view.findViewById(R.id.newsRecyclerView);
        swipeToRefresh = view.findViewById(R.id.swipeToRefresh);
        menuImageView = view.findViewById(R.id.menuImageView);
        newsAdapter = new NewsAdapter(getActivity(), listOfNewsCardClasses,mAuth, getParentFragmentManager());
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.setAdapter(newsAdapter);


        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newsCardsData();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeToRefresh.isRefreshing()) {
                            swipeToRefresh.setRefreshing(false);
                        }
                    }
                }, 800);
            }
        });
        newsCardsData();

        floatingActionButton = view.findViewById(R.id.createMessageButton);
        fragment = this;

        //TODO: DISABLE BUTTON
        /**
         *if the user is not logged in the add button disappears,
         * otherwise the button appears and if clicked it opens a bottom sheet that allows the creation of a new message in the community
         */
        if (mAuth.getCurrentUser().isAnonymous()) {
            floatingActionButton.setVisibility(View.GONE);
        } else {
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            if (!mAuth.getCurrentUser().isAnonymous()) {
                                                                NewPostNewsFragment newPostNewsFragment = new NewPostNewsFragment(fragment);
                                                                newPostNewsFragment.show(getParentFragmentManager(), newPostNewsFragment.getTag());
                                                            }
                                                        }
                                                    }
            );
        }

        menuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        //Returns the view
        return view;

    }

    public void newsCardsData() {
        listOfNewsCardClasses.clear();
        firebaseFirestore.collection("news")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                NewsCardClass newsCardClass = document.toObject(NewsCardClass.class);
                                listOfNewsCardClasses.add(newsCardClass);
                            }
                            newsAdapter = new NewsAdapter(getActivity(), listOfNewsCardClasses,mAuth,getParentFragmentManager());
                            recyclerView.setAdapter(newsAdapter);
                            newsAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), "erro", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
