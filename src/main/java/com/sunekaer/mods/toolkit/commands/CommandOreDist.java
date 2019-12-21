package com.sunekaer.mods.toolkit.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.Tags;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public class CommandOreDist {
    private static final DecimalFormat FORMATTER = new DecimalFormat("########0.00");

    public static ArgumentBuilder<CommandSource, ?> register() {
        return literal("oredist")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(ctx -> {
                    ctx.getSource().sendErrorMessage(new TranslationTextComponent("commands.toolkit.oredist.missing"));
                    return 1;
                })
                .then(argument("Dimension", DimensionArgument.getDimension())
                        .executes(ctx -> {
                            ctx.getSource().sendErrorMessage(new TranslationTextComponent("commands.toolkit.oredist.missing1"));
                            return 1;
                        })
                        .then(argument("AreaSize", IntegerArgumentType.integer())
                                .executes(ctx -> getOreDist(
                                        ctx.getSource(),
                                        ctx.getSource().asPlayer(),
                                        DimensionArgument.func_212592_a(ctx, "Dimension"),
                                        IntegerArgumentType.getInteger(ctx, "AreaSize")
                                ))
                        )
                );
    }

    private static int getOreDist(CommandSource source, PlayerEntity player, DimensionType dim, int size) {
        Map<String, Integer> map = new HashMap<String, Integer>();

        double searchSize = ((16 * size) / 2);
        double startX = player.getPosition().getX() - searchSize;
        double startZ = player.getPosition().getZ() - searchSize;
        double endX = player.getPosition().getX() + searchSize;
        double endZ = player.getPosition().getZ() + searchSize;
        World world = source.getServer().getWorld(dim);

        for (int y = 0; y < world.getActualHeight(); ++y) {
            for (double x = startX; x < endX; x++) {
                for (double z = startZ; z < endZ; z++) {
                    BlockPos tBlockPos = new BlockPos(x, y, z);
                    BlockState tBlockState = world.getBlockState(tBlockPos);
                    Block tBlock = tBlockState.getBlock();
                    if (!tBlock.equals(Blocks.AIR) && !tBlock.equals(Blocks.BEDROCK) && !tBlock.equals(Blocks.STONE) && !tBlock.equals(Blocks.DIRT) && !tBlock.equals(Blocks.WATER)) {
                        if (Tags.Blocks.ORES.contains(tBlock.getBlock())) {
                            String key = tBlock.getBlock().getRegistryName().toString();
                            Object value = map.get(tBlock.getBlock().getRegistryName().toString());
                            if (value != null) {
                                map.put(key, map.get(key) + 1);
                            } else {
                                map.put(key, 1);
                            }
                        }
                    }
                }
            }
        }

        double sum = map.values().stream().reduce(0, Integer::sum);
        map.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x ->
                source.sendFeedback(new TranslationTextComponent("\u00A7c" + x.getKey() + " \u00A7rCount: " + x.getValue() + " (" + FORMATTER.format(x.getValue() * 100 / sum) + "%%)"), true)
        );

        return 1;
    }
}
