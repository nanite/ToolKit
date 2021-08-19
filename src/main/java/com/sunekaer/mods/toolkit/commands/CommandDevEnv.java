package com.sunekaer.mods.toolkit.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Iterator;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public class CommandDevEnv {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return literal("devenv")
                .requires(cs -> cs.hasPermissionLevel(2)) //permission
                .then(argument("True/False", BoolArgumentType.bool())
                        .executes(ctx -> setDevEnv(
                                        ctx.getSource(),
                                        BoolArgumentType.getBool(ctx, "True/False")
                                )
                        )
                );
    }

    private static int setDevEnv(CommandSource source, Boolean value) {
        long time = 6000;

        GameRules.BooleanValue d = ServerLifecycleHooks.getCurrentServer()
                .getGameRules()
                .get(GameRules.DO_DAYLIGHT_CYCLE);
        d.set(!value, ServerLifecycleHooks.getCurrentServer());

        GameRules.BooleanValue m = ServerLifecycleHooks.getCurrentServer()
                .getGameRules()
                .get(GameRules.DO_MOB_SPAWNING);
        m.set(!value, ServerLifecycleHooks.getCurrentServer());

        GameRules.BooleanValue w = ServerLifecycleHooks.getCurrentServer()
                .getGameRules()
                .get(GameRules.DO_WEATHER_CYCLE);
        w.set(!value, ServerLifecycleHooks.getCurrentServer());

        if (value) {
            Iterator var2 = source.getServer().getWorlds().iterator();

            while (var2.hasNext()) {
                ServerWorld lvt_3_1_ = (ServerWorld) var2.next();
                lvt_3_1_.func_241114_a_((long) time);
            }
        }
        return 1;
    }
}
