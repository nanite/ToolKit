package com.sunekaer.mods.toolkit.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.sunekaer.mods.toolkit.ToolKit;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ToolKit.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TKCommand {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("tk")
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
//            .then(CommandJER.register())

            //TODO Add drain command (Removes all fluids in given area)
            //TODO kill all by entity id
        );
    }
}
