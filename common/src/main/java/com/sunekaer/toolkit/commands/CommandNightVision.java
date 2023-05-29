package com.sunekaer.toolkit.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;

import static net.minecraft.world.effect.MobEffects.NIGHT_VISION;

public class CommandNightVision {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("nightvision")
            .requires(cs -> cs.hasPermission(2))
            .executes(ctx -> addEffect(
                ctx.getSource().getPlayerOrException()
                )
            );
    }

    private static int addEffect(ServerPlayer player) {
        MobEffectInstance mobeffectinstance = new MobEffectInstance(NIGHT_VISION, 9999999, 3, false, false);
        player.addEffect(mobeffectinstance);
    return 1;
    }
}
