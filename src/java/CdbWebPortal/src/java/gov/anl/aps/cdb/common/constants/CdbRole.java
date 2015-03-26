package gov.anl.aps.cdb.common.constants;


public enum CdbRole {
    USER("user"),
    ADMIN("admin");
    
    private final String type;
    private CdbRole(String type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return type;
    }
    
    public static CdbRole fromString(String type) {
        CdbRole role = null;
        switch (type) {
            case "user":
                role = USER;
                break;
            case "admin":
                role = ADMIN;
                break;
        }
        return role;
    }
}
