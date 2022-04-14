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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterFragment extends BottomSheetDialogFragment {

    private ImageView closeBtn;
    private Button signUpBtn;
    private EditText nameText, surnameText, emailText, passwordText;


    //Firebase Authentication
    private FirebaseAuth mAuth;

    private FirebaseUser user;
    FirebaseFirestore firebaseFirestore;

    private static final String TAG = AuthActivity.class.getName();

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

        //Initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        //Initialize firebase firestore
        firebaseFirestore = FirebaseFirestore.getInstance();

        //Initialize the layout
        View view = inflater.inflate(R.layout.register_bottom, container, false);

        closeBtn = view.findViewById(R.id.close);
        signUpBtn = view.findViewById(R.id.signUpButton);
        nameText = view.findViewById(R.id.nameText);
        surnameText = view.findViewById(R.id.surnameText);
        emailText = view.findViewById(R.id.emailText);
        passwordText = view.findViewById(R.id.passwordText);


        //Quando é carregado no fechar, fecha a página
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = nameText.getText().toString().trim();
                String surname = surnameText.getText().toString().trim();
                String email = emailText.getText().toString().trim().toLowerCase(Locale.ROOT);
                String password = passwordText.getText().toString();

                registerUser(name, surname, email, password);
            }
        });

        return view;
    }

    public void registerUser(String name, String surname, String email, String password) {

        //TODO - Test and change the toast to Focus
        // E-mail's field check
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getActivity().getApplicationContext(), "Please enter name", Toast.LENGTH_LONG).show();
            //focus
            return;
        }

        //TODO - Test and change the toast to Focus
        // E-mail's field check
        if (TextUtils.isEmpty(surname)) {
            Toast.makeText(getActivity().getApplicationContext(), "Please enter surname", Toast.LENGTH_LONG).show();
            //focus
            return;
        }

        //TODO - Test and change the toast to Focus
        // E-mail's field check
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getActivity().getApplicationContext(), "Please enter email", Toast.LENGTH_LONG).show();
            //focus
            return;
        }

        //TODO - Test and change the toast to Focus
        // Password's field check
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getActivity().getApplicationContext(),"Please enter password",Toast.LENGTH_LONG).show();
            //focus
            return;
        }

        //TODO - Test and change the toast to Focus
        // Email's string check
        if(!isEmailValid(email)){
            Toast.makeText(getActivity().getApplicationContext(),"Email not valid",Toast.LENGTH_LONG).show();
            //focus
            return;
        }

        if (mAuth.fetchSignInMethodsForEmail(email).isSuccessful()) {
            Log.d(TAG, "E-mail already in use");
        } else
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                user = mAuth.getCurrentUser();
                                if(user != null){

                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("uid", user.getUid());
                                    userData.put("firstName", name);
                                    userData.put("lastName", surname);

                                    firebaseFirestore.collection("users")
                                        .add(userData)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "DocumentSnapshot with the ID: " + documentReference.getId());
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Erro");
                                        }
                                    });
                                }
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

    //verifica se o email é valido
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
