package eu.pb4.permissions.api.v0;

@SuppressWarnings({"unused"})
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

    /**
     * Converts PermissionValue to boolean
     *
     * @param defaultValue Default value
     * @return Boolean
     */
    public boolean toBoolean(boolean defaultValue) {
        return switch (this) {
            case TRUE -> true;
            case DEFAULT -> defaultValue;
            case FALSE -> false;
        };
    }

    /**
     * Checks if value equals PermissionValue
     *
     * @param value Default value
     * @return Boolean
     */
    public boolean pass(boolean value) {
        return switch (this) {
            case TRUE -> value;
            case DEFAULT -> true;
            case FALSE -> !value;
        };
    }

    public static PermissionValue of(boolean value) {
        return value ? TRUE : FALSE;
    }

    public static PermissionValue of(Boolean value) {
        return value == Boolean.TRUE ? TRUE : value == Boolean.FALSE ? FALSE : null;
    }
}
