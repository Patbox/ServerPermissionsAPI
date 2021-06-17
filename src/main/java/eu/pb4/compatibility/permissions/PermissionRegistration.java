package eu.pb4.compatibility.permissions;

import com.mojang.authlib.GameProfile;
import eu.pb4.compatibility.CompatibilityMod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public final class PermissionRegistration {
    private static boolean DONE = false;
    private static Set<Event> EVENTS = new HashSet<>();

    public static void register(Event event) {
        EVENTS.add(event);
    }

    @ApiStatus.Internal
    public static void run(MinecraftServer server) {
        if (DONE) {
            return;
        }
        DONE = true;
        PermissionProvider current = createVanillaProvider(server);
        Permissions.PROVIDERS.put(current.getIdentifier(), current);
        for (Event event : EVENTS) {
            try {
                current = event.create(server, current);
                Permissions.PROVIDERS.put(current.getIdentifier(), current);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Permissions.DEFAULT_PROVIDER = current;

        CompatibilityMod.LOGGER.info("Selected permission provider: " + current.getName());
    }

    @FunctionalInterface
    public interface Event {
        PermissionProvider create(MinecraftServer server, PermissionProvider currentProvider);
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
            public Set<String> getUserPermissions(GameProfile user, @Nullable ServerWorld world, PermissionValue value) {
                OperatorEntry entry = server.getPlayerManager().getOpList().get(user);
                return entry != null && entry.getPermissionLevel() == 4 ? Set.of("*") : Collections.EMPTY_SET;
            }

            @Override
            public Set<String> getUserSpecificPermissions(GameProfile user, @Nullable ServerWorld world, PermissionValue value) {
                OperatorEntry entry = server.getPlayerManager().getOpList().get(user);
                return entry != null && entry.getPermissionLevel() == 4 ? Set.of("*") : Collections.EMPTY_SET;
            }

            @Override
            public void setUserPermission(GameProfile user, @Nullable ServerWorld world, PermissionValue value) {

            }

            @Override
            public void setUserPermission(GameProfile user, @Nullable ServerWorld world, PermissionValue value, Duration duration) {

            }

            @Override
            public Set<String> getUserGroups(GameProfile user, @Nullable ServerWorld world) {
                OperatorEntry entry = server.getPlayerManager().getOpList().get(user);
                Set<String> set = new HashSet<>();
                if (entry != null) {
                    for (int x = 1; x <= entry.getPermissionLevel(); x++) {
                        set.add("operator-" + x);
                    }
                }

                return set;
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
            public Set<String> getGroupPermissions(String group, @Nullable ServerWorld world, PermissionValue value) {
                return Collections.EMPTY_SET;
            }

            @Override
            public Set<String> getGroupSpecificPermissions(String group, @Nullable ServerWorld world, PermissionValue value) {
                return Collections.EMPTY_SET;
            }
        };
    }
}
