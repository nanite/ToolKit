package com.sunekaer.toolkit.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;

public class CommandDevEnv {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("devenv")
                .requires(cs -> cs.hasPermission(2)) //permission
                .then(Commands.argument("True/False", BoolArgumentType.bool())
                        .executes(ctx -> setDevEnv(
                                        ctx.getSource(),
                                        BoolArgumentType.getBool(ctx, "True/False")
                                )
                        )
                );
    }

    private static int setDevEnv(CommandSourceStack source, Boolean value) {
        var server = source.getServer();
        long time = 6000;

        GameRules.BooleanValue d = server.getGameRules().getRule(GameRules.RULE_DAYLIGHT);
        d.set(!value, server);

        GameRules.BooleanValue m = server.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING);
        m.set(!value, server);

        GameRules.BooleanValue w = server.getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE);
        w.set(!value, server);

        if (value) {
            for (ServerLevel serverLevel : source.getServer().getAllLevels()) {
                serverLevel.setDayTime(time);
            }
        }
        return 1;
    }
}
