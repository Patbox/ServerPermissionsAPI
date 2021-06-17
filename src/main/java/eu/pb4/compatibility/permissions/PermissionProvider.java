package eu.pb4.compatibility.permissions;


import com.mojang.authlib.GameProfile;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Set;

public interface PermissionProvider {
    /**
     * Name of the provider
     */
    String getName();

    /**
     * Identifier of the provider
     */
    String getIdentifier();

    /**
     * Returns true, if provider supports groups
     */
    boolean supportsGroups();

    /**
     * Returns true, if provider timed groups
     */
    boolean supportsTimedPermissions();

    /**
     * Returns true, if provider supports groups
     */
    boolean supportsTimedGroups();

    /**
     * Returns true, if provider supports per world permissions
     */
    boolean supportsPerWorldPermissions();

    /**
     * Returns true, if provider supports per world groups
     */
    boolean supportsPerWorldGroups();


    /**
     * Checks value of player's permission
     *
     * @param user       Player's GameProfile
     * @param world      Current player's world (global and local permissions) or null (for global only)
     * @param permission String of permission
     * @return Corresponding PermissionValue
     */
    PermissionValue checkUserPermission(GameProfile user, @Nullable ServerWorld world, String permission);

    /**
     * Gets Set of all permissions of player (including inherited) with PermissionValue.TRUE
     *
     * @param user  Player's GameProfile
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @return Set of permissions
     */
    default Set<String> getUserPermissions(GameProfile user, @Nullable ServerWorld world) {
        return this.getUserPermissions(user, world, PermissionValue.TRUE);
    }

    /**
     * Gets Set of all permissions of player (including inherited) with specific value
     *
     * @param user  Player's GameProfile
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @return Set of permissions
     */
    Set<String> getUserPermissions(GameProfile user, @Nullable ServerWorld world, PermissionValue value);


    /**
     * Gets Set of permissions specific to player with PermissionValue.TRUE
     *
     * @param user  Player's GameProfile
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @return Set of permissions
     */
    default Set<String> getUserSpecificPermissions(GameProfile user, @Nullable ServerWorld world) {
        return this.getUserSpecificPermissions(user, world, PermissionValue.TRUE);
    }

    /**
     * Gets Set of permissions specific to player with specific value
     *
     * @param user  Player's GameProfile
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @param value Value of permission
     * @return Set of permissions
     */
    Set<String> getUserSpecificPermissions(GameProfile user, @Nullable ServerWorld world, PermissionValue value);

    /**
     * Sets value of users permission.
     *
     * @param user  Player's GameProfile
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @param value Value of permission, DEFAULT removes it
     */
    void setUserPermission(GameProfile user, @Nullable ServerWorld world, PermissionValue value);

    /**
     * Sets value of users permission.
     *
     * @param user     Player's GameProfile
     * @param world    Current player's world (global and local permissions) or null (for global only)
     * @param value    Value of permission, DEFAULT removes it
     * @param duration Duration of permission
     */
    void setUserPermission(GameProfile user, @Nullable ServerWorld world, PermissionValue value, Duration duration);

    /**
     * Gets set of names of groups user is in
     *
     * @param user  Player's GameProfile
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @return Set of groups
     */
    Set<String> getUserGroups(GameProfile user, @Nullable ServerWorld world);

    /**
     * Adds user to group
     *
     * @param user  Player's GameProfile
     * @param world Current world (for world only group) or null (for global)
     * @param group Group's name
     */
    void addUserGroup(GameProfile user, @Nullable ServerWorld world, String group);

    /**
     * Adds user to group for specified duration
     *
     * @param user  Player's GameProfile
     * @param world Current world (for world only group) or null (for global)
     * @param group Group's name
     */
    void addUserGroup(GameProfile user, @Nullable ServerWorld world, String group, Duration duration);

    /**
     * Remove user from group
     *
     * @param user  Player's GameProfile
     * @param world Current world (for world only group) or null (for global)
     * @param group Group's name
     */
    void removeUserGroup(GameProfile user, @Nullable ServerWorld world, String group);

    /**
     * Checks value of group's permission
     *
     * @param group      Group's name
     * @param world      Current world (global and local permissions) or null (for global only)
     * @param permission String of permission
     * @return Corresponding PermissionValue
     */
    PermissionValue checkGroupPermission(String group, @Nullable ServerWorld world, String permission);

    /**
     * Gets Set of all permissions of group (including inherited) with PermissionValue.TRUE
     *
     * @param group Group's name
     * @param world Current world (global and local permissions) or null (for global only)
     * @return Set of permissions
     */
    default Set<String> getGroupPermissions(String group, @Nullable ServerWorld world) {
        return this.getGroupPermissions(group, world, PermissionValue.TRUE);
    }

    /**
     * Gets Set of all permissions of group (including inherited) with specific value
     *
     * @param group Group's name
     * @param world Current world (global and local permissions) or null (for global only)
     * @param value Value of permission
     * @return Set of permissions
     */
    Set<String> getGroupPermissions(String group, @Nullable ServerWorld world, PermissionValue value);


    /**
     * Gets Set of permissions specific to group with PermissionValue.TRUE
     *
     * @param group Group's name
     * @param world Current world (global and local permissions) or null (for global only)
     * @return Set of permissions
     */
    default Set<String> getGroupSpecificPermissions(String group, @Nullable ServerWorld world) {
        return this.getGroupSpecificPermissions(group, world, PermissionValue.TRUE);
    }

    /**
     * Gets Set of permissions specific to group with specific value
     *
     * @param group Group's name
     * @param world Current world (global and local permissions) or null (for global only)
     * @param value Value of permission
     * @return Set of permissions
     */
    Set<String> getGroupSpecificPermissions(String group, @Nullable ServerWorld world, PermissionValue value);
}
