package cm.protectu.Authentication;

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
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cm.protectu.MainActivity;
import cm.protectu.R;

public class ForgotPasswordFragment extends BottomSheetDialogFragment {

    //Button
    private Button sendEmailBtn;

    //ImageView
    private ImageView closeBtn;

    //EditText
    private EditText emailText;

    //Firebase Authentication
    private FirebaseAuth mAuth;

    //TAG for debug logs
    private static final String TAG = AuthActivity.class.getName();

    public ForgotPasswordFragment() {
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.forgot_password_bottom, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        //Check if has stored session, if true, redirects to the App
        if(mAuth.getCurrentUser() != null){
            getActivity().finish();
            startActivity(new Intent(getActivity(), MainActivity.class));
        }

        //Link the view objects with the XML
        closeBtn = view.findViewById(R.id.close);
        sendEmailBtn = view.findViewById(R.id.sendEmailBtn);
        emailText = view.findViewById(R.id.emailText);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().cancel();
            }
        });

        sendEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword(emailText.getText().toString());
            }
        });

        return view;
    }

    public void forgotPassword(String email){

        // E-mail's field check
        if (TextUtils.isEmpty(email)) {
            emailText.setError(getResources().getString(R.string.error_enter_your_mail));
            emailText.requestFocus();
            return;
        }

        // Email's string check
        if(!isEmailValid(email)){
            emailText.setError(getResources().getString(R.string.error_email_not_valid));
            emailText.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), getString(R.string.email_sent), Toast.LENGTH_LONG).show();
                        }else{
                            try {
                                throw task.getException();
                            }catch (FirebaseAuthInvalidUserException e){
                                emailText.setError(getResources().getString(R.string.error_invalid_email));
                                emailText.requestFocus();
                            }catch (Exception e){
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


    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }
}
