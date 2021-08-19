package com.sunekaer.mods.toolkit.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.impl.WeatherCommand;
import net.minecraft.world.GameRules;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

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
        boolean envValue = false;
        long time = 6000;

        if (value) {
            envValue = false;
        } else if (!value) {
            envValue = true;
        }

        GameRules.BooleanValue d = ServerLifecycleHooks.getCurrentServer()
                .getGameRules()
                .get(GameRules.DO_DAYLIGHT_CYCLE);
        d.set(envValue, ServerLifecycleHooks.getCurrentServer());

        GameRules.BooleanValue m = ServerLifecycleHooks.getCurrentServer()
                .getGameRules()
                .get(GameRules.DO_MOB_SPAWNING);
        m.set(envValue, ServerLifecycleHooks.getCurrentServer());

        GameRules.BooleanValue w = ServerLifecycleHooks.getCurrentServer()
                .getGameRules()
                .get(GameRules.DO_WEATHER_CYCLE);
        w.set(envValue, ServerLifecycleHooks.getCurrentServer());

        if(value){
            //TODO
        }
        return 1;
    }
}
