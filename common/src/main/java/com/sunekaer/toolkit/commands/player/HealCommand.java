package com.sunekaer.toolkit.commands.player;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

public class HealCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("heal")
                .requires(cs -> cs.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .executes(ctx -> heal(ctx.getSource().getPlayerOrException()))
                .then(Commands.argument("player", EntityArgument.player()).executes(ctx -> heal(EntityArgument.getPlayer(ctx, "player"))));
    }

    private static int heal(ServerPlayer player) {
        player.setHealth((player.getMaxHealth()));
        player.getFoodData().eat(20, 20);
        return 1;
    }
}
