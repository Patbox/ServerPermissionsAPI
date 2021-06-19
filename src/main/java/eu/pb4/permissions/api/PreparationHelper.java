package eu.pb4.permissions.api;

import eu.pb4.permissions.PermissionsAPIMod;
import eu.pb4.permissions.api.context.UserContext;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.server.MinecraftServer;
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
            public PermissionValue check(UserContext user, String permission) {
                return user.getPermissionLevel() >= 4 ? PermissionValue.TRUE : PermissionValue.DEFAULT;
            }

            @Override
            public List<String> getList(UserContext user, @Nullable ServerWorld world, PermissionValue value) {
                return Collections.EMPTY_LIST;
            }

            @Override
            public List<String> getList(UserContext user, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
                return Collections.EMPTY_LIST;
            }

            @Override
            public List<String> getListNonInherited(UserContext user, @Nullable ServerWorld world, PermissionValue value) {
                return Collections.EMPTY_LIST;
            }

            @Override
            public List<String> getListNonInherited(UserContext user, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
                return Collections.EMPTY_LIST;
            }

            @Override
            public void set(UserContext user, @Nullable ServerWorld world, PermissionValue value) {

            }

            @Override
            public void set(UserContext user, @Nullable ServerWorld world, PermissionValue value, Duration duration) {

            }

            @Override
            public List<String> getGroups(UserContext user, @Nullable ServerWorld world) {
                return Collections.EMPTY_LIST;
            }

            @Override
            public void addGroup(UserContext user, @Nullable ServerWorld world, String group) {

            }

            @Override
            public void addGroup(UserContext user, @Nullable ServerWorld world, String group, Duration duration) {

            }

            @Override
            public void removeGroup(UserContext user, @Nullable ServerWorld world, String group) {

            }

            @Override
            public PermissionValue checkGroup(String group, @Nullable ServerWorld world, String permission) {
                return PermissionValue.DEFAULT;
            }

            @Override
            public List<String> getListGroup(String group, @Nullable ServerWorld world, PermissionValue value) {
                return Collections.EMPTY_LIST;
            }

            @Override
            public List<String> getListGroup(String group, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
                return Collections.EMPTY_LIST;
            }

            @Override
            public List<String> getListNonInheritedGroup(String group, @Nullable ServerWorld world, PermissionValue value) {
                return Collections.EMPTY_LIST;
            }

            @Override
            public List<String> getListNonInheritedGroup(String group, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
                return Collections.EMPTY_LIST;
            }

        };
    }
}
