package cm.protectu.News;

import java.util.Date;

import javax.annotation.Nullable;

public class NewsCardClass {
    private String newsTitle;
    private String newsText;
    private String newsID;
    private String imageURL;
    private String pubImgURL;
    private String pubID;
    private Date date;

    public NewsCardClass(){

    }

    public NewsCardClass(String newsTitle, String newsText, String newsID, @Nullable String imgUrl, String pubImgURL, String pubID, Date date){
        this.newsTitle = newsTitle;
        this.newsText = newsText;
        this.newsID = newsID;
        this.imageURL = imgUrl;
        this.pubImgURL = pubImgURL;
        this.pubID = pubID;
        this.date = date;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsText() {
        return newsText;
    }

    public void setNewsText(String newsText) {
        this.newsText = newsText;
    }

    public String getNewsID() {
        return newsID;
    }

    public void setNewsID(String newsID) {
        this.newsID = newsID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getPubImgURL() {
        return pubImgURL;
    }

    public void setPubImgURL(String pubImgURL) {
        this.pubImgURL = pubImgURL;
    }

    public String getPubID() {
        return pubID;
    }

    public void setPubID(String pubID) {
        this.pubID = pubID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
