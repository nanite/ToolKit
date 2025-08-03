package com.sunekaer.toolkit.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.sunekaer.toolkit.commands.dev.DevEnvCommand;
import com.sunekaer.toolkit.commands.dev.NightVisionCommand;
import com.sunekaer.toolkit.commands.inventory.CopyCommand;
import com.sunekaer.toolkit.commands.inventory.PrintCommand;
import com.sunekaer.toolkit.commands.items.EnchantCommand;
import com.sunekaer.toolkit.commands.items.RepairItemCommand;
import com.sunekaer.toolkit.commands.items.SlayerCommand;
import com.sunekaer.toolkit.commands.level.*;
import com.sunekaer.toolkit.commands.player.FeedCommand;
import com.sunekaer.toolkit.commands.player.GodCommand;
import com.sunekaer.toolkit.commands.player.HealCommand;
import com.sunekaer.toolkit.commands.player.TeleportDimensionSpawnCommand;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;

public class TKCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal("toggledownfall")
                .requires(cs -> cs.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(c -> {
                    ServerLevel overworld = c.getSource().getServer().overworld();

                    if (overworld.isRaining() || overworld.isThundering()) {
                        overworld.setWeatherParameters(6000, 0, false, false);
                    } else {
                        overworld.setWeatherParameters(0, 6000, true, false);
                    }

                    return 1;
                })
        );

        dispatcher.register(
                Commands.literal("tk")
                        .then(DevEnvCommand.register())
                        .then(SlayerCommand.register())
                        .then(EnchantCommand.register(context))
                        .then(BlockDistributionCommand.register())
                        .then(ClearCommand.register(context))
                        .then(KillEntitiesCommand.register(context))
                        .then(HealCommand.register())
                        .then(RepairItemCommand.register())
                        .then(NightVisionCommand.register())
                        .then(GodCommand.register())
                        .then(PrintCommand.register())
                        .then(CopyCommand.register())
                        .then(PrintCommand.registerHandCommand())
                        .then(TeleportDimensionSpawnCommand.register())
                        .then(DrainFluidCommand.register())
                        .then(FeedCommand.register())
//                        .then(MineAreaCommand.register())

                //TODO kill all by entity id
        );
    }
}
