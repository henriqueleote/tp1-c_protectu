package cm.protectu.Community;

public class UserDataClass {
    private String firstName;
    private String lastName;
    private String uid;
    private String phoneNumber;
    private String imageURL;
    private String type;

    public UserDataClass() {
    }

    public UserDataClass(String firstName, String lastName, String userID, String phoneNumber, String imageURL,String type) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.uid = userID;
        this.phoneNumber = phoneNumber;
        this.imageURL = imageURL;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
