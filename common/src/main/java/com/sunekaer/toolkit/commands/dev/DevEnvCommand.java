package com.sunekaer.toolkit.commands.dev;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.level.gamerules.GameRules;

public class DevEnvCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("devenv")
                .requires(cs -> cs.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .then(Commands.argument("True/False", BoolArgumentType.bool())
                        .executes(ctx -> setDevEnv(ctx.getSource(), BoolArgumentType.getBool(ctx, "True/False")))
                );
    }

    private static int setDevEnv(CommandSourceStack source, Boolean value) {
        var server = source.getServer();
        long time = 6000;

        server.overworld().getGameRules().set(GameRules.ADVANCE_TIME, !value, server);
        server.overworld().getGameRules().set(GameRules.ADVANCE_WEATHER, !value, server);
        server.overworld().getGameRules().set(GameRules.SPAWN_MOBS, !value, server);

        if (value) {
            for (ServerLevel serverLevel : source.getServer().getAllLevels()) {
                serverLevel.setDayTime(time);
            }
        }

        return 1;
    }
}
