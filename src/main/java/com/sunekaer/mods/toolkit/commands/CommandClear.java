package com.sunekaer.mods.toolkit.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

import java.util.Objects;

public class CommandClear {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("clear")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("ClearSize", IntegerArgumentType.integer())
                        .executes(ctx -> remove(
                                ctx.getSource(),
                                ctx.getSource().getPlayerOrException(),
                                IntegerArgumentType.getInteger(ctx, "ClearSize")
                                )
                        )
                );
    }

    private static int remove(CommandSourceStack source, Player player, int size) {
        Level world = source.getLevel();
        double removeSize = ((16 * size) / 2f);
        double startX = player.getX() - removeSize;
        double startZ = player.getZ() - removeSize;
        double endX = player.getX() + removeSize;
        double endZ = player.getZ() + removeSize;

        source.sendSuccess(new TranslatableComponent("commands.toolkit.remove.lagwarring"), true);
        for (int y = world.getMinBuildHeight(); y < world.getMaxBuildHeight(); ++y) {
            for (double x = startX; x < endX; x++) {
                for (double z = startZ; z < endZ; z++) {
                    BlockPos tBlockPos = new BlockPos(x, y, z);
                    BlockState tBlockState = world.getBlockState(tBlockPos);
                    Block tBlock = tBlockState.getBlock();

                        if (!tBlock.equals(Blocks.AIR) && !tBlock.equals(Blocks.BEDROCK) && !Tags.Blocks.ORES.contains(tBlock)) {
                            if(Objects.requireNonNull(tBlock.getRegistryName()).getNamespace().equals("minecraft")){
                                world.setBlock(tBlockPos, Blocks.AIR.defaultBlockState(), 2);
                            }
                        }


                }
            }
        }
        source.sendSuccess(new TranslatableComponent("commands.toolkit.remove.done"), true);
        return 1;
    }
}

