package com.sunekaer.toolkit.commands.level;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sunekaer.toolkit.Toolkit;
import com.sunekaer.toolkit.jobs.ServerTickJobRunner;
import com.sunekaer.toolkit.utils.ChunkRangeIterator;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class ClearCommand {
    private static final AtomicBoolean COMPLETED = new AtomicBoolean(true);
    public static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "Toolkit-ClearCommand-Executor");
        thread.setDaemon(true); // Ensure it's killed with the server stopping
        return thread;
    });

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext commandBuildContext) {
        return (Commands.literal("clear")
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> remove(context.getSource(), 1, RemovalPredicate.NAMES[0], false))
                .then(Commands.argument("range", IntegerArgumentType.integer())
                        .executes(ctx -> remove(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "range"), RemovalPredicate.NAMES[0], false))
                        .then(Commands.argument("filter", StringArgumentType.string())
                                .suggests((commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(RemovalPredicate.NAMES, suggestionsBuilder))
                                .executes(ctx -> remove(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "range"), StringArgumentType.getString(ctx, "filter"), false))
                                .then(Commands.argument("removes_bedrock", BoolArgumentType.bool())
                                        .executes(ctx -> remove(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "range"), StringArgumentType.getString(ctx, "filter"), BoolArgumentType.getBool(ctx, "removes_bedrock")))
                                )
                        )
                        .then(Commands.literal("custom_filter")
                                .then(Commands.argument("block", BlockPredicateArgument.blockPredicate(commandBuildContext))
                                        .executes(ctx -> remove(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "range"), BlockPredicateArgument.getBlockPredicate(ctx, "block")))
                                )
                        )
                )
        );
    }

    private static int remove(CommandSourceStack source, int size, String filter, boolean removesBedrock) throws CommandSyntaxException {
        _remove(new RemoveContext(
            source,
            size,
            RemovalPredicate.getFromName(filter).orElse(RemovalPredicate.JUST_ORES).stateCheck,
            removesBedrock
        ));

        return 1;
    }

    private static int remove(CommandSourceStack source, int size, Predicate<BlockInWorld> blockCheck) throws CommandSyntaxException {
        _remove(new RemoveContext(
                source,
                size,
                blockCheck,
                false
        ));

        return 1;
    }

    private static void _remove(RemoveContext context) throws CommandSyntaxException {
        var source = context.commandStack;

        // Block running
        if (!COMPLETED.get()) {
            source.sendFailure(Component.literal("Already running, give it a second."));
            return;
        }

        COMPLETED.set(false);
        var player = source.getPlayerOrException();

        // Resolve the predicate check, we're lazy, so we'll get the enum regardless but if the filter looks like a
        // tag then lets try a tag instead
        var removalCheck = context.filter;

        ServerLevel level = source.getLevel();
        source.sendSuccess(() -> Component.translatable("commands.toolkit.remove.lagwarring"), true);

        // We're removing 1 to make it so 1 = 0, 2 - 1, etc, this means we'll have correct radius 0 = 1x1, 1 = 3x3, 2 = 5x5
        var range = context.size - 1;
        var chunkPos = player.chunkPosition();

        COMPLETED.set(false);

        for (int x = chunkPos.x - range; x <= chunkPos.x + range; x++) {
            for (int z = chunkPos.z- range; z <= chunkPos.z + range; z++) {
                var currentChunkPos = new ChunkPos(x, z);

                final boolean shouldComplete = x == chunkPos.x + range && z == chunkPos.z + range;

                ServerTickJobRunner.get().add(() -> {
                    removeChunk(level, currentChunkPos, removalCheck, context.removesBedrock);

                    // If this is the last chunk, then we're done
                    if (shouldComplete) {
                        COMPLETED.set(true);
                    }
                });
            }
        }
    }

    /**
     * Removes an entire chunks worth of blocks based on a block check predicate
     *
     * @param level current world
     * @param chunkPos the chunk to remove
     * @param blockCheck the predicate that defines what blocks are removed
     */
    private static void removeChunk(ServerLevel level, ChunkPos chunkPos, Predicate<BlockInWorld> blockCheck, boolean removeBedrock) {
        BlockState airState = Blocks.AIR.defaultBlockState();

        ChunkRangeIterator iterator = new ChunkRangeIterator(level, chunkPos, 1, true);
        List<BlockPos> updatedBlocks = new ArrayList<>();

        int maxHeight = level.dimension() == ServerLevel.NETHER ? 127 : level.getMaxY();
        while (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            final BlockState state = level.getBlockState(pos);

            // Don't remove bedrock and skip air, it's a waste of computation
            if (state.isAir() || (!removeBedrock && state.getBlock() == Blocks.BEDROCK)) {
                if (!state.isAir() && pos.getY() > level.getMinY() && pos.getY() < maxHeight) {
                    level.setBlock(pos, airState, Block.UPDATE_CLIENTS);
                    updatedBlocks.add(pos);
                }

                continue;
            }

            var blockInWorld = new BlockInWorld(level, pos, true);
            if (!blockCheck.test(blockInWorld)) {
                continue;
            }

            level.setBlock(pos, airState, Block.UPDATE_CLIENTS);
            updatedBlocks.add(pos);
        }

        for (BlockPos pos : updatedBlocks) {
            level.updateNeighborsAt(pos, airState.getBlock());
        }
    }

    private enum RemovalPredicate {
        JUST_ORES(state -> !state.getState().is(Toolkit.PLATFORM.oresTag())),
        ORES_AND_MODDED(state -> !state.getState().is(Toolkit.PLATFORM.oresTag()) || !BuiltInRegistries.BLOCK.getKey(state.getState().getBlock()).getNamespace().equals("minecraft"));

        public static final List<RemovalPredicate> VALUES = Arrays.asList(values());
        public static final String[] NAMES = VALUES.stream().map(e -> e.toString().toLowerCase()).toArray(String[]::new);

        final Predicate<BlockInWorld> stateCheck;
        RemovalPredicate(Predicate<BlockInWorld> stateCheck) {
            this.stateCheck = stateCheck;
        }

        public static Optional<RemovalPredicate> getFromName(String name) {
            return VALUES.stream().filter(e -> e.toString().toLowerCase().equals(name)).findFirst();
        }
    }

    private record RemoveContext(
        CommandSourceStack commandStack,
        int size,
        Predicate<BlockInWorld> filter,
        boolean removesBedrock
    ) {}
}

