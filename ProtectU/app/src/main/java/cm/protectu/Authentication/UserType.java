package cm.protectu.Authentication;

public class UserType {

    String code;
    String logo;
    String type;

    public UserType(String code, String logo, String type) {
        this.code = code;
        this.logo = logo;
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
