package cm.protectu.Customization;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cm.protectu.MainActivity;
import cm.protectu.R;

public class CustomizationManager {

    private static CustomizationManager instance = null;

    private String selectedTheme;
    private Context ct;

    private CustomizationManager(Context ct) {
        this.ct = ct;
        selectedTheme = "";
        readTheme();
    }

    public static CustomizationManager getInstance(Context ct) {
        if (instance == null) {
            instance = new CustomizationManager(ct);
        }


        return instance;
    }

    private void readTheme() {
        File path = ct.getFilesDir();
        File file = new File(path, "theme.txt");

        int length = (int) file.length();

        byte[] bytes = new byte[length];

        try {
            FileInputStream in = new FileInputStream(file);
            in.read(bytes);
            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("Theme File Missing!");
        } catch (IOException e) {
            e.printStackTrace();
        }


        selectedTheme = new String(bytes);

//        Se for utilizar o AppCompatDelegate, ver como guardar as coisas em SharedPreferences
//        SharedPreferences sharedPreferences = ct.getSharedPreferences("night", 0);
//        String string = sharedPreferences.getString("nightMode", "light");
//        boolean nightMode = sharedPreferences.getBoolean("nightMode", false);
        if (selectedTheme.equalsIgnoreCase("dark")) {
            ct.setTheme(R.style.Theme_Dark);
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (selectedTheme.equalsIgnoreCase("light")) {
            ct.setTheme(R.style.Theme_Light);
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }


    public void saveTheme(String theme) {
        Context context = MainActivity.getAppContext();
        File path = context.getFilesDir();
        File file = new File(path, "theme.txt");
        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(theme.getBytes());
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSelectedTheme() {
        return selectedTheme;
    }

    public void updateTheme(){
        if (selectedTheme.equalsIgnoreCase("dark")) {
            ct.setTheme(R.style.Theme_Dark);
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (selectedTheme.equalsIgnoreCase("light")) {
            ct.setTheme(R.style.Theme_Light);
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
