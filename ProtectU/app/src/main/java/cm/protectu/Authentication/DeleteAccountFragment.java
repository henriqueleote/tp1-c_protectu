package cm.protectu.Authentication;

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
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cm.protectu.MainActivity;
import cm.protectu.R;

public class DeleteAccountFragment extends BottomSheetDialogFragment {

    //Button
    private Button deleteBtn;

    //ImageView
    private ImageView closeBtn;

    //EditText
    private EditText passwordText;

    //Firebase Authentication
    private FirebaseAuth mAuth;
    private FirebaseAuth _mAuth;

    private CheckBox passwordCheckBox;


    //TAG for debug logs
    private static final String TAG = AuthActivity.class.getName();

    public DeleteAccountFragment() {
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.delete_account_bottom, container, false);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        _mAuth = mAuth;

        //Link the view objects with the XML
        closeBtn = view.findViewById(R.id.close);
        passwordText = view.findViewById(R.id.passwordText);
        deleteBtn = view.findViewById(R.id.deleteBtn);
        passwordCheckBox = view.findViewById(R.id.passwordCheckBox);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().cancel();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccount(passwordText.getText().toString());
            }
        });

        passwordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passwordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordCheckBox.setText(getString(R.string.hide_password));
                } else {
                    passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordCheckBox.setText(getString(R.string.show_password));
                }
            }
        });

        return view;
    }

    public void deleteAccount(String password){

        // Password field check
        if (TextUtils.isEmpty(password)) {
            passwordText.setError(getResources().getString(R.string.error_enter_your_password));
            passwordText.requestFocus();
            return;
        }

        _mAuth.signInWithEmailAndPassword(mAuth.getCurrentUser().getEmail(), password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            if(_mAuth.getCurrentUser() != null) {
                                FirebaseUser user = _mAuth.getCurrentUser();
                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            startActivity(new Intent(getActivity(), AuthActivity.class));
                                            Toast.makeText(getActivity(), getString(R.string.see_you_next_time), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            passwordText.setError(getResources().getString(R.string.error_invalid_password));
                            passwordText.requestFocus();
                        }
                    }
                });
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }
}
