package com.sunekaer.toolkit.commands.player;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

public class GodCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("god").requires(cs -> cs.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .executes(context -> god(context, context.getSource().getPlayerOrException()))
                .then(Commands.argument("player", EntityArgument.player()).executes(ctx -> god(ctx, EntityArgument.getPlayer(ctx, "player"))));
    }

    private static int god(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        player.setInvulnerable(!player.isInvulnerable());
        context.getSource().sendSuccess(() -> Component.literal(player.isInvulnerable() ? "God mode enabled" : "God mode disabled"), false);

        return 1;
    }
}
