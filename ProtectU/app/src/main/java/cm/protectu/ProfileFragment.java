package cm.protectu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class ProfileFragment extends Fragment {

    TextView nameTextView;
    FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;

    private static final String TAG = MainActivity.class.getName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //Initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        //Initialize firebase firestore
        firebaseFirestore = FirebaseFirestore.getInstance();

        nameTextView = view.findViewById(R.id.nameTextView);

        if (mAuth.getCurrentUser() == null) {
            getActivity().finish();
            //verificar o que isto fazia no outro codigo
            //getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            startActivity(new Intent(getActivity(), AuthActivity.class));
        }

        getData();

        return view;

    }

    public void getData(){
        firebaseFirestore.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //nameTextView.setText(document.getData().firstname);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                nameTextView.setText(document.getString("firstName") + document.getString("lastName"));
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
