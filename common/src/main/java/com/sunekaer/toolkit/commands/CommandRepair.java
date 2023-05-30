package com.sunekaer.toolkit.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class CommandRepair {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("repair")
                .requires(cs -> cs.hasPermission(2))
                .executes(CommandRepair::repair);
    }

    // TODO: Maybe support being run from command blocks and server ops?
    private static int repair(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayer();

        var item = CommandEnchant.getItemInHand(player);
        if (item == null) {
            throw CommandEnchant.ERROR_MISSING_PLAYER.create();
        }

        item.setDamageValue(0);
        source.sendSuccess(Component.translatable("commands.toolkit.repair.success", item.getItem().getName(item).getString()), false);
        return 1;
    }
}
