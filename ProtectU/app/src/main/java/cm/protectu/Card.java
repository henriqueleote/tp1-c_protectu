package cm.protectu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class Card {

    private String nameOfWhoisMissing;
    private String Description;
    private int Age;
    private int PhoneNumber;
    private int foto;

    public Card(){

    }

    public Card(String nameOfWhoisMissing, String description, int age, int phoneNumber,int foto) {
        this.nameOfWhoisMissing = nameOfWhoisMissing;
        Description = description;
        Age = age;
        PhoneNumber = phoneNumber;
        this.foto = foto;
    }


    public int getFoto() {
        return foto;
    }

    public void setFoto(int foto) {
        this.foto = foto;
    }

    public String getNameOfWhoisMissing() {
        return nameOfWhoisMissing;
    }

    public void setNameOfWhoisMissing(String nameOfWhoisMissing) {
        this.nameOfWhoisMissing = nameOfWhoisMissing;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getAge() {
        return Age;
    }

    public void setAge(int age) {
        Age = age;
    }

    public int getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        PhoneNumber = phoneNumber;
    }
}
