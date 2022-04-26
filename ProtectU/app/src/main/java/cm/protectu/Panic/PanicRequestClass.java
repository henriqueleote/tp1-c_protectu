package cm.protectu.Panic;

public class PanicRequestClass {
    private String userID;
    private String requestID;
    private int numberOfPeople;
    private String urgencyLevel;

    public PanicRequestClass(String userID, String requestID, int numberOfPeople, String urgencyLevel) {
        this.userID = userID;
        this.requestID = requestID;
        this.numberOfPeople = numberOfPeople;
        this.urgencyLevel = urgencyLevel;
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
