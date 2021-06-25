package eu.pb4.permissions.api.v0;


import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused"})
public interface PermissionProvider {
    static <V> V getValueFrom(List<String> permissions, V defaultValue, ValueAdapter<V> adapter) {
        List<V> list = new ArrayList<>();
        list.add(defaultValue);
        for (String s : permissions) {
            V val = adapter.create(s);
            if (val != null) {
                list.add(val);
            }
        }
        list.sort(adapter::sort);

        return list.get(0);
    }

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
     * Returns true, if provider supports temporary permissions
     */
    boolean supportsTemporaryPermissions();

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
     * Returns true, if provider supports checking of permissions for offline players
     */
    boolean supportsOfflineChecks();

    /**
     * Returns true, if provider supports dynamic changing of players permissions
     */
    boolean supportsChangingPlayersPermissions();

    /**
     * Returns true, if this Permission should be a candidate for being default
     */
    Priority getPriority();

    /**
     * Checks value of player's permission
     * By default, it checks only for single permission
     * If requested permission ends with a ".?" it will return PermissionValue.TRUE
     * when any of child permission is true, PermissionValue.DEFAULT when doesn't exist
     * or PermissionValue.FALSE when negated
     *
     * @param user       Player's UserContext
     * @param permission String of permission
     * @return Corresponding PermissionValue
     */
    PermissionValue check(UserContext user, String permission);

    /**
     * Checks value of player's permission
     * By default, it checks only for single permission
     * If requested permission ends with a wildcard (.*) it will return PermissionValue.TRUE
     * when any of child permission is true, PermissionValue.DEFAULT when doesn't exist
     * or PermissionValue.FALSE when negated
     *
     * @param user         Player's UserContext
     * @param permission   String of permission
     * @param defaultLevel Default otherwise required OP level
     * @return Corresponding PermissionValue
     */
    default boolean check(UserContext user, String permission, int defaultLevel) {
        return check(user, permission).toBoolean(user.getPermissionLevel() >= defaultLevel);
    }

    /**
     * Checks value of player's permission
     * By default, it checks only for single permission
     * If requested permission ends with a wildcard (.*) it will return PermissionValue.TRUE
     * when any of child permission is true, PermissionValue.DEFAULT when doesn't exist
     * or PermissionValue.FALSE when negated
     *
     * @param user         Player's UserContext
     * @param permission   String of permission
     * @param defaultValue If should be allowed by default
     * @return Corresponding PermissionValue
     */
    default boolean check(UserContext user, String permission, boolean defaultValue) {
        return check(user, permission).toBoolean(defaultValue);
    }

    /**
     * Gets list of all permissions of player with value of PermissionValue.TRUE
     * Ordered from most to least significant
     *
     * @param user Player's UserContext
     * @return List of permissions
     */
    default List<String> getList(UserContext user) {
        return this.getList(user, user.getWorld());
    }

    /**
     * Gets list of all permissions of player with value of PermissionValue.TRUE
     * Ordered from most to least significant
     *
     * @param user  Player's UserContext
     * @param world World for check (returns global and local permissions) or null (for global only)
     * @return List of permissions
     */
    default List<String> getList(UserContext user, @Nullable ServerWorld world) {
        return this.getList(user, world, PermissionValue.TRUE);
    }

    /**
     * Gets list of all permissions of player with provided value
     * In case of PlaceholderValue.DEFAULT it returns ignores value
     * Ordered from most to least significant
     *
     * @param user Player's UserContext
     * @return List of permissions
     */
    default List<String> getList(UserContext user, PermissionValue value) {
        return this.getList(user, user.getWorld(), value);
    }

    /**
     * Gets list of all permissions of player with provided value
     * In case of PlaceholderValue.DEFAULT it returns ignores value
     * Ordered from most to least significant
     *
     * @param user  Player's UserContext
     * @param world World for check (returns global and local permissions) or null (for global only)
     * @return List of permissions
     */
    List<String> getList(UserContext user, @Nullable ServerWorld world, PermissionValue value);

    /**
     * Gets list of all permissions of player with value of PermissionValue.TRUE
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * Ordered from most to least significant
     *
     * @param user             Player's UserContext
     * @param parentPermission Parent permission
     * @return List of permissions
     */
    default List<String> getList(UserContext user, String parentPermission) {
        return this.getList(user, parentPermission, user.getWorld());
    }

