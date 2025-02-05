package cm.protectu.Buildings;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class BuildingClass {

    String buildingID;
    String buildingName;
    String type;
    ArrayList<String> images;
    GeoPoint buildingLocation;
    String buildingContact;

    public BuildingClass(String buildingID, String buildingName, String type, ArrayList<String> images, GeoPoint buildingLocation, String buildingContact) {
        this.buildingID = buildingID;
        this.buildingName = buildingName;
        this.type = type;
        this.images = images;
        this.buildingLocation = buildingLocation;
        this.buildingContact = buildingContact;
    }

    public GeoPoint getBuildingLocation() {
        return buildingLocation;
    }

    public void setBuildingLocation(GeoPoint buildingLocation) {
        this.buildingLocation = buildingLocation;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getBuildingID() {
        return buildingID;
    }

    public void setBuildingID(String buildingID) {
        this.buildingID = buildingID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getBuildingContact() {
        return buildingContact;
    }

    public void setBuildingContact(String buildingContact) {
        this.buildingContact = buildingContact;
    }

    @Override
    public String toString(){
        return getBuildingID() + "\n" + getType() + "\n" + getImages() + "\n";
    }
}
