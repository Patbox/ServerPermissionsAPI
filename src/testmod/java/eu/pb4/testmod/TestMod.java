package eu.pb4.testmod;

import com.mojang.brigadier.context.CommandContext;
import eu.pb4.permissions.api.Permissions;
import eu.pb4.permissions.api.ValueAdapter;
import eu.pb4.permissions.api.context.UserContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;


public class TestMod implements ModInitializer {

    private static int test(CommandContext<ServerCommandSource> context) {
        return 0;
    }

    private static int test2(CommandContext<ServerCommandSource> context) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayer();
            UserContext userContext = UserContext.of(player);
            List<Text> text = new ArrayList<>();
            text.add(new LiteralText("Perms all:"));

            for (var entry : Permissions.get().getAll(userContext).entrySet()) {
                text.add(new LiteralText("- " + entry.getKey() + " - " + entry.getValue().toBoolean(false)));
            }

            text.add(new LiteralText("Perms true:"));

            for (var entry : Permissions.get().getList(userContext)) {
                text.add(new LiteralText("- " + entry));
            }

            text.add(new LiteralText("Perms value home.*: " + Permissions.get().check(userContext, "home.*")));

            text.add(new LiteralText("Perms value home.X: " + Permissions.get().getValue(userContext, "home", 0, ValueAdapter.INTEGER)));
            text.add(new LiteralText("Perms value duration.X: " + Permissions.get().getValue(userContext, "duration", Duration.ZERO, ValueAdapter.DURATION).toString()));

            text.add(new LiteralText("Groups: " + String.join(", ", Permissions.get().getGroups(userContext))));


            for(Text t : text) {
                context.getSource().sendFeedback(t, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(
                    literal("test")
                            .requires(Permissions.require("test.permission", false, false))
                            .executes(TestMod::test)
            );

            dispatcher.register(
                    literal("test2").executes(TestMod::test2)
            );
        });

        //ServerLifecycleEvents.SERVER_STARTING.register((server) -> LuckPermsProvider.SERVER = server);
    }

}
