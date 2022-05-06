package cm.protectu.Community;

import java.util.Date;

public class UserReactionsClass {
    private String userID;
    private String messageID;
    private String type;
    private String image;
    private Date date;


    public UserReactionsClass() {
    }

    public UserReactionsClass(String userID, String messageID, String type, Date date,String image) {
        this.userID = userID;
        this.messageID = messageID;
        this.type = type;
        this.date = date;
        this.image = image;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
