package com.sunekaer.mods.toolkit.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public class CommandRemoveNoneOres {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return literal("removeNoneOres")
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

        List<String> list = new ArrayList<>();
            list.add("minecraft:acacia_leaves");
            list.add("minecraft:acacia_log");
            list.add("minecraft:andesite");
            list.add("minecraft:birch_leaves");
            list.add("minecraft:birch_log");
            list.add("minecraft:chest");
            list.add("minecraft:chest_minecart");
            list.add("minecraft:clay");
            list.add("minecraft:cobblestone");
            list.add("minecraft:cobweb");
            list.add("minecraft:dark_oak_leaves");
            list.add("minecraft:dark_oak_log");
            list.add("minecraft:diorite");
            list.add("minecraft:dirt");
            list.add("minecraft:end_stone");
            list.add("minecraft:flowing_lava");
            list.add("minecraft:flowing_water");
            list.add("minecraft:granite");
            list.add("minecraft:grass");
            list.add("minecraft:grass_block");
            list.add("minecraft:gravel");
            list.add("minecraft:jungle_leaves");
            list.add("minecraft:jungle_log");
            list.add("minecraft:lava");
            list.add("minecraft:mossy_cobblestone");
            list.add("minecraft:netherrack");
            list.add("minecraft:oak_fence");
            list.add("minecraft:oak_leaves");
            list.add("minecraft:oak_log");
            list.add("minecraft:oak_planks");
            list.add("minecraft:obsidian");
            list.add("minecraft:rail");
            list.add("minecraft:sand");
            list.add("minecraft:sandstone");
            list.add("minecraft:spruce_leaves");
            list.add("minecraft:spruce_log");
            list.add("minecraft:stone");
            list.add("minecraft:tall_grass");
            list.add("minecraft:water");
        String[] removeList = list.toArray(new String[0]);

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
                        Arrays.stream(removeList).filter(tBlock.getRegistryName().toString()::equals).forEachOrdered(block ->
                            world.setBlockState(tBlockPos, Objects.requireNonNull(
                                ForgeRegistries.BLOCKS.getValue(
                                    new ResourceLocation("minecraft:air")
                                )
                            ).getDefaultState(), 3)
                        );
                    }
                }
            }
        }
        source.sendFeedback(new TranslationTextComponent("commands.toolkit.remove.done"), true);
        return 1;
    }
}

