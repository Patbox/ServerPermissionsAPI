package eu.pb4.permissions.api.context;

import com.mojang.authlib.GameProfile;
import eu.pb4.permissions.mixin.EntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

record GameProfileUserContext(GameProfile profile, ServerWorld world) implements UserContext {

    @Override
    public int getPermissionLevel() {
        return 0;
    }

    @Override
    public GameProfile getGameProfile() {
        return this.profile;
    }

    @Override
    public UUID getUuid() {
        return this.profile.getId();
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
