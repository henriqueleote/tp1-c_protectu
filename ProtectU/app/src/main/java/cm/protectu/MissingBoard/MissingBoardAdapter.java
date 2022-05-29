package cm.protectu.MissingBoard;

import android.content.Context;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import cm.protectu.Authentication.UserDataClass;
import cm.protectu.MainActivity;
import cm.protectu.R;


public class MissingBoardAdapter extends RecyclerView.Adapter<MissingBoardAdapter.MyViewHolder> {



    private Context mContext;
    private List<MissingCardClass> mData;
    private FragmentManager parentFragment;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private MissingBoardFragment missingBoardFragment;
    private String idUser;


    private static final String TAG = MainActivity.class.getName();

    /**
     * Constructor that lets you initialize objects
     * @param mContext
     * @param mData
     * @param fragment
     * @param firebaseAuth
     */
    public MissingBoardAdapter(Context mContext, List<MissingCardClass> mData, FragmentManager fragment, FirebaseAuth firebaseAuth, MissingBoardFragment missingBoardFragment, String id) {
        this.mContext = mContext;
        this.mData = mData;
        this.parentFragment= fragment;
        this.mAuth = firebaseAuth;
        firebaseFirestore = FirebaseFirestore.getInstance();
        this.missingBoardFragment = missingBoardFragment;
        this.idUser = id;

    }

    /**
     * calls this method whenever it needs to create a new ViewHolder. The method creates and
     * initializes the ViewHolder and the associated View, but does not populate the contents of the view.
     * The ViewHolder has not yet been linked to specific data.
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_missing,parent,false);

        return new MyViewHolder(view);
    }

    /**
     * calls this method to bind a ViewHolder to the data.
     * The method fetches the appropriate data and uses that data to populate the view pin layout.
     * For example, if the RecyclerView displays a list of names,
     * the method will be able to find the appropriate name in the list and populate the TextView widget from the view pin.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int pos = position;
        holder.missingName.setText(mData.get(position).getMissingName());
        String missingID = mData.get(position).getMissingID();
        String userID = mData.get(position).getUserID();

        Glide.with(mContext)
                .load(mData.get(position).getFotoMissing())
                .centerCrop()
                
                .into(holder.missingImage);




        /**
         *
         * Lets you go to the clicked card post
         */
        holder.cardMissing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MissingPostFragment fragment = new MissingPostFragment(mData.get(holder.getAdapterPosition()).getUserID(),mData.get(holder.getAdapterPosition()).getMissingName(),mData.get(holder.getAdapterPosition()).getDescription(),mData.get(holder.getAdapterPosition()).getMissingAge(),mData.get(holder.getAdapterPosition()).getPhoneNumber(),mData.get(pos).getFotoMissing());
                FragmentTransaction transaction = parentFragment.beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        if (!mAuth.getCurrentUser().isAnonymous()) {
            if (MainActivity.sessionUser.getUid().equals(userID) || !MainActivity.sessionUser.getUserType().equals("user")) {
                holder.removeMissing.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeMissing(missingID,idUser);
                    }
                });
            } else {
                holder.removeMissing.setVisibility(View.INVISIBLE);
            }
        }else {
            holder.removeMissing.setVisibility(View.INVISIBLE);
        }

    }

    /**
     *
     * @return The size of the list of cards in the list
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * This method is responsible for removing a mising post from the missingBoard
     *
     * @param missingID
     */
    public void removeMissing(String missingID,String id) {
        firebaseFirestore.collection("missing-board").document(missingID).delete();
        missingBoardFragment.missingCardsData(id);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        //Declaração dos elementos a serem utilizados
        TextView description, userName, missingName, age;
        ImageView userImage, missingImage,removeMissing;
        CardView cardMissing;


        // stores the reference to the UI Elements in the Card Layout and they can be modified dynamically during the execution of the program by the list of data.
        public MyViewHolder(View itemView){
            super(itemView);
            //Link the view objects with the XML
            description = itemView.findViewById(R.id.descriptionPostID);
            userName = itemView.findViewById(R.id.profileNameID);
            missingName = itemView.findViewById(R.id.nameOfTheMissingID);
            //age = itemView.findViewById(R.id.ageID);
            userImage = itemView.findViewById(R.id.imageProfileID);
            missingImage = itemView.findViewById(R.id.imageMissingID);
            cardMissing = itemView.findViewById(R.id.cardMissingID);
            removeMissing = itemView.findViewById(R.id.removeMissing);

        }

        public ImageView getUserImage() {
            return userImage;
        }

        public ImageView getMissingImage() {
            return missingImage;
        }
    }

    /**
     *
     * @return The list of all existing publications
     */
    public List<MissingCardClass> getmData() {
        return mData;
    }

    /**
     * Allows you to exchange the information from the list of publications for a new list
     * @param mData new list to be placed in the list of publications
     */
    public void setmData(List<MissingCardClass> mData) {
        this.mData = mData;
    }

}


