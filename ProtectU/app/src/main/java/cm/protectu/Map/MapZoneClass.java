package cm.protectu.Map;

import com.google.firebase.firestore.GeoPoint;

public class MapZoneClass {

    //String with the Zone ID
    String zoneID;

    //GeoPoint with the points location (Latitude, Longitude)
    GeoPoint topOneLocation, topTwoLocation, bottomOneLocation, bottomTwoLocation;

    //Constructor
    public MapZoneClass(String zoneID, GeoPoint topOneLocation, GeoPoint topTwoLocation, GeoPoint bottomOneLocation, GeoPoint bottomTwoLocation) {
        this.zoneID = zoneID;
        this.topOneLocation = topOneLocation;
        this.topTwoLocation = topTwoLocation;
        this.bottomOneLocation = bottomOneLocation;
        this.bottomTwoLocation = bottomTwoLocation;
    }

    /* Getters and Setters */

    public String getZoneID() {
        return zoneID;
    }

    public void setZoneID(String zoneID) {
        this.zoneID = zoneID;
    }

    public GeoPoint getTopOneLocation() {
        return topOneLocation;
    }

    public void setTopOneLocation(GeoPoint topOneLocation) {
        this.topOneLocation = topOneLocation;
    }

    public GeoPoint getTopTwoLocation() {
        return topTwoLocation;
    }

    public void setTopTwoLocation(GeoPoint topTwoLocation) {
        this.topTwoLocation = topTwoLocation;
    }

    public GeoPoint getBottomOneLocation() {
        return bottomOneLocation;
    }

    public void setBottomOneLocation(GeoPoint bottomOneLocation) {
        this.bottomOneLocation = bottomOneLocation;
    }

    public GeoPoint getBottomTwoLocation() {
        return bottomTwoLocation;
    }

    public void setBottomTwoLocation(GeoPoint bottomTwoLocation) {
        this.bottomTwoLocation = bottomTwoLocation;
    }

    //Object To String
    @Override
    public String toString(){
        return "ID : "+ getZoneID() +
                "\nTop 1 : [" + getTopOneLocation().getLatitude() + ", " + getTopOneLocation().getLongitude() + "]" +
                "\nTop 2 : [" + getTopTwoLocation().getLatitude() + ", " + getTopTwoLocation().getLongitude() + "]" +
                "\nBottom 1 : [" + getBottomOneLocation().getLatitude() + ", " + getBottomOneLocation().getLongitude() + "]" +
                "\nBottom 2 : [" + getBottomTwoLocation().getLatitude() + ", " + getBottomTwoLocation().getLongitude() + "]";
    }
}
