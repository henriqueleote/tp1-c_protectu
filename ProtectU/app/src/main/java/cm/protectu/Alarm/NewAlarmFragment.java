package cm.protectu.Alarm;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import cm.protectu.Authentication.AuthActivity;
import cm.protectu.R;

public class NewAlarmFragment extends BottomSheetDialogFragment {

    private AlarmClass alarm;

    private Spinner alarmType;

    private EditText message;

    private EditText subMessage;

    private ImageView closeButton;

    private Button createAlarm;

    private boolean alarmT;

    //Firebase Authentication
    private FirebaseFirestore firebaseFirestore;

    //TAG for debug logs
    private static final String TAG = AuthActivity.class.getName();

    public NewAlarmFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Link the layout to the Fragment
        View view = inflater.inflate(R.layout.fragment_new_alarm, container, false);

        //Initialize Firebase Authentication
        closeButton = view.findViewById(R.id.closeNewAlarm);
        message = view.findViewById(R.id.messageAlarm);
        alarmType = view.findViewById(R.id.alarmType);
        subMessage = view.findViewById(R.id.subMessageAlarm);
        createAlarm = view.findViewById(R.id.createAlarm);
        firebaseFirestore = FirebaseFirestore.getInstance();

        //On click closes the form sheet
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });
        alarmType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                alarmT = parent.getItemAtPosition(position).toString().equalsIgnoreCase("Red");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                alarmT = false;
            }
        });
        createAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAlarm(message.getText().toString().trim()
                        ,subMessage.getText().toString().trim());
                getDialog().cancel();
            }
        });

        //Returns the view
        return view;

    }
    public void createNewAlarm(String mes,String subMes){
        if (TextUtils.isEmpty(mes)) {
            message.setError(getString(R.string.error_number_of_people));
            message.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(subMes)) {
            subMessage.setError(getString(R.string.error_number_of_people));
            subMessage.requestFocus();
            return;
        }
        firebaseFirestore.collection("air-alarm")
                .add(new AlarmClass(alarmT, new Date(), mes, subMes))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Document successfully created!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error creating document", e);
                    }
                });
    }


    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }
}
