package eu.pb4.testmod;

import eu.pb4.permissions.api.PermissionProvider;
import eu.pb4.permissions.api.PermissionValue;
import eu.pb4.permissions.api.context.UserContext;
import eu.pb4.permissions.mixin.WorldAccessor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeBuilder;
import net.luckperms.api.query.Flag;
import net.luckperms.api.query.QueryOptions;
import net.luckperms.api.util.Tristate;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LuckPermsProvider implements PermissionProvider {
    public static MinecraftServer SERVER;

    private LuckPerms luckPerms = null;

    protected LuckPerms getLuckPerms() {
        if (luckPerms == null) {
            luckPerms = net.luckperms.api.LuckPermsProvider.get();
        }
        return luckPerms;
    }

    protected User getUser(UserContext context) {
        return context.getPlayerEntity() != null ? getLuckPerms().getPlayerAdapter(ServerPlayerEntity.class).getUser(context.getPlayerEntity()) : getLuckPerms().getUserManager().getUser(context.getUuid());
    }

    protected QueryOptions getQuery(UserContext context, ServerWorld world, boolean inheritance) {
        ContextSet contextSet = getUser(context).getQueryOptions().context().mutableCopy();
        if (world != null) {
            contextSet = ImmutableContextSet.builder().addAll(contextSet).add("world", ((WorldAccessor) world).getRegistryKey().getValue().toString()).build();
        }
        return getUser(context).getQueryOptions().toBuilder().context(contextSet).flag(Flag.RESOLVE_INHERITANCE, inheritance).build();
    }

    protected QueryOptions getQuery(String group, ServerWorld world, boolean inheritance) {
        ContextSet contextSet = ImmutableContextSet.empty();
        if (world != null) {
            contextSet = ImmutableContextSet.builder().addAll(contextSet).add("world", ((WorldAccessor) world).getRegistryKey().getValue().toString()).build();
        }
        return getLuckPerms().getGroupManager().getGroup(group).getQueryOptions().toBuilder().context(contextSet).flag(Flag.RESOLVE_INHERITANCE, inheritance).build();
    }

    protected PermissionValue toValue(Tristate tristate) {
        return switch (tristate) {
            case TRUE -> PermissionValue.TRUE;
            case UNDEFINED -> PermissionValue.DEFAULT;
            case FALSE -> PermissionValue.FALSE;
        };
    }

    @Override
    public String getName() {
        return "LuckPerms";
    }

    @Override
    public String getIdentifier() {
        return "luckperms";
    }

    @Override
    public boolean supportsGroups() {
        return true;
    }

    @Override
    public boolean supportsTimedPermissions() {
        return true;
    }

    @Override
    public boolean supportsTimedGroups() {
        return true;
    }

    @Override
    public boolean supportsPerWorldPermissions() {
        return true;
    }

    @Override
    public boolean supportsPerWorldGroups() {
        return true;
    }

    @Override
    public Priority getPriority() {
        return Priority.MAIN;
    }

    @Override
    public PermissionValue check(UserContext user, String permission) {
        if (permission.endsWith(".*")) {
            if (this.getList(user, permission, PermissionValue.TRUE).size() > 0) {
                return PermissionValue.TRUE;
            } else if (this.getList(user, permission, PermissionValue.FALSE).size() > 0) {
                return PermissionValue.FALSE;
            }
            return PermissionValue.DEFAULT;
        }

        return toValue(getUser(user).getCachedData().getPermissionData().checkPermission(permission));
    }

    @Override
    public List<String> getList(UserContext user, @Nullable ServerWorld world, PermissionValue value) {
        var stream = getUser(user).getCachedData().getPermissionData(getQuery(user, world, true)).getPermissionMap().entrySet().stream();
        if (value == PermissionValue.TRUE) {
            stream = stream.filter(n -> n.getValue());
        } else if (value == PermissionValue.FALSE) {
            stream = stream.filter(n -> !n.getValue());
        }
        return stream.map(n -> n.getKey()).collect(Collectors.toList());
    }

    @Override
    public List<String> getList(UserContext user, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
        var stream = getUser(user).getCachedData().getPermissionData(getQuery(user, world, true)).getPermissionMap().entrySet().stream();
        if (value == PermissionValue.TRUE) {
            stream = stream.filter(n -> n.getValue());
        } else if (value == PermissionValue.FALSE) {
            stream = stream.filter(n -> !n.getValue());
        }
        return stream.filter(n -> n.getKey().startsWith(parentPermission)).map(n -> n.getKey().substring(parentPermission.length() + 1)).collect(Collectors.toList());
    }

    @Override
    public List<String> getListNonInherited(UserContext user, @Nullable ServerWorld world, PermissionValue value) {
        var stream = getUser(user).getCachedData().getPermissionData(getQuery(user, world, false)).getPermissionMap().entrySet().stream();
        if (value == PermissionValue.TRUE) {
            stream = stream.filter(n -> n.getValue());
        } else if (value == PermissionValue.FALSE) {
            stream = stream.filter(n -> !n.getValue());
        }
        return stream.map(n -> n.getKey()).collect(Collectors.toList());
    }

    @Override
    public List<String> getListNonInherited(UserContext user, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
        var stream = getUser(user).getCachedData().getPermissionData(getQuery(user, world, false)).getPermissionMap().entrySet().stream();
        if (value == PermissionValue.TRUE) {
            stream = stream.filter(n -> n.getValue());
        } else if (value == PermissionValue.FALSE) {
            stream = stream.filter(n -> !n.getValue());
        }
        return stream.filter(n -> n.getKey().startsWith(parentPermission)).map(n -> n.getKey().substring(parentPermission.length() + 1)).collect(Collectors.toList());
    }

    @Override
    public Map<String, PermissionValue> getAll(UserContext user, @Nullable ServerWorld world) {
        Map<String, PermissionValue> map = new LinkedHashMap<>();
        for (Map.Entry<String, Boolean> entry : getUser(user).getCachedData().getPermissionData(getQuery(user, world, true)).getPermissionMap().entrySet()) {
            map.put(entry.getKey(), entry.getValue() == true ? PermissionValue.TRUE : PermissionValue.FALSE);
        }

        return map;
    }

    @Override
    public Map<String, PermissionValue> getAll(UserContext user, String parentPermission, @Nullable ServerWorld world) {
        Map<String, PermissionValue> map = new LinkedHashMap<>();
        for (Map.Entry<String, Boolean> entry : getUser(user).getCachedData().getPermissionData(getQuery(user, world, true)).getPermissionMap().entrySet()) {
            if (entry.getKey().startsWith(parentPermission)) {
                map.put(entry.getKey().substring(parentPermission.length() + 1), entry.getValue() == true ? PermissionValue.TRUE : PermissionValue.FALSE);
            }
        }

        return map;
    }

    @Override
    public Map<String, PermissionValue> getAllNonInherited(UserContext user, @Nullable ServerWorld world) {
        Map<String, PermissionValue> map = new LinkedHashMap<>();
        for (Map.Entry<String, Boolean> entry : getUser(user).getCachedData().getPermissionData(getQuery(user, world, true)).getPermissionMap().entrySet()) {
            map.put(entry.getKey(), entry.getValue() == false ? PermissionValue.TRUE : PermissionValue.FALSE);
        }

        return map;
    }

    @Override
    public Map<String, PermissionValue> getAllNonInherited(UserContext user, String parentPermission, @Nullable ServerWorld world) {
        Map<String, PermissionValue> map = new LinkedHashMap<>();
        for (Map.Entry<String, Boolean> entry : getUser(user).getCachedData().getPermissionData(getQuery(user, world, false)).getPermissionMap().entrySet()) {
            if (entry.getKey().startsWith(parentPermission)) {
                map.put(entry.getKey().substring(parentPermission.length() + 1), entry.getValue() == true ? PermissionValue.TRUE : PermissionValue.FALSE);
            }
        }

        return map;
    }

    @Override
    public void set(UserContext user, @Nullable ServerWorld world, String permission, PermissionValue value) {
        getLuckPerms().getUserManager().modifyUser(user.getUuid(), lpUser -> {
            if (value == PermissionValue.DEFAULT) {
                NodeBuilder node = Node.builder(permission);
                if (world != null) {
                    node.withContext("world", ((WorldAccessor) world).getRegistryKey().getValue().toString());
                }
                lpUser.data().remove(node.build());
            } else {
                NodeBuilder node = Node.builder(permission).value(value == PermissionValue.TRUE);
                if (world != null) {
                    node.withContext("world", ((WorldAccessor) world).getRegistryKey().getValue().toString());
                }
                lpUser.data().add(node.build());
            }
        });
    }

    @Override
    public void set(UserContext user, @Nullable ServerWorld world, String permission, PermissionValue value, Duration duration) {
        getLuckPerms().getUserManager().modifyUser(user.getUuid(), lpUser -> {
            if (value == PermissionValue.DEFAULT) {
                NodeBuilder node = Node.builder(permission);
                if (world != null) {
                    node.withContext("world", ((WorldAccessor) world).getRegistryKey().getValue().toString());
                }
                lpUser.data().remove(node.build());
            } else {
                NodeBuilder node = Node.builder(permission).value(value == PermissionValue.TRUE);
                if (world != null) {
                    node.withContext("world", ((WorldAccessor) world).getRegistryKey().getValue().toString());
                }
                node.expiry(duration.getSeconds());
                lpUser.data().add(node.build());
            }
        });
    }

    @Override
    public List<String> getGroups(UserContext user, @Nullable ServerWorld world) {
        List<String> list = new ArrayList<>();
        for (Group g : getUser(user).getInheritedGroups(getQuery(user, world, true))) {
            String name = g.getName();
            list.add(name);
        }
        return list;
    }

    @Override
    public void addGroup(UserContext user, @Nullable ServerWorld world, String group) {
        getLuckPerms().getUserManager().modifyUser(user.getUuid(), lpUser -> {
            NodeBuilder node = Node.builder("group." + group);
            if (world != null) {
                node.withContext("world", ((WorldAccessor) world).getRegistryKey().getValue().toString());
            }
            lpUser.data().add(node.build());
        });
    }

    @Override
    public void addGroup(UserContext user, @Nullable ServerWorld world, String group, Duration duration) {
        getLuckPerms().getUserManager().modifyUser(user.getUuid(), lpUser -> {
            NodeBuilder node = Node.builder("group." + group);
            if (world != null) {
                node.withContext("world", ((WorldAccessor) world).getRegistryKey().getValue().toString());
            }
            node.expiry(duration.getSeconds());
            lpUser.data().add(node.build());
        });
    }

    @Override
    public void removeGroup(UserContext user, @Nullable ServerWorld world, String group) {
        getLuckPerms().getUserManager().modifyUser(user.getUuid(), lpUser -> {
            NodeBuilder node = Node.builder("group." + group);
            if (world != null) {
                node.withContext("world", ((WorldAccessor) world).getRegistryKey().getValue().toString());
            }
            lpUser.data().remove(node.build());
        });
    }

    @Override
    public PermissionValue checkGroup(String group, @Nullable ServerWorld world, String permission) {
        return toValue(getLuckPerms().getGroupManager().getGroup(group).getCachedData().getPermissionData(getQuery(group, world, true)).checkPermission(permission));
    }

    @Override
    public List<String> getListGroup(String group, @Nullable ServerWorld world, PermissionValue value) {
        var stream = getLuckPerms().getGroupManager().getGroup(group).getCachedData().getPermissionData(getQuery(group, world, true)).getPermissionMap().entrySet().stream();
        if (value == PermissionValue.TRUE) {
            stream = stream.filter(n -> n.getValue());
        } else if (value == PermissionValue.FALSE) {
            stream = stream.filter(n -> !n.getValue());
        }
        return stream.map(n -> n.getKey()).collect(Collectors.toList());
    }

    @Override
    public List<String> getListGroup(String group, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
        var stream = getLuckPerms().getGroupManager().getGroup(group).getCachedData().getPermissionData(getQuery(group, world, true)).getPermissionMap().entrySet().stream();
        if (value == PermissionValue.TRUE) {
            stream = stream.filter(n -> n.getValue());
        } else if (value == PermissionValue.FALSE) {
            stream = stream.filter(n -> !n.getValue());
        }
        return stream.filter(n -> n.getKey().startsWith(parentPermission)).map(n -> n.getKey().substring(parentPermission.length() + 1)).collect(Collectors.toList());
    }

    @Override
    public List<String> getListNonInheritedGroup(String group, @Nullable ServerWorld world, PermissionValue value) {
        var stream = getLuckPerms().getGroupManager().getGroup(group).getCachedData().getPermissionData(getQuery(group, world, false)).getPermissionMap().entrySet().stream();
        if (value == PermissionValue.TRUE) {
            stream = stream.filter(n -> n.getValue());
        } else if (value == PermissionValue.FALSE) {
            stream = stream.filter(n -> !n.getValue());
        }
        return stream.map(n -> n.getKey()).collect(Collectors.toList());
    }

    @Override
    public List<String> getListNonInheritedGroup(String group, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
        var stream = getLuckPerms().getGroupManager().getGroup(group).getCachedData().getPermissionData(getQuery(group, world, false)).getPermissionMap().entrySet().stream();
        if (value == PermissionValue.TRUE) {
            stream = stream.filter(n -> n.getValue());
        } else if (value == PermissionValue.FALSE) {
            stream = stream.filter(n -> !n.getValue());
        }
        return stream.filter(n -> n.getKey().startsWith(parentPermission)).map(n -> n.getKey().substring(parentPermission.length() + 1)).collect(Collectors.toList());
    }

    @Override
    public Map<String, PermissionValue> getAllGroup(String group, @Nullable ServerWorld world) {
        Map<String, PermissionValue> map = new LinkedHashMap<>();
        for (Map.Entry<String, Boolean> entry : getLuckPerms().getGroupManager().getGroup(group).getCachedData().getPermissionData(getQuery(group, world, true)).getPermissionMap().entrySet()) {
            map.put(entry.getKey(), entry.getValue() == true ? PermissionValue.TRUE : PermissionValue.FALSE);
        }

        return map;
    }

    @Override
    public Map<String, PermissionValue> getAllGroup(String group, String parentPermission, @Nullable ServerWorld world) {
        Map<String, PermissionValue> map = new LinkedHashMap<>();
        for (Map.Entry<String, Boolean> entry : getLuckPerms().getGroupManager().getGroup(group).getCachedData().getPermissionData(getQuery(group, world, true)).getPermissionMap().entrySet()) {
            if (entry.getKey().startsWith(parentPermission)) {
                map.put(entry.getKey().substring(parentPermission.length() + 1), entry.getValue() == true ? PermissionValue.TRUE : PermissionValue.FALSE);
            }
        }

        return map;
    }

    @Override
    public Map<String, PermissionValue> getAllInheritedGroup(String group, @Nullable ServerWorld world) {
        Map<String, PermissionValue> map = new LinkedHashMap<>();
        for (Map.Entry<String, Boolean> entry : getLuckPerms().getGroupManager().getGroup(group).getCachedData().getPermissionData(getQuery(group, world, false)).getPermissionMap().entrySet()) {
            map.put(entry.getKey(), entry.getValue() == true ? PermissionValue.TRUE : PermissionValue.FALSE);
        }

        return map;
    }

    @Override
    public Map<String, PermissionValue> getAllInheritedGroup(String group, String parentPermission, @Nullable ServerWorld world) {
        Map<String, PermissionValue> map = new LinkedHashMap<>();
        for (Map.Entry<String, Boolean> entry : getLuckPerms().getGroupManager().getGroup(group).getCachedData().getPermissionData(getQuery(group, world, false)).getPermissionMap().entrySet()) {
            if (entry.getKey().startsWith(parentPermission)) {
                map.put(entry.getKey().substring(parentPermission.length() + 1), entry.getValue() == true ? PermissionValue.TRUE : PermissionValue.FALSE);
            }
        }

        return map;
    }
}
