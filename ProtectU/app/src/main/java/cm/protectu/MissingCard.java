package cm.protectu;

/**
 * Card of the missing people, on missing Board
 */
public class MissingCard {

    private String missingName;
    private String description;
    private String missingAge;
    private String phoneNumber;
    private String userID;
    private String foto;
    private String fotoMissing;
    private String missingID;

    public MissingCard(){

    }


    public MissingCard(String missingName, String description, String missingAge, String phoneNumber, String foto,String userID,String fotoMissing,String missingID) {
        this.missingName = missingName;
        this.description = description;
        this.missingAge = missingAge;
        this.phoneNumber = phoneNumber;
        this.foto = foto;
        this.userID = userID;
        this.fotoMissing = fotoMissing;
        this.missingID = missingID;
    }


    //Getters and Setters
    public String getMissingID() {
        return missingID;
    }

    public void setMissingID(String missingID) {
        this.missingID = missingID;
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
