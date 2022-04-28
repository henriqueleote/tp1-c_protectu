package cm.protectu.MissingBoard;

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
import com.squareup.picasso.Picasso;


import java.util.List;

import cm.protectu.MainActivity;
import cm.protectu.R;
import cm.protectu.Community.UserDataClass;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;


public class MissingBoardAdapter extends RecyclerView.Adapter<MissingBoardAdapter.MyViewHolder> {

    //TODO - Change variables names and comment

    private Context mContext;
    private List<MissingCardClass> mData;
    private FragmentManager parentFragment;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private String urlProfile;
    private String urlMissing;

    private static final String TAG = MainActivity.class.getName();

    /**
     * Construtor que permite inicializar os objetos
     * @param mContext
     * @param mData lista que contem as publicações existentes
     * @param fragment
     * @param firebaseAuth
     */
    public MissingBoardAdapter(Context mContext, List<MissingCardClass> mData, FragmentManager fragment, FirebaseAuth firebaseAuth) {
        this.mContext = mContext;
        this.mData = mData;
        this.parentFragment= fragment;
        this.mAuth = firebaseAuth;
        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    /**
     * chama esse método sempre que precisa criar um novo ViewHolder. O método cria e
     * inicializa o ViewHolder e a View associada, mas não preenche o conteúdo da visualização.
     * O ViewHolder ainda não foi vinculado a dados específicos.
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
     * chama esse método para associar um ViewHolder aos dados.
     * O método busca os dados apropriados e usa esses dados para preencher o layout do fixador de visualização.
     * Por exemplo, se a RecyclerView exibir uma lista de nomes,
     * o método poderá encontrar o nome apropriado na lista e preencher o widget TextView do fixador de visualização.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int pos = position;
        holder.missingName.setText(mData.get(position).getMissingName());
        holder.age.setText(String.valueOf(mData.get(position).getMissingAge()));

        Picasso.get()
                .load(mData.get(position).getFotoMissing())
                .centerCrop()
                .fit()
                .into(holder.missingImage);

        String userID = mData.get(position).getUserID();
        
        /**
         * will fetch the database the name of the user who created the message through the user id
         * that is in the missing data and then put it in the Missing fragment
         */
        firebaseFirestore.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getId().equals(userID)) {
                                    UserDataClass userDataClass = document.toObject(UserDataClass.class);
                                    holder.userName.setText(userDataClass.getFirstName() + " " + userDataClass.getLastName());
                                    if (!document.get("imageURL").equals("")){
                                        urlProfile = document.getString("imageURL");
                                        Picasso.get()
                                                .load(urlProfile)
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

        /**
         * Permite ir para a publicação do cartão clicado
         */
        holder.cardMissing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MissingPostFragment fragment = new MissingPostFragment(mData.get(holder.getAdapterPosition()).getUserID(),mData.get(holder.getAdapterPosition()).getMissingName(),mData.get(holder.getAdapterPosition()).getDescription(),mData.get(holder.getAdapterPosition()).getMissingAge(),mData.get(holder.getAdapterPosition()).getPhoneNumber(),urlProfile,mData.get(pos).getFotoMissing());
                FragmentTransaction transaction = parentFragment.beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

    }

    /**
     *
     * @return O tamanho da lista de cartões existentes na lista
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        //Declaração dos elementos a serem utilizados
        TextView description, userName, missingName, age;
        ImageView userImage, missingImage;
        CardView cardMissing;


        // stores the reference to the UI Elements in the Card Layout and they can be modified dynamically during the execution of the program by the list of data.
        public MyViewHolder(View itemView){
            super(itemView);
            //Link the view objects with the XML
            description = itemView.findViewById(R.id.descriptionPostID);
            userName = itemView.findViewById(R.id.profileNameID);
            missingName = itemView.findViewById(R.id.nameOfTheMissingID);
            age = itemView.findViewById(R.id.ageID);
            userImage = itemView.findViewById(R.id.imageProfileID);
            missingImage = itemView.findViewById(R.id.imageMissingID);
            cardMissing = itemView.findViewById(R.id.cardMissingID);


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
     * @return A lista com todas as publicações existentes
     */
    public List<MissingCardClass> getmData() {
        return mData;
    }

    /**
     * Permite trocar a informação da lista de publicações por uma nova lista
     * @param mData nova lista a ser colocada na lista de publicações
     */
    public void setmData(List<MissingCardClass> mData) {
        this.mData = mData;
    }

}


