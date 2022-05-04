package cm.protectu.Map;

import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class MapZoneClass {

    //String with the Zone ID
    String zoneID;

    //GeoPoint with the Zone location (Latitude, Longitude)
    List<GeoPoint> location;

    //Constructor
    public MapZoneClass(String zoneID, List<GeoPoint> location, String type) {
        this.zoneID = zoneID;
        this.location = location;
    }

    /* Getters and Setters */

    public String getZoneID() {
        return zoneID;
    }

    public void setZoneID(String zoneID) {
        this.zoneID = zoneID;
    }

    public List<GeoPoint> getLocation() {
        return location;
    }

    public void setLocation(List<GeoPoint> location) {
        this.location = location;
    }

}
