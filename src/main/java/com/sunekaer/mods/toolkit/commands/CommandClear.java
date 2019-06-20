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

import java.util.ArrayList;
import java.util.List;

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
        if (!(player instanceof PlayerEntity)) {
            return 0;
        }

        List<Block> list = new ArrayList<>();
            list.add(Blocks.ACACIA_FENCE);
            list.add(Blocks.ACACIA_LOG);
            list.add(Blocks.ANDESITE);
            list.add(Blocks.BIRCH_LEAVES);
            list.add(Blocks.BIRCH_LOG);
            list.add(Blocks.CHEST);
            list.add(Blocks.CLAY);
            list.add(Blocks.COBBLESTONE);
            list.add(Blocks.COBWEB);
            list.add(Blocks.DARK_OAK_LEAVES);
            list.add(Blocks.DARK_OAK_LOG);
            list.add(Blocks.DIORITE);
            list.add(Blocks.DIRT);
            list.add(Blocks.END_STONE);
            list.add(Blocks.GRANITE);
            list.add(Blocks.GRASS);
            list.add(Blocks.GRASS_BLOCK);
            list.add(Blocks.GRASS_PATH);
            list.add(Blocks.GRAVEL);
            list.add(Blocks.JUNGLE_LEAVES);
            list.add(Blocks.JUNGLE_LOG);
            list.add(Blocks.MOSSY_COBBLESTONE);
            list.add(Blocks.NETHERRACK);
            list.add(Blocks.NETHER_BRICKS);
            list.add(Blocks.NETHER_WART);
            list.add(Blocks.NETHER_WART_BLOCK);
            list.add(Blocks.OAK_FENCE);
            list.add(Blocks.OAK_LEAVES);
            list.add(Blocks.OAK_LOG);
            list.add(Blocks.OAK_PLANKS);
            list.add(Blocks.OBSIDIAN);
            list.add(Blocks.RAIL);
            list.add(Blocks.SAND);
            list.add(Blocks.SANDSTONE);
            list.add(Blocks.SPRUCE_LEAVES);
            list.add(Blocks.SPRUCE_LOG);
            list.add(Blocks.STONE);
            list.add(Blocks.TALL_GRASS);

        List<String> list2 = new ArrayList<>();
            list2.add("minecraft:water");
            list2.add("minecraft:lava");
            list2.add("minecraft:flowing_water");
            list2.add("minecraft:flowing_lava");


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
                    if (!tBlock.equals(Blocks.AIR) && !tBlock.equals(Blocks.BEDROCK)) {
                        if(list.contains(tBlock) || list2.contains(tBlock.getRegistryName().toString())){
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

