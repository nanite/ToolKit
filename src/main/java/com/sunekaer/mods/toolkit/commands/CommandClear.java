package com.sunekaer.mods.toolkit.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.Objects;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public class CommandClear {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return literal("clear")
                .requires(cs -> cs.hasPermissionLevel(2))
                .then(argument("ClearSize", IntegerArgumentType.integer())
                        .executes(ctx -> remove(
                                ctx.getSource(),
                                ctx.getSource().asPlayer(),
                                IntegerArgumentType.getInteger(ctx, "ClearSize")
                                )
                        )
                );
    }

    private static int remove(CommandSource source, PlayerEntity player, int size) {
        World world = source.getWorld();
        double removeSize = ((16 * size) / 2f);
        double startX = player.getPositionVec().getX() - removeSize;
        double startZ = player.getPositionVec().getZ() - removeSize;
        double endX = player.getPositionVec().getX() + removeSize;
        double endZ = player.getPositionVec().getZ() + removeSize;

        source.sendFeedback(new TranslationTextComponent("commands.toolkit.remove.lagwarring"), true);
        for (int y = 0; y < 255; ++y) {
            for (double x = startX; x < endX; x++) {
                for (double z = startZ; z < endZ; z++) {
                    BlockPos tBlockPos = new BlockPos(x, y, z);
                    BlockState tBlockState = world.getBlockState(tBlockPos);
                    Block tBlock = tBlockState.getBlock();

                        if (!tBlock.equals(Blocks.AIR) && !tBlock.equals(Blocks.BEDROCK)) {
                            if(Objects.requireNonNull(tBlock.getRegistryName()).getNamespace().equals("minecraft")){
                                world.setBlockState(tBlockPos, Blocks.AIR.getDefaultState(), 2);
                            }
                        }


                }
            }
        }
        source.sendFeedback(new TranslationTextComponent("commands.toolkit.remove.done"), true);
        return 1;
    }
}

