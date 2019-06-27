package com.sunekaer.mods.toolkit.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.sun.org.apache.xpath.internal.functions.FuncFalse;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;

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
        String envValue = "";
        long time = 6000;

        if (value) {
            envValue = "false";
        } else if (!value) {
            envValue = "true";
        }

//        source.getServer().getGameRules().setOrCreateGameRule("doDaylightCycle", envValue, source.getServer());
//        source.getServer().getGameRules().setOrCreateGameRule("doMobSpawning", envValue, source.getServer());
//        source.getServer().getGameRules().setOrCreateGameRule("doWeatherCycle", envValue, source.getServer());

        if(value) {
            source.getWorld().setDayTime(time);
            source.getWorld().getWorldInfo().setClearWeatherTime(6000);
            source.getWorld().getWorldInfo().setRainTime(0);
            source.getWorld().getWorldInfo().setThunderTime(0);
            source.getWorld().getWorldInfo().setRaining(false);
            source.getWorld().getWorldInfo().setThundering(false);
        }
        return 1;
    }
}
