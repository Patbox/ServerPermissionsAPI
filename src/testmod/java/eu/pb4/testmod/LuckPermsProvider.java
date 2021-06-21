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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class LuckPermsProvider implements PermissionProvider {
    private LuckPerms luckPerms = null;

    protected LuckPerms getLuckPerms() {
        if (luckPerms == null) {
            luckPerms = net.luckperms.api.LuckPermsProvider.get();
        }
        return luckPerms;
    }

    protected @Nullable User getUser(UserContext context) {
        if (context.getPlayerEntity() != null) {
            return getLuckPerms().getPlayerAdapter(ServerPlayerEntity.class).getUser(context.getPlayerEntity());
        }

        User user = getLuckPerms().getUserManager().getUser(context.getUuid());
        if (user != null) {
            return user;
        }

        try {
            return getLuckPerms().getUserManager().loadUser(context.getUuid()).get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            return null;
        }
    }

    protected QueryOptions getQuery(UserContext context, User user, ServerWorld world, boolean inheritance) {
        ContextSet contextSet = user.getQueryOptions().context().mutableCopy();
        if (world != null) {
            contextSet = ImmutableContextSet.builder().addAll(contextSet).add("world", ((WorldAccessor) world).getRegistryKey().getValue().toString()).build();
        }
        return user.getQueryOptions().toBuilder().context(contextSet).flag(Flag.RESOLVE_INHERITANCE, inheritance).build();
    }

    protected QueryOptions getQuery(String group, ServerWorld world, boolean inheritance) {
        ContextSet contextSet = ImmutableContextSet.empty();
        if (world != null) {
            contextSet = ImmutableContextSet.builder().addAll(contextSet).add("world", ((WorldAccessor) world).getRegistryKey().getValue().toString()).build();
        }
        Group group1 = getLuckPerms().getGroupManager().getGroup(group);

        return group1 != null ? group1.getQueryOptions().toBuilder().context(contextSet).flag(Flag.RESOLVE_INHERITANCE, inheritance).build() : null;
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
        if (permission.endsWith(".*") || permission.endsWith(".?")) {
            String basePermission = permission.substring(0, permission.length() - 2);

            if (this.getList(user, basePermission, PermissionValue.TRUE).size() > 0) {
                return PermissionValue.TRUE;
            } else if (this.getList(user, basePermission, PermissionValue.FALSE).size() > 0) {
                return PermissionValue.FALSE;
            }
            return PermissionValue.DEFAULT;
        }

        User user1 = getUser(user);
        if (user1 != null) {
            return toValue(user1.getCachedData().getPermissionData().checkPermission(permission));
        } else {
            return PermissionValue.DEFAULT;
        }
    }

    @Override
    public List<String> getList(UserContext user, @Nullable ServerWorld world, PermissionValue value) {
        User user1 = getUser(user);
        if (user1 == null) {
            return Collections.emptyList();
        }

        var stream = user1.getCachedData().getPermissionData(getQuery(user, user1, world, true)).getPermissionMap().entrySet().stream();
        if (value == PermissionValue.TRUE) {
            stream = stream.filter(Map.Entry::getValue);
        } else if (value == PermissionValue.FALSE) {
            stream = stream.filter(n -> !n.getValue());
        }
        return stream.map(Map.Entry::getKey).collect(Collectors.toList());
    }

    @Override
    public List<String> getList(UserContext user, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
        User user1 = getUser(user);
        if (user1 == null) {
            return Collections.emptyList();
        }

        var stream = user1.getCachedData().getPermissionData(getQuery(user, user1, world, true)).getPermissionMap().entrySet().stream();
        if (value == PermissionValue.TRUE) {
            stream = stream.filter(Map.Entry::getValue);
        } else if (value == PermissionValue.FALSE) {
            stream = stream.filter(n -> !n.getValue());
        }
        return stream.filter(n -> n.getKey().startsWith(parentPermission)).map(n -> n.getKey().substring(parentPermission.length() + 1)).collect(Collectors.toList());
    }

    @Override
    public List<String> getListNonInherited(UserContext user, @Nullable ServerWorld world, PermissionValue value) {
        User user1 = getUser(user);
        if (user1 == null) {
            return Collections.emptyList();
        }

        var stream = user1.getCachedData().getPermissionData(getQuery(user, user1, world, false)).getPermissionMap().entrySet().stream();
        if (value == PermissionValue.TRUE) {
            stream = stream.filter(Map.Entry::getValue);
        } else if (value == PermissionValue.FALSE) {
            stream = stream.filter(n -> !n.getValue());
        }
        return stream.map(Map.Entry::getKey).collect(Collectors.toList());
    }

    @Override
    public List<String> getListNonInherited(UserContext user, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
        User user1 = getUser(user);
        if (user1 == null) {
            return Collections.emptyList();
        }

        var stream = user1.getCachedData().getPermissionData(getQuery(user, user1, world, false)).getPermissionMap().entrySet().stream();
        if (value == PermissionValue.TRUE) {
            stream = stream.filter(Map.Entry::getValue);
        } else if (value == PermissionValue.FALSE) {
            stream = stream.filter(n -> !n.getValue());
        }
        return stream.filter(n -> n.getKey().startsWith(parentPermission)).map(n -> n.getKey().substring(parentPermission.length() + 1)).collect(Collectors.toList());
    }

    @Override
    public Map<String, PermissionValue> getAll(UserContext user, @Nullable ServerWorld world) {
        User user1 = getUser(user);
        if (user1 == null) {
            return Collections.emptyMap();
        }

        Map<String, PermissionValue> map = new LinkedHashMap<>();
        for (Map.Entry<String, Boolean> entry : user1.getCachedData().getPermissionData(getQuery(user, user1, world, true)).getPermissionMap().entrySet()) {
            map.put(entry.getKey(), entry.getValue() ? PermissionValue.TRUE : PermissionValue.FALSE);
        }

        return map;
    }

    @Override
    public Map<String, PermissionValue> getAll(UserContext user, String parentPermission, @Nullable ServerWorld world) {
        User user1 = getUser(user);
        if (user1 == null) {
            return Collections.emptyMap();
        }

        Map<String, PermissionValue> map = new LinkedHashMap<>();
        for (Map.Entry<String, Boolean> entry : user1.getCachedData().getPermissionData(getQuery(user, user1, world, true)).getPermissionMap().entrySet()) {
            if (entry.getKey().startsWith(parentPermission)) {
                map.put(entry.getKey().substring(parentPermission.length() + 1), entry.getValue() ? PermissionValue.TRUE : PermissionValue.FALSE);
            }
        }

        return map;
    }

    @Override
    public Map<String, PermissionValue> getAllNonInherited(UserContext user, @Nullable ServerWorld world) {
        User user1 = getUser(user);
        if (user1 == null) {
            return Collections.emptyMap();
        }

        Map<String, PermissionValue> map = new LinkedHashMap<>();
        for (Map.Entry<String, Boolean> entry : user1.getCachedData().getPermissionData(getQuery(user, user1, world, true)).getPermissionMap().entrySet()) {
            map.put(entry.getKey(), !entry.getValue() ? PermissionValue.TRUE : PermissionValue.FALSE);
        }

        return map;
    }

    @Override
    public Map<String, PermissionValue> getAllNonInherited(UserContext user, String parentPermission, @Nullable ServerWorld world) {
        User user1 = getUser(user);
        if (user1 == null) {
            return Collections.emptyMap();
        }

        Map<String, PermissionValue> map = new LinkedHashMap<>();
        for (Map.Entry<String, Boolean> entry : user1.getCachedData().getPermissionData(getQuery(user, user1, world, false)).getPermissionMap().entrySet()) {
            if (entry.getKey().startsWith(parentPermission)) {
                map.put(entry.getKey().substring(parentPermission.length() + 1), entry.getValue() ? PermissionValue.TRUE : PermissionValue.FALSE);
            }
        }

        return map;
    }

    @Override
    public void set(UserContext user, @Nullable ServerWorld world, String permission, PermissionValue value) {
        getLuckPerms().getUserManager().modifyUser(user.getUuid(), lpUser -> {
            if (value == PermissionValue.DEFAULT) {
                NodeBuilder<?, ?> node = Node.builder(permission);
                if (world != null) {
                    node.withContext("world", ((WorldAccessor) world).getRegistryKey().getValue().toString());
                }
                lpUser.data().remove(node.build());
            } else {
                NodeBuilder<?, ?> node = Node.builder(permission).value(value == PermissionValue.TRUE);
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
                NodeBuilder<?, ?> node = Node.builder(permission);
                if (world != null) {
                    node.withContext("world", ((WorldAccessor) world).getRegistryKey().getValue().toString());
                }
                lpUser.data().remove(node.build());
            } else {
                NodeBuilder<?, ?> node = Node.builder(permission).value(value == PermissionValue.TRUE);
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
        User user1 = getUser(user);
        if (user1 == null) {
            return Collections.emptyList();
        }

        List<String> list = new ArrayList<>();
        for (Group g : user1.getInheritedGroups(getQuery(user, user1, world, true))) {
            String name = g.getName();
            list.add(name);
        }
        return list;
    }

    @Override
    public void addGroup(UserContext user, @Nullable ServerWorld world, String group) {
        getLuckPerms().getUserManager().modifyUser(user.getUuid(), lpUser -> {
            NodeBuilder<?, ?> node = Node.builder("group." + group);
            if (world != null) {
                node.withContext("world", ((WorldAccessor) world).getRegistryKey().getValue().toString());
            }
            lpUser.data().add(node.build());
        });
    }

    @Override
    public void addGroup(UserContext user, @Nullable ServerWorld world, String group, Duration duration) {
        getLuckPerms().getUserManager().modifyUser(user.getUuid(), lpUser -> {
            NodeBuilder<?, ?> node = Node.builder("group." + group);
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
            NodeBuilder<?, ?> node = Node.builder("group." + group);
            if (world != null) {
                node.withContext("world", ((WorldAccessor) world).getRegistryKey().getValue().toString());
            }
            lpUser.data().remove(node.build());
        });
    }

    @Override
    public PermissionValue checkGroup(String group, @Nullable ServerWorld world, String permission) {
        if (permission.endsWith(".*") || permission.endsWith(".?")) {
            String basePermission = permission.substring(0, permission.length() - 2);

            if (this.getListGroup(group, basePermission, PermissionValue.TRUE).size() > 0) {
                return PermissionValue.TRUE;
            } else if (this.getListGroup(group, basePermission, PermissionValue.FALSE).size() > 0) {
                return PermissionValue.FALSE;
            }
            return PermissionValue.DEFAULT;
        }
        Group group1 = getLuckPerms().getGroupManager().getGroup(group);

        return group1 != null ? toValue(group1.getCachedData().getPermissionData().checkPermission(permission)) : PermissionValue.DEFAULT;
    }

    @Override
    public List<String> getListGroup(String group, @Nullable ServerWorld world, PermissionValue value) {
        Group group1 = getLuckPerms().getGroupManager().getGroup(group);
        if (group1 == null) {
            return Collections.emptyList();
        }

        var stream = group1.getCachedData().getPermissionData(getQuery(group, world, true)).getPermissionMap().entrySet().stream();
        if (value == PermissionValue.TRUE) {
            stream = stream.filter(Map.Entry::getValue);
        } else if (value == PermissionValue.FALSE) {
            stream = stream.filter(n -> !n.getValue());
        }
        return stream.map(Map.Entry::getKey).collect(Collectors.toList());
    }

    @Override
    public List<String> getListGroup(String group, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
        Group group1 = getLuckPerms().getGroupManager().getGroup(group);
        if (group1 == null) {
            return Collections.emptyList();
        }

        var stream = group1.getCachedData().getPermissionData(getQuery(group, world, true)).getPermissionMap().entrySet().stream();
        if (value == PermissionValue.TRUE) {
            stream = stream.filter(Map.Entry::getValue);
        } else if (value == PermissionValue.FALSE) {
            stream = stream.filter(n -> !n.getValue());
        }
        return stream.filter(n -> n.getKey().startsWith(parentPermission)).map(n -> n.getKey().substring(parentPermission.length() + 1)).collect(Collectors.toList());
    }

    @Override
    public List<String> getListNonInheritedGroup(String group, @Nullable ServerWorld world, PermissionValue value) {
        Group group1 = getLuckPerms().getGroupManager().getGroup(group);
        if (group1 == null) {
            return Collections.emptyList();
        }

        var stream = group1.getCachedData().getPermissionData(getQuery(group, world, false)).getPermissionMap().entrySet().stream();
        if (value == PermissionValue.TRUE) {
            stream = stream.filter(Map.Entry::getValue);
        } else if (value == PermissionValue.FALSE) {
            stream = stream.filter(n -> !n.getValue());
        }
        return stream.map(Map.Entry::getKey).collect(Collectors.toList());
    }

    @Override
    public List<String> getListNonInheritedGroup(String group, String parentPermission, @Nullable ServerWorld world, PermissionValue value) {
        Group group1 = getLuckPerms().getGroupManager().getGroup(group);
        if (group1 == null) {
            return Collections.emptyList();
        }

        var stream = group1.getCachedData().getPermissionData(getQuery(group, world, false)).getPermissionMap().entrySet().stream();
        if (value == PermissionValue.TRUE) {
            stream = stream.filter(Map.Entry::getValue);
        } else if (value == PermissionValue.FALSE) {
            stream = stream.filter(n -> !n.getValue());
        }
        return stream.filter(n -> n.getKey().startsWith(parentPermission)).map(n -> n.getKey().substring(parentPermission.length() + 1)).collect(Collectors.toList());
    }

    @Override
    public Map<String, PermissionValue> getAllGroup(String group, @Nullable ServerWorld world) {
        Group group1 = getLuckPerms().getGroupManager().getGroup(group);
        if (group1 == null) {
            return Collections.emptyMap();
        }

        Map<String, PermissionValue> map = new LinkedHashMap<>();
        for (Map.Entry<String, Boolean> entry : group1.getCachedData().getPermissionData(getQuery(group, world, true)).getPermissionMap().entrySet()) {
            map.put(entry.getKey(), entry.getValue() ? PermissionValue.TRUE : PermissionValue.FALSE);
        }

        return map;
    }

    @Override
    public Map<String, PermissionValue> getAllGroup(String group, String parentPermission, @Nullable ServerWorld world) {
        Group group1 = getLuckPerms().getGroupManager().getGroup(group);
        if (group1 == null) {
            return Collections.emptyMap();
        }

        Map<String, PermissionValue> map = new LinkedHashMap<>();
        for (Map.Entry<String, Boolean> entry : group1.getCachedData().getPermissionData(getQuery(group, world, true)).getPermissionMap().entrySet()) {
            if (entry.getKey().startsWith(parentPermission)) {
                map.put(entry.getKey().substring(parentPermission.length() + 1), entry.getValue() ? PermissionValue.TRUE : PermissionValue.FALSE);
            }
        }

        return map;
    }

    @Override
    public Map<String, PermissionValue> getAllInheritedGroup(String group, @Nullable ServerWorld world) {
        Group group1 = getLuckPerms().getGroupManager().getGroup(group);
        if (group1 == null) {
            return Collections.emptyMap();
        }

        Map<String, PermissionValue> map = new LinkedHashMap<>();
        for (Map.Entry<String, Boolean> entry : group1.getCachedData().getPermissionData(getQuery(group, world, false)).getPermissionMap().entrySet()) {
            map.put(entry.getKey(), entry.getValue() ? PermissionValue.TRUE : PermissionValue.FALSE);
        }

        return map;
    }

    @Override
    public Map<String, PermissionValue> getAllInheritedGroup(String group, String parentPermission, @Nullable ServerWorld world) {
        Group group1 = getLuckPerms().getGroupManager().getGroup(group);
        if (group1 == null) {
            return Collections.emptyMap();
        }

        Map<String, PermissionValue> map = new LinkedHashMap<>();
        for (Map.Entry<String, Boolean> entry : group1.getCachedData().getPermissionData(getQuery(group, world, false)).getPermissionMap().entrySet()) {
            if (entry.getKey().startsWith(parentPermission)) {
                map.put(entry.getKey().substring(parentPermission.length() + 1), entry.getValue() ? PermissionValue.TRUE : PermissionValue.FALSE);
            }
        }

        return map;
    }
}
