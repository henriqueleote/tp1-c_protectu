package cm.protectu.MissingBoard;

import java.util.Date;

/**
 * Card of the missing people, on missing Board
 */
public class MissingCardClass {

    private String missingName;
    private String description;
    private String missingAge;
    private String phoneNumber;
    private String userID;
    private String foto;
    private String fotoMissing;
    private String missingID;
    private Date date;

    public MissingCardClass(){

    }


    public MissingCardClass(String missingName, String description, String missingAge, String phoneNumber, String foto, String userID, String fotoMissing, String missingID, Date date) {
        this.missingName = missingName;
        this.description = description;
        this.missingAge = missingAge;
        this.phoneNumber = phoneNumber;
        this.foto = foto;
        this.userID = userID;
        this.fotoMissing = fotoMissing;
        this.missingID = missingID;
        this.date = date;
    }


    //Getters and Setters
    public String getMissingID() {
        return missingID;
    }

    public void setMissingID(String missingID) {
        this.missingID = missingID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUserID() {
        return userID; }

    public void setUserID(String userID) {
        this.userID = userID; }

    public String getFotoMissing() {
        return fotoMissing;
    }

    public void setFotoMissing(String fotoMissing) {
        this.fotoMissing = fotoMissing;
    }


    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getMissingName() {
        return missingName;
    }

    public void setMissingName(String missingName) {
        this.missingName = missingName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMissingAge() {
        return missingAge;
    }

    public void setMissingAge(String missingAge) {
        this.missingAge = missingAge;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
