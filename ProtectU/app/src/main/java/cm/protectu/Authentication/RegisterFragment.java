package cm.protectu.Authentication;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cm.protectu.Community.CommunityCard;
import cm.protectu.MainActivity;
import cm.protectu.R;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class RegisterFragment extends BottomSheetDialogFragment {

    //ImageView
    private ImageView closeBtn;

    //Button
    private Button signUpBtn;

    //EditText
    private EditText nameText, surnameText,contactText, emailText, passwordText, passwordConfirmText, securityCodeText;

    //CountryCodePicker
    private CountryCodePicker countryCodePicker;

    //Firebase Authentication
    private FirebaseAuth mAuth;

    //Firebase User
    private FirebaseUser user;

    //Firebase Firestore Database
    private FirebaseFirestore firebaseFirestore;

    private CheckBox passwordCheckBox;

    List<UserType> users;

    String userType;

    //TAG for debug logs
    private static final String TAG = AuthActivity.class.getName();

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.register_bottom, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        //Initialize Firebase Firestore Database
        firebaseFirestore = FirebaseFirestore.getInstance();

        //Link the view objects with the XML
        closeBtn = view.findViewById(R.id.close);
        signUpBtn = view.findViewById(R.id.signUpButton);
        nameText = view.findViewById(R.id.nameText);
        surnameText = view.findViewById(R.id.surnameText);
        contactText = view.findViewById(R.id.contactText);
        emailText = view.findViewById(R.id.emailText);
        passwordText = view.findViewById(R.id.passwordText);
        passwordConfirmText = view.findViewById(R.id.passwordText2);
        securityCodeText = view.findViewById(R.id.securityCodeText);
        passwordCheckBox = view.findViewById(R.id.passwordCheckBox);
        countryCodePicker = view.findViewById(R.id.contactPicker);

        users = new ArrayList<>();

        passwordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passwordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordConfirmText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordCheckBox.setText("Hide password");
                } else {
                    passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordConfirmText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordCheckBox.setText("Show password");
                }
            }
        });

        getAccountPin();


        //On click closes the form sheet
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });

        //On click starts the register process
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser(nameText.getText().toString().trim(),
                        surnameText.getText().toString().trim(),
                        contactText.getText().toString().trim(),
                        emailText.getText().toString().trim().toLowerCase(Locale.ROOT),
                        passwordText.getText().toString(), passwordConfirmText.getText().toString(), securityCodeText.getText().toString().trim());
            }
        });

        //Returns the view
        return view;
    }

    public void getAccountPin(){
        firebaseFirestore.collection("authorities")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //TODO FINISH THIS
                                users = new ArrayList<>();
                                List<Map<String, String>> usersData = (List<Map<String, String>>) document.get("users");
                                usersData.forEach(user -> {
                                    //array de mapas
                                    Log.d(TAG, "each -> " +user.get("code") + " " + user.get("logo") + " " + user.get("type"));
                                    users.add(new UserType(user.get("code"), user.get("logo"), user.get("type")));
                                });
                            }
                            checkPin("oi");
                        } else {
                            //Shows the error
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @SuppressLint("NewApi")
    public String checkPin(String securityPin){
        String type = "";
        for(int i = 0 ; i < users.size(); i++){
            Log.d(TAG, "Code: " + securityPin);
            Log.d(TAG, "User: " + users.get(i).getCode());
            if (users.get(i).getCode().equals(securityPin)){
                type = users.get(i).getType();
                break;
            }
            else
                type = "user";
        }
        return type;
    }

    //User register
    public void registerUser(String name, String surname,String contact, String email, String password, String confirmPassword, String securityPin) {

        // First name field check
        if (TextUtils.isEmpty(name)) {
            nameText.setError(getResources().getString(R.string.error_enter_your_name));  //Apresentar um erro
            nameText.requestFocus();
            return;
        }

        // Last name field check
        if (TextUtils.isEmpty(surname)) {
            surnameText.setError(getResources().getString(R.string.error_enter_your_surname));  //Apresentar um erro
            surnameText.requestFocus();
            return;
        }

        //TODO: add string check
        // Contact's field check
        if (TextUtils.isEmpty(contact)) {
            contactText.setError(getResources().getString(R.string.error_invalid_contact));  //Apresentar um erro
            contactText.requestFocus();
            return;
        }

        // E-mail's field check
        if (TextUtils.isEmpty(email)) {
            emailText.setError(getResources().getString(R.string.error_enter_your_mail));  //Apresentar um erro
            emailText.requestFocus();
            return;
        }

        // Password's field check
        if (TextUtils.isEmpty(password)) {
            passwordText.setError(getResources().getString(R.string.error_enter_your_password));  //Apresentar um erro
            passwordText.requestFocus();
            return;
        }


        // Email's string check
        if(!isEmailValid(email)){
            emailText.setError(getResources().getString(R.string.error_email_not_valid));  //Apresentar um erro
            emailText.requestFocus();
            return;
        }

        //Check if both passwords match
        if(!confirmPassword.equals(password)){
            passwordConfirmText.setError(getResources().getString(R.string.error_password_dont_match));  //Apresentar um erro
            passwordConfirmText.requestFocus();
            return;
        }

        if(!TextUtils.isEmpty(securityPin)){
            userType = checkPin(securityPin);
        }else{
            userType = "user";
        }
        //TODO CHECK IF THE LIST OR THE STRING IS NOT EMPTY, WHICH MEANS THAT THE USER CONFIG WAS DONE

        //Check if the email is already registered in any other authentication  provider
        if (mAuth.fetchSignInMethodsForEmail(email).isSuccessful()) {
            emailText.setError(getResources().getString(R.string.error_email_in_use));
            emailText.requestFocus();
            return;
        } else{
            ProgressDialog mDialog = new ProgressDialog(getActivity());
            mDialog.setMessage("Registering...");
            mDialog.setCancelable(false);
            mDialog.show();
            //TODO Test with an existing email to see the error, and in Login, test with one that doesnt exist
            //Firebase Authentication function to register the user via email and password, with success listeners
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                //Set the Firebase User to the just logged in one
                                user = mAuth.getCurrentUser();

                                // TODO - Add date of register
                                String url = "null";
                                //Create HashMap object with the user's profile data
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("uid", user.getUid());
                                userData.put("firstName", name);
                                userData.put("lastName", surname);
                                userData.put("email", email);
                                userData.put("phoneNumber", countryCodePicker.getSelectedCountryCodeWithPlus() +" "+ contact);
                                userData.put("imageURL", url);
                                userData.put("userType", userType);

                                //Inserts in Firestore the user data with the correspondent user ID from Authentication
                                firebaseFirestore.collection("users").document(user.getUid())
                                        .set(userData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot with the ID: " + user.getUid());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error writing document", e);
                                            }
                                        });

                                //Show success message and redirects to the app
                                mDialog.dismiss();
                                startActivity(new Intent(getActivity(), MainActivity.class));
                            } else {
                                mDialog.dismiss();
                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthWeakPasswordException e) {
                                    passwordText.setError(getString(R.string.error_weak_password));
                                    passwordText.requestFocus();
                                }catch(FirebaseAuthUserCollisionException e) {
                                    emailText.setError(getResources().getString(R.string.error_email_in_use));
                                    emailText.requestFocus();
                                } catch(Exception e) {
                                    Log.e(TAG, e.getMessage());
                                }
                            }
                        }
                    });
        }

    }

    //Method that checks if the email's string is valid within a certain pattern
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}