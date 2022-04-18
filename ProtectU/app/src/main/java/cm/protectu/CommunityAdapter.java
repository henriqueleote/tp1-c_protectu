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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.MyViewHolder> {

    Context context;
    ArrayList<CommunityCard> listOfCommunityCards;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;

    private static final String TAG = MainActivity.class.getName();

    public CommunityAdapter(Context ct, ArrayList<CommunityCard> l, FirebaseAuth firebaseAuth) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        context = ct;
        listOfCommunityCards = l;
        mAuth = firebaseAuth;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview_community, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int pos = position;
        String userID = listOfCommunityCards.get(position).getUserID();
        String messageID = listOfCommunityCards.get(position).getMessageText();

        firebaseFirestore.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getId().equals(userID)) {
                                    UserData userData = document.toObject(UserData.class);
                                    holder.userName.setText(userData.getFirstName() + " " + userData.getLastName());
                                    break;
                                } else {
                                    holder.userName.setText("Not Found");
                                }
                            }
                        } else {
                            Log.d(TAG, "Insucess");
                        }
                    }
                });


        //String[] userData = getUserData(id);
        //holder.userName.setText(userData[1]); //username
        //holder.userName.setText(username); //username
        holder.text.setText(messageID);
        holder.likeCounter.setText(listOfCommunityCards.get(position).getLikes() + "");
        holder.dislikeCounter.setText(listOfCommunityCards.get(position).getDislikes() + "");

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mAuth.getCurrentUser().isAnonymous() && !checkUserHasLike(userID)) {
                    Log.d(TAG, "2");
                    int actualLikes = listOfCommunityCards.get(pos).getLikes() + 1;
                    DocumentReference likeRef = firebaseFirestore.collection("community-chat").document(listOfCommunityCards.get(pos).getMessageID());
                    likeRef
                            .update("likes", actualLikes)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    holder.likeCounter.setText(actualLikes + "");
                                    UserReactions userReactions = new UserReactions(userID, messageID, "Likes", new Date());
                                    firebaseFirestore.collection("community-chat-reactions").add(userReactions);
                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document", e);
                                }
                            });
                }
            }
        });

        holder.dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mAuth.getCurrentUser().isAnonymous() && !checkUserHasLike(userID)) {
                    int actualDislikes = listOfCommunityCards.get(pos).getDislikes() + 1;
                    DocumentReference likeRef = firebaseFirestore.collection("community-chat").document(listOfCommunityCards.get(pos).getMessageID());
                    likeRef
                            .update("likes", actualDislikes)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    holder.dislikeCounter.setText(actualDislikes + "");
                                    UserReactions userReactions = new UserReactions(userID, messageID, "Dislikes", new Date());
                                    firebaseFirestore.collection("community-chat-reactions").add(userReactions);
                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document", e);
                                }
                            });
                }
            }
        });
    }

    public boolean checkUserHasLike(String userID) {
        final boolean[] asLike = {false};

        firebaseFirestore.collection("community-chat-reactions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.get("userID").equals(userID)) {
                                    asLike[0] = true;
                                    Log.d(TAG, "1");
                                }
                            }
                        } else {
                            Log.d(TAG, "Insucess");
                        }
                    }
                });

            return asLike[0];
    }

        @Override
        public int getItemCount () {
            return listOfCommunityCards.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView userName, text, likeCounter, dislikeCounter;
            ImageView userImage;
            CardView cardCommunity;
            ImageView likeButton, dislikeButton;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                userName = itemView.findViewById(R.id.userName);
                text = itemView.findViewById(R.id.communityText);
                userImage = itemView.findViewById(R.id.userImage);
                likeCounter = itemView.findViewById(R.id.numberOfLikes);
                dislikeCounter = itemView.findViewById(R.id.numberOfDislikes);
                likeButton = itemView.findViewById(R.id.likeButton);
                dislikeButton = itemView.findViewById(R.id.dislikeButton);
                cardCommunity = itemView.findViewById(R.id.cardCommunity);

            }
        }

    /*public String[] getUserData(String id){
        String[] userData = {};

        DocumentReference docRef = firebaseFirestore.collection("users").document(id);
        Log.d(TAG, "DocRef -> " + id);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG, "Data -> " + documentSnapshot.get("firstName").toString());
                userData[0] = documentSnapshot.get("uid").toString();
                userData[1] = documentSnapshot.get("firstName").toString();
                userData[2] = documentSnapshot.get("lastName").toString();
                //userData[3] = documentSnapshot.get("profilePic").toString();
            }
        });

        return userData;
    }*/
    }
