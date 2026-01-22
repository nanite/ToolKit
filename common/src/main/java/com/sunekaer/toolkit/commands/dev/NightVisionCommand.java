package com.sunekaer.toolkit.commands.dev;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.effect.MobEffectInstance;

import static net.minecraft.world.effect.MobEffects.NIGHT_VISION;

public class NightVisionCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("nightvision")
            .requires(cs -> cs.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
            .executes(ctx -> addEffect(ctx.getSource().getPlayerOrException()));
    }

    private static int addEffect(ServerPlayer player) {
        MobEffectInstance mobeffectinstance = new MobEffectInstance(NIGHT_VISION, 9999999, 3, false, false);

        if (player.hasEffect(NIGHT_VISION)) {
            player.removeEffect(NIGHT_VISION);
        } else {
            player.addEffect(mobeffectinstance);
        }

        return 1;
    }
}
