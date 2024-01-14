package com.sunekaer.toolkit.commands.items;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class RepairItemCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("repair")
                .requires(cs -> cs.hasPermission(2)).executes(context -> repair(context, context.getSource().getPlayerOrException()))
                .then(Commands.argument("player", EntityArgument.player()).executes(context -> repair(context, EntityArgument.getPlayer(context, "player"))));
    }

    private static int repair(CommandContext<CommandSourceStack> context, ServerPlayer player) throws CommandSyntaxException {
        var source = context.getSource();

        var item = EnchantCommand.getItemInHand(player);
        if (item == null) {
            throw EnchantCommand.ERROR_MISSING_PLAYER.create();
        }

        item.setDamageValue(0);
        source.sendSuccess(Component.translatable("commands.toolkit.repair.success", item.getItem().getName(item).getString()), false);
        return 1;
    }
}
