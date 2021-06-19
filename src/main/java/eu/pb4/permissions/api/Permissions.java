package eu.pb4.permissions.api;

import eu.pb4.permissions.api.context.UserContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;


import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

@SuppressWarnings({"null", "unused"})
public class Permissions {
    static final Map<String, PermissionProvider> PROVIDERS = new HashMap<>();
    static PermissionProvider DEFAULT_PROVIDER = null;

    /**
     * Returns default permission provider.
     */
    public static PermissionProvider get() {
        return DEFAULT_PROVIDER;
    }

    /**
     * Gets permission provider basing on Identifier
     *
     * @param identifier Mods identifier
     * @return PermissionProvider or null
     */
    public static PermissionProvider getById(String identifier) {
        return PROVIDERS.get(identifier);
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

                return get().check(UserContext.of(player), permission).allow(source.hasPermissionLevel(defaultRequiredLevel));
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

                return get().check(UserContext.of(player), permission).allow(playerByDefault);
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
