package cm.protectu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends AppCompatActivity {

    //Buttons
    private Button signInBtn, signUpBtn;

    //Firebase Authentication
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null)
            startActivity(new Intent(this, MainActivity.class));

        signInBtn = findViewById(R.id.signInButton);
        signUpBtn = findViewById(R.id.signUpButton);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment bottomLogin = new LoginFragment();
                bottomLogin.show(getSupportFragmentManager(), bottomLogin.getTag());
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterFragment bottomRegister = new RegisterFragment();
                bottomRegister.show(getSupportFragmentManager(), bottomRegister.getTag());
            }
        });
    }
}