    /**
     * Gets list of all permissions of player with value of PermissionValue.TRUE
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * Ordered from most to least significant
     *
     * @param user             Player's UserContext
     * @param parentPermission Parent permission
     * @param world            World for check (returns global and local permissions) or null (for global only)
     * @return List of permissions
     */
    default List<String> getList(UserContext user, String parentPermission, @Nullable ServerWorld world) {
        return this.getList(user, parentPermission, world, PermissionValue.TRUE);
    }

    /**
     * Gets list of all permissions of player with provided value
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * In case of PlaceholderValue.DEFAULT it returns ignores value
     * Ordered from most to least significant
     *
     * @param user             Player's UserContext
     * @param parentPermission Parent permission
     * @return List of permissions
     */
    default List<String> getList(UserContext user, String parentPermission, PermissionValue value) {
        return this.getList(user, parentPermission, user.getWorld(), value);
    }

    /**
     * Gets list of all permissions of player with provided value
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * In case of PlaceholderValue.DEFAULT it returns ignores value
     * Ordered from most to least significant
     *
     * @param user             Player's UserContext
     * @param parentPermission Parent permission
     * @param world            World for check (returns global and local permissions) or null (for global only)
     * @return List of permissions
     */
    List<String> getList(UserContext user, String parentPermission, @Nullable ServerWorld world, PermissionValue value);

    /**
     * Gets List of permissions of player, that aren't inherited from groups
     * and have value of PermissionValue.TRUE
     * Ordered from most to least significant
     *
     * @param user Player's UserContext
     * @return List of permissions
     */
    default List<String> getListNonInherited(UserContext user) {
        return this.getListNonInherited(user, user.getWorld());
    }

    /**
     * Gets List of permissions of player, that aren't inherited from groups
     * and have value of PermissionValue.TRUE
     * Ordered from most to least significant
     *
     * @param user  Player's UserContext
     * @param world World for check (returns global and local permissions) or null (for global only)
     * @return List of permissions
     */
    default List<String> getListNonInherited(UserContext user, @Nullable ServerWorld world) {
        return this.getListNonInherited(user, world, PermissionValue.TRUE);
    }

    /**
     * Gets List of permissions of player, that aren't inherited from groups
     * In case of PlaceholderValue.DEFAULT, it returns all permissions
     * Ordered from most to least significant
     *
     * @param user  Player's UserContext
     * @param value Value of permission
     * @return List of permissions
     */
    default List<String> getListNonInherited(UserContext user, PermissionValue value) {
        return this.getListNonInherited(user, user.getWorld(), value);
    }

    /**
     * Gets List of permissions of player, that aren't inherited from groups
     * In case of PlaceholderValue.DEFAULT, it returns all permissions
     * Ordered from most to least significant
     *
     * @param user  Player's UserContext
     * @param world World for check (returns global and local permissions) or null (for global only)
     * @param value Value of permission
     * @return List of permissions
     */
    List<String> getListNonInherited(UserContext user, @Nullable ServerWorld world, PermissionValue value);

    /**
     * Gets list of all permissions of player with value of PermissionValue.TRUE, that aren't inherited from groups
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * Ordered from most to least significant
     *
     * @param user             Player's UserContext
     * @param parentPermission Parent permission
     * @return List of permissions
     */
    default List<String> getListNonInherited(UserContext user, String parentPermission) {
        return this.getListNonInherited(user, parentPermission, user.getWorld());
    }

    /**
     * Gets list of all permissions of player with value of PermissionValue.TRUE, that aren't inherited from groups
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * Ordered from most to least significant
     *
     * @param user             Player's UserContext
     * @param parentPermission Parent permission
     * @param world            World for check (returns global and local permissions) or null (for global only)
     * @return List of permissions
     */
    default List<String> getListNonInherited(UserContext user, String parentPermission, @Nullable ServerWorld world) {
        return this.getListNonInherited(user, parentPermission, world, PermissionValue.TRUE);
    }

    /**
     * Gets list of all permissions of player with provided value, that aren't inherited from groups
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * In case of PlaceholderValue.DEFAULT it returns ignores value
     * Ordered from most to least significant
     *
     * @param user             Player's UserContext
     * @param parentPermission Parent permission
     * @return List of permissions
     */
    default List<String> getListNonInherited(UserContext user, String parentPermission, PermissionValue value) {
        return this.getListNonInherited(user, parentPermission, user.getWorld(), value);
    }

