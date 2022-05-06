package cm.protectu.Language;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import cm.protectu.MainActivity;

public class LanguageManagerClass {

    private static LanguageManagerClass instance = null;

    private LanguageManagerClass() {
    }

    public static LanguageManagerClass getInstance() {
        if (instance == null) {
            instance = new LanguageManagerClass();
        }

        return instance;
    }


    public void updateResource(Resources resources, String code) {
        Locale locale = new Locale(code);
        Locale.setDefault(locale);
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        saveLocale(code);
    }

    private void saveLocale(String code) {
        Context context = MainActivity.getAppContext();
        File path = context.getFilesDir();
        File file = new File(path, "language.txt");
        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(code.getBytes());
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String readLocale(Resources resources) {
        Context context = MainActivity.getAppContext();
        File path = context.getFilesDir();
        File file = new File(path, "language.txt");

        int length = (int) file.length();

        byte[] bytes = new byte[length];

        try {
            FileInputStream in = new FileInputStream(file);
            in.read(bytes);
            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("Language File Missing!");
        } catch (IOException e) {
            e.printStackTrace();
        }


        String code = new String(bytes);
        if (code.isEmpty())
            code = "en";
        return code;
    }



}
