package cm.protectu;

import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;


public class NewMessageCommunity extends BottomSheetDialogFragment {

    private EditText message;
    private Button createButton;
    private ImageView closeButton, upLoadedImage;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String imagePath;
    private CommunityFragment communityFragment;

    //TAG for debug logs
    private static final String TAG = AuthActivity.class.getName();

    public NewMessageCommunity(CommunityFragment communityFragment) {
        this.communityFragment = communityFragment;
        imagePath = "";
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_add_message, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        //Link the view objects with the XML
        closeButton = view.findViewById(R.id.closeMessage);
        message = view.findViewById(R.id.message);
        createButton = view.findViewById(R.id.createNewMessageButton);
        upLoadedImage = view.findViewById(R.id.uploadImage);

        firebaseFirestore = FirebaseFirestore.getInstance();

        /**
         * When the dialog is canceled it goes back to the community fragment and re-updates the messages
         */
        getDialog().setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                communityFragment.communityCardsData();
            }
        });

        /**
         * On click closes the form sheet
         */
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });

        /**
         * Will fetch an image from the user's downloads
         */
        upLoadedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,3);
            }
        });

        /**
         * Creates the new message and returns to the fragment
         */
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data!=null){
            Uri selectedImages = data.getData();
            imagePath = selectedImages.getPath();
            upLoadedImage.setImageURI(selectedImages);
        }
    }

    /**
     * This method will create a message with what the user writes and with an image if the user transfers it,
     * when creating the message sends it to the database
     * @param messageText text written by the user
     * @param userID
     */
    public void createMessage(String messageText,String userID){
        // Message's field check
        if (TextUtils.isEmpty(messageText)) {
            message.setError("Please enter a message");
            message.requestFocus();
            return;
        }

        firebaseFirestore.collection("community-chat")
                .add(new CommunityCard(userID,"",messageText,"",imagePath,new Date(),0,0,false))
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
