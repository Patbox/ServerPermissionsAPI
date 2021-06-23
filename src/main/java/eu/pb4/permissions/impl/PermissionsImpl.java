package eu.pb4.permissions.impl;

import eu.pb4.permissions.PermissionsAPIMod;
import eu.pb4.permissions.api.v1.PermissionProvider;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApiStatus.Internal
public final class PermissionsImpl {
    static final Map<String, PermissionProvider> PROVIDERS = new HashMap<>();
    static PermissionProvider DEFAULT_PROVIDER = null;
    private static boolean DONE = false;

    public static PermissionProvider get() {
        return DEFAULT_PROVIDER;
    }

    public static PermissionProvider getById(String identifier) {
        return PROVIDERS.get(identifier);
    }

    @ApiStatus.Internal
    public static void setup(MinecraftServer server) {
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
        PermissionProvider selectedProvider;

        VanillaConfig config = ConfigHelper.getConfig();

        selectedProvider = providerMap.get(config.defaultProvider);


        List<String> allProviders = new ArrayList<>();

        allProviders.addAll(mainProvider.stream().map(PermissionProvider::getIdentifier).collect(Collectors.toList()));
        allProviders.addAll(optionalProvider.stream().map(PermissionProvider::getIdentifier).collect(Collectors.toList()));

        if (mainProvider.size() > 0) {
            PermissionsAPIMod.LOGGER.info(String.format("Registered main providers (%s): %s",
                    mainProvider.size(), mainProvider.stream().map(provider -> String.format("%s (%s)", provider.getName(), provider.getIdentifier())).collect(Collectors.joining(", "))
            ));

            if (selectedProvider == null || selectedProvider.getPriority() != PermissionProvider.Priority.MAIN) {
                selectedProvider = mainProvider.get(0);
            }

            if (mainProvider.size() > 1) {
                PermissionsAPIMod.LOGGER.warn("There are registered more than one main providers! This should be avoided if possible!");
            }
        }

        if (optionalProvider.size() > 0) {
            PermissionsAPIMod.LOGGER.info(String.format("Registered optional providers (%s): %s",
                    optionalProvider.size(),
                    optionalProvider.stream().map(provider -> String.format("%s (%s)", provider.getName(), provider.getIdentifier())).collect(Collectors.joining(", "))));
            if (selectedProvider == null || selectedProvider.getPriority() == PermissionProvider.Priority.FALLBACK) {
                selectedProvider = optionalProvider.get(0);
            }
        }

        config._availableProviders = "Available providers: " + String.join(", ", allProviders);

        String oldProvider = config.defaultProvider;

        if (selectedProvider == null) {
            selectedProvider = VanillaPermissionProvider.createInstance(server, config);
            config.defaultProvider = "vanilla";
            ConfigHelper.saveConfig(config);
        } else {
            config.defaultProvider = selectedProvider.getIdentifier();
            ConfigHelper.saveConfig(config.asSimple());
        }
        if (oldProvider != null && !oldProvider.isEmpty() && !oldProvider.equals(selectedProvider.getIdentifier())) {
            PermissionsAPIMod.LOGGER.warn(String.format("Previous permission provider (%s) was replaced with new one!", oldProvider));
        }
        PermissionsAPIMod.LOGGER.info(String.format("Selected permission provider: %s (%s)", selectedProvider.getName(), selectedProvider.getIdentifier()));
        DEFAULT_PROVIDER = selectedProvider;
        PROVIDERS.putAll(providerMap);
    }
}
