package eu.pb4.testmod;

import com.mojang.brigadier.context.CommandContext;
import eu.pb4.permissions.api.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.literal;


public class TestMod implements ModInitializer {

    private static int test(CommandContext<ServerCommandSource> context) {
        return 0;
    }

    private static int test2(CommandContext<ServerCommandSource> context) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayer();

            PermissionTest.test(player, Permissions.get());
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
