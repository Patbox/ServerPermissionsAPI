package eu.pb4.permissions.api.context;

import com.mojang.authlib.GameProfile;
import eu.pb4.permissions.mixin.EntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

record PlayerUserContext(ServerPlayerEntity player, ServerWorld world) implements UserContext {

    @Override
    public int getPermissionLevel() {
        return ((EntityAccessor) this.player).invokeGetPermissionLevel();
    }

    @Override
    public GameProfile getGameProfile() {
        return this.player.getGameProfile();
    }

    @Override
    public UUID getUuid() {
        return this.player.getUuid();
    }

    @Override
    public @Nullable
    ServerPlayerEntity getPlayerEntity() {
        return this.player;
    }

    @Override
    public @Nullable
    Entity getEntity() {
        return this.player;
    }

    @Override
    public @Nullable
    ServerWorld getWorld() {
        return this.world;
    }
}
