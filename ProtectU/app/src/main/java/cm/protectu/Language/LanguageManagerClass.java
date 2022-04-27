package cm.protectu.Language;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.fragment.app.Fragment;

import java.util.Locale;

public class LanguageManagerClass {
    private Fragment ct;

    public LanguageManagerClass(Fragment ct) {
        this.ct = ct;
    }

    public void updateResorce(String code) {
        Locale locale = new Locale(code);
        Locale.setDefault(locale);
        Resources resources = ct.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
}
