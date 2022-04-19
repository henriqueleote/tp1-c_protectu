package cm.protectu;

/**
 * Card of the missing people, on missing Board
 */
public class MissingCard {

    private String profileName;
    private String missingName;
    private String description;
    private int missingAge;
    private int phoneNumber;
    private int missingID;
    private String foto;
    private String fotoMissing;

    public MissingCard(){

    }

    public MissingCard(String missingName, String description, int missingAge, int phoneNumber, String foto, String profileName,int missingID,String fotoMissing) {
        this.missingName = missingName;
        this.description = description;
        this.missingAge = missingAge;
        this.phoneNumber = phoneNumber;
        this.foto = foto;
        this.profileName = profileName;
        this.missingID = missingID;
        this.fotoMissing = fotoMissing;
    }

    public int getMissingID() {
        return missingID;
    }

    public String getFotoMissing() {
        return fotoMissing;
    }

    public void setFotoMissing(String fotoMissing) {
        this.fotoMissing = fotoMissing;
    }

    public void setMissingID(int missingID) {
        this.missingID = missingID;
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

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMissingAge() {
        return missingAge;
    }

    public void setMissingAge(int missingAge) {
        this.missingAge = missingAge;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
/*
*
Card Layout: The card layout is an XML file that will represent each individual grid item inside your Recycler view.
View Holder: View Holder Class is the java class that stores the reference to the UI Elements in the Card Layout and they can be modified dynamically during the execution of the program by the list of data.
Data Class: Data Class is an object class that holds information to be displayed in each recycler view item that is to be displayed in Recycler View.
* */