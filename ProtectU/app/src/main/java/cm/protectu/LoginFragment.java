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
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class LoginFragment extends BottomSheetDialogFragment {

    private ImageView closeBtn;
    private Button signIn;
    private EditText emailText, passwordText;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private static final String TAG = AuthActivity.class.getName();

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    //setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

        //Initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        View view = inflater.inflate(R.layout.login_bottom, container, false);

        closeBtn = view.findViewById(R.id.close);
        signIn = view.findViewById(R.id.signInButton);
        emailText = view.findViewById(R.id.emailText);
        passwordText = view.findViewById(R.id.passwordText);

        //Quando é carregado no fechar, fecha a página
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });



        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailText.getText().toString().toLowerCase(Locale.ROOT);
                String password = passwordText.getText().toString();

                loginUser(email, password);
            }
        });

        return view;
    }

    public void loginUser(String email, String password){

        //TODO - Test and change the toast to Focus
        // E-mail's field check
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getActivity().getApplicationContext(), "Please enter email!!", Toast.LENGTH_LONG).show();
            //focus
            return;
        }

        //TODO - Test and change the toast to Focus
        // Password's field check
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getActivity().getApplicationContext(),"Please enter password!!",Toast.LENGTH_LONG).show();
            //focus
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            Log.d(TAG, "Dados: " + user.getEmail());

                            Toast.makeText(getActivity(), "Registration successful!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        } else {

                            Log.d(TAG, task.getException().toString());
                            Toast.makeText(getActivity(), "Something happened, please try again", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }


}
