package cm.protectu;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    // Shared preferences file name
    private static final String PREF_NAME = "ProtectU-welcome";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String LANGUAGE = "us";
    private static final String NOTIFICATIONS = "true";


    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public void setLanguage(String language){
        editor.putString(LANGUAGE, language);
        editor.commit();
    }

    public String getLanguage(){
        return pref.getString(LANGUAGE, "us");
    }

    public void setNotifications(String notifications){
        editor.putString(NOTIFICATIONS, notifications);
        editor.commit();
    }

    public String getNotifications(){
        return pref.getString(NOTIFICATIONS, "true");
    }
}
