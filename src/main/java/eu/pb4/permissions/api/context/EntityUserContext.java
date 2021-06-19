package eu.pb4.permissions.api.context;

import com.mojang.authlib.GameProfile;
import eu.pb4.permissions.mixin.EntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

class EntityUserContext implements UserContext {
    private final GameProfile gameProfile;
    private final Entity entity;

    EntityUserContext(Entity entity) {
        this.entity = entity;
        this.gameProfile = new GameProfile(this.entity.getUuid(), this.entity.getEntityName());
    }


    @Override
    public int getPermissionLevel() {
        return ((EntityAccessor) this.entity).invokeGetPermissionLevel();
    }

    @Override
    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    @Override
    public UUID getUuid() {
        return this.entity.getUuid();
    }

    @Override
    public @Nullable
    ServerPlayerEntity getPlayerEntity() {
        return null;
    }

    @Override
    public @Nullable
    Entity getEntity() {
        return this.entity;
    }

    @Override
    public @Nullable
    ServerWorld getWorld() {
        return (this.entity.getEntityWorld() instanceof ServerWorld world) ? world : null;
    }
}
