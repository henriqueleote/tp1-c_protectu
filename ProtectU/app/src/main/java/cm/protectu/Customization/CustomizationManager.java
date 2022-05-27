package cm.protectu.Customization;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import cm.protectu.MainActivity;
import cm.protectu.R;

public class CustomizationManager {

    private static CustomizationManager instance = null;

//    private String selectedTheme;
    private SharedPreferences sharedPreferences;

    private CustomizationManager() {
//        selectedTheme = "";
        sharedPreferences = MainActivity.getAppContext().getSharedPreferences("theme", 0);
    }

    public static CustomizationManager getInstance() {
        if (instance == null) {
            instance = new CustomizationManager();
        }

        return instance;
    }


//    public void loadTheme() {
//        String themeChosen = sharedPreferences.getString("theme", "light");
//        System.out.println("Tema: " + themeChosen);
//        switch (themeChosen.toLowerCase(Locale.ROOT)) {
//            case "dark":
//                ct.setTheme(R.style.Theme_Dark);
//                break;
//            default:
//                ct.setTheme(R.style.Theme_Light);
//
//        }


//        if (selectedTheme.equalsIgnoreCase("dark")) {
//            ct.setTheme(R.style.Theme_Dark);
////                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        } else if (selectedTheme.equalsIgnoreCase("light")) {
//            ct.setTheme(R.style.Theme_Light);
////            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        }
//    }


    public void saveTheme(String theme) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("theme", theme);
        editor.commit();
    }

    public String getSelectedTheme() {
        return sharedPreferences.getString("theme", "light");
    }
}
