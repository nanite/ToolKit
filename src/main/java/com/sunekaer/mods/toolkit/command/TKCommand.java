package com.sunekaer.mods.toolkit.command;

import com.mojang.brigadier.CommandDispatcher;
import com.sunekaer.mods.toolkit.commands.*;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;


public class TKCommand {
    public TKCommand(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(
                Commands.literal("tk")
                .then(CommandDevEnv.register())
                .then(CommandHand.register())
                .then(CommandHotbar.register())
                .then(CommandInventory.register())
                .then(CommandLookingAt.register())
                .then(CommandOreDist.register())
                .then(CommandRemoveNoneOres.register())
                .then(CommandStructureClean.register())
        );
    }
}
