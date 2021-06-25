package eu.pb4.permissions.api.v0;

import eu.pb4.permissions.impl.PermissionsImpl;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@SuppressWarnings({"null", "unused"})
public class Permissions {
    /**
     * Returns default permission provider.
     */
    public static PermissionProvider get() {
        return PermissionsImpl.get();
    }

    /**
     * Gets permission provider basing on Identifier
     *
     * @param identifier Mods identifier
     * @return PermissionProvider or null
     */
    public static PermissionProvider getById(String identifier) {
        return PermissionsImpl.getById(identifier);
    }

    /**
     * Creates a predicate, which returns the result of permission check,
     * Falling back to operator level one.
     *
     * @param permission           Required permission
     * @param defaultRequiredLevel Otherwise required operator level
     * @return Boolean indication if user have permission
     */
    public static Predicate<ServerCommandSource> require(String permission, int defaultRequiredLevel) {
        return source -> {
            try {
                ServerPlayerEntity player = source.getPlayer();

                return get().check(UserContext.of(player), permission).toBoolean(source.hasPermissionLevel(defaultRequiredLevel));
            } catch (Exception e) {
                return source.hasPermissionLevel(defaultRequiredLevel);
            }
        };
    }

    /**
     * Creates a predicate, which returns the result of permission check,
     * Falling back to operator level one.
     *
     * @param permission      Required permission
     * @param playerByDefault If player should be allowed by default
     * @return Boolean indication if user have permission
     */
    public static Predicate<ServerCommandSource> require(String permission, boolean playerByDefault, boolean consoleByDefault) {
        return source -> {
            try {
                ServerPlayerEntity player = source.getPlayer();

                return get().check(UserContext.of(player), permission).toBoolean(playerByDefault);
            } catch (Exception e) {
                return consoleByDefault;
            }
        };
    }

    /**
     * Creates a predicate, which returns the result of permission check,
     * And default value
     *
     * @param permission      Required permission
     * @param playerByDefault If player should be allowed by default
     * @return Boolean indication if user have permission
     */
    public static Predicate<ServerCommandSource> require(String permission, boolean playerByDefault) {
        return require(permission, playerByDefault, true);
    }


    // Redirects start here!

    /**
     * Name of the provider
     */
    public static String getName() {
        return get().getName();
    }

    /**
     * Identifier of the provider
     */
    public static String getIdentifier() {
        return get().getIdentifier();
    }

    /**
     * Returns true, if provider supports groups
     */
    public static boolean supportsGroups() {
        return get().supportsGroups();
    }

    /**
     * Returns true, if provider supports temporary permissions
     */
    public static boolean supportsTemporaryPermissions() {
        return get().supportsTemporaryPermissions();
    }

    /**
     * Returns true, if provider supports groups
     */
    public static boolean supportsTimedGroups() {
        return get().supportsGroups();
    }

    /**
     * Returns true, if provider supports per world permissions
     */
    public static boolean supportsPerWorldPermissions() {
        return get().supportsTemporaryPermissions();
    }

    /**
     * Returns true, if provider supports per world groups
     */
    public static boolean supportsPerWorldGroups() {
        return get().supportsPerWorldGroups();
    }

    /**
     * Returns true, if provider supports checking of permissions for offline players
     */
    public static boolean supportsOfflineChecks() {
        return get().supportsOfflineChecks();
    }

