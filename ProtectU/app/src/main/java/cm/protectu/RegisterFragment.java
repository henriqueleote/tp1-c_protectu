package cm.protectu;

import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterFragment extends BottomSheetDialogFragment {

    //ImageView
    private ImageView closeBtn;

    //Button
    private Button signUpBtn;

    //EditText
    private EditText nameText, surnameText, emailText, passwordText, passwordConfirmText;

    //Firebase Authentication
    private FirebaseAuth mAuth;

    //Firebase User
    private FirebaseUser user;

    //Firebase Firestore Database
    private FirebaseFirestore firebaseFirestore;

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
        emailText = view.findViewById(R.id.emailText);
        passwordText = view.findViewById(R.id.passwordText);
        passwordConfirmText = view.findViewById(R.id.passwordText2);


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
                registerUser(nameText.getText().toString().trim()
                        , surnameText.getText().toString().trim(),
                        emailText.getText().toString().trim().toLowerCase(Locale.ROOT),
                        passwordText.getText().toString(), passwordConfirmText.getText().toString());
            }
        });

        //Returns the view
        return view;
    }

    //User register
    public void registerUser(String name, String surname, String email, String password, String confirmPassword) {

        // E-mail's field check
        if (TextUtils.isEmpty(name)) {
            nameText.setError(getResources().getString(R.string.error_enter_your_name));  //Apresentar um erro
            nameText.requestFocus();
            return;
        }

        // E-mail's field check
        if (TextUtils.isEmpty(surname)) {
            surnameText.setError(getResources().getString(R.string.error_enter_your_surname));  //Apresentar um erro
            surnameText.requestFocus();
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

        //Check if the email is already registered in any other authentication  provider
        if (mAuth.fetchSignInMethodsForEmail(email).isSuccessful()) {
            emailText.setError(getResources().getString(R.string.error_email_in_use));
            emailText.requestFocus();
            return;
        } else
            //TODO Test with an existing email to see the error, and in Login, test with one that doesnt exist
            //Firebase Authentication function to register the user via email and password, with success listeners
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                //Set the Firebase User to the just logged in one
                                user = mAuth.getCurrentUser();

                                //Create HashMap object with the user's profile data
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("uid", user.getUid());
                                    userData.put("firstName", name);
                                    userData.put("lastName", surname);

                                //Firebase Firestore function to store data with objects, with success listeners
                                    firebaseFirestore.collection("users")
                                        .add(userData)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "DocumentSnapshot with the ID: " + documentReference.getId());
                                        }
                                    });

                                //Show success message and redirects to the app
                                Toast.makeText(getActivity(), "Registration successful!", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getActivity(), MainActivity.class));
                            } else {
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

    //Method that checks if the email's string is valid within a certain pattern
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
