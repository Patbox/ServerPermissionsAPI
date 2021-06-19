package eu.pb4.permissions.api.context;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
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
     * Creates UserContext from player's entity
     *
     * @param player ServerPlayerEntity
     * @return UserContext of player
     */
    static UserContext of(ServerPlayerEntity player) {
        return new PlayerUserContext(player);
    }

    /**
     * Creates UserContext from entity
     *
     * @param entity entity
     * @return UserContext of entity
     */
    static UserContext of(Entity entity) {
        if (entity instanceof ServerPlayerEntity player) {
            return new PlayerUserContext(player);
        }
        return new EntityUserContext(entity);
    }

    /**
     * Creates UserContext from command source
     *
     * @param source ServerCommandSource
     * @return UserContext of source
     */
    static UserContext of(ServerCommandSource source) {
        try {
            return new PlayerUserContext(source.getPlayer());
        } catch (Exception e) {
            try {
                return new EntityUserContext(source.getEntityOrThrow());
            } catch (Exception e1) {
                return new CommandSourceUserContext(source);
            }
        }
    }
}
