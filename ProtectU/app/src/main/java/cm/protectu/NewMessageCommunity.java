package cm.protectu;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Date;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;


public class NewMessageCommunity extends BottomSheetDialogFragment {

    private EditText message;
    private Button createButton;
    private ImageView closeButton, upLoadedImage;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage storage;
    private Uri imguri;
    private String firebaseUrl;
    private CommunityFragment communityFragment;

    //TAG for debug logs
    private static final String TAG = AuthActivity.class.getName();

    public NewMessageCommunity(CommunityFragment communityFragment) {
        this.communityFragment = communityFragment;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_add_message, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

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
                imageFileChooser();
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

    public void imageFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null & data.getData() != null) {

            imguri = data.getData();
            Picasso.get()
                    .load(imguri)
                    .centerCrop()
                    .fit()
                    .transform(new CropCircleTransformation())
                    .into(upLoadedImage);


            ProgressDialog mDialog = new ProgressDialog(getActivity());
            //TODO UPDATE WITH STATUS
            mDialog.setMessage("Loading image...");
            mDialog.setCancelable(false);
            mDialog.show();

            StorageReference storageReference = storage.getInstance().getReference();
            final StorageReference imageRef = storageReference.child("community-chat/" + mAuth.getCurrentUser().getUid() + "-" + System.currentTimeMillis());
            UploadTask uploadTask = imageRef.putFile(imguri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Task<Uri> downloadUrl = imageRef.getDownloadUrl();
                    downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            firebaseUrl = uri.toString();
                            Log.d(TAG, "Link: " + firebaseUrl);
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            mDialog.dismiss();
                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    });
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
                .add(new CommunityCard(userID,"",messageText, firebaseUrl,new Date(),0,0,false))
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
