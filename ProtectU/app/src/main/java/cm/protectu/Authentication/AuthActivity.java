package cm.protectu.Authentication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cm.protectu.MainActivity;
import cm.protectu.R;

public class AuthActivity extends AppCompatActivity {

    //Buttons
    private Button signInBtn, signUpBtn;

    //TextView
    private TextView anonymousButton;

    //Firebase Authentication
    private FirebaseAuth mAuth;

    //Firebase User
    private FirebaseUser user;

    private String[] PERMISSIONS;

    private static final String TAG = AuthActivity.class.getName();

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.Theme_App_SplashScreen);
        super.onCreate(savedInstanceState);

        //Link the layout to the activity
        setContentView(R.layout.activity_auth);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        PERMISSIONS = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.FOREGROUND_SERVICE
        };

        if (!hasPermissions(AuthActivity.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(AuthActivity.this, PERMISSIONS, 1);
        }


        //Check if has stored session, if true, redirects to the App if not, kills the prior session
        if (mAuth.getCurrentUser() != null) {
            if (mAuth.getCurrentUser().isAnonymous()) {
                mAuth.getCurrentUser().delete();
                mAuth.signOut();
            }else{
                startActivity(new Intent(this, MainActivity.class));
            }
        }


        //Link the view objects with the XML
        signInBtn = findViewById(R.id.signInButton);
        signUpBtn = findViewById(R.id.signUpButton);
        anonymousButton = findViewById(R.id.anonymousButton);

        //On click opens the Login form sheet
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment bottomLogin = new LoginFragment();
                bottomLogin.show(getSupportFragmentManager(), bottomLogin.getTag());
            }
        });

        //On click opens the register form sheet
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterFragment bottomRegister = new RegisterFragment();
                bottomRegister.show(getSupportFragmentManager(), bottomRegister.getTag());
            }
        });

        anonymousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUserAnonymous(); //this is the correct code
            }
        });
    }

    //TODO THE LOGO HAS A WEIRD BORDER

    public void loginUserAnonymous() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.logging_in));
        progressDialog.show();
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            progressDialog.dismiss();
                            startActivity(new Intent(AuthActivity.this, MainActivity.class));
                            Log.d(TAG, "Data: " + user.getUid() + "\nEmail: " + user.getEmail());
                        } else {
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(AuthActivity.this, getString(R.string.registration_failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean hasPermissions(Context context, String... PERMISSIONS) {

        if (context != null && PERMISSIONS != null)
            for (String permission : PERMISSIONS)
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                    return false;

                return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {

    }
}
