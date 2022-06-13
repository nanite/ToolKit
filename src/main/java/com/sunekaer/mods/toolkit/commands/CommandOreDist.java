package com.sunekaer.mods.toolkit.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommandOreDist {
    private static final DecimalFormat FORMATTER = new DecimalFormat("########0.00");

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("oredist")
                .requires(cs -> cs.hasPermission(0))
                .executes(ctx -> {
                    ctx.getSource().sendFailure(Component.translatable("commands.toolkit.oredist.missing"));
                    return 1;
                })
                .then(Commands.argument("AreaSize", IntegerArgumentType.integer())
                        .executes(ctx -> getOreDist(
                                ctx.getSource(),
                                ctx.getSource().getPlayerOrException(),
                                IntegerArgumentType.getInteger(ctx, "AreaSize")
                        ))
                );
    }

    private static int getOreDist(CommandSourceStack source, Player player, int size) {
        Map<String, Integer> map = new HashMap<>();

        double searchSize = ((16 * size) >> 1);
        double startX = player.position().x - searchSize;
        double startZ = player.position().z - searchSize;
        double endX = player.position().x + searchSize;
        double endZ = player.position().z + searchSize;
        Level world = player.getLevel();

        for (int y = world.getMinBuildHeight(); y < world.getMaxBuildHeight(); ++y) {
            for (double x = startX; x < endX; x++) {
                for (double z = startZ; z < endZ; z++) {
                    BlockPos tBlockPos = new BlockPos(x, y, z);
                    BlockState tBlockState = world.getBlockState(tBlockPos);
                    Block tBlock = tBlockState.getBlock();
                    if (!tBlock.equals(Blocks.AIR) && !tBlock.equals(Blocks.BEDROCK) && !tBlock.equals(Blocks.STONE) && !tBlock.equals(Blocks.DIRT) && !tBlock.equals(Blocks.WATER)) {
                        if (tBlock.builtInRegistryHolder().is(Tags.Blocks.ORES)) {
                            ResourceLocation key1 = Registry.BLOCK.getKey(tBlock);
                            String key = Objects.requireNonNull(key1).toString();
                            Object value = map.get(key1.toString());
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
        if (sum == 0) {
            source.sendSuccess(Component.translatable("\u00A7c No ores found"), true);
            return 1;
        }
        map.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x ->
                source.sendSuccess(Component.translatable("\u00A7c" + x.getKey() + " \u00A7rCount: " + x.getValue() + " (" + FORMATTER.format(x.getValue() * 100 / sum) + "%%)"), true)
        );
        return 1;
    }
}
