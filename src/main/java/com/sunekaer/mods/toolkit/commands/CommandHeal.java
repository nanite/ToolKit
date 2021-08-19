package com.sunekaer.mods.toolkit.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;


import static net.minecraft.command.Commands.literal;

public class CommandHeal {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return literal("heal")
                .requires(cs -> cs.hasPermissionLevel(2))
                .executes(ctx -> heal(
                        ctx.getSource().asPlayer()
                        )
                );
    }

    private static int heal(ServerPlayerEntity player) {
        player.setHealth((player.getMaxHealth()));
        player.getFoodStats().addStats(20, 20);
        return 1;
    }
}