package com.sunekaer.mods.toolkit.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.sunekaer.mods.toolkit.ToolKit;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ToolKit.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
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
                        .then(CommandKillAll.register())
                        .then(CommandKillAllMonsters.register())
                        .then(CommandKillAllAnimals.register())
                        .then(CommandKillAllItems.register())
                        .then(CommandKillAllExpOrbs.register())
                        .then(CommandHeal.register())
                        .then(CommandNightVision.register())

                //TODO Add drain command (Removes all fluids in given area)
                //TODO kill all by entity id
        );
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        new TKCommand(event.getDispatcher());
    }
}
