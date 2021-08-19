package com.sunekaer.mods.toolkit.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

import static net.minecraft.command.Commands.literal;

public class CommandNightVision {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return literal("nightvision")
            .requires(cs -> cs.hasPermissionLevel(2))
            .executes(ctx -> addEffect(
                ctx.getSource().asPlayer()
                )
            );
    }

    private static int addEffect(ServerPlayerEntity player) {
        EffectInstance effectinstance = new EffectInstance(Effects.NIGHT_VISION, 9999999, 3, false, false);
        player.addPotionEffect(effectinstance);
    return 1;
    }
}
