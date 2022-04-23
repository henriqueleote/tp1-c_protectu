package cm.protectu;

import com.google.firebase.firestore.GeoPoint;

public class MapPin {

    //String with the Pin ID and the Pin type
    String pinID, type;

    //GeoPoint with the Pin location (Latitude, Longitude)
    GeoPoint location;

    //Constructor
    public MapPin(String pinID, GeoPoint location, String type) {
        this.pinID = pinID;
        this.location = location;
        this.type = type;
    }

    /* Getters and Setters */

    public String getPinID() {
        return pinID;
    }

    public void setPinID(String pinID) {
        this.pinID = pinID;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    //Object To String
    @Override
    public String toString(){
        return "ID : "+ getPinID() +
                "\nType : " + getType() +
                "\nLat : " + getLocation().getLatitude() +
                "\nLong : " + getLocation().getLongitude();
    }
}
