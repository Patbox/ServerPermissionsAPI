package eu.pb4.permissions.impl;

import eu.pb4.permissions.api.PermissionProvider;
import eu.pb4.permissions.api.PermissionValue;
import eu.pb4.permissions.api.context.UserContext;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.OperatorList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;

@SuppressWarnings({"unchecked"})
public class VanillaPermissionProvider implements PermissionProvider {
    private final static String OPERATOR_PREFIX = "operator-level-";
    private static VanillaPermissionProvider INSTANCE;
    private final MinecraftServer server;
    public Object2BooleanMap<String> defaultPermissions = new Object2BooleanArrayMap<>();
    public Object2BooleanMap<String> level1Permissions = new Object2BooleanArrayMap<>();
    public Object2BooleanMap<String> level1PermissionsNon = new Object2BooleanArrayMap<>();
    public Object2BooleanMap<String> level2Permissions = new Object2BooleanArrayMap<>();
    public Object2BooleanMap<String> level2PermissionsNon = new Object2BooleanArrayMap<>();
    public Object2BooleanMap<String> level3Permissions = new Object2BooleanArrayMap<>();
    public Object2BooleanMap<String> level3PermissionsNon = new Object2BooleanArrayMap<>();
    public Object2BooleanMap<String> level4Permissions = new Object2BooleanArrayMap<>();
    public Object2BooleanMap<String> level4PermissionsNon = new Object2BooleanArrayMap<>();

    private VanillaPermissionProvider(MinecraftServer server, VanillaConfig config) {
        this.server = server;
        this.setConfig(config);
    }

    public static VanillaPermissionProvider getInstance() {
        return INSTANCE;
    }

    public static VanillaPermissionProvider createInstance(MinecraftServer server, VanillaConfig config) {
        INSTANCE = new VanillaPermissionProvider(server, config);
        return INSTANCE;
    }

    public void setConfig(VanillaConfig config) {
        this.defaultPermissions.clear();
        this.level1Permissions.clear();
        this.level2Permissions.clear();
        this.level3Permissions.clear();
        this.level4Permissions.clear();
        this.level1PermissionsNon.clear();
        this.level2PermissionsNon.clear();
        this.level3PermissionsNon.clear();
        this.level4PermissionsNon.clear();

        this.defaultPermissions.putAll(config.defaultPermissions);

        this.level1Permissions.putAll(this.defaultPermissions);
        this.level1Permissions.putAll(config.level1Permissions);
        this.level1PermissionsNon.putAll(config.level1Permissions);

        this.level2Permissions.putAll(this.level1Permissions);
        this.level2Permissions.putAll(config.level2Permissions);
        this.level2PermissionsNon.putAll(config.level2Permissions);

        this.level3Permissions.putAll(this.level2Permissions);
        this.level3Permissions.putAll(config.level3Permissions);
        this.level3PermissionsNon.putAll(config.level3Permissions);

        this.level4Permissions.putAll(this.level3Permissions);
        this.level4Permissions.putAll(config.level4Permissions);
        this.level4PermissionsNon.putAll(config.level4Permissions);

    }

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
        return true;
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
    public Priority getPriority() {
        return Priority.FALLBACK;
    }

    private Object2BooleanMap<String> getPermissionMap(UserContext context) {
        return switch (context.getPermissionLevel()) {
            case 4 -> level4Permissions;
            case 3 -> level3Permissions;
            case 2 -> level2Permissions;
            case 1 -> level1Permissions;
            default -> defaultPermissions;
        };
    }

    private Object2BooleanMap<String> getPermissionMap(String group) {
        return switch (group) {
            case OPERATOR_PREFIX + 4 -> level4Permissions;
            case OPERATOR_PREFIX + 3 -> level3Permissions;
            case OPERATOR_PREFIX + 2 -> level2Permissions;
            case OPERATOR_PREFIX + 1 -> level1Permissions;
            default -> defaultPermissions;
        };
    }

    private Object2BooleanMap<String> getPermissionMapNon(String group) {
        return switch (group) {
            case OPERATOR_PREFIX + 4 -> level4PermissionsNon;
            case OPERATOR_PREFIX + 3 -> level3PermissionsNon;
            case OPERATOR_PREFIX + 2 -> level2PermissionsNon;
            case OPERATOR_PREFIX + 1 -> level1PermissionsNon;
            default -> defaultPermissions;
        };
    }

