package eu.pb4.permissions.api.context;

import com.mojang.authlib.GameProfile;
import eu.pb4.permissions.mixin.ServerCommandSourceAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

record CommandSourceUserContext(ServerCommandSource source, ServerWorld world) implements UserContext {
    @Override
    public int getPermissionLevel() {
        return ((ServerCommandSourceAccessor) this.source).getPermissionLevel();
    }

    @Override
    public GameProfile getGameProfile() {
        return UserContext.CONSOLE_GAME_PROFILE;
    }

    @Override
    public UUID getUuid() {
        return Util.NIL_UUID;
    }

    @Override
    public @Nullable
    ServerPlayerEntity getPlayerEntity() {
        return null;
    }

    @Override
    public @Nullable
    Entity getEntity() {
        return null;
    }

    @Override
    public @Nullable
    ServerWorld getWorld() {
        return this.world;
    }
}
