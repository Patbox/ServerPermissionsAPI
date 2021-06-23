package eu.pb4.permissions.api.v1;

import com.mojang.authlib.GameProfile;
import eu.pb4.permissions.impl.context.CommandSourceUserContext;
import eu.pb4.permissions.impl.context.EntityUserContext;
import eu.pb4.permissions.impl.context.GameProfileUserContext;
import eu.pb4.permissions.impl.context.PlayerUserContext;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@SuppressWarnings({"unused"})
public interface UserContext {
    GameProfile CONSOLE_GAME_PROFILE = new GameProfile(Util.NIL_UUID, "Console");

    /**
     * Returns users operator permission level
     *
     * @return Permission level
     */
    int getPermissionLevel();

    /**
     * Returns GameProfile representing user
     *
     * @return GameProfile
     */
    GameProfile getGameProfile();

    /**
     * Returns UUID of user;
     *
     * @return UUID
     */
    UUID getUuid();

    /**
     * Returns ServerPlayerEntity representing user, which can be used for additional lookups
     *
     * @return ServerPlayerEntity of user or null if not present
     */
    @Nullable ServerPlayerEntity getPlayerEntity();

    /**
     * Returns Entity representing user, which can be used for additional lookups
     *
     * @return Entity of user or null if not present
     */
    @Nullable Entity getEntity();

    /**
     * Returns user's world
     *
     * @return ServerWorld of user or null if global
     */
    @Nullable ServerWorld getWorld();


    /**
     * Returns user's world's registry key
     *
     * @return RegistryKey of ServerWorld of user or null if global
     */
    default @Nullable RegistryKey<World> getWorldKey() {
        return this.getWorld() != null ? this.getWorld().getRegistryKey() : null;
    }

    /**
     * Creates UserContext from player's entity
     *
     * @param player ServerPlayerEntity
     * @return UserContext of player
     */
    static UserContext of(ServerPlayerEntity player) {
        return new PlayerUserContext(player, player.getServerWorld());
    }

    /**
     * Creates UserContext from player's entity
     *
     * @param player ServerPlayerEntity
     * @param world Targeted world
     * @return UserContext of player
     */
    static UserContext of(ServerPlayerEntity player, ServerWorld world) {
        return new PlayerUserContext(player, world);
    }

    /**
     * Creates UserContext from game profile
     * This context allows for offline usage, however it can require more time time to complete
     *
     * @param profile GameProfile
     * @return UserContext of game profile
     */
    static UserContext of(GameProfile profile) {
        return new GameProfileUserContext(profile, null);
    }

    /**
     * Creates UserContext from game profile
     * This context allows for offline usage, however it can require more time time to complete
     *
     * @param profile GameProfile
     * @param world Targeted world
     * @return UserContext of player
     */
    static UserContext of(GameProfile profile, ServerWorld world) {
        return new GameProfileUserContext(profile, world);
    }

    /**
     * Creates UserContext from entity
     *
     * @param entity entity
     * @return UserContext of entity
     */
    static UserContext of(Entity entity) {
        if (entity instanceof ServerPlayerEntity player) {
            return new PlayerUserContext(player, player.getServerWorld());
        }
        return new EntityUserContext(entity, (ServerWorld) entity.getEntityWorld());
    }

    /**
     * Creates UserContext from entity
     *
     * @param entity entity
     * @param world Targeted world
     * @return UserContext of entity
     */
    static UserContext of(Entity entity, ServerWorld world) {
        if (entity instanceof ServerPlayerEntity player) {
            return new PlayerUserContext(player, world);
        }
        return new EntityUserContext(entity, world);
    }

    /**
     * Creates UserContext from command source
     *
     * @param source ServerCommandSource
     * @return UserContext of source
     */
    static UserContext of(ServerCommandSource source) {
        try {
            return new PlayerUserContext(source.getPlayer(), source.getWorld());
        } catch (Exception e) {
            try {
                return new EntityUserContext(source.getEntityOrThrow(), source.getWorld());
            } catch (Exception e1) {
                return new CommandSourceUserContext(source, source.getWorld());
            }
        }
    }

    /**
     * Creates UserContext from command source
     *
     * @param source ServerCommandSource
     * @param world Targeted world
     * @return UserContext of source
     */
    static UserContext of(ServerCommandSource source, ServerWorld world) {
        try {
            return new PlayerUserContext(source.getPlayer(), world);
        } catch (Exception e) {
            try {
                return new EntityUserContext(source.getEntityOrThrow(), world);
            } catch (Exception e1) {
                return new CommandSourceUserContext(source, world);
            }
        }
    }
}
