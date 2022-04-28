package cm.protectu.Panic;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class PanicRequestClass {
    private String userID;
    private String requestID;
    private int numberOfPeople;
    private String urgencyLevel;
    private Date date;
    private GeoPoint curLocation;

    public PanicRequestClass(String userID, String requestID, int numberOfPeople, String urgencyLevel, Date date, GeoPoint curLocation) {
        this.userID = userID;
        this.requestID = requestID;
        this.numberOfPeople = numberOfPeople;
        this.urgencyLevel = urgencyLevel;
        this.date = date;
        this.curLocation = curLocation;
    }

    public GeoPoint getCurLocation() {
        return curLocation;
    }

    public void setCurLocation(GeoPoint curLocation) {
        this.curLocation = curLocation;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public PanicRequestClass(){ }

    public String getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(String urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
