package cm.protectu.News;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.R;

public class NewPostNewsFragment extends BottomSheetDialogFragment {
    private EditText message, title;
    private Button createButton;
    private ImageView closeButton, upLoadedImage;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage storage;
    private String photoPath;
    private Uri imguri;
    private String firebaseUrl;
    private File photoFile;
    private NewsFragment newsFragment;

    //TAG for debug logs
    private static final String TAG = AuthActivity.class.getName();

    public NewPostNewsFragment(NewsFragment newsFragment) {
        this.newsFragment = newsFragment;
        this.photoFile = null;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_add_news, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        //Link the view objects with the XML
        closeButton = view.findViewById(R.id.closeMessage);
        message = view.findViewById(R.id.message);
        title = view.findViewById(R.id.title);
        createButton = view.findViewById(R.id.createNewMessageButton);
        upLoadedImage = view.findViewById(R.id.uploadImage);

        firebaseFirestore = FirebaseFirestore.getInstance();

        /**
         * When the dialog is canceled it goes back to the community fragment and re-updates the messages
         */
        getDialog().setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                newsFragment.newsCardsData();
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

        //TODO: VIDEO PLAYER
        /**
         * Will fetch an image from the user's downloads
         */
        upLoadedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.pick_image_intent_text)
                        .setPositiveButton(R.string.gallery, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                imageFileChooser();
                            }
                        })
                        .setNegativeButton(R.string.camera, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dispatchTakePictureIntent();
                            }
                        });
                // Create the AlertDialog object and return it
                builder.show();
            }
        });

        /**
         * Creates the new message and returns to the fragment
         */
        createButton.setOnClickListener(new View.OnClickListener() {
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
                                        createMessage(message.getText().toString().trim()
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

        //Returns the view
        return view;
    }

    public void imageFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    /**
     * This method is responsible for creating aN Intent to take the photograph and if successful transform the image file into Uri
     */
    public void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imguri = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imguri);
                startActivityForResult(takePictureIntent, 1);
            }

        }
    }

    //TODO: REVIEW THE DOCUMENTATION
    /**
     * This method create a file for the photo, with the chosen directory for the file
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        photoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * This method puts the photo inside the image view and sends it to storage, with a dialog while loading the image
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {

            if(data.getData() != null){
                imguri = data.getData();
                Log.d(TAG, "Value: " + data.getData());
            }

            Glide.with(getActivity())
                    .load(imguri)
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
                            Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    /**
     * This method will create a message with what the user writes and with an image if the user transfers it,
     * when creating the message sends it to the database
     * @param newsText text written by the user
     * @param pubID
     */
    public void createMessage(String newsText,String pubID, String newsTitle, String pubURL){
        // Message's field check
        if (TextUtils.isEmpty(newsText)) {
            message.setError("Please enter a message");
            message.requestFocus();
            return;
        }

        if (firebaseUrl == null){
            firebaseUrl = "";
        }

        DocumentReference documentReference = firebaseFirestore.collection("news").document();
        documentReference.set(new NewsCardClass(newsTitle,newsText,documentReference.getId(),firebaseUrl,pubURL,pubID, new Date()))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
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

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }
}