    /**
     * Gets list of all permissions of player with provided value, that aren't inherited from groups
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * In case of PlaceholderValue.DEFAULT it returns ignores value
     * Ordered from most to least significant
     *
     * @param user             Player's UserContext
     * @param parentPermission Parent permission
     * @param world            World for check (returns global and local permissions) or null (for global only)
     * @return List of permissions
     */
    List<String> getListNonInherited(UserContext user, String parentPermission, @Nullable ServerWorld world, PermissionValue value);

    /**
     * Gets map of all permissions with their values for user
     * Should be ordered from most to least significant
     *
     * @param user Player's UserContext
     * @return Map of permissions and values
     */
    default Map<String, PermissionValue> getAll(UserContext user) {
        return this.getAll(user, user.getWorld());
    }

    /**
     * Gets map of all permissions with their values for user
     * Should be ordered from most to least significant
     *
     * @param user  Player's UserContext
     * @param world World for check (returns global and local permissions) or null (for global only)
     * @return Map of permissions and values
     */
    Map<String, PermissionValue> getAll(UserContext user, @Nullable ServerWorld world);

    /**
     * Gets map of all permissions with their values for user
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * Should be ordered from most to least significant
     *
     * @param user             Player's UserContext
     * @param parentPermission Parent permission
     * @return Map of permissions and values
     */
    default Map<String, PermissionValue> getAll(UserContext user, String parentPermission) {
        return this.getAll(user, parentPermission, user.getWorld());
    }

    /**
     * Gets map of all permissions with their values for user
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * Should be ordered from most to least significant
     *
     * @param user             Player's UserContext
     * @param parentPermission Parent permission
     * @param world            World for check (returns global and local permissions) or null (for global only)
     * @return Map of permissions and values
     */
    Map<String, PermissionValue> getAll(UserContext user, String parentPermission, @Nullable ServerWorld world);

    /**
     * Gets map of all non inherited permissions with their values for user
     * Should be ordered from most to least significant
     *
     * @param user Player's UserContext
     * @return Map of permissions and values
     */
    default Map<String, PermissionValue> getAllNonInherited(UserContext user) {
        return this.getAllNonInherited(user, user.getWorld());
    }

    /**
     * Gets map of all non inherited permissions with their values for user
     * Should be ordered from most to least significant
     *
     * @param user  Player's UserContext
     * @param world World for check (returns global and local permissions) or null (for global only)
     * @return Map of permissions and values
     */
    Map<String, PermissionValue> getAllNonInherited(UserContext user, @Nullable ServerWorld world);

    /**
     * Gets map of all non inherited permissions with their values for user
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * Should be ordered from most to least significant
     *
     * @param user             Player's UserContext
     * @param parentPermission Parent permission
     * @return Map of permissions and values
     */
    default Map<String, PermissionValue> getAllNonInherited(UserContext user, String parentPermission) {
        return this.getAllNonInherited(user, user.getWorld());
    }

    /**
     * Gets map of all non inherited permissions with their values for user
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * Should be ordered from most to least significant
     *
     * @param user             Player's UserContext
     * @param parentPermission Parent permission
     * @param world            World for check (returns global and local permissions) or null (for global only)
     * @return Map of permissions and values
     */
    Map<String, PermissionValue> getAllNonInherited(UserContext user, String parentPermission, @Nullable ServerWorld world);

    /**
     * This methods tries to read permission as value with usage of provided adapter.
     * Value permission needs to be set to PermissionValue.TRUE and have format of
     * `permission.value`
     *
     * @param user         User permission is checked for
     * @param permission   Base permission
     * @param defaultValue Default value if not provided
     * @param adapter      Default value adapter
     * @return Highest value;
     */
    default <V> V getAsValue(UserContext user, String permission, V defaultValue, ValueAdapter<V> adapter) {
        return getValueFrom(this.getList(user, permission, user.getWorld()), defaultValue, adapter);
    }

    /**
     * This methods tries to read permission as value with usage of provided adapter.
     * Value permission needs to be set to PermissionValue.TRUE and have format of
     * `permission.value`
     *
     * @param user         User permission is checked for
     * @param permission   Base permission
     * @param world        World for check (returns global and local permissions) or null (for global only)
     * @param defaultValue Default value if not provided
     * @param adapter      Default value adapter
     * @return Highest value;
     */
    default <V> V getAsValue(UserContext user, String permission, @Nullable ServerWorld world, V defaultValue, ValueAdapter<V> adapter) {
        return getValueFrom(this.getList(user, permission, world), defaultValue, adapter);
    }