    /**
     * Returns true, if provider supports dynamic changing of players permissions
     */
    public static boolean supportsChangingPlayersPermissions() {
        return get().supportsChangingPlayersPermissions();
    }

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
    public static PermissionValue check(UserContext user, String permission) {
        return get().check(user, permission);
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
     * @param defaultLevel Default otherwise required OP level
     * @return Corresponding PermissionValue
     */
    public static boolean check(UserContext user, String permission, int defaultLevel) {
        return get().check(user, permission, defaultLevel);
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
    public static boolean check(UserContext user, String permission, boolean defaultValue) {
        return get().check(user, permission, defaultValue);
    }

    /**
     * Gets list of all permissions of player with value of PermissionValue.TRUE
     * Ordered from most to least significant
     *
     * @param user Player's UserContext
     * @return List of permissions
     */
    public static List<String> getList(UserContext user) {
        return get().getList(user);
    }

    /**
     * Gets list of all permissions of player with value of PermissionValue.TRUE
     * Ordered from most to least significant
     *
     * @param user  Player's UserContext
     * @param world World for check (returns global and local permissions) or null (for global only)
     * @return List of permissions
     */
    public static List<String> getList(UserContext user, @Nullable ServerWorld world) {
        return get().getList(user, world);
    }

    /**
     * Gets list of all permissions of player with provided value
     * In case of PlaceholderValue.DEFAULT it returns ignores value
     * Ordered from most to least significant
     *
     * @param user Player's UserContext
     * @return List of permissions
     */
    public static List<String> getList(UserContext user, PermissionValue value) {
        return get().getList(user, value);
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
    public static List<String> getList(UserContext user, @Nullable ServerWorld world, PermissionValue value) {
        return get().getList(user, world, value);
    }

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
    public static List<String> getList(UserContext user, String parentPermission) {
        return get().getList(user, parentPermission);
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
    public static List<String> getList(UserContext user, String parentPermission, @Nullable ServerWorld world) {
        return get().getList(user, parentPermission, world);
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
    public static List<String> getList(UserContext user, String parentPermission, PermissionValue value) {
        return get().getList(user, parentPermission, value);
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
    public static List<String> getList(UserContext user, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
        return get().getList(user, parentPermission, world, value);
    }

    /**
     * Gets List of permissions of player, that aren't inherited from groups
     * and have value of PermissionValue.TRUE
     * Ordered from most to least significant
     *
     * @param user Player's UserContext
     * @return List of permissions
     */
    public static List<String> getListNonInherited(UserContext user) {
        return get().getListNonInherited(user);
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
    public static List<String> getListNonInherited(UserContext user, @Nullable ServerWorld world) {
        return get().getListNonInherited(user, world);
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
    public static List<String> getListNonInherited(UserContext user, PermissionValue value) {
        return get().getListNonInherited(user, value);
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
    public static List<String> getListNonInherited(UserContext user, @Nullable ServerWorld world, PermissionValue value) {
        return get().getListNonInherited(user, world, value);
    }

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
    public static List<String> getListNonInherited(UserContext user, String parentPermission) {
        return get().getListNonInherited(user, parentPermission);
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
    public static List<String> getListNonInherited(UserContext user, String parentPermission, @Nullable ServerWorld world) {
        return get().getListNonInherited(user, parentPermission, world);
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
    public static List<String> getListNonInherited(UserContext user, String parentPermission, PermissionValue value) {
        return get().getListNonInherited(user, parentPermission, value);
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
    public static List<String> getListNonInherited(UserContext user, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
        return get().getListNonInherited(user, parentPermission, world, value);
    }

    /**
     * Gets map of all permissions with their values for user
     * Should be ordered from most to least significant
     *
     * @param user Player's UserContext
     * @return Map of permissions and values
     */
    public static Map<String, PermissionValue> getAll(UserContext user) {
        return get().getAll(user);
    }

    /**
     * Gets map of all permissions with their values for user
     * Should be ordered from most to least significant
     *
     * @param user  Player's UserContext
     * @param world World for check (returns global and local permissions) or null (for global only)
     * @return Map of permissions and values
     */
    public static Map<String, PermissionValue> getAll(UserContext user, @Nullable ServerWorld world) {
        return get().getAll(user, world);
    }

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
    public static Map<String, PermissionValue> getAll(UserContext user, String parentPermission) {
        return get().getAll(user, parentPermission);
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
    public static Map<String, PermissionValue> getAll(UserContext user, String parentPermission, @Nullable ServerWorld world) {
        return get().getAll(user, parentPermission, world);
    }

    /**
     * Gets map of all non inherited permissions with their values for user
     * Should be ordered from most to least significant
     *
     * @param user Player's UserContext
     * @return Map of permissions and values
     */
    public static Map<String, PermissionValue> getAllNonInherited(UserContext user) {
        return get().getAllNonInherited(user);
    }

    /**
     * Gets map of all non inherited permissions with their values for user
     * Should be ordered from most to least significant
     *
     * @param user  Player's UserContext
     * @param world World for check (returns global and local permissions) or null (for global only)
     * @return Map of permissions and values
     */
    public static Map<String, PermissionValue> getAllNonInherited(UserContext user, @Nullable ServerWorld world) {
        return get().getAllNonInherited(user, world);
    }

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
    public static Map<String, PermissionValue> getAllNonInherited(UserContext user, String parentPermission) {
        return get().getAllNonInherited(user, parentPermission);
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
    public static Map<String, PermissionValue> getAllNonInherited(UserContext user, String parentPermission, @Nullable ServerWorld world) {
        return get().getAllNonInherited(user, parentPermission, world);
    }

    /**
     * This methods tries to read permission as value with usage of provided adapter.
     * Value permission needs to be set to PermissionValue.TRUE and have format of
     * `permission.value`
     *
     * @param user         User permission is checked for
     * @param permission   Base permission
     * @param defaultValue Default value if not provided
     * @param adapter      Default value adapter
     * @return Highest value
     */
    public static <V> V getAsValue(UserContext user, String permission, V defaultValue, ValueAdapter<V> adapter) {
        return get().getAsValue(user, permission, defaultValue, adapter);
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
    public static <V> V getAsValue(UserContext user, String permission, @Nullable ServerWorld world, V defaultValue, ValueAdapter<V> adapter) {
        return get().getAsValue(user, permission, world, defaultValue, adapter);
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
    public static <V> V getAsValueNonInherited(UserContext user, String permission, V defaultValue, ValueAdapter<V> adapter) {
        return get().getAsValueNonInherited(user, permission, defaultValue, adapter);
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
    public static <V> V getAsValueNonInherited(UserContext user, String permission, @Nullable ServerWorld world, V defaultValue, ValueAdapter<V> adapter) {
        return get().getAsValueNonInherited(user, permission, world, defaultValue, adapter);
    }

    /**
     * Sets value of player's placeholder to provided one
     * In case of PlaceholderValue.DEFAULT, it gets removed from player
     *
     * @param user       Player's UserContext
     * @param permission Permission to change
     * @param value      Value of permission, DEFAULT removes it
     */
    public static void set(UserContext user, String permission, PermissionValue value) {
        get().set(user, permission, value);
    }

    /**
     * Sets value of player's placeholder to provided one with specific duration
     * In case of PlaceholderValue.DEFAULT, it gets removed from player
     *
     * @param user       Player's UserContext
     * @param permission Permission to change
     * @param value      Value of permission, DEFAULT removes it
     * @param duration   Duration of permission
     */
    public static void set(UserContext user, String permission, PermissionValue value, Duration duration) {
        get().set(user, permission, value, duration);
    }

    /**
     * Gets list of groups player is in. Ordered from most to least significant
     *
     * @param user Player's UserContext
     * @return List of groups
     */
    public static List<String> getGroups(UserContext user) {
        return get().getGroups(user);
    }

    /**
     * Gets list of groups player is in. Ordered from most to least significant
     *
     * @param user  Player's UserContext
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @return List of groups
     */
    public static List<String> getGroups(UserContext user, @Nullable ServerWorld world) {
        return get().getGroups(user, world);
    }

    /**
     * Adds user to group
     *
     * @param user  Player's UserContext
     * @param group Group's name
     */
    public static void addGroup(UserContext user, String group) {
        get().addGroup(user, group);
    }

    /**
     * Adds user to group for specified duration
     *
     * @param user  Player's UserContext
     * @param group Group's name
     */
    public static void addGroup(UserContext user, String group, Duration duration) {
        get().addGroup(user, group, duration);
    }

    /**
     * Remove user from group
     *
     * @param user  Player's UserContext
     * @param group Group's name
     */
    public static void removeGroup(UserContext user, String group) {
        get().removeGroup(user, group);
    }

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
    public static PermissionValue checkGroup(String group, String permission) {
        return get().checkGroup(group, permission);
    }

    /**
     * Gets list of all permissions of group with PermissionValue.TRUE
     * Ordered from most to least significant
     *
     * @param group Group's name
     * @return List of permissions
     */
    public static List<String> getListGroup(String group) {
        return get().getListGroup(group);
    }

    /**
     * Gets list of all permissions of group with PermissionValue.TRUE
     * Ordered from most to least significant
     *
     * @param group Group's name
     * @param world Current world (global and local permissions) or null (for global only)
     * @return List of permissions
     */
    public static List<String> getListGroup(String group, @Nullable ServerWorld world) {
        return get().getListGroup(group, world);
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
    public static List<String> getListGroup(String group, PermissionValue value) {
        return get().getListGroup(group, value);
    }

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
    public static List<String> getListGroup(String group, String parentPermission) {
        return get().getListGroup(group, parentPermission);
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
    public static List<String> getListGroup(String group, String parentPermission, @Nullable ServerWorld world) {
        return get().getListGroup(group, parentPermission, world);
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
    public static List<String> getListGroup(String group, String parentPermission, PermissionValue value) {
        return get().getListGroup(group, parentPermission, value);
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
    public static List<String> getListGroup(String group, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
        return get().getListGroup(group, parentPermission, world, value);
    }

    /**
     * Gets List of permissions of group, that aren't inherited from other groups
     * and have value of PermissionValue.TRUE
     * Ordered from most to least significant
     *
     * @param group Group's name
     * @return List of permissions
     */
    public static List<String> getListNonInheritedGroup(String group) {
        return get().getListNonInheritedGroup(group);
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
    public static List<String> getListNonInheritedGroup(String group, @Nullable ServerWorld world) {
        return get().getListNonInheritedGroup(group, world);
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
    public static List<String> getListNonInheritedGroup(String group, PermissionValue value) {
        return get().getListNonInheritedGroup(group, value);
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
    public static List<String> getListNonInheritedGroup(String group, @Nullable ServerWorld world, PermissionValue value) {
        return get().getListNonInheritedGroup(group, world, value);
    }

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
    public static List<String> getListNonInheritedGroup(String group, String parentPermission) {
        return get().getListNonInheritedGroup(group, parentPermission);
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
    public static List<String> getListNonInheritedGroup(String group, String parentPermission, @Nullable ServerWorld world) {
        return get().getListNonInheritedGroup(group, parentPermission, world);
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
    public static List<String> getListNonInheritedGroup(String group, String parentPermission, PermissionValue value) {
        return get().getListNonInheritedGroup(group, parentPermission, value);
    }

    /**
     * Gets map of all permissions with their values for group
     * Should be ordered from most to least significant
     *
     * @param group Group's name
     * @return Map of permissions and values
     */
    public static Map<String, PermissionValue> getAllGroup(String group) {
        return get().getAllGroup(group);
    }

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
    public static Map<String, PermissionValue> getAllGroup(String group, String parentPermission) {
        return get().getAllGroup(group, parentPermission);
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
    public static Map<String, PermissionValue> getAllGroup(String group, String parentPermission, @Nullable ServerWorld world) {
        return get().getAllGroup(group, parentPermission, world);
    }

    /**
     * Gets map of all non inherited permissions with their values for group
     * Should be ordered from most to least significant
     *
     * @param group Group's name
     * @return Map of permissions and values
     */
    public static Map<String, PermissionValue> getAllNonInheritedGroup(String group) {
        return get().getAllNonInheritedGroup(group);
    }

    /**
     * Gets map of all non inherited permissions with their values for group
     * Should be ordered from most to least significant
     *
     * @param group Group's name
     * @param world World for check (returns global and local permissions) or null (for global only)
     * @return Map of permissions and values
     */
    public static Map<String, PermissionValue> getAllNonInheritedGroup(String group, @Nullable ServerWorld world) {
        return get().getAllNonInheritedGroup(group, world);
    }

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
    public static Map<String, PermissionValue> getAllNonInheritedGroup(String group, String parentPermission) {
        return get().getAllNonInheritedGroup(group, parentPermission);
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
    public static Map<String, PermissionValue> getAllNonInheritedGroup(String group, String parentPermission, @Nullable ServerWorld world) {
        return get().getAllNonInheritedGroup(group, parentPermission, world);
    }

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
    public static <V> V getAsValueGroup(String group, String permission, V defaultValue, ValueAdapter<V> adapter) {
        return get().getAsValueGroup(group, permission, defaultValue, adapter);
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
    public static <V> V getAsValueGroup(String group, String permission, @Nullable ServerWorld world, V defaultValue, ValueAdapter<V> adapter) {
        return get().getAsValueGroup(group, permission, world, defaultValue, adapter);
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
    public static <V> V getAsValueNonInheritedGroup(String group, String permission, V defaultValue, ValueAdapter<V> adapter) {
        return get().getAsValueNonInheritedGroup(group, permission, defaultValue, adapter);
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
    public static <V> V getAsValueNonInheritedGroup(String group, String permission, @Nullable ServerWorld world, V defaultValue, ValueAdapter<V> adapter) {
        return get().getAsValueNonInheritedGroup(group, permission, world, defaultValue, adapter);
    }

    /**
     * Sets value of player's placeholder to provided one
     * In case of PlaceholderValue.DEFAULT, it gets removed from player
     *
     * @param user       Player's UserContext
     * @param permission Permission to change
     * @param world      Permission world (local permission) or null (for global)
     * @param value      Value of permission, DEFAULT removes it
     */
    public static void set(UserContext user, @Nullable ServerWorld world, String permission, PermissionValue value) {
        get().set(user, world, permission, value);
    }

    /**
     * Sets value of player's placeholder to provided one with specific duration
     * In case of PlaceholderValue.DEFAULT, it gets removed from player
     *
     * @param user       Player's UserContext
     * @param world      Permission world (local permission) or null (for global)
     * @param permission Permission to change
     * @param value      Value of permission, DEFAULT removes it
     * @param duration   Duration of permission
     */
    public static void set(UserContext user, @Nullable ServerWorld world, String permission, PermissionValue value, Duration duration) {
        get().set(user, world, permission, value, duration);
    }

    /**
     * Adds user to group
     *
     * @param user  Player's UserContext
     * @param world Permission world (local group) or null (for global)
     * @param group Group's name
     */
    public static void addGroup(UserContext user, @Nullable ServerWorld world, String group) {
        get().addGroup(user, world, group);
    }

    /**
     * Adds user to group for specified duration
     *
     * @param user  Player's UserContext
     * @param world Permission world (local group) or null (for global)
     * @param group Group's name
     */
    public static void addGroup(UserContext user, @Nullable ServerWorld world, String group, Duration duration) {
        get().addGroup(user, world, group, duration);
    }

    /**
     * Remove user from group
     *
     * @param user  Player's UserContext
     * @param world Permission world (local group) or null (for global)
     * @param group Group's name
     */
    public static void removeGroup(UserContext user, @Nullable ServerWorld world, String group) {
        get().removeGroup(user, world, group);
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
    public static PermissionValue checkGroup(String group, @Nullable ServerWorld world, String permission) {
        return get().checkGroup(group, world, permission);
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
    public static List<String> getListGroup(String group, @Nullable ServerWorld world, PermissionValue value) {
        return get().getListGroup(group, world, value);
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
    public static List<String> getListNonInheritedGroup(String group, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
        return get().getListNonInheritedGroup(group, parentPermission, world, value);
    }

    /**
     * Gets map of all permissions with their values for group
     * Should be ordered from most to least significant
     *
     * @param group Group's name
     * @param world World for check (returns global and local permissions) or null (for global only)
     * @return Map of permissions and values
     */
    public static Map<String, PermissionValue> getAllGroup(String group, @Nullable ServerWorld world) {
        return get().getAllGroup(group, world);
    }
}
