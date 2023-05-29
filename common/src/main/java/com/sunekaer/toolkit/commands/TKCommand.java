package com.sunekaer.toolkit.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;

public class TKCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal("toggledownfall")
                .requires(cs -> cs.hasPermission(0))
                .executes(c -> {
                    ServerLevel level = c.getSource()
                            .getPlayerOrException()
                            .getLevel();

                    if (level.isRaining() || level.isThundering()) {
                        level.setWeatherParameters(6000, 0, false, false);
                    } else {
                        level.setWeatherParameters(0, 6000, true, false);
                    }

                    return 1;
                })
        );

        dispatcher.register(
                Commands.literal("tk")
                        .then(CommandDevEnv.register())
                        .then(CommandHand.register())
                        .then(CommandHotbar.register())
                        .then(CommandInventory.register())
                        .then(CommandSlayer.register())
                        .then(CommandEnchant.register())
                        .then(CommandOreDist.register())
                        .then(CommandClear.register())
                        .then(CommandKill.register())
                        .then(CommandHeal.register())
                        .then(CommandRepair.register())
                        .then(CommandNightVision.register())

                //TODO Add drain command (Removes all fluids in given area)
                //TODO kill all by entity id
        );
    }
}
