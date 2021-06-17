package eu.pb4.permissions.api;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.function.Predicate;

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
    public static PermissionValue checkUserPermission(GameProfile user, @Nullable ServerWorld world, String permission) {
        return DEFAULT_PROVIDER.checkUserPermission(user, world, permission);
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
    public static List<String> getUserPermissions(GameProfile user, @Nullable ServerWorld world) {
        return DEFAULT_PROVIDER.getUserPermissions(user, world);
    }

    /**
     * Gets List of permissions of player, that aren't inherited from groups
     * and have value of PermissionValue.TRUE
     * Ordered from most to least significant
     *
     * @param user  Player's GameProfile
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @return List of permissions
     */
    public static List<String> getUserPermissions(GameProfile user, @Nullable ServerWorld world, PermissionValue value) {
        return DEFAULT_PROVIDER.getUserPermissions(user, world, value);
    }

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
    public static List<String> getUserPermissions(GameProfile user, String parentPermission, @Nullable ServerWorld world) {
        return getUserPermissions(user, parentPermission, world, PermissionValue.TRUE);
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
    public static List<String> getUserPermissions(GameProfile user, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
        return DEFAULT_PROVIDER.getUserPermissions(user, parentPermission, world, value);
    }


    /**
     * Gets List of permissions of player, that aren't inherited from groups
     * and have value of PermissionValue.TRUE
     *
     *
     * @param user  Player's GameProfile
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @return List of permissions
     */
    public static List<String> getUserSpecificPermissions(GameProfile user, @Nullable ServerWorld world) {
        return DEFAULT_PROVIDER.getUserSpecificPermissions(user, world);
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
    public static List<String> getUserSpecificPermissions(GameProfile user, @Nullable ServerWorld world, PermissionValue value) {
        return DEFAULT_PROVIDER.getUserSpecificPermissions(user, world, value);
    }

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
    public static List<String> getUserSpecificPermissions(GameProfile user, String parentPermission, @Nullable ServerWorld world) {
        return DEFAULT_PROVIDER.getUserSpecificPermissions(user, parentPermission, world);
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
    public static List<String> getUserSpecificPermissions(GameProfile user, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
        return DEFAULT_PROVIDER.getUserSpecificPermissions(user, parentPermission, world, value);
    }


    /**
     * Sets value of player's placeholder to provided one
     * In case of PlaceholderValue.DEFAULT, it gets removed from player
     *
     *
     * @param user  Player's GameProfile
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @param value Value of permission, DEFAULT removes it
     */
    public static void setUserPermission(GameProfile user, @Nullable ServerWorld world, PermissionValue value) {
        DEFAULT_PROVIDER.setUserPermission(user, world, value);
    }

    /**
     * Sets value of player's placeholder to provided one with specific duration
     * In case of PlaceholderValue.DEFAULT, it gets removed from player
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
     * Gets list of groups player is in. Ordered from most significant to least
     *
     * @param user  Player's GameProfile
     * @param world Current player's world (global and local permissions) or null (for global only)
     * @return List of groups
     */
    public static List<String> getUserGroups(GameProfile user, @Nullable ServerWorld world) {
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
    public static PermissionValue checkGroupPermission(String group, @Nullable ServerWorld world, String permission) {
        return DEFAULT_PROVIDER.checkGroupPermission(group, world, permission);
    }

    /**
     * Gets list of all permissions of group with PermissionValue.TRUE
     * Ordered from most to least significant
     *
     * @param group Group's name
     * @param world Current world (global and local permissions) or null (for global only)
     * @return List of permissions
     */
    public static List<String> getGroupPermissions(String group, @Nullable ServerWorld world) {
        return DEFAULT_PROVIDER.getGroupPermissions(group, world);
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
    public static List<String> getGroupPermissions(String group, @Nullable ServerWorld world, PermissionValue value) {
        return DEFAULT_PROVIDER.getGroupPermissions(group, world, value);
    }

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
    public static List<String> getGroupPermissions(String group, String parentPermission, @Nullable ServerWorld world) {
        return DEFAULT_PROVIDER.getGroupPermissions(group, parentPermission, world);
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
    public static List<String> getGroupPermissions(String group, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
        return DEFAULT_PROVIDER.getGroupPermissions(group, parentPermission, world, value);
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
    public static List<String> getGroupSpecificPermissions(String group, @Nullable ServerWorld world) {
        return DEFAULT_PROVIDER.getGroupSpecificPermissions(group, world);
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
    public static List<String> getGroupSpecificPermissions(String group, @Nullable ServerWorld world, PermissionValue value) {
        return DEFAULT_PROVIDER.getGroupSpecificPermissions(group, world, value);
    }

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
    public static  List<String> getGroupSpecificPermissions(String group, String parentPermission, @Nullable ServerWorld world) {
        return DEFAULT_PROVIDER.getGroupSpecificPermissions(group, parentPermission, world);
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
    public static List<String> getGroupSpecificPermissions(String group, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
        return DEFAULT_PROVIDER.getGroupSpecificPermissions(group, parentPermission, world, value);
    }


    /**
     * Creates a predicate, which returns the result of permission check,
     * Falling back to operator level one.
     *
     * @param permission Required permission
     * @param defaultRequiredLevel Otherwise required operator level
     * @return Boolean indication if user have permission
     */
    public static Predicate<ServerCommandSource> require(String permission, int defaultRequiredLevel) {
        return source -> {
            try {
                ServerPlayerEntity player = source.getPlayer();

                return checkUserPermission(player.getGameProfile(), player.getServerWorld(), permission).allow(source.hasPermissionLevel(defaultRequiredLevel));
            } catch (Exception e) {
                return source.hasPermissionLevel(defaultRequiredLevel);
            }
        };
    }

    /**
     * Creates a predicate, which returns the result of permission check,
     * Falling back to operator level one.
     *
     * @param permission Required permission
     * @param playerByDefault If player should be allowed by default
     * @return Boolean indication if user have permission
     */
    public static Predicate<ServerCommandSource> require(String permission, boolean playerByDefault, boolean consoleByDefault) {
        return source -> {
            try {
                ServerPlayerEntity player = source.getPlayer();

                return checkUserPermission(player.getGameProfile(), player.getServerWorld(), permission).allow(playerByDefault);
            } catch (Exception e) {
                return consoleByDefault;
            }
        };
    }

    /**
     * Creates a predicate, which returns the result of permission check,
     * And default value
     *
     * @param permission Required permission
     * @param playerByDefault If player should be allowed by default
     * @return Boolean indication if user have permission
     */
    public static Predicate<ServerCommandSource> require(String permission, boolean playerByDefault) {
        return require(permission, playerByDefault, true);
    }
}
