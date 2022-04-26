package cm.protectu;

public class UserData {
    private String firstName;
    private String lastName;
    private String userID;
    private String phoneNumber;
    private String imageURL;

    public UserData() {
    }

    public UserData(String firstName, String lastName, String userID, String phoneNumber, String imageURL) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userID = userID;
        this.phoneNumber = phoneNumber;
        this.imageURL = imageURL;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
