package eu.pb4.compatibility.permissions;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"null", "unused"})
public class Permissions {
    static final Map<String, PermissionProvider> PROVIDERS = new HashMap<>();
    static PermissionProvider DEFAULT_PROVIDER = null;

    /**
     * Returns default permission provider.
     */
    public static PermissionProvider getDefaultProvider() {
        return DEFAULT_PROVIDER;
    }

    /**
     * Gets permission provider basing on Identifier
     *
     * @param identifier Mods identifier
     * @return PermissionProvider or null
     */
    public static PermissionProvider getPermissionProvider(String identifier) {
        return PROVIDERS.get(identifier);
    }

    /**
     * Returns true, if provider supports groups
     */
    public static boolean supportsGroups() {
        return DEFAULT_PROVIDER.supportsGroups();
    }

    /**
     * Returns true, if provider timed groups
     */
    public static boolean supportsTimedPermissions() {
        return DEFAULT_PROVIDER.supportsTimedPermissions();
    }

    /**
     * Returns true, if provider supports groups
     */
    public static boolean supportsTimedGroups() {
        return DEFAULT_PROVIDER.supportsTimedGroups();
    }

    /**
     * Returns true, if provider supports per world permissions
     */
    public static boolean supportsPerWorldPermissions() {
        return DEFAULT_PROVIDER.supportsPerWorldPermissions();
    }

    /**
     * Returns true, if provider supports per world groups
     */
    public static boolean supportsPerWorldGroups() {
        return DEFAULT_PROVIDER.supportsPerWorldGroups();
    }


    /**
     * Checks value of player's permission
     *
     * @param user       Player's GameProfile
     * @param world      Current player's world (global and local permissions) or null (for global only)
     * @param permission String of permission
     * @return Corresponding PermissionValue
     */
    public static PermissionValue checkUserPermission(GameProfile user, @Nullable ServerWorld world, String permission) {
        return DEFAULT_PROVIDER.checkUserPermission(user, world, permission);
    }

    /**
     * Gets Set of all permissions of player (including inherited) with PermissionValue.TRUE
     *
     * @param user  Player's GameProfile
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @return Set of permissions
     */
    public static Set<String> getUserPermissions(GameProfile user, @Nullable ServerWorld world) {
        return DEFAULT_PROVIDER.getUserPermissions(user, world);
    }

    /**
     * Gets Set of all permissions of player (including inherited) with specific value
     *
     * @param user  Player's GameProfile
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @return Set of permissions
     */
    public static Set<String> getUserPermissions(GameProfile user, @Nullable ServerWorld world, PermissionValue value) {
        return DEFAULT_PROVIDER.getUserPermissions(user, world, value);
    }

    /**
     * Gets Set of permissions specific to player with PermissionValue.TRUE
     *
     * @param user  Player's GameProfile
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @return Set of permissions
     */
    public static Set<String> getUserSpecificPermissions(GameProfile user, @Nullable ServerWorld world) {
        return DEFAULT_PROVIDER.getUserSpecificPermissions(user, world);
    }

    /**
     * Gets Set of permissions specific to player with specific value
     *
     * @param user  Player's GameProfile
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @param value Value of permission
     * @return Set of permissions
     */
    public static Set<String> getUserSpecificPermissions(GameProfile user, @Nullable ServerWorld world, PermissionValue value) {
        return DEFAULT_PROVIDER.getUserSpecificPermissions(user, world, value);
    }

    /**
     * Sets value of users permission.
     *
     * @param user  Player's GameProfile
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @param value Value of permission, DEFAULT removes it
     */
    public static void setUserPermission(GameProfile user, @Nullable ServerWorld world, PermissionValue value) {
        DEFAULT_PROVIDER.setUserPermission(user, world, value);
    }

    /**
     * Sets value of users permission.
     *
     * @param user     Player's GameProfile
     * @param world    Current player's world (global and local permissions) or null (for global only)
     * @param value    Value of permission, DEFAULT removes it
     * @param duration Duration of permission
     */
    public static void setUserPermission(GameProfile user, @Nullable ServerWorld world, PermissionValue value, Duration duration) {
        DEFAULT_PROVIDER.setUserPermission(user, world, value, duration);
    }

    /**
     * Gets set of names of groups user is in
     *
     * @param user  Player's GameProfile
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @return Set of groups
     */
    public static Set<String> getUserGroups(GameProfile user, @Nullable ServerWorld world) {
        return DEFAULT_PROVIDER.getUserGroups(user, world);
    }

    /**
     * Adds user to group
     *
     * @param user  Player's GameProfile
     * @param world Current world (for world only group) or null (for global)
     * @param group Group's name
     */
    public static void addUserGroup(GameProfile user, @Nullable ServerWorld world, String group) {
        DEFAULT_PROVIDER.addUserGroup(user, world, group);
    }

    /**
     * Adds user to group for specified duration
     *
     * @param user  Player's GameProfile
     * @param world Current world (for world only group) or null (for global)
     * @param group Group's name
     */
    public static void addUserGroup(GameProfile user, @Nullable ServerWorld world, String group, Duration duration) {
        DEFAULT_PROVIDER.addUserGroup(user, world, group, duration);
    }

    /**
     * Remove user from group
     *
     * @param user  Player's GameProfile
     * @param world Current world (for world only group) or null (for global)
     * @param group Group's name
     */
    public static void removeUserGroup(GameProfile user, @Nullable ServerWorld world, String group) {
        DEFAULT_PROVIDER.removeUserGroup(user, world, group);
    }


    /**
     * Checks value of group's permission
     *
     * @param group      Groups name
     * @param world      Current world (global and local permissions) or null (for global only)
     * @param permission String of permission
     * @return Corresponding PermissionValue
     */
    public static PermissionValue checkGroupPermission(String group, @Nullable ServerWorld world, String permission) {
        return DEFAULT_PROVIDER.checkGroupPermission(group, world, permission);
    }

    /**
     * Gets Set of all permissions of group (including inherited) with PermissionValue.TRUE
     *
     * @param group Group's name
     * @param world Current world (global and local permissions) or null (for global only)
     * @return Set of permissions
     */
    public static Set<String> getGroupPermissions(String group, @Nullable ServerWorld world) {
        return DEFAULT_PROVIDER.getGroupPermissions(group, world);
    }

    /**
     * Gets Set of all permissions of group (including inherited) with specific value
     *
     * @param group Group's name
     * @param world Current world (global and local permissions) or null (for global only)
     * @param value Value of permission
     * @return Set of permissions
     */
    public static Set<String> getGroupPermissions(String group, @Nullable ServerWorld world, PermissionValue value) {
        return DEFAULT_PROVIDER.getGroupPermissions(group, world, value);
    }


    /**
     * Gets Set of permissions specific to group with PermissionValue.TRUE
     *
     * @param group Group's name
     * @param world Current world (global and local permissions) or null (for global only)
     * @return Set of permissions
     */
    public static Set<String> getGroupSpecificPermissions(String group, @Nullable ServerWorld world) {
        return DEFAULT_PROVIDER.getGroupSpecificPermissions(group, world);
    }

    /**
     * Gets Set of permissions specific to group with specific value
     *
     * @param group Group's name
     * @param world Current world (global and local permissions) or null (for global only)
     * @param value Value of permission
     * @return Set of permissions
     */
    public static Set<String> getGroupSpecificPermissions(String group, @Nullable ServerWorld world, PermissionValue value) {
        return DEFAULT_PROVIDER.getGroupSpecificPermissions(group, world, value);
    }
}
