package eu.pb4.permissions.api;

import com.mojang.authlib.GameProfile;
import eu.pb4.permissions.PermissionsAPIMod;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApiStatus.Internal
public final class PreparationHelper {
    private static boolean DONE = false;

    @ApiStatus.Internal
    public static void run(MinecraftServer server) {
        if (DONE) {
            return;
        }
        DONE = true;
        PermissionProvider current = createVanillaProvider(server);
        List<String> providersNames = new ArrayList<>();
        Permissions.PROVIDERS.put("vanilla", current);
        providersNames.add(current.getName());
        for (EntrypointContainer<PermissionProvider> container : FabricLoader.getInstance().getEntrypointContainers("permission-provider", PermissionProvider.class)) {
            try {
                current = container.getEntrypoint();
                Permissions.PROVIDERS.put(current.getIdentifier(), current);
                providersNames.add(current.getName());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Permissions.DEFAULT_PROVIDER = current;

        PermissionsAPIMod.LOGGER.info("Registered providers: " + String.join(", ", providersNames));
        PermissionsAPIMod.LOGGER.info("Selected: " + current.getName());
    }

    private static PermissionProvider createVanillaProvider(MinecraftServer server) {
        return new PermissionProvider() {
            @Override
            public String getName() {
                return "Vanilla";
            }

            @Override
            public String getIdentifier() {
                return "vanilla";
            }

            @Override
            public boolean supportsGroups() {
                return false;
            }

            @Override
            public boolean supportsTimedPermissions() {
                return false;
            }

            @Override
            public boolean supportsTimedGroups() {
                return false;
            }

            @Override
            public boolean supportsPerWorldPermissions() {
                return false;
            }

            @Override
            public boolean supportsPerWorldGroups() {
                return false;
            }

            @Override
            public PermissionValue checkUserPermission(GameProfile user, @Nullable ServerWorld world, String permission) {
                OperatorEntry entry = server.getPlayerManager().getOpList().get(user);
                return entry != null && entry.getPermissionLevel() == 4 ? PermissionValue.TRUE : PermissionValue.DEFAULT;
            }

            @Override
            public List<String> getUserPermissions(GameProfile user, @Nullable ServerWorld world, PermissionValue value) {
                OperatorEntry entry = server.getPlayerManager().getOpList().get(user);
                return entry != null && entry.getPermissionLevel() == 4 ? List.of("*") : Collections.EMPTY_LIST;
            }

            @Override
            public List<String> getUserPermissions(GameProfile user, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
                return this.getUserPermissions(user, world, value);
            }

            @Override
            public List<String> getUserSpecificPermissions(GameProfile user, @Nullable ServerWorld world, PermissionValue value) {
                return this.getUserPermissions(user, world, value);
            }

            @Override
            public List<String> getUserSpecificPermissions(GameProfile user, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
                return this.getUserPermissions(user, world, value);
            }

            @Override
            public void setUserPermission(GameProfile user, @Nullable ServerWorld world, PermissionValue value) {

            }

            @Override
            public void setUserPermission(GameProfile user, @Nullable ServerWorld world, PermissionValue value, Duration duration) {

            }

            @Override
            public List<String> getUserGroups(GameProfile user, @Nullable ServerWorld world) {
                OperatorEntry entry = server.getPlayerManager().getOpList().get(user);
                List<String> list = new ArrayList<>();
                if (entry != null) {
                    for (int x = 1; x <= entry.getPermissionLevel(); x++) {
                        list.add("operator-" + x);
                    }
                }

                return list;
            }

            @Override
            public void addUserGroup(GameProfile user, @Nullable ServerWorld world, String group) {

            }

            @Override
            public void addUserGroup(GameProfile user, @Nullable ServerWorld world, String group, Duration duration) {

            }

            @Override
            public void removeUserGroup(GameProfile user, @Nullable ServerWorld world, String group) {

            }

            @Override
            public PermissionValue checkGroupPermission(String group, @Nullable ServerWorld world, String permission) {
                return PermissionValue.DEFAULT;
            }

            @Override
            public List<String> getGroupPermissions(String group, @Nullable ServerWorld world, PermissionValue value) {
                return Collections.EMPTY_LIST;
            }

            @Override
            public List<String> getGroupPermissions(String group, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
                return Collections.EMPTY_LIST;
            }

            @Override
            public List<String> getGroupSpecificPermissions(String group, @Nullable ServerWorld world, PermissionValue value) {
                return Collections.EMPTY_LIST;
            }

            @Override
            public List<String> getGroupSpecificPermissions(String group, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
                return Collections.EMPTY_LIST;
            }
        };
    }
}
