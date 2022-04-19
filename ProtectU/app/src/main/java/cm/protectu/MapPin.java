package cm.protectu;

import com.google.firebase.firestore.GeoPoint;

public class MapPin {

    String pinID;
    GeoPoint location;
    String type;

    public MapPin(String pinID, GeoPoint location, String type) {
        this.pinID = pinID;
        this.location = location;
        this.type = type;
    }

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

    @Override
    public String toString(){
        return "ID : "+ getPinID() +
                "\nType : " + getType() +
                "\nLat : " + getLocation().getLatitude() +
                "\nLong : " + getLocation().getLongitude();
    }
}