    @Override
    public PermissionValue check(UserContext user, String permission) {
        var map = getPermissionMap(user);
        if (permission.endsWith(".*")) {
            String substring = permission.substring(0, permission.length() - 2);

            return this.getList(user, substring, PermissionValue.TRUE).size() > 0
                    ? PermissionValue.TRUE
                    : this.getList(user, substring, PermissionValue.FALSE).size() > 0
                    ? PermissionValue.FALSE : PermissionValue.DEFAULT;
        } else {
            if (map.containsKey(permission)) {
                return PermissionValue.of(map.getBoolean(permission));
            } else {
                String[] parts = permission.split("\\.");
                int length = parts.length - 1;
                while (length != 0) {
                    StringBuilder key = new StringBuilder();
                    for (int x = 0; x < length; x++) {
                        key.append(parts[x]).append(".");
                    }
                    key.append("*");

                    length--;
                    if (map.containsKey(key.toString())) {
                        return PermissionValue.of(map.getBoolean(key.toString()));
                    }
                }
                return PermissionValue.DEFAULT;
            }
        }
    }

    @Override
    public List<String> getList(UserContext user, @Nullable ServerWorld world, PermissionValue value) {
        List<String> list = new ArrayList<>();
        for (Object2BooleanMap.Entry<String> n : this.getPermissionMap(user).object2BooleanEntrySet()) {
            if (value.pass(n.getBooleanValue())) {
                String key = n.getKey();
                list.add(key);
            }
        }
        return list;
    }

    @Override
    public List<String> getList(UserContext user, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
        List<String> list = new ArrayList<>();
        String basePermission = parentPermission + ".";
        int baseLength = basePermission.length();

        for (Object2BooleanMap.Entry<String> n : this.getPermissionMap(user).object2BooleanEntrySet()) {
            if (n.getKey().startsWith(basePermission) && value.pass(n.getBooleanValue())) {
                String key = n.getKey().substring(baseLength);
                if (!key.isEmpty()) {
                    list.add(key);
                }
            }
        }
        return list;
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
        Map<String, PermissionValue> map = new LinkedHashMap<>();
        for (Object2BooleanMap.Entry<String> entry : this.getPermissionMap(user).object2BooleanEntrySet()) {
            map.put(entry.getKey(), PermissionValue.of(entry.getBooleanValue()));
        }

        return map;
    }

    @Override
    public Map<String, PermissionValue> getAll(UserContext user, String parentPermission, @Nullable ServerWorld world) {
        Map<String, PermissionValue> map = new LinkedHashMap<>();
        String basePermission = parentPermission + ".";
        int baseLength = basePermission.length();

        for (Object2BooleanMap.Entry<String> entry : this.getPermissionMap(user).object2BooleanEntrySet()) {
            if (entry.getKey().startsWith(basePermission)) {
                map.put(entry.getKey().substring(baseLength), PermissionValue.of(entry.getBooleanValue()));
            }
        }

        return map;
    }

    @Override
    public Map<String, PermissionValue> getAllNonInherited(UserContext user, @Nullable ServerWorld world) {
        return Collections.EMPTY_MAP;
    }

    @Override
    public Map<String, PermissionValue> getAllNonInherited(UserContext user, String parentPermission, @Nullable ServerWorld world) {
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
        List<String> list = new ArrayList<>();
        list.add("default");

        for (int x = 1; x <= user.getPermissionLevel(); x++) {
            list.add(OPERATOR_PREFIX + x);
        }

        return list;
    }

    @Override
    public void addGroup(UserContext user, @Nullable ServerWorld world, String group) {
        if (user.getGameProfile() != UserContext.CONSOLE_GAME_PROFILE && group.startsWith(OPERATOR_PREFIX)) {
            try {
                int level = MathHelper.clamp(Integer.parseInt(group.substring(OPERATOR_PREFIX.length())), 1, 4);
                this.server.getPlayerManager().getOpList().add(new OperatorEntry(user.getGameProfile(), level, false));
            } catch (Exception e) {
                // Don't do anything
            }
        }
    }

    @Override
    public void addGroup(UserContext user, @Nullable ServerWorld world, String group, Duration duration) {
        this.addGroup(user, world, group);
    }

    @Override
    public void removeGroup(UserContext user, @Nullable ServerWorld world, String group) {
        if (user.getGameProfile() != UserContext.CONSOLE_GAME_PROFILE && group.startsWith(OPERATOR_PREFIX)) {
            try {
                int level = MathHelper.clamp(Integer.parseInt(group.substring(OPERATOR_PREFIX.length())), 1, 4);
                OperatorList operatorList = this.server.getPlayerManager().getOpList();
                OperatorEntry entry = operatorList.get(user.getGameProfile());

                if (entry != null && entry.getPermissionLevel() == level) {
                    operatorList.remove(user.getGameProfile());
                }
            } catch (Exception e) {
                // Don't do anything
            }
        }
    }

