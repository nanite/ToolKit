package com.sunekaer.mods.toolkit.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class TKCommand {
    public TKCommand(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
            Commands.literal("tk")
                .then(CommandDevEnv.register())
                .then(CommandHand.register())
                .then(CommandHotbar.register())
                .then(CommandInventory.register())
                .then(CommandSlayer.register())
                .then(CommandOreDist.register())
                .then(CommandClear.register())
                .then(CommandStructureClean.register())
                .then(CommandKillAll.register())
                .then(CommandKillAllMonsters.register())
                .then(CommandKillAllAnimals.register())
                .then(CommandKillAllItems.register())
                .then(CommandKillAllExpOrbs.register())
        );
    }
}
