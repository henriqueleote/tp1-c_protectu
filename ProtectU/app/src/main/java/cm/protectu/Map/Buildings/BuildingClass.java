package cm.protectu.Map.Buildings;

import java.util.ArrayList;

public class BuildingClass {

    String buildingID;
    String buildingName;
    String type;
    ArrayList<String> images;

    public BuildingClass(String buildingID, String buildingName, String type, ArrayList<String> images) {
        this.buildingID = buildingID;
        this.buildingName = buildingName;
        this.type = type;
        this.images = images;
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

    @Override
    public String toString(){
        return getBuildingID() + "\n" + getType() + "\n" + getImages() + "\n";
    }
}
