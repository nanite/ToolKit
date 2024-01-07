package com.sunekaer.toolkit.commands.player;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class FeedCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("feed").requires(cs -> cs.hasPermission(2))
                .executes(ctx -> feed(ctx, ctx.getSource().getPlayerOrException()))
                .then(Commands.argument("player", EntityArgument.player()).executes(ctx -> feed(ctx, EntityArgument.getPlayer(ctx, "player"))));
    }

    private static int feed(CommandContext<CommandSourceStack> source, ServerPlayer playerOrException) {
        playerOrException.getFoodData().eat(20, 20);
        return 1;
    }
}
