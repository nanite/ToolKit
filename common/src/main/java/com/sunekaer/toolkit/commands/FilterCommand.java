package com.sunekaer.toolkit.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.permissions.Permissions;

public class FilterCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("filter")
                .requires(cs -> cs.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER));
//                .then(Commands.literal("create").executes(context -> createFilter(context)))
//                .then(Commands.literal("edit").executes(context -> addFilter(context)))
//                .then(Commands.literal("delete").executes(context -> addFilter(context)))
//                .then(Commands.literal("list").executes(context -> addFilter(context)));
    }
}
