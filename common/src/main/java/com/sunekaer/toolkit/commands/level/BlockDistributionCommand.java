package com.sunekaer.toolkit.commands.level;

import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.sunekaer.toolkit.ToolkitPlatform;
import com.sunekaer.toolkit.utils.ChunkRangeIterator;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.text.DecimalFormat;
import java.util.Objects;

public class BlockDistributionCommand {
    private static final DecimalFormat FORMATTER = new DecimalFormat("########0.00");

    // Formatter for adding commas to large numbers
    private static final DecimalFormat COMMA_FORMATTER = new DecimalFormat("#,###");

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("oredist")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("area-size", IntegerArgumentType.integer(1, 1_000))
                        .executes(ctx -> getOreDist(
                                ctx.getSource(),
                                ctx.getSource().getPlayerOrException(),
                                IntegerArgumentType.getInteger(ctx, "area-size")
                        ))
                );
    }

    private static int getOreDist(CommandSourceStack source, Player player, int size) {
        ServerLevel level = source.getLevel();
        ChunkRangeIterator iterator = new ChunkRangeIterator(level, player.chunkPosition(), size);

        // Get the region files from the server stuffs

        Multiset<String> blockOccurrences = LinkedHashMultiset.create();

        while (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            BlockState state = level.getBlockState(pos);

            if (state.isAir() || state.is(Blocks.BEDROCK) || state.is(Blocks.STONE) || state.is(Blocks.DIRT) || state.is(Blocks.WATER)) {
                continue;
            }

            if (state.is(ToolkitPlatform.getOresTag())) {
                ResourceLocation key = BuiltInRegistries.BLOCK.getKey(state.getBlock());
                blockOccurrences.add(key.toString());
            }
        }

        double sum = blockOccurrences.size();
        if (sum == 0) {
            source.sendSuccess(() -> Component.literal("No ores found").withStyle(ChatFormatting.RED), true);
            return 1;
        }

        // Sort the multiset by count
        var sortedSet = LinkedHashMultiset.create(blockOccurrences.entrySet().stream().sorted((a, b) -> Integer.compare(b.getCount(), a.getCount())).toList());

        source.sendSuccess(() -> Component.literal("Block distribution for " + size + "x" + size + " chunks (total: " + COMMA_FORMATTER.format(sum) + ")").withStyle(ChatFormatting.GREEN), false);
        source.sendSuccess(() -> Component.literal(""), false);

        int i = 0;
        for (Multiset.Entry<String> entry : sortedSet) {
            int white = (i % 2 == 0) ? Objects.requireNonNull(ChatFormatting.WHITE.getColor()) : 12895171;
            int yellow =  (i % 2 == 0) ? Objects.requireNonNull(ChatFormatting.YELLOW.getColor()) : 14013728;

            var component = Component.literal("");

            component.append(Component.literal("[")).withStyle(Style.EMPTY.withColor(yellow));
            component.append(Component.literal(COMMA_FORMATTER.format(entry.getCount())).withStyle(Style.EMPTY.withColor(yellow).withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(FORMATTER.format(entry.getCount() * 100 / sum) + "%"))
            )));
            component.append(Component.literal("]").withStyle(Style.EMPTY.withColor(yellow)));
            component.append(Component.literal(" " + entry.getElement()).withStyle(Style.EMPTY.withColor(white)));

            source.sendSuccess(() -> component, false);
            i ++;
        }

        return 1;
    }
}
