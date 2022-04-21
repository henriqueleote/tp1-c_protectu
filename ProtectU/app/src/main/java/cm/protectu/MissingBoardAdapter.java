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

import java.util.List;


public class MissingBoardAdapter extends RecyclerView.Adapter<MissingBoardAdapter.MyViewHolder> {

    //TODO - Change variables names and comment

    private Context mContext;
    private List<MissingCard> mData;
    private FragmentManager parentFragment;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;

    private static final String TAG = MainActivity.class.getName();

    public MissingBoardAdapter(Context mContext, List<MissingCard> mData, FragmentManager fragment, FirebaseAuth firebaseAuth) {
        this.mContext = mContext;
        this.mData = mData;
        this.parentFragment= fragment;
        this.mAuth = firebaseAuth;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_missing,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //holder.description.setText(mData.get(position).getDescription());
        holder.userName.setText(mData.get(position).getProfileName());
        holder.missingName.setText(mData.get(position).getMissingName());
        holder.age.setText(String.valueOf(mData.get(position).getMissingAge()));
        //falta buscar imagens

        String userID = mData.get(position).getUserID();

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


        //set click listener
        holder.cardMissing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MissingPostFragment fragment = new MissingPostFragment(mData.get(holder.getAdapterPosition()).getUserID(),mData.get(holder.getAdapterPosition()).getMissingName(),mData.get(holder.getAdapterPosition()).getDescription(),mData.get(holder.getAdapterPosition()).getMissingAge(),mData.get(holder.getAdapterPosition()).getPhoneNumber());
                FragmentTransaction transaction = parentFragment.beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView description, userName, missingName, age;
        ImageView userImage, missingImage;
        CardView cardMissing;


        //View Holder: View Holder Class is the java class that stores the reference to the UI Elements in the Card Layout and they can be modified dynamically during the execution of the program by the list of data.
        public MyViewHolder(View itemView){
            super(itemView);
            description = itemView.findViewById(R.id.descriptionPostID);
            userName = itemView.findViewById(R.id.profileNameID);
            missingName = itemView.findViewById(R.id.nameOfTheMissingID);
            age = itemView.findViewById(R.id.ageID);
            userImage = itemView.findViewById(R.id.imageProfileID);
            missingImage = itemView.findViewById(R.id.imageMissingID);
            cardMissing = itemView.findViewById(R.id.cardMissingID);

        }

    }

    public List<MissingCard> getmData() {
        return mData;
    }

    public void setmData(List<MissingCard> mData) {
        this.mData = mData;
    }
}