    /**
     * This methods tries to read non inherited permission as value with usage of provided adapter.
     * Value permission needs to be set to PermissionValue.TRUE and have format of
     * `permission.value`
     *
     * @param user         User permission is checked for
     * @param permission   Base permission
     * @param defaultValue Default value if not provided
     * @param adapter      Default value adapter
     * @return Highest value;
     */
    default <V> V getAsValueNonInherited(UserContext user, String permission, V defaultValue, ValueAdapter<V> adapter) {
        return getValueFrom(this.getListNonInherited(user, permission, user.getWorld()), defaultValue, adapter);
    }

    /**
     * This methods tries to read permission as value with usage of provided adapter.
     * Value permission needs to be set to PermissionValue.TRUE and have format of
     * `permission.value`
     *
     * @param user         User permission is checked for
     * @param permission   Base permission
     * @param world        World for check (returns global and local permissions) or null (for global only)
     * @param defaultValue Default value if not provided
     * @param adapter      Default value adapter
     * @return Highest value;
     */
    default <V> V getAsValueNonInherited(UserContext user, String permission, @Nullable ServerWorld world, V defaultValue, ValueAdapter<V> adapter) {
        return getValueFrom(this.getListNonInherited(user, permission, world), defaultValue, adapter);
    }

    /**
     * Sets value of player's placeholder to provided one
     * In case of PlaceholderValue.DEFAULT, it gets removed from player
     *
     * @param user  Player's UserContext
     * @param permission Permission to change
     * @param value Value of permission, DEFAULT removes it
     */
    default void set(UserContext user, String permission, PermissionValue value) {
        this.set(user, null, permission, value);
    }

    /**
     * Sets value of player's placeholder to provided one
     * In case of PlaceholderValue.DEFAULT, it gets removed from player
     *
     * @param user  Player's UserContext
     * @param permission Permission to change
     * @param world Permission world (local permission) or null (for global)
     * @param value Value of permission, DEFAULT removes it
     */
    void set(UserContext user, @Nullable ServerWorld world, String permission, PermissionValue value);

    /**
     * Sets value of player's placeholder to provided one with specific duration
     * In case of PlaceholderValue.DEFAULT, it gets removed from player
     *
     * @param user     Player's UserContext
     * @param permission Permission to change
     * @param value    Value of permission, DEFAULT removes it
     * @param duration Duration of permission
     */
    default void set(UserContext user, String permission, PermissionValue value, Duration duration) {
        this.set(user, null, permission, value, duration);
    }

    /**
     * Sets value of player's placeholder to provided one with specific duration
     * In case of PlaceholderValue.DEFAULT, it gets removed from player
     *
     * @param user     Player's UserContext
     * @param world    Permission world (local permission) or null (for global)
     * @param permission Permission to change
     * @param value    Value of permission, DEFAULT removes it
     * @param duration Duration of permission
     */
    void set(UserContext user, @Nullable ServerWorld world, String permission, PermissionValue value, Duration duration);

    /**
     * Gets list of groups player is in. Ordered from most to least significant
     *
     * @param user Player's UserContext
     * @return List of groups
     */
    default List<String> getGroups(UserContext user) {
        return this.getGroups(user, user.getWorld());
    }

    /**
     * Gets list of groups player is in. Ordered from most to least significant
     *
     * @param user  Player's UserContext
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @return List of groups
     */
    List<String> getGroups(UserContext user, @Nullable ServerWorld world);

    /**
     * Adds user to group
     *
     * @param user  Player's UserContext
     * @param group Group's name
     */
    default void addGroup(UserContext user, String group) {
        this.addGroup(user, null, group);
    }

    /**
     * Adds user to group
     *
     * @param user  Player's UserContext
     * @param world Permission world (local group) or null (for global)
     * @param group Group's name
     */
    void addGroup(UserContext user, @Nullable ServerWorld world, String group);

    /**
     * Adds user to group for specified duration
     *
     * @param user  Player's UserContext
     * @param group Group's name
     */
    default void addGroup(UserContext user, String group, Duration duration) {
        this.addGroup(user, null, group, duration);
    }

