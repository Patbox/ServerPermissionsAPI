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
import java.util.*;
import java.util.stream.Collectors;

@ApiStatus.Internal
public final class PreparationHelper {
    private static boolean DONE = false;

    @ApiStatus.Internal
    public static void run(MinecraftServer server) {
        if (DONE) {
            return;
        }
        DONE = true;
        HashMap<String, PermissionProvider> providerMap = new HashMap<>();

        ArrayList<PermissionProvider> mainProvider = new ArrayList<>();
        ArrayList<PermissionProvider> optionalProvider = new ArrayList<>();

        for (EntrypointContainer<PermissionProvider> container : FabricLoader.getInstance().getEntrypointContainers("permission-provider", PermissionProvider.class)) {
            try {
                PermissionProvider provider = container.getEntrypoint();
                providerMap.put(provider.getIdentifier(), provider);

                switch (provider.getPriority()) {
                    case MAIN -> mainProvider.add(provider);
                    case OPTIONAL -> optionalProvider.add(provider);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        PermissionProvider selectedProvider = null;

        if (mainProvider.size() > 0) {
            PermissionsAPIMod.LOGGER.info(String.format("Registered main providers (%s): %s",
                    mainProvider.size(),
                    mainProvider.stream().map(provider -> String.format("%s (%s)", provider.getName(), provider.getIdentifier())).collect(Collectors.joining(", "))));
            selectedProvider = mainProvider.get(0);
            if (mainProvider.size() > 1) {
                PermissionsAPIMod.LOGGER.warn("There are registered more than one main providers! This should be avoided if possible!");
            }
        }


        if (optionalProvider.size() > 0) {
            PermissionsAPIMod.LOGGER.info(String.format("Registered optional providers (%s): %s",
                    optionalProvider.size(),
                    optionalProvider.stream().map(provider -> String.format("%s (%s)", provider.getName(), provider.getIdentifier())).collect(Collectors.joining(", "))));
            if (selectedProvider == null) {
                selectedProvider = optionalProvider.get(0);
            }
        }

        if (selectedProvider == null) {
            selectedProvider = createVanillaProvider(server);
        }

        PermissionsAPIMod.LOGGER.info("Selected: " + selectedProvider.getName());
        Permissions.DEFAULT_PROVIDER = selectedProvider;
        Permissions.PROVIDERS.putAll(providerMap);
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
            public Priority getPriority() {
                return Priority.FALLBACK;
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
            public Map<String, PermissionValue> getAll(UserContext user, @Nullable ServerWorld world) {
                return Collections.EMPTY_MAP;
            }

            @Override
            public Map<String, PermissionValue> getAll(UserContext user, String parentPermission, @Nullable ServerWorld world) {
                return Collections.EMPTY_MAP;
            }

            @Override
            public Map<String, PermissionValue> getAllInherited(UserContext user, @Nullable ServerWorld world) {
                return Collections.EMPTY_MAP;
            }

            @Override
            public Map<String, PermissionValue> getAllInherited(UserContext user, String parentPermission, @Nullable ServerWorld world) {
                return Collections.EMPTY_MAP;
            }

            @Override
            public void set(UserContext user, @Nullable ServerWorld world, String permission, PermissionValue value) {

            }

            @Override
            public void set(UserContext user, @Nullable ServerWorld world, String permission, PermissionValue value, Duration duration) {

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

            @Override
            public Map<String, PermissionValue> getAllGroup(String group, @Nullable ServerWorld world) {
                return Collections.EMPTY_MAP;
            }

            @Override
            public Map<String, PermissionValue> getAllGroup(String group, String parentPermission, @Nullable ServerWorld world) {
                return Collections.EMPTY_MAP;
            }

            @Override
            public Map<String, PermissionValue> getAllInheritedGroup(String group, @Nullable ServerWorld world) {
                return Collections.EMPTY_MAP;
            }

            @Override
            public Map<String, PermissionValue> getAllInheritedGroup(String group, String parentPermission, @Nullable ServerWorld world) {
                return Collections.EMPTY_MAP;
            }

        };
    }
}
