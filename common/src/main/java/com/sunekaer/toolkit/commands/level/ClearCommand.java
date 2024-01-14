package com.sunekaer.toolkit.commands.level;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sunekaer.toolkit.ToolkitPlatform;
import com.sunekaer.toolkit.jobs.ServerTickJobRunner;
import com.sunekaer.toolkit.utils.ChunkRangeIterator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class ClearCommand {
    private static final AtomicBoolean COMPLETED = new AtomicBoolean(true);
    public static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return (Commands.literal("clear")
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> remove(context.getSource(), 1, RemovalPredicate.NAMES[0]))
                .then(Commands.argument("range", IntegerArgumentType.integer()).executes(ctx -> remove(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "range"), RemovalPredicate.NAMES[0]))
                .then(Commands.argument("filter", StringArgumentType.string()).suggests((commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(RemovalPredicate.NAMES, suggestionsBuilder)).executes(ctx -> remove(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "range"), StringArgumentType.getString(ctx, "filter"))))));
    }

    private static int remove(CommandSourceStack source, int size, String filter) throws CommandSyntaxException {
        // Block running
        if (!COMPLETED.get()) {
            source.sendFailure(Component.literal("Already running, give it a second."));
            return 1;
        }

        COMPLETED.set(false);
        var player = source.getPlayerOrException();

        // Resolve the predicate check, we're lazy, so we'll get the enum regardless but if the filter looks like a
        // tag then lets try a tag instead
        var removalCheck = RemovalPredicate.getFromName(filter).orElse(RemovalPredicate.JUST_ORES);
        Predicate<BlockState> customCheck = null;
        if (filter.startsWith("#")) {
            customCheck = state -> state.is(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(filter.replace("#", ""))));
        } else if(filter.contains(":")) {
            customCheck = state -> Registry.BLOCK.getKey(state.getBlock()).toString().equalsIgnoreCase(filter);
        }

        ServerLevel level = source.getLevel();
        source.sendSuccess(Component.translatable("commands.toolkit.remove.lagwarring"), true);

        // We're removing 1 to make it so 1 = 0, 2 - 1, etc, this means we'll have correct radius 0 = 1x1, 1 = 3x3, 2 = 5x5
        var range = size - 1;
        var chunkPos = player.chunkPosition();

        // Compute the max height for the wall and queue the chunk removal
        Predicate<BlockState> finalCustomCheck = customCheck;
        COMPLETED.set(false);

        for (int x = chunkPos.x - range; x <= chunkPos.x + range; x++) {
            for (int z = chunkPos.z- range; z <= chunkPos.z + range; z++) {
                var currentChunkPos = new ChunkPos(x, z);

                final boolean shouldComplete = x == chunkPos.x + range && z == chunkPos.z + range;

                ServerTickJobRunner.get().add(() -> {
                    removeChunk(level, currentChunkPos, finalCustomCheck != null ? finalCustomCheck : removalCheck.stateCheck);

                    // If this is the last chunk, then we're done
                    if (shouldComplete) {
                        COMPLETED.set(true);
                    }
                });
            }
        }

        return 1;
    }

    /**
     * Removes an entire chunks worth of blocks based on a block check predicate
     *
     * @param level current world
     * @param chunkPos the chunk to remove
     * @param blockCheck the predicate that defines what blocks are removed
     */
    private static void removeChunk(ServerLevel level, ChunkPos chunkPos, Predicate<BlockState> blockCheck) {
        BlockState airState = Blocks.AIR.defaultBlockState();

        ChunkRangeIterator iterator = new ChunkRangeIterator(level, chunkPos, 1, true);
        List<BlockPos> updatedBlocks = new ArrayList<>();

        int maxHeight = level.dimension() == ServerLevel.NETHER ? 127 : level.getMaxBuildHeight();

        while (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            final BlockState state = level.getBlockState(pos);

            // Don't remove bedrock and skip air, it's a waste of computation
            if (state.isAir() || state.getBlock() == Blocks.BEDROCK) {
                if (!state.isAir() && pos.getY() > level.getMinBuildHeight() && pos.getY() < maxHeight) {
                    level.setBlock(pos, airState, Block.UPDATE_CLIENTS);
                    updatedBlocks.add(pos);
                }

                continue;
            }

            if (blockCheck.test(state)) {
                continue;
            }

            level.setBlock(pos, airState, Block.UPDATE_CLIENTS);
            updatedBlocks.add(pos);
        }

        for (BlockPos pos : updatedBlocks) {
            level.blockUpdated(pos, airState.getBlock());
        }
    }

    private enum RemovalPredicate {
        JUST_ORES(state -> state.is(ToolkitPlatform.getOresTag())),
        ORES_AND_MODDED(state -> state.is(ToolkitPlatform.getOresTag()) && Registry.BLOCK.getKey(state.getBlock()).getNamespace().equals("minecraft"));

        public static final List<RemovalPredicate> VALUES = Arrays.asList(values());
        public static final String[] NAMES = VALUES.stream().map(e -> e.toString().toLowerCase()).toArray(String[]::new);

        final Predicate<BlockState> stateCheck;
        RemovalPredicate(Predicate<BlockState> stateCheck) {
            this.stateCheck = stateCheck;
        }

        public static Optional<RemovalPredicate> getFromName(String name) {
            return VALUES.stream().filter(e -> e.toString().toLowerCase().equals(name)).findFirst();
        }
    }
}

