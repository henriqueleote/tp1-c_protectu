package cm.protectu.Community;

import java.util.Date;

public class CommunityCard {
    private String userID;
    private String messageID;
    private String messageText;
    private String imageURL;
    private Date date;
    private int likes;
    private int dislikes;
    private boolean verified;
    private boolean isVideo;

    public CommunityCard() {
    }

    public CommunityCard(String userID, String messageID, String messageText, String imageURL,Date date, int likes, int dislikes, boolean verified,boolean isVideo) {
        this.userID = userID;
        this.messageID = messageID;
        this.likes = likes;
        this.dislikes = dislikes;
        this.verified = verified;
        this.date = date;
        this.messageText = messageText;
        this.imageURL = imageURL;
        this.isVideo = isVideo;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
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

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }
}
