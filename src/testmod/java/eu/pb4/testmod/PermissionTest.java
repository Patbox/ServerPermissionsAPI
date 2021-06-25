package eu.pb4.testmod;

import eu.pb4.permissions.api.v0.PermissionProvider;
import eu.pb4.permissions.api.v0.UserContext;
import eu.pb4.permissions.api.v0.ValueAdapter;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


/**
 * Before doing the test make sure that:
 * User has permissions:
 *  home.6 - true, duration.20h - true, permission.test - false
 * And is in group `operator-level-1`
 * Group `operator-level-1` should exist and have permission:
 *  home.2 - false, duration.10h - true, test.test - true, test.test2 - false
 */
public class PermissionTest {
    public static void test(ServerPlayerEntity player, PermissionProvider provider) {
        List<Text> text = new ArrayList<>();
        UserContext userContext = UserContext.of(player);
        text.add(new LiteralText("===================="));

        text.add(new LiteralText("Player permission list:"));

        for (var entry : provider.getAll(userContext).entrySet()) {
            text.add(new LiteralText("- " + entry.getKey() + " - " + entry.getValue().toBoolean(false)));
        }

        text.add(new LiteralText("Perms true:"));

        for (var entry : provider.getList(userContext)) {
            text.add(new LiteralText("- " + entry));
        }

        text.add(new LiteralText("Perms value home.?: " + provider.check(userContext, "home.?")));

        text.add(new LiteralText("Perms value home.X: "
                + provider.getAsValue(userContext, "home", 0, ValueAdapter.INTEGER)));
        text.add(new LiteralText("Perms value duration.X: "
                + provider.getAsValue(userContext, "duration", Duration.ZERO, ValueAdapter.DURATION).toString()));

        text.add(new LiteralText("Groups: " + String.join(", ", provider.getGroups(userContext))));

        text.add(new LiteralText("===================="));

        text.add(new LiteralText("Group permission list:"));

        String group = "operator-level-1";

        for (var entry : provider.getAllGroup(group).entrySet()) {
            text.add(new LiteralText("- " + entry.getKey() + " - " + entry.getValue().toBoolean(false)));
        }

        text.add(new LiteralText("Perms true:"));

        for (var entry : provider.getListGroup(group)) {
            text.add(new LiteralText("- " + entry));
        }

        text.add(new LiteralText("Perms value home.?: " + provider.checkGroup(group, "home.?")));

        text.add(new LiteralText("Perms value home.X: "
                + provider.getAsValueGroup(group, "home", 0, ValueAdapter.INTEGER)));

        text.add(new LiteralText("Perms value duration.X: "
                + provider.getAsValueGroup(group, "duration", Duration.ZERO, ValueAdapter.DURATION).toString()));

        text.add(new LiteralText("===================="));

        for(Text t : text) {
            player.sendMessage(t, false);
        }
    }
}
