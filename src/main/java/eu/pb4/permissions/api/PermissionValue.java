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

    public boolean pass(boolean val) {
        return switch (this) {
            case TRUE -> val;
            case DEFAULT -> true;
            case FALSE -> !val;
        };
    }

    public static PermissionValue of(boolean value) {
        return value ? TRUE : FALSE;
    }

    public static PermissionValue of(Boolean value) {
        return value == Boolean.TRUE ? TRUE : value == Boolean.FALSE ? FALSE : null;
    }
}
