package cm.protectu;

public class CommunityCard {
    private String userId;
    private String id;
    private String text;
    private String image;
    private int likes;
    private int dislikes;
    private boolean verified;

    public CommunityCard() {
    }

    public CommunityCard(String userId, String id, String text, String image, int likes, int dislikes, boolean verified) {
        this.userId = userId;
        this.id = id;
        this.likes = likes;
        this.dislikes = dislikes;
        this.verified = verified;
        this.text = text;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
