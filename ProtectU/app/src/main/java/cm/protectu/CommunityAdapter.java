package cm.protectu;


import android.app.AlertDialog;
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

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Date;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.MyViewHolder> {

    Context context;
    ArrayList<CommunityCard> listOfCommunityCards;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    View view;

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
        String userID = listOfCommunityCards.get(pos).getUserID();
        String currentUserID = mAuth.getUid();
        String messageText = listOfCommunityCards.get(pos).getMessageText();
        String messageID = listOfCommunityCards.get(pos).getMessageID();

        getClickedLikeOrDislike(currentUserID, messageID, holder);

        /**
         * If the message is not verified it makes the symbol invisible
         */
        if (!listOfCommunityCards.get(pos).isVerified()) {
            holder.verified.setVisibility(View.INVISIBLE);
        }

        /**
         * will fetch the database the name of the user who created the message through the user id
         * that is in the message data and then put it in the community fragment
         */
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
                                    if (!document.get("imageURL").equals("")){
                                        Picasso.get()
                                                .load(document.get("imageURL").toString())
                                                .centerCrop()
                                                .fit()
                                                .transform(new CropCircleTransformation())
                                                .into(holder.userImage);

                                    }
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

        //if the message does not have an image, just put the text if it does, adjust the text with the image
        if (!listOfCommunityCards.get(pos).getImageURL().equals("")) {
            holder.imageCommunity.setVisibility(View.VISIBLE);

            final int radius = 8;
            final int margin = 5;
            final Transformation transformation = new RoundedCornersTransformation(radius, margin);

            Picasso.get()
                    .load(listOfCommunityCards.get(pos).getImageURL())
                    .centerCrop()
                    .fit()
                    .transform(transformation)
                    .into(holder.imageCommunity);

            holder.imageCommunity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                    View mView = view.inflate(context,R.layout.fragment_photoview_fullscreen, null);
                    PhotoView photoView = mView.findViewById(R.id.imageView);
                    Picasso.get()
                            .load(listOfCommunityCards.get(pos).getImageURL())
                            .into(photoView);

                    mBuilder.setView(mView);
                    AlertDialog mDialog = mBuilder.create();
                    mDialog.show();
                }
            });

            holder.communityTextWithImage.setVisibility(View.VISIBLE);
            holder.communityText.setVisibility(View.GONE);

            holder.communityTextWithImage.setText(messageText);

        } else {
            holder.communityTextWithImage.setVisibility(View.GONE);
            holder.communityText.setText(messageText);
        }

        //Put the number of likes and dislikes in the message
        holder.likeCounter.setText(listOfCommunityCards.get(pos).getLikes() + "");
        holder.dislikeCounter.setText(listOfCommunityCards.get(pos).getDislikes() + "");

        //Put the like action
        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likesAndDislikes(messageID, "like", holder.likeCounter, currentUserID, holder);
            }
        });

        //Put the dislike action
        holder.dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likesAndDislikes(messageID, "dislike", holder.dislikeCounter, currentUserID, holder);
            }
        });

    }

    //TODO: CHANGE THE METHOD DESC

    /**
     * method that checks if the user already has a like or dislike in the message, if he already has a like or dislike, don't let him put it, if he doesn't,
     * put a like or a like, increase the number of likes or dislikes, depending on whether it's the like button or of the dislike, and puts the reaction information in the database
     *
     * @param messageID
     * @param type
     * @param textNumber
     * @param currentUserID
     * @param holder
     */
    public void likesAndDislikes(String messageID, String type, TextView textNumber, String currentUserID, MyViewHolder holder) {
        if (!mAuth.getCurrentUser().isAnonymous()) {
            firebaseFirestore.collection("community-chat-reactions")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                boolean hasReaction = false;
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.get("userID").equals(currentUserID) && document.get("messageID").equals(messageID)) {
                                        hasReaction = true;
                                        if (document.get("type").equals("like")) {
                                            changeLikeOrDislike(holder.dislikeButton, holder.likeButton, holder.dislikeButtonClicked, holder.likeButtonClicked,
                                                    currentUserID, holder, "like", "dislike", document.getId(), messageID, holder.dislikeCounter, holder.likeCounter);
                                        } else {
                                            changeLikeOrDislike(holder.likeButton, holder.dislikeButton, holder.likeButtonClicked, holder.dislikeButtonClicked,
                                                    currentUserID, holder, "dislike", "like", document.getId(), messageID, holder.likeCounter, holder.dislikeCounter);
                                        }
                                        Log.d(TAG, "User already has a like or dislike in this message");
                                        break;
                                    }
                                }
                                if (!hasReaction) {
                                    int actualNumber = Integer.parseInt(textNumber.getText().toString()) + 1;
                                    DocumentReference likeRef = firebaseFirestore.collection("community-chat").document(messageID);
                                    likeRef
                                            .update(type, actualNumber)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    textNumber.setText(actualNumber + "");
                                                    UserReactions userReactions = new UserReactions(currentUserID, messageID, type, new Date());
                                                    firebaseFirestore.collection("community-chat-reactions").add(userReactions);
                                                    getClickedLikeOrDislike(currentUserID, messageID, holder);
                                                    Log.d(TAG, "Document successfully updated!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error updating document", e);
                                                }
                                            });
                                }
                            } else {
                                Log.d(TAG, "Insucess");
                            }
                        }
                    });
        }
    }

    /**
     * this method checks if the user has a like, if he has, put the like button in blue and do the same for the dislike
     *
     * @param userID
     * @param messageID
     * @param holder
     */
    public void getClickedLikeOrDislike(String userID, String messageID, MyViewHolder holder) {
        if (!mAuth.getCurrentUser().isAnonymous()) {
            firebaseFirestore.collection("community-chat-reactions")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.get("userID").equals(userID) && document.get("messageID").equals(messageID)) {
                                        if (document.get("type").equals("like")) {
                                            holder.likeButtonClicked.setVisibility(View.VISIBLE);
                                            holder.likeButton.setVisibility(View.INVISIBLE);
                                            removeLikeOrDislike(holder.likeButton, holder.likeButtonClicked, "like",
                                                    document.getId(), messageID, holder.likeCounter);
                                        } else {
                                            holder.dislikeButtonClicked.setVisibility(View.VISIBLE);
                                            holder.dislikeButton.setVisibility(View.INVISIBLE);
                                            removeLikeOrDislike(holder.dislikeButton, holder.dislikeButtonClicked, "dislike",
                                                    document.getId(), messageID, holder.dislikeCounter);
                                        }
                                    }
                                }
                            } else {
                                Log.d(TAG, "Insucess");
                            }
                        }
                    });
        }
    }

    public void changeLikeOrDislike(ImageView buttonClicked, ImageView lastButtonClicked, ImageView buttonClickedBlue, ImageView lastButtonClickedBlue, String userID,
                                    MyViewHolder holder, String type, String newType, String reactionID, String messageID, TextView textNumber, TextView lastTextNumber) {

        lastButtonClickedBlue.setVisibility(View.INVISIBLE);
        lastButtonClicked.setVisibility(View.VISIBLE);
        firebaseFirestore.collection("community-chat-reactions").document(reactionID)
                .update("type", newType)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        buttonClicked.setVisibility(View.INVISIBLE);
                        buttonClickedBlue.setVisibility(View.VISIBLE);
                        int numberLastUpdated = Integer.parseInt(lastTextNumber.getText().toString().trim()) - 1;
                        lastTextNumber.setText(numberLastUpdated + "");
                        int numberNewUpdated = Integer.parseInt(textNumber.getText().toString().trim()) + 1;
                        textNumber.setText(numberNewUpdated + "");

                        DocumentReference likeRef = firebaseFirestore.collection("community-chat").document(messageID);
                        likeRef.update(type, numberLastUpdated);
                        likeRef = firebaseFirestore.collection("community-chat").document(messageID);
                        likeRef.update(newType, numberNewUpdated);

                        removeLikeOrDislike(buttonClicked, buttonClickedBlue, newType,
                                reactionID, messageID, textNumber);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Fail");
            }
        });
    }

    public void removeLikeOrDislike(ImageView button, ImageView buttonClicked, String type, String reactionID, String messageID, TextView textNumber) {
        buttonClicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClicked.setVisibility(View.INVISIBLE);
                firebaseFirestore.collection("community-chat-reactions").document(reactionID)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                button.setVisibility(View.VISIBLE);
                                int numberOfLikes = Integer.parseInt(textNumber.getText().toString()) - 1;
                                DocumentReference likeRef = firebaseFirestore.collection("community-chat").document(messageID);
                                likeRef
                                        .update(type, numberOfLikes)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                textNumber.setText(numberOfLikes + "");
                                                Log.d(TAG, "Document successfully updated!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error updating document", e);
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });

            }
        });
    }

    @Override
    public int getItemCount() {
        return listOfCommunityCards.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userName, communityText, likeCounter, dislikeCounter, communityTextWithImage;
        ImageView userImage, verified, likeButton, dislikeButton, imageCommunity, dislikeButtonClicked, likeButtonClicked;
        CardView cardCommunity;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            communityText = itemView.findViewById(R.id.communityText);
            userImage = itemView.findViewById(R.id.userImage);
            likeCounter = itemView.findViewById(R.id.numberOfLikes);
            dislikeCounter = itemView.findViewById(R.id.numberOfDislikes);
            likeButton = itemView.findViewById(R.id.likeButton);
            dislikeButton = itemView.findViewById(R.id.dislikeButton);
            verified = itemView.findViewById(R.id.verified);
            communityTextWithImage = itemView.findViewById(R.id.communityTextWithImage);
            imageCommunity = itemView.findViewById(R.id.imageCommunity);
            dislikeButtonClicked = itemView.findViewById(R.id.dislikeButtonClicked);
            likeButtonClicked = itemView.findViewById(R.id.likeButtonClicked);
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