    /**
     * Adds user to group for specified duration
     *
     * @param user  Player's UserContext
     * @param world Permission world (local group) or null (for global)
     * @param group Group's name
     */
    void addGroup(UserContext user, @Nullable ServerWorld world, String group, Duration duration);

    /**
     * Remove user from group
     *
     * @param user  Player's UserContext
     * @param group Group's name
     */
    default void removeGroup(UserContext user, String group) {
        this.removeGroup(user, null, group);
    }

    /**
     * Remove user from group
     *
     * @param user  Player's UserContext
     * @param world Permission world (local group) or null (for global)
     * @param group Group's name
     */
    void removeGroup(UserContext user, @Nullable ServerWorld world, String group);

    /**
     * Checks value of group's permission
     * By default, it checks only for single permission
     * If requested permission ends with a ".*" or ".?" it will return PermissionValue.TRUE
     * when any of child permission is true, PermissionValue.DEFAULT when doesn't exist
     * or PermissionValue.FALSE when negated
     *
     * @param group      Group's name
     * @param permission String of permission
     * @return Corresponding PermissionValue
     */
    default PermissionValue checkGroup(String group, String permission) {
        return this.checkGroup(group, null, permission);
    }

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
    PermissionValue checkGroup(String group, @Nullable ServerWorld world, String permission);

    /**
     * Gets list of all permissions of group with PermissionValue.TRUE
     * Ordered from most to least significant
     *
     * @param group Group's name
     * @return List of permissions
     */
    default List<String> getListGroup(String group) {
        return this.getListGroup(group, (ServerWorld) null);
    }

    /**
     * Gets list of all permissions of group with PermissionValue.TRUE
     * Ordered from most to least significant
     *
     * @param group Group's name
     * @param world Current world (global and local permissions) or null (for global only)
     * @return List of permissions
     */
    default List<String> getListGroup(String group, @Nullable ServerWorld world) {
        return this.getListGroup(group, world, PermissionValue.TRUE);
    }