    @Override
    public PermissionValue checkGroup(String group, @Nullable ServerWorld world, String permission) {
        var map = getPermissionMap(group);
        if (permission.endsWith(".*")) {
            String substring = permission.substring(0, permission.length() - 2);

            return this.getListGroup(group, substring, PermissionValue.TRUE).size() > 0
                    ? PermissionValue.TRUE
                    : this.getListGroup(group, substring, PermissionValue.FALSE).size() > 0
                    ? PermissionValue.FALSE : PermissionValue.DEFAULT;
        } else {
            if (map.containsKey(permission)) {
                return PermissionValue.of(map.getBoolean(permission));
            } else {
                String[] parts = permission.split("\\.");
                int length = parts.length - 1;
                while (length != 0) {
                    StringBuilder key = new StringBuilder();
                    for (int x = 0; x < length; x++) {
                        key.append(parts[x]).append(".");
                    }
                    key.append("*");

                    length--;
                    if (map.containsKey(key.toString())) {
                        return PermissionValue.of(map.getBoolean(key.toString()));
                    }
                }
                return PermissionValue.DEFAULT;
            }
        }    }

    @Override
    public List<String> getListGroup(String group, @Nullable ServerWorld world, PermissionValue value) {
        List<String> list = new ArrayList<>();
        for (Object2BooleanMap.Entry<String> n : this.getPermissionMap(group).object2BooleanEntrySet()) {
            if (value.pass(n.getBooleanValue())) {
                String key = n.getKey();
                list.add(key);
            }
        }
        return list;
    }

    @Override
    public List<String> getListGroup(String group, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
        List<String> list = new ArrayList<>();
        String basePermission = parentPermission + ".";
        int baseLength = basePermission.length();

        for (Object2BooleanMap.Entry<String> n : this.getPermissionMap(group).object2BooleanEntrySet()) {
            if (n.getKey().startsWith(basePermission) && value.pass(n.getBooleanValue())) {
                String key = n.getKey().substring(baseLength);
                if (!key.isEmpty()) {
                    list.add(key);
                }
            }
        }
        return list;
    }

    @Override
    public List<String> getListNonInheritedGroup(String group, @Nullable ServerWorld world, PermissionValue value) {
        List<String> list = new ArrayList<>();
        for (Object2BooleanMap.Entry<String> n : this.getPermissionMap(group).object2BooleanEntrySet()) {
            if (value.pass(n.getBooleanValue())) {
                String key = n.getKey();
                list.add(key);
            }
        }
        return list;
    }

    @Override
    public List<String> getListNonInheritedGroup(String group, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
        List<String> list = new ArrayList<>();
        String basePermission = parentPermission + ".";
        int baseLength = basePermission.length();

        for (Object2BooleanMap.Entry<String> n : this.getPermissionMap(group).object2BooleanEntrySet()) {
            if (n.getKey().startsWith(basePermission) && value.pass(n.getBooleanValue())) {
                String key = n.getKey().substring(baseLength);
                if (!key.isEmpty()) {
                    list.add(key);
                }
            }
        }
        return list;
    }

    @Override
    public Map<String, PermissionValue> getAllGroup(String group, @Nullable ServerWorld world) {
        Map<String, PermissionValue> map = new LinkedHashMap<>();
        for (Object2BooleanMap.Entry<String> entry : this.getPermissionMap(group).object2BooleanEntrySet()) {
            map.put(entry.getKey(), PermissionValue.of(entry.getBooleanValue()));
        }

        return map;
    }

    @Override
    public Map<String, PermissionValue> getAllGroup(String group, String parentPermission, @Nullable ServerWorld world) {
        Map<String, PermissionValue> map = new LinkedHashMap<>();
        String basePermission = parentPermission + ".";
        int baseLength = basePermission.length();

        for (Object2BooleanMap.Entry<String> entry : this.getPermissionMap(group).object2BooleanEntrySet()) {
            if (entry.getKey().startsWith(basePermission)) {
                map.put(entry.getKey().substring(baseLength), PermissionValue.of(entry.getBooleanValue()));
            }
        }

        return map;
    }

    @Override
    public Map<String, PermissionValue> getAllInheritedGroup(String group, @Nullable ServerWorld world) {
        Map<String, PermissionValue> map = new LinkedHashMap<>();
        for (Object2BooleanMap.Entry<String> entry : this.getPermissionMapNon(group).object2BooleanEntrySet()) {
            map.put(entry.getKey(), PermissionValue.of(entry.getBooleanValue()));
        }

        return map;
    }

    @Override
    public Map<String, PermissionValue> getAllInheritedGroup(String group, String parentPermission, @Nullable ServerWorld world) {
        Map<String, PermissionValue> map = new LinkedHashMap<>();
        for (Object2BooleanMap.Entry<String> entry : this.getPermissionMapNon(group).object2BooleanEntrySet()) {
            map.put(entry.getKey(), PermissionValue.of(entry.getBooleanValue()));
        }

        return map;
    }
}
