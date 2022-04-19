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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class LoginFragment extends BottomSheetDialogFragment {

    //ImageView
    private ImageView closeBtn;

    //Button
    private Button signInBtn;

    //TextView
    private TextView forgotPasswordBtn;

    //EditText
    private EditText emailText, passwordText;

    //Firebase Authentication
    private FirebaseAuth mAuth;

    //Firebase User
    private FirebaseUser user;

    //TAG for debug logs
    private static final String TAG = AuthActivity.class.getName();

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.login_bottom, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        //Link the view objects with the XML
        closeBtn = view.findViewById(R.id.close);
        signInBtn = view.findViewById(R.id.signInButton);
        emailText = view.findViewById(R.id.emailText);
        passwordText = view.findViewById(R.id.passwordText);
        forgotPasswordBtn = view.findViewById(R.id.forgotPasswordBtn);

        //TODO Close the one in the back
        //If users forgot the password, sends a recover link
        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForgotPasswordFragment bottomForgot = new ForgotPasswordFragment();
                bottomForgot.show(getParentFragmentManager(), bottomForgot.getTag());
            }
        });

        //On click closes the form sheet
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });

        //On click starts the login process
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUserWithCredentials(emailText.getText().toString().toLowerCase(Locale.ROOT),
                        passwordText.getText().toString());
            }
        });

        //Returns the view
        return view;
    }

    //User login with credentials
    public void loginUserWithCredentials(String email, String password){

        // E-mail's field check
        if (TextUtils.isEmpty(email)) {
            emailText.setError(getString(R.string.error_enter_your_mail));

            emailText.requestFocus();
            return;
        }

        // Password's field check
        if (TextUtils.isEmpty(password)) {
            passwordText.setError(getString(R.string.error_enter_your_password));
            passwordText.requestFocus();
            return;
        }

        //Firebase Authentication function to login the user via email and password, with success listeners
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //Set the Firebase User to the just logged in one
                            user = mAuth.getCurrentUser();

                            //Show success message and redirects to the app
                            Toast.makeText(getActivity(), getString(R.string.registration_sucessful), Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        } else {
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthInvalidCredentialsException e) { //Error if password doesnt match the account //TODO error appears when email is wrong
                                passwordText.setError(getString(R.string.error_invalid_password));
                                passwordText.requestFocus();
                                passwordText.setText("");
                            }catch(FirebaseAuthInvalidUserException e){ //Error if email does not exists
                                emailText.setError(getString(R.string.error_invalid_email));
                                emailText.requestFocus();
                            }catch(Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    }
                });
    }

}
