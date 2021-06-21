package eu.pb4.permissions.mixin;

import eu.pb4.permissions.PermissionsAPIMod;
import eu.pb4.permissions.api.PreparationHelper;
import eu.pb4.permissions.impl.ConfigHelper;
import eu.pb4.permissions.impl.VanillaPermissionProvider;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setupServer()Z", shift = At.Shift.BEFORE, ordinal = 0))
    private void setupPermissionsProviders(CallbackInfo info) {
        PreparationHelper.run((MinecraftServer) (Object) this);
    }

    @Inject(method = "reloadResources", at = @At("HEAD"))
    private void startResourceReload(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        if (VanillaPermissionProvider.getInstance() != null) {
            VanillaPermissionProvider.getInstance().setConfig(ConfigHelper.getConfig());
            PermissionsAPIMod.LOGGER.info("Permissions reloaded!");
        }
    }
}
