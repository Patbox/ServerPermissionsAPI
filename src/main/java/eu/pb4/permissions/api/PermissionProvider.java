package eu.pb4.permissions.api;


import com.mojang.authlib.GameProfile;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;

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
     * By default, it checks only for single permission
     * If requested permission ends with a wildcard (.*) it will return PermissionValue.TRUE
     * when any of child permission is true, PermissionValue.DEFAULT when doesn't exist
     * or PermissionValue.FALSE when negated
     *
     * @param user       Player's GameProfile
     * @param world      Current player's world (global and local permissions) or null (for global only)
     * @param permission String of permission
     * @return Corresponding PermissionValue
     */
    PermissionValue checkUserPermission(GameProfile user, @Nullable ServerWorld world, String permission);

    /**
     * Gets list of all permissions of player with value of PermissionValue.TRUE
     * Ordered from most to least significant
     *
     * @param user  Player's GameProfile
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @return List of permissions
     */
    default List<String> getUserPermissions(GameProfile user, @Nullable ServerWorld world) {
        return this.getUserPermissions(user, world, PermissionValue.TRUE);
    }

    /**
     * Gets list of all permissions of player with provided value
     * In case of PlaceholderValue.DEFAULT it returns ignores value
     * Ordered from most to least significant
     *
     * @param user  Player's GameProfile
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @return List of permissions
     */
    List<String> getUserPermissions(GameProfile user, @Nullable ServerWorld world, PermissionValue value);

    /**
     * Gets list of all permissions of player with value of PermissionValue.TRUE
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * Ordered from most to least significant
     *
     * @param user  Player's GameProfile
     * @param parentPermission Parent permission
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @return List of permissions
     */
    default List<String> getUserPermissions(GameProfile user, String parentPermission, @Nullable ServerWorld world) {
        return this.getUserPermissions(user, parentPermission, world, PermissionValue.TRUE);
    }

    /**
     * Gets list of all permissions of player with provided value
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * In case of PlaceholderValue.DEFAULT it returns ignores value
     * Ordered from most to least significant
     *
     * @param user  Player's GameProfile
     * @param parentPermission Parent permission
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @return List of permissions
     */
    List<String> getUserPermissions(GameProfile user, String parentPermission, @Nullable ServerWorld world, PermissionValue value);

    /**
     * Gets List of permissions of player, that aren't inherited from groups
     * and have value of PermissionValue.TRUE
     * Ordered from most to least significant
     *
     * @param user  Player's GameProfile
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @return List of permissions
     */
    default List<String> getUserSpecificPermissions(GameProfile user, @Nullable ServerWorld world) {
        return this.getUserSpecificPermissions(user, world, PermissionValue.TRUE);
    }

    /**
     * Gets List of permissions of player, that aren't inherited from groups
     * In case of PlaceholderValue.DEFAULT, it returns all permissions
     * Ordered from most to least significant
     *
     * @param user  Player's GameProfile
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @param value Value of permission
     * @return List of permissions
     */
    List<String> getUserSpecificPermissions(GameProfile user, @Nullable ServerWorld world, PermissionValue value);

    /**
     * Gets list of all permissions of player with value of PermissionValue.TRUE, that aren't inherited from groups
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * Ordered from most to least significant
     *
     * @param user  Player's GameProfile
     * @param parentPermission Parent permission
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @return List of permissions
     */
    default List<String> getUserSpecificPermissions(GameProfile user, String parentPermission, @Nullable ServerWorld world) {
        return this.getUserSpecificPermissions(user, parentPermission, world, PermissionValue.TRUE);
    }

    /**
     * Gets list of all permissions of player with provided value, that aren't inherited from groups
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * In case of PlaceholderValue.DEFAULT it returns ignores value
     * Ordered from most to least significant
     *
     * @param user  Player's GameProfile
     * @param parentPermission Parent permission
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @return List of permissions
     */
    List<String> getUserSpecificPermissions(GameProfile user, String parentPermission, @Nullable ServerWorld world, PermissionValue value);

    /**
     * Sets value of player's placeholder to provided one
     * In case of PlaceholderValue.DEFAULT, it gets removed from player
     *
     * @param user  Player's GameProfile
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @param value Value of permission, DEFAULT removes it
     */
    void setUserPermission(GameProfile user, @Nullable ServerWorld world, PermissionValue value);

    /**
     * Sets value of player's placeholder to provided one with specific duration
     * In case of PlaceholderValue.DEFAULT, it gets removed from player
     *
     * @param user     Player's GameProfile
     * @param world    Current player's world (global and local permissions) or null (for global only)
     * @param value    Value of permission, DEFAULT removes it
     * @param duration Duration of permission
     */
    void setUserPermission(GameProfile user, @Nullable ServerWorld world, PermissionValue value, Duration duration);

    /**
     * Gets list of groups player is in. Ordered from most to least significant
     *
     * @param user  Player's GameProfile
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @return List of groups
     */
    List<String> getUserGroups(GameProfile user, @Nullable ServerWorld world);

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
     * By default, it checks only for single permission
     * If requested permission ends with a wildcard (.*) it will return PermissionValue.TRUE
     * when any of child permission is true, PermissionValue.DEFAULT when doesn't exist
     * or PermissionValue.FALSE when negated
     *
     * @param group      Group's name
     * @param world      Current world (global and local permissions) or null (for global only)
     * @param permission String of permission
     * @return Corresponding PermissionValue
     */
    PermissionValue checkGroupPermission(String group, @Nullable ServerWorld world, String permission);

    /**
     * Gets list of all permissions of group with PermissionValue.TRUE
     * Ordered from most to least significant
     *
     * @param group Group's name
     * @param world Current world (global and local permissions) or null (for global only)
     * @return List of permissions
     */
    default List<String> getGroupPermissions(String group, @Nullable ServerWorld world) {
        return this.getGroupPermissions(group, world, PermissionValue.TRUE);
    }

    /**
     * Gets List of all permissions of group with specific value
     * In case of PlaceholderValue.DEFAULT it returns ignores value and returns all
     * Ordered from most to least significant
     *
     * @param group Group's name
     * @param world Current world (global and local permissions) or null (for global only)
     * @param value Value of permission
     * @return List of permissions
     */
    List<String> getGroupPermissions(String group, @Nullable ServerWorld world, PermissionValue value);

    /**
     * Gets list of all permissions of group with value of PermissionValue.TRUE
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * Ordered from most to least significant
     *
     * @param group Group's name
     * @param parentPermission Parent permission
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @return List of permissions
     */
    default List<String> getGroupPermissions(String group, String parentPermission, @Nullable ServerWorld world) {
        return this.getGroupPermissions(group, parentPermission, world, PermissionValue.TRUE);
    }

    /**
     * Gets list of all permissions of group with provided value
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * In case of PlaceholderValue.DEFAULT it returns ignores value
     * Ordered from most to least significant
     *
     * @param group Group's name
     * @param parentPermission Parent permission
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @return List of permissions
     */
    List<String> getGroupPermissions(String group, String parentPermission, @Nullable ServerWorld world, PermissionValue value);

    /**
     * Gets List of permissions of group, that aren't inherited from other groups
     * and have value of PermissionValue.TRUE
     * Ordered from most to least significant
     *
     * @param group Group's name
     * @param world Current world (global and local permissions) or null (for global only)
     * @return List of permissions
     */
    default List<String> getGroupSpecificPermissions(String group, @Nullable ServerWorld world) {
        return this.getGroupSpecificPermissions(group, world, PermissionValue.TRUE);
    }

    /**
     * Gets List of permissions of player, that aren't inherited from other groups
     * In case of PlaceholderValue.DEFAULT, it returns all permissions
     * Ordered from most to least significant
     *
     * @param group Group's name
     * @param world Current world (global and local permissions) or null (for global only)
     * @param value Value of permission
     * @return List of permissions
     */
    List<String> getGroupSpecificPermissions(String group, @Nullable ServerWorld world, PermissionValue value);

    /**
     * Gets list of all permissions of group with value of PermissionValue.TRUE, that aren't inherited from other groups
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * Ordered from most to least significant
     *
     * @param group Group's name
     * @param parentPermission Parent permission
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @return List of permissions
     */
    default List<String> getGroupSpecificPermissions(String group, String parentPermission, @Nullable ServerWorld world) {
        return this.getGroupSpecificPermissions(group, parentPermission, world, PermissionValue.TRUE);
    }

    /**
     * Gets list of all permissions of group with provided value, that aren't inherited from other groups
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * In case of PlaceholderValue.DEFAULT it returns ignores value
     * Ordered from most to least significant
     *
     * @param group Group's name
     * @param parentPermission Parent permission
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @return List of permissions
     */
    List<String> getGroupSpecificPermissions(String group, String parentPermission, @Nullable ServerWorld world, PermissionValue value);
}
