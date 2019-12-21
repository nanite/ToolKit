package com.sunekaer.mods.toolkit.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
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
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.Structure;

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
                                IntegerArgumentType.getInteger(ctx, "ClearSize"),
                                false
                                )
                        )
                )
                .then(argument("ClearSize", IntegerArgumentType.integer())
                        .then(argument("keepStructure", BoolArgumentType.bool())
                                .executes(ctx -> remove(
                                        ctx.getSource(),
                                        ctx.getSource().asPlayer(),
                                        IntegerArgumentType.getInteger(ctx, "ClearSize"),
                                        BoolArgumentType.getBool(ctx, "keepStructure")
                                        )
                                )
                        ));
    }

    private static int remove(CommandSource source, PlayerEntity player, int size, Boolean keep) {
        World world = source.getWorld();
        double removeSize = ((16 * size) / 2);
        double startX = player.getPosition().getX() - removeSize;
        double startZ = player.getPosition().getZ() - removeSize;
        double endX = player.getPosition().getX() + removeSize;
        double endZ = player.getPosition().getZ() + removeSize;

        source.sendFeedback(new TranslationTextComponent("commands.toolkit.remove.lagwarring"), true);
        for (int y = 0; y < world.getActualHeight(); ++y) {
            for (double x = startX; x < endX; x++) {
                for (double z = startZ; z < endZ; z++) {
                    BlockPos tBlockPos = new BlockPos(x, y, z);
                    BlockState tBlockState = world.getBlockState(tBlockPos);
                    Block tBlock = tBlockState.getBlock();
                    if (!keep){
                        if (!tBlock.equals(Blocks.AIR) && !tBlock.equals(Blocks.BEDROCK)) {
                            if(tBlock.getRegistryName().getNamespace().equals("minecraft")){
                                world.setBlockState(tBlockPos, Blocks.AIR.getDefaultState(), 2);
                            }
                        }
                    }
                    if (keep) {
                        if (!tBlock.equals(Blocks.AIR) && !tBlock.equals(Blocks.BEDROCK) && !partOfStucture(world, tBlockPos)) {
                            if (tBlock.getRegistryName().getNamespace().equals("minecraft")) {
                                world.setBlockState(tBlockPos, Blocks.AIR.getDefaultState(), 2);
                            }
                        }
                    }
                }
            }
        }
        source.sendFeedback(new TranslationTextComponent("commands.toolkit.remove.done"), true);
        return 1;
    }

    private static boolean partOfStucture(World world, BlockPos pos){
        for (Structure<?> structure : Feature.STRUCTURES.values())
        {
            if (structure.isPositionInsideStructure(world, pos))
            {
                return true;
            }
        }
        return false;
    }
}

