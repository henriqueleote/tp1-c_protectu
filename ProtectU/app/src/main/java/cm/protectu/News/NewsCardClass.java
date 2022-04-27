package cm.protectu.News;

import java.util.Date;

public class NewsCardClass {
    private String newsTitle;
    private String newsText;
    private String newsID;
    private String imgUrl;
    private String pubImgURL;
    private String pubID;

    public NewsCardClass(){

    }

    public NewsCardClass(String newsTitle, String newsText, String newsID, String imgUrl, String pubImgURL, String pubID){
        this.newsTitle = newsTitle;
        this.newsText = newsText;
        this.newsID = newsID;
        this.imgUrl = imgUrl;
        this.pubImgURL = pubImgURL;
        this.pubID = pubID;
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
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
}
