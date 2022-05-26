package cm.protectu.Community;

import static android.app.Activity.RESULT_OK;

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
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.R;


public class NewMessageCommunityFragment extends BottomSheetDialogFragment {

    private EditText message;
    private Button createButton, videoButton, imageButton;
    private ImageView closeButton, upLoadedImage;
    private VideoView videoView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage storage;
    private String path;
    private Uri imguri;
    private String firebaseUrl;
    private File file;
    private CommunityFragment communityFragment;
    private boolean isVideo;
    private RelativeLayout frameLayout;

    //TAG for debug logs
    private static final String TAG = AuthActivity.class.getName();

    public NewMessageCommunityFragment(CommunityFragment communityFragment) {
        this.communityFragment = communityFragment;
        this.file = null;
        this.isVideo = false;
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
        videoButton = view.findViewById(R.id.video);
        imageButton = view.findViewById(R.id.image);
        videoView = view.findViewById(R.id.uploadVideo);
        frameLayout = view.findViewById(R.id.frameVideo);

        firebaseFirestore = FirebaseFirestore.getInstance();

        /**
         * When the dialog is canceled it goes back to the community fragment and re-updates the messages
         */
        getDialog().setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                communityFragment.communityCardsData(null);
            }
        });

        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.setVisibility(View.VISIBLE);
                frameLayout.setVisibility(View.VISIBLE);
                upLoadedImage.setVisibility(View.GONE);

                frameLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dispatchTakeVideoIntent();

                    }
                });
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.setVisibility(View.GONE);
                frameLayout.setVisibility(View.GONE);
                upLoadedImage.setVisibility(View.VISIBLE);
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
                dispatchTakePictureIntent();

            }
        });



        /**
         * Creates the new message and returns to the fragment
         */
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createMessage(message.getText().toString().trim()
                        , mAuth.getUid());
                getDialog().cancel();
            }
        });

        //Returns the view
        return view;
    }

    /**
     * This method is responsible for creating aN Intent to take the photograph and if successful transform the image file into Uri
     */
    public void dispatchTakePictureIntent() {
        isVideo = false;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                file = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (file != null) {
                imguri = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.fileprovider",
                        file);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imguri);
                startActivityForResult(takePictureIntent, 1);
            }

        }
    }

    public void dispatchTakeVideoIntent() {
        isVideo = true;
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                file = createVideoFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (file != null) {
                imguri = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.fileprovider",
                        file);
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imguri);
                takeVideoIntent.putExtra(android.provider.MediaStore.EXTRA_DURATION_LIMIT, 3);
                takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                takeVideoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 12582912L); //12*1024*1024=12MiB

                startActivityForResult(takeVideoIntent, 1);
            }

        }
    }

    //TODO: REVIEW THE DOCUMENTATION

    /**
     * This method create a file for the photo, with the chosen directory for the file
     *
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
        path = image.getAbsolutePath();
        return image;
    }

    private File createVideoFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoFileName = "mp4_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File video = File.createTempFile(
                videoFileName,
                ".mp4",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        path = video.getAbsolutePath();
        return video;
    }

    /**
     * This method puts the photo inside the image view and sends it to storage, with a dialog while loading the image
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ProgressDialog mDialog = new ProgressDialog(getActivity());

        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (!isVideo) {
                mDialog.setMessage("Loading image...");

                Glide.with(getActivity())
                        .load(imguri)
                        .into(upLoadedImage);
            } else {
                mDialog.setMessage("Loading video...");

                videoView.setVideoURI(imguri);
                MediaController mediaController = new MediaController(getContext());
                videoView.setMediaController(mediaController);
                mediaController.setAnchorView(videoView);
                videoView.start();
            }

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
     *
     * @param messageText text written by the user
     * @param userID
     */
    public void createMessage(String messageText, String userID) {
        // Message's field check
        if (TextUtils.isEmpty(messageText)) {
            message.setError("Please enter a message");
            message.requestFocus();
            return;
        }

        if (firebaseUrl == null) {
            firebaseUrl = "";
        }

        DocumentReference documentReference = firebaseFirestore.collection("community-chat").document();
        documentReference.set(new CommunityCard(userID, documentReference.getId(), messageText, firebaseUrl, new Date(), 0, 0, false, isVideo))
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
