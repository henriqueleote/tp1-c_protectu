package cm.protectu.Alarm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Locale;

import cm.protectu.MainActivity;
import cm.protectu.Map.FilterMapFragment;
import cm.protectu.Map.MapFragment;
import cm.protectu.R;

public class AlarmFragment extends AlertDialog {

    private AlarmClass alarm;

    public AlarmFragment(@NonNull Context context) {
        super(context);
    }

    public AlarmFragment(@NonNull Context context, AlarmClass alarmClass) {
        super(context);
        this.alarm = alarmClass;
    }

    private static final String TAG = MainActivity.class.getName();

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        View view = getLayoutInflater().inflate(R.layout.fragment_alarm, null);
        this.setView(view);

        ImageView imageView = view.findViewById(R.id.imageAlarm);
        TextView message = view.findViewById(R.id.alarmMessage);
        TextView subMessage = view.findViewById(R.id.alarmSubMessage);
        Button mapButton = view.findViewById(R.id.mapButton);
        AlarmFragment dialog = this;

                message.setText(new SimpleDateFormat("h:mm a", Locale.getDefault()).format(alarm.getTime())  + " " + alarm.getMessage());
        subMessage.setText(alarm.getSubMessage());

        if(!alarm.isAlarm()){
            imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.green_circle_alarm));
            mapButton.setVisibility(View.GONE);
        }else imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.red_circle_alarm));

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapFragment frag = (MapFragment) MainActivity.currentFragment;
                FilterMapFragment.filterBunker();
                frag.loadFilteredPins();
                dialog.dismiss();
            }
        });

        super.onCreate(savedInstanceState);
    }

}
