package eu.pb4.permissions.api;

public enum PermissionValue {
    /**
     * Default permission - User doesn't have it defined
     */
    DEFAULT,
    /**
     * User has permission
     */
    TRUE,
    /**
     * User has permission negated
     */
    FALSE;


    public boolean allow(boolean or) {
        return switch (this) {
            case TRUE -> true;
            case DEFAULT -> or;
            case FALSE -> false;
        };
    }
}
