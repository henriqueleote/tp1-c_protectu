package cm.protectu;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

import cm.protectu.Alarm.AlarmClass;

public class NotificationService extends Service {
    //Firebase Authentication
    private FirebaseFirestore firebaseFirestore;
    String TAG = "Notification service";
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && new PrefManager(this).getNotifications().equals("true")) {
            startForeground(8071987, triggerNotification("ProtectU", getResources().getString(R.string.pay_attention)));
        } else stopForeground(true);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("air-alarm")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        Query q = firebaseFirestore.collection("air-alarm").orderBy("time");
                        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult().getDocuments().get(task.getResult().getDocuments().size() - 1);
                                    AlarmClass alarmClass = document.toObject(AlarmClass.class);
                                    long cur = System.currentTimeMillis();
                                    if(alarmClass.getTime().before((new Date(cur + 120000))) && alarmClass.getTime().after((new Date(cur - 120000)))){
                                        triggerNotification(alarmClass.getMessage(), alarmClass.getSubMessage());
                                    }
                                } else {
                                    Log.d(TAG, "Error");
                                }
                            }
                        });
                    }
                });

        return START_STICKY;
    }


    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        if(new PrefManager(this).getNotifications().equals("true")){
            Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
            sendBroadcast(broadcastIntent);
        }

    }

    public Notification triggerNotification(String title, String content) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("sync_rv_notification", "ReservedValuesSync", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "sync_rv_notification")
                        .setContentTitle(title)
                        .setSmallIcon(R.drawable.icon_24dp)
                        .setContentText(content)
                        .setAutoCancel(false)
                        .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0,
                                new Intent(getApplicationContext(), MainActivity.class), 0))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(8071987, notificationBuilder.build());
        return notificationBuilder.build();
    }

}
