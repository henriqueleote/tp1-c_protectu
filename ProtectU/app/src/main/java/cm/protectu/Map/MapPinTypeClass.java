package cm.protectu.Map;

public class MapPinTypeClass {

    private String type;
    private String logo;
    private String name;

    public MapPinTypeClass(String type, String logo, String name) {
        this.type = type;
        this.logo = logo;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