    /**
     * Gets List of all permissions of group with specific value
     * In case of PlaceholderValue.DEFAULT it returns ignores value and returns all
     * Ordered from most to least significant
     *
     * @param group Group's name
     * @param value Value of permission
     * @return List of permissions
     */
    default List<String> getListGroup(String group, PermissionValue value) {
        return this.getListGroup(group, (ServerWorld) null, value);
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
    List<String> getListGroup(String group, @Nullable ServerWorld world, PermissionValue value);

    /**
     * Gets list of all permissions of group with value of PermissionValue.TRUE
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * Ordered from most to least significant
     *
     * @param group            Group's name
     * @param parentPermission Parent permission
     * @return List of permissions
     */
    default List<String> getListGroup(String group, String parentPermission) {
        return this.getListGroup(group, parentPermission, null, PermissionValue.TRUE);
    }

    /**
     * Gets list of all permissions of group with value of PermissionValue.TRUE
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * Ordered from most to least significant
     *
     * @param group            Group's name
     * @param parentPermission Parent permission
     * @param world            Current player's world (global and local permissions) or null (for global only)
     * @return List of permissions
     */
    default List<String> getListGroup(String group, String parentPermission, @Nullable ServerWorld world) {
        return this.getListGroup(group, parentPermission, world, PermissionValue.TRUE);
    }

    /**
     * Gets list of all permissions of group with provided value
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * In case of PlaceholderValue.DEFAULT it returns ignores value
     * Ordered from most to least significant
     *
     * @param group            Group's name
     * @param parentPermission Parent permission
     * @return List of permissions
     */
    default List<String> getListGroup(String group, String parentPermission, PermissionValue value) {
        return this.getListGroup(group, parentPermission, null, value);
    }

    /**
     * Gets list of all permissions of group with provided value
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * In case of PlaceholderValue.DEFAULT it returns ignores value
     * Ordered from most to least significant
     *
     * @param group            Group's name
     * @param parentPermission Parent permission
     * @param world            Current player's world (global and local permissions) or null (for global only)
     * @return List of permissions
     */
    List<String> getListGroup(String group, String parentPermission, @Nullable ServerWorld world, PermissionValue value);

    /**
     * Gets List of permissions of group, that aren't inherited from other groups
     * and have value of PermissionValue.TRUE
     * Ordered from most to least significant
     *
     * @param group Group's name
     * @return List of permissions
     */
    default List<String> getListNonInheritedGroup(String group) {
        return this.getListNonInheritedGroup(group, (ServerWorld) null);
    }

    /**
     * Gets List of permissions of group, that aren't inherited from other groups
     * and have value of PermissionValue.TRUE
     * Ordered from most to least significant
     *
     * @param group Group's name
     * @param world Current world (global and local permissions) or null (for global only)
     * @return List of permissions
     */
    default List<String> getListNonInheritedGroup(String group, @Nullable ServerWorld world) {
        return this.getListNonInheritedGroup(group, world, PermissionValue.TRUE);
    }

    /**
     * Gets List of permissions of player, that aren't inherited from other groups
     * In case of PlaceholderValue.DEFAULT, it returns all permissions
     * Ordered from most to least significant
     *
     * @param group Group's name
     * @param value Value of permission
     * @return List of permissions
     */
    default List<String> getListNonInheritedGroup(String group, PermissionValue value) {
        return this.getListNonInheritedGroup(group, (ServerWorld) null, value);
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
    List<String> getListNonInheritedGroup(String group, @Nullable ServerWorld world, PermissionValue value);

    /**
     * Gets list of all permissions of group with value of PermissionValue.TRUE, that aren't inherited from other groups
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * Ordered from most to least significant
     *
     * @param group            Group's name
     * @param parentPermission Parent permission
     * @return List of permissions
     */
    default List<String> getListNonInheritedGroup(String group, String parentPermission) {
        return this.getListNonInheritedGroup(group, parentPermission, null, PermissionValue.TRUE);
    }

    /**
     * Gets list of all permissions of group with value of PermissionValue.TRUE, that aren't inherited from other groups
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * Ordered from most to least significant
     *
     * @param group            Group's name
     * @param parentPermission Parent permission
     * @param world            Current player's world (global and local permissions) or null (for global only)
     * @return List of permissions
     */
    default List<String> getListNonInheritedGroup(String group, String parentPermission, @Nullable ServerWorld world) {
        return this.getListNonInheritedGroup(group, parentPermission, world, PermissionValue.TRUE);
    }

    /**
     * Gets list of all permissions of group with provided value, that aren't inherited from other groups
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * In case of PlaceholderValue.DEFAULT it returns ignores value
     * Ordered from most to least significant
     *
     * @param group            Group's name
     * @param parentPermission Parent permission
     * @return List of permissions
     */
    default List<String> getListNonInheritedGroup(String group, String parentPermission, PermissionValue value) {
        return this.getListNonInheritedGroup(group, parentPermission, null, value);
    }

    /**
     * Gets list of all permissions of group with provided value, that aren't inherited from other groups
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * In case of PlaceholderValue.DEFAULT it returns ignores value
     * Ordered from most to least significant
     *
     * @param group            Group's name
     * @param parentPermission Parent permission
     * @param world            Current player's world (global and local permissions) or null (for global only)
     * @return List of permissions
     */
    List<String> getListNonInheritedGroup(String group, String parentPermission, @Nullable ServerWorld world, PermissionValue value);

    /**
     * Gets map of all permissions with their values for group
     * Should be ordered from most to least significant
     *
     * @param group Group's name
     * @return Map of permissions and values
     */
    default Map<String, PermissionValue> getAllGroup(String group) {
        return this.getAllGroup(group, (ServerWorld) null);
    }

    /**
     * Gets map of all permissions with their values for group
     * Should be ordered from most to least significant
     *
     * @param group Group's name
     * @param world World for check (returns global and local permissions) or null (for global only)
     * @return Map of permissions and values
     */
    Map<String, PermissionValue> getAllGroup(String group, @Nullable ServerWorld world);

    /**
     * Gets map of all permissions with their values for group
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * Should be ordered from most to least significant
     *
     * @param group            Group's name
     * @param parentPermission Parent permission
     * @return Map of permissions and values
     */
    default Map<String, PermissionValue> getAllGroup(String group, String parentPermission) {
        return this.getAllGroup(group, parentPermission, null);
    }

    /**
     * Gets map of all permissions with their values for group
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * Should be ordered from most to least significant
     *
     * @param group            Group's name
     * @param parentPermission Parent permission
     * @param world            World for check (returns global and local permissions) or null (for global only)
     * @return Map of permissions and values
     */
    Map<String, PermissionValue> getAllGroup(String group, String parentPermission, @Nullable ServerWorld world);

    /**
     * Gets map of all non inherited permissions with their values for group
     * Should be ordered from most to least significant
     *
     * @param group Group's name
     * @return Map of permissions and values
     */
    default Map<String, PermissionValue> getAllNonInheritedGroup(String group) {
        return this.getAllNonInheritedGroup(group, (ServerWorld) null);
    }

    /**
     * Gets map of all non inherited permissions with their values for group
     * Should be ordered from most to least significant
     *
     * @param group Group's name
     * @param world World for check (returns global and local permissions) or null (for global only)
     * @return Map of permissions and values
     */
    Map<String, PermissionValue> getAllNonInheritedGroup(String group, @Nullable ServerWorld world);

    /**
     * Gets map of all non inherited permissions with their values for group
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * Should be ordered from most to least significant
     *
     * @param group            Group's name
     * @param parentPermission Parent permission
     * @return Map of permissions and values
     */
    default Map<String, PermissionValue> getAllNonInheritedGroup(String group, String parentPermission) {
        return this.getAllNonInheritedGroup(group, parentPermission, null);
    }

    /**
     * Gets map of all non inherited permissions with their values for group
     * and are child of specified permission.
     * Returned permissions have their parent string removed.
     * Should be ordered from most to least significant
     *
     * @param group            Group's name
     * @param parentPermission Parent permission
     * @param world            World for check (returns global and local permissions) or null (for global only)
     * @return Map of permissions and values
     */
    Map<String, PermissionValue> getAllNonInheritedGroup(String group, String parentPermission, @Nullable ServerWorld world);

    /**
     * This methods tries to read permission as value with usage of provided adapter.
     * Value permission needs to be set to PermissionValue.TRUE and have format of
     * `permission.value`
     *
     * @param group        Group's name permission is checked for
     * @param permission   Base permission
     * @param defaultValue Default value if not provided
     * @param adapter      Default value adapter
     * @return Highest value;
     */
    default <V> V getAsValueGroup(String group, String permission, V defaultValue, ValueAdapter<V> adapter) {
        return getValueFrom(this.getListGroup(group, permission, (ServerWorld) null), defaultValue, adapter);
    }

    /**
     * This methods tries to read permission as value with usage of provided adapter.
     * Value permission needs to be set to PermissionValue.TRUE and have format of
     * `permission.value`
     *
     * @param group        Group's name permission is checked for
     * @param permission   Base permission
     * @param world        World for check (returns global and local permissions) or null (for global only)
     * @param defaultValue Default value if not provided
     * @param adapter      Default value adapter
     * @return Highest value;
     */
    default <V> V getAsValueGroup(String group, String permission, @Nullable ServerWorld world, V defaultValue, ValueAdapter<V> adapter) {
        return getValueFrom(this.getListGroup(group, permission, world), defaultValue, adapter);
    }

    /**
     * This methods tries to read non inherited permission as value with usage of provided adapter.
     * Value permission needs to be set to PermissionValue.TRUE and have format of
     * `permission.value`
     *
     * @param group        Group's name permission is checked for
     * @param permission   Base permission
     * @param defaultValue Default value if not provided
     * @param adapter      Default value adapter
     * @return Highest value;
     */
    default <V> V getAsValueNonInheritedGroup(String group, String permission, V defaultValue, ValueAdapter<V> adapter) {
        return getValueFrom(this.getListNonInheritedGroup(group, permission, (ServerWorld) null), defaultValue, adapter);
    }

    /**
     * This methods tries to read permission as value with usage of provided adapter.
     * Value permission needs to be set to PermissionValue.TRUE and have format of
     * `permission.value`
     *
     * @param group        Group's name permission is checked for
     * @param permission   Base permission
     * @param world        World for check (returns global and local permissions) or null (for global only)
     * @param defaultValue Default value if not provided
     * @param adapter      Default value adapter
     * @return Highest value;
     */
    default <V> V getAsValueNonInheritedGroup(String group, String permission, @Nullable ServerWorld world, V defaultValue, ValueAdapter<V> adapter) {
        return getValueFrom(this.getListNonInheritedGroup(group, permission, world), defaultValue, adapter);
    }

    enum Priority {
        /**
         * PermissionProvider should be default, unless already taken
         */
        MAIN,
        /**
         * PermissionProvider should be only used, when there is no main one
         */
        OPTIONAL,
        /**
         * PermissionProvider should be only used for vanilla PermissionProvider
         */
        @ApiStatus.Internal
        FALLBACK
    }
}
