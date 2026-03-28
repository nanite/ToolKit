package com.sunekaer.toolkit.commands.dev;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
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

        server.overworld().getGameRules().set(GameRules.ADVANCE_TIME, !value, server);
        server.overworld().getGameRules().set(GameRules.ADVANCE_WEATHER, !value, server);
        server.overworld().getGameRules().set(GameRules.SPAWN_MOBS, !value, server);

        if (value) {
            var clockManager = server.clockManager();
            var overworldClock = server.registryAccess().get(WorldClocks.OVERWORLD);
            overworldClock.ifPresent(clock -> clockManager.setTotalTicks(clock, 6000L));
        }

        return 1;
    }
}
