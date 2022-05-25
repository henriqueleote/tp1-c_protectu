package cm.protectu.Community;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.News.NewsCardClass;
import cm.protectu.R;

public class MakeVerifiedFragment extends BottomSheetDialogFragment {
    private CommunityFragment communityFragment;
    private Button makeVerifiedButton;
    private EditText title;
    private ImageView closeButton;
    private CommunityCard card;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String firebaseUrl,messageID;
    private CommunityAdapter communityAdapter;
    private CommunityAdapter.MyViewHolder holder;

    private static final String TAG = AuthActivity.class.getName();

    public MakeVerifiedFragment(CommunityFragment communityFragment, CommunityCard card, String messageID, CommunityAdapter communityAdapter, CommunityAdapter.MyViewHolder holder) {
        this.communityFragment = communityFragment;
        this.card = card;
        firebaseUrl = card.getImageURL();
        this.messageID = messageID;
        this.communityAdapter = communityAdapter;
        this.holder = holder;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_make_verified, container, false);

        makeVerifiedButton = view.findViewById(R.id.createNewNewsButton);
        title = view.findViewById(R.id.title);
        closeButton = view.findViewById(R.id.close);

        firebaseFirestore = FirebaseFirestore.getInstance();

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });

        makeVerifiedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("users")
                        //where the userID is the same as the logged in user
                        .whereEqualTo("uid", mAuth.getCurrentUser().getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        createMessage(card.getMessageText().trim()
                                                , mAuth.getUid(), title.getText().toString().trim(), document.getString("imageURL"));
                                        getDialog().cancel();
                                    }
                                } else {
                                    //TODO Maybe reload the page or kill the session?
                                    //Shows the error
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });
            }

        });

        return view;
    }

    /**
     * This method will create a message with what the user writes and with an image if the user transfers it,
     * when creating the message sends it to the database
     * @param newsText text written by the user
     * @param pubID
     */
    public void createMessage(String newsText,String pubID, String newsTitle, String pubURL){
        // Message's field check
        if (TextUtils.isEmpty(newsTitle)) {
            title.setError("Please enter a message");
            title.requestFocus();
            return;
        }

        DocumentReference documentReference = firebaseFirestore.collection("news").document();
        documentReference.set(new NewsCardClass(newsTitle,newsText,documentReference.getId(),firebaseUrl,pubURL,pubID, new Date()))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firebaseFirestore.collection("community-chat")
                                .document(messageID)
                                .update("verified", new Boolean("true"))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                communityAdapter.adaptConstrains(holder,true);
                            }
                        });
                        Log.d(TAG, "DocumentSnapshot with the ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }
}
