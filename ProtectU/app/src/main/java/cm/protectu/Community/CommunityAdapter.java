package cm.protectu.Community;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.github.chrisbanes.photoview.PhotoView;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Date;

import cm.protectu.MainActivity;
import cm.protectu.R;
import cm.protectu.UserDataClass;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<CommunityCard> listOfCommunityCards;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private CommunityFragment communityFragment;
    private int lastPosition;

    private static final String TAG = MainActivity.class.getName();

    public CommunityAdapter(Context ct, ArrayList<CommunityCard> l, FirebaseAuth firebaseAuth, CommunityFragment cf, int lastPos) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        context = ct;
        listOfCommunityCards = new ArrayList<>(l);
        mAuth = firebaseAuth;
        communityFragment = cf;
        lastPosition = lastPos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview_community, parent, false));
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //if(position < 2 && getLastPosition() < 2 && getLastPosition() >= 1){
        //   communityFragment.communityCardsData(null);
        //}
        setLastPosition(position);

        String userID = listOfCommunityCards.get(position).getUserID();
        String currentUserID = mAuth.getUid();
        String messageText = listOfCommunityCards.get(position).getMessageText();
        String messageID = listOfCommunityCards.get(position).getMessageID();

        /**
         * will fetch the database the name of the user who created the message through the user id
         * that is in the message data and then put it in the community fragment
         */
        firebaseFirestore.collection("users")
                .document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserDataClass userDataClass = documentSnapshot.toObject(UserDataClass.class);
                        if (userDataClass != null && userDataClass.getFirstName() != null) {
                            holder.userName.setText(userDataClass.getFirstName() + " " + userDataClass.getLastName());

                            if (!userDataClass.getImageURL().equals("null")) {
                                Picasso.get()
                                        .load(userDataClass.getImageURL())
                                        .centerCrop()
                                        .fit()
                                        .transform(new CropCircleTransformation())
                                        .into(holder.userImage);
                            }
                        }
                    }
                });

        adaptConstrains(holder, position);

        getClickedLikeOrDislike(currentUserID, messageID, holder);

        holder.dateText.setText(DateFormat.getDateInstance(DateFormat.FULL).format(listOfCommunityCards.get(position).getDate()));

        textAndMessageAdjuster(holder, position, messageText);

        if (!mAuth.getCurrentUser().isAnonymous()) {
            if (MainActivity.sessionUser.getUid().equals(userID) || !MainActivity.sessionUser.getUserType().equals("user")) {
                holder.removeMessageCommunity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeMessage(messageID);
                    }
                });
            } else {
                holder.removeMessageCommunity.setVisibility(View.INVISIBLE);
            }

            if (!MainActivity.sessionUser.getUserType().equals("user")) {
                holder.makeVerified.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        makeVerified(messageID, holder, position);
                    }
                });
            } else {
                holder.makeVerified.setVisibility(View.INVISIBLE);
            }

            //Put the number of likes and dislikes in the message
            holder.likeCounter.setText(listOfCommunityCards.get(position).getLikes() + "");
            holder.dislikeCounter.setText(listOfCommunityCards.get(position).getDislikes() + "");

            //Put the like action
            holder.likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    likesAndDislikes(messageID, "likes", holder.likeCounter, currentUserID, holder, "R.drawable.ic_thumb_up_blue_24dp");
                }
            });

            //Put the dislike action
            holder.dislikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    likesAndDislikes(messageID, "dislikes", holder.dislikeCounter, currentUserID, holder, "R.drawable.ic_thumb_down_blue_24dp");
                }
            });

            //Put the share action
            holder.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    String shareBody = "Your Body here";
                    String shareSub = "Your Subject here";
                    intent.putExtra(Intent.EXTRA_SUBJECT, shareBody);
                    intent.putExtra(Intent.EXTRA_TEXT, shareSub);
                    context.startActivity(Intent.createChooser(intent, "Share this Post"));
                }
            });

        } else {
            holder.removeMessageCommunity.setVisibility(View.INVISIBLE);
            holder.makeVerified.setVisibility(View.INVISIBLE);
        }
    }

    //TODO: CHANGE THE METHOD VERIFICATION WHEN HE CREATE THE AUTHORITY USERS


    public void adaptConstrains(MyViewHolder holder, int position) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(holder.constraintLayout);
        int margin = 5;

        if (!listOfCommunityCards.get(position).isVerified()) {
            if (!MainActivity.sessionUser.getUserType().equals("user")) {
                constraintSet.connect(holder.makeVerified.getId(), ConstraintSet.END, holder.verified.getId(), ConstraintSet.END, margin);
                constraintSet.applyTo(holder.constraintLayout);

                constraintSet.connect(holder.removeMessageCommunity.getId(), ConstraintSet.END, holder.makeVerified.getId(), ConstraintSet.START, margin);

            } else {
                constraintSet.connect(holder.removeMessageCommunity.getId(), ConstraintSet.END, holder.makeVerified.getId(), ConstraintSet.END, margin);
            }
            constraintSet.applyTo(holder.constraintLayout);
            holder.verified.setVisibility(View.INVISIBLE);
        } else {
            constraintSet.clear(holder.removeMessageCommunity.getId(), ConstraintSet.END);

            constraintSet.connect(holder.removeMessageCommunity.getId(), ConstraintSet.END, holder.verified.getId(), ConstraintSet.START, margin);
            constraintSet.applyTo(holder.constraintLayout);
        }
    }

    /**
     * This method is responsible for making the message from the community verified if the user is an authority
     *
     * @param messageID
     */
    public void makeVerified(String messageID, MyViewHolder holder, int position) {
        firebaseFirestore.collection("community-chat")
                .document(messageID)
                .update("verified", new Boolean("true"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        adaptConstrains(holder, position);
                        Log.d(TAG, "Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Insuccess");
                    }
                });


    }

    /**
     * This method is responsible for removing a message from the community if the user is an authority
     *
     * @param messageID
     */
    public void removeMessage(String messageID) {
        firebaseFirestore.collection("community-chat").document(messageID).delete();
        firebaseFirestore.collection("community-chat-reactions").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.get("messageID").equals(messageID)) {
                            firebaseFirestore.collection("community-chat-reactions").document(document.getId()).delete();
                        }
                    }
                    communityFragment.communityCardsData(null);
                } else {
                    Log.d(TAG, "Insucess");
                }
            }
        });

    }

    /**
     * This method is responsible for adjusting the text when the message has an image and when it doesn't, use Picasso to place the image in the image view
     *
     * @param holder
     * @param pos
     * @param messageText
     */
    public void textAndMessageAdjuster(MyViewHolder holder, int pos, String messageText) {
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
                    View mView = view.inflate(context, R.layout.fragment_photoview_fullscreen, null);
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
            holder.imageCommunity.setVisibility(View.GONE);
            holder.communityText.setText(messageText);
        }
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
    public void likesAndDislikes(String messageID, String type, TextView textNumber, String currentUserID, MyViewHolder holder, String image) {
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
                                        changeLikeOrDislike(holder.likeButton, holder.dislikeButton,type,
                                                "likes", "dislikes", document.getId(), messageID, holder.dislikeCounter, holder.likeCounter,currentUserID,holder);
                                    } else {
                                        changeLikeOrDislike(holder.dislikeButton, holder.dislikeButton,type,
                                                "dislikes", "likes", document.getId(), messageID, holder.likeCounter, holder.dislikeCounter,currentUserID,holder);
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
                                                UserReactionsClass userReactionsClass = new UserReactionsClass(currentUserID, messageID, type.substring(0, type.length() - 1), new Date(), image);
                                                firebaseFirestore.collection("community-chat-reactions").add(userReactionsClass);
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

    /**
     * This method checks if the user has a like, if he has, put the like button in blue and do the same for the dislike
     *
     * @param userID
     * @param messageID
     * @param holder
     */
    public void getClickedLikeOrDislike(String userID, String messageID, MyViewHolder holder) {
        userIsAnonymous(holder);
        firebaseFirestore.collection("community-chat-reactions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.get("userID").equals(userID) && document.get("messageID").equals(messageID)) {
                                    if (document.get("type").equals("like")) {
                                        holder.likeButton.setImageResource(R.drawable.ic_thumb_up_blue_24dp);
                                        //removeLikeOrDislike(holder.likeButton, "likes", document.getId(), messageID, holder.likeCounter);
                                    } else {
                                        holder.dislikeButton.setImageResource(R.drawable.ic_thumb_down_blue_24dp);
                                        //removeLikeOrDislike(holder.dislikeButton, "dislikes",document.getId(), messageID, holder.dislikeCounter);
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, "Insucess");
                        }
                    }
                });

    }

    /**
     * This method is responsible for checking if the user is a guest and if it is, makes the like, dislike, number of likes, dislikes and the share button invisible
     *
     * @param holder
     */
    public void userIsAnonymous(MyViewHolder holder) {
        if (MainActivity.sessionUser == null) {
            holder.likeButton.setVisibility(View.INVISIBLE);
            holder.dislikeButton.setVisibility(View.INVISIBLE);
            holder.likeCounter.setVisibility(View.INVISIBLE);
            holder.dislikeCounter.setVisibility(View.INVISIBLE);
            holder.shareButton.setVisibility(View.INVISIBLE);
        }
    }

    public void changeLikeOrDislike(ImageView buttonBlue, ImageView lastButtonClicked, String typeOfButton,String type,
                                    String newType, String reactionID, String messageID, TextView textNumber, TextView lastTextNumber, String userID , MyViewHolder holder) {
        String newTypeXs = newType.substring(0, newType.length() - 1);
        String typeOfButtonXs = typeOfButton.substring(0, typeOfButton.length() - 1);
        firebaseFirestore.collection("community-chat-reactions").document(reactionID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.get("type").equals(typeOfButtonXs)) {
                            removeLikeOrDislike(buttonBlue, typeOfButton, reactionID, messageID, lastTextNumber);
                        } else {
                            firebaseFirestore.collection("community-chat-reactions").document(reactionID)
                                    .update("type", newTypeXs)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG, "here");
                                            if (newTypeXs.equals("like")) {
                                                buttonBlue.setImageResource(R.drawable.ic_thumb_down_black_24dp);
                                            } else {
                                                buttonBlue.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                                            }
                                            getClickedLikeOrDislike(userID,  messageID, holder);
                                            int numberLastUpdated = Integer.parseInt(lastTextNumber.getText().toString().trim()) - 1;
                                            lastTextNumber.setText(numberLastUpdated + "");
                                            int numberNewUpdated = Integer.parseInt(textNumber.getText().toString().trim()) + 1;
                                            textNumber.setText(numberNewUpdated + "");


                                            DocumentReference likeRef = firebaseFirestore.collection("community-chat").document(messageID);
                                            likeRef.update(type, numberLastUpdated);
                                            likeRef = firebaseFirestore.collection("community-chat").document(messageID);
                                            likeRef.update(newType, numberNewUpdated);

                                            //removeLikeOrDislike(lastButtonClicked, newType,reactionID, messageID, textNumber);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Fail");
                                }
                            });
                        }
                    }
                });
    }

    public void removeLikeOrDislike(ImageView buttonClicked, String type, String reactionID, String messageID, TextView textNumber) {
        firebaseFirestore.collection("community-chat-reactions").document(reactionID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        int number = Integer.parseInt(textNumber.getText().toString()) - 1;
                        DocumentReference likeRef = firebaseFirestore.collection("community-chat").document(messageID);
                        likeRef
                                .update(type, number)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        textNumber.setText(number + "");
                                        if (type.equals("likes")) {
                                            buttonClicked.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                                        } else {
                                            buttonClicked.setImageResource(R.drawable.ic_thumb_down_black_24dp);
                                        }
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

    @Override
    public int getItemCount() {
        return listOfCommunityCards.size();
    }

    public int getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(int position) {
        this.lastPosition = position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userName, communityText, likeCounter, dislikeCounter, communityTextWithImage, dateText;
        ImageView userImage, verified, likeButton, dislikeButton, imageCommunity, /*dislikeButtonClicked,
                likeButtonClicked,*/
                shareButton, removeMessageCommunity, makeVerified;
        CardView cardCommunity;
        ConstraintLayout constraintLayout;


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
            //dislikeButtonClicked = itemView.findViewById(R.id.dislikeButtonClicked);
            //likeButtonClicked = itemView.findViewById(R.id.likeButtonClicked);
            shareButton = itemView.findViewById(R.id.shareButton);
            dateText = itemView.findViewById(R.id.dateText);
            removeMessageCommunity = itemView.findViewById(R.id.removeMessageCommunity);
            makeVerified = itemView.findViewById(R.id.makeVerified);
            cardCommunity = itemView.findViewById(R.id.cardCommunity);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }
    }
}
