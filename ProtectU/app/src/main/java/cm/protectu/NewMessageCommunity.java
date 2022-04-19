package cm.protectu;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class NewMessageCommunity extends BottomSheetDialogFragment {

    //EditText
    private EditText message;

    //Button
    private Button createButton;

    private ImageView closeButton;

    //Firebase Authentication
    private FirebaseAuth mAuth;

    private FirebaseFirestore firebaseFirestore;

    //TAG for debug logs
    private static final String TAG = AuthActivity.class.getName();

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_add_message, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        //Link the view objects with the XML
        closeButton = view.findViewById(R.id.closeMessage);
        message = view.findViewById(R.id.message);
        createButton = view.findViewById(R.id.createNewMessageButton);

        firebaseFirestore = FirebaseFirestore.getInstance();

        //On click closes the form sheet
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });

        //On click starts the login process
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createMessage(message.getText().toString().trim()
                        ,mAuth.getUid());
                getDialog().cancel();
            }
        });

        //Returns the view
        return view;
    }

    public void createMessage(String messageText,String userID){
        // Message's field check
        if (TextUtils.isEmpty(messageText)) {
            message.setError(getString(R.string.error_enter_your_mail));
            message.requestFocus();
            return;
        }

        firebaseFirestore.collection("community-chat")
                .add(new CommunityCard(userID,"",messageText,"",0,0,false))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        firebaseFirestore.collection("community-chat").document(documentReference.getId())
                                .update("messageID",documentReference.getId());
                        Log.d(TAG, "Document successfully created!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error creating document", e);
                    }
                });
    }

}
