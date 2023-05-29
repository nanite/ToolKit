package com.sunekaer.toolkit.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class CommandHeal {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("heal")
                .requires(cs -> cs.hasPermission(2))
                .executes(ctx -> heal(ctx.getSource().getPlayerOrException()));
    }

    private static int heal(ServerPlayer player) {
        player.setHealth((player.getMaxHealth()));
        player.getFoodData().eat(20, 20);
        return 1;
    }
}
