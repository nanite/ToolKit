package com.sunekaer.mods.toolkit.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraftforge.common.Tags;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class CommandClear {
    private static final AtomicInteger remainingActions = new AtomicInteger(0);

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return (Commands.literal("clear")
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> remove(context.getSource(), 1, RemovalPredicate.NAMES[0]))
                .then(Commands.argument("range", IntegerArgumentType.integer()).executes(ctx -> remove(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "range"), RemovalPredicate.NAMES[0]))
                .then(Commands.argument("filter", StringArgumentType.string()).suggests((commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(RemovalPredicate.NAMES, suggestionsBuilder)).executes(ctx -> remove(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "range"), StringArgumentType.getString(ctx, "filter"))))));
    }

    private static int remove(CommandSourceStack source, int size, String filter) throws CommandSyntaxException {
        // Block running
        if (remainingActions.get() > 0) {
            source.sendFailure(new TextComponent("Already running, give it a second."));
            return 1;
        }

        var player = source.getPlayerOrException();

        // Resolve the predicate check, we're lazy, so we'll get the enum regardless but if the filter looks like a
        // tag then lets try a tag instead
        var removalCheck = RemovalPredicate.getFromName(filter).orElse(RemovalPredicate.JUST_ORES);
        Predicate<BlockState> customCheck = null;
        if (filter.startsWith("#")) {
            customCheck = state -> state.is(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(filter.replace("#", ""))));
        } else if(filter.contains(":")) {
            customCheck = state -> state.getBlock().getRegistryName().toString().equalsIgnoreCase(filter);
        }

        ServerLevel level = source.getLevel();
        source.sendSuccess(new TranslatableComponent("commands.toolkit.remove.lagwarring"), true);

        // We're removing 1 to make it so 1 = 0, 2 - 1, etc, this means we'll have correct radius 0 = 1x1, 1 = 3x3, 2 = 5x5
        var range = size - 1;
        var chunkPos = player.chunkPosition();

        // Generates a min & max bounds for the removal range
        RangeBounds bounds = RangeBounds.from(chunkPos, range);

        // Make a wall around the blocks area
        List<BlockPos> wallPositions = new ArrayList<>();
        for (int x = bounds.minX - 1; x < bounds.maxX + 1; x++) {
            for (int z = bounds.minZ - 1; z < bounds.maxZ + 1; z ++ ) {
                if (x == (bounds.minX - 1) || x == (bounds.maxX + 1) -1 || z == (bounds.minZ - 1) || z == ((bounds.maxZ + 1) - 1)) {
                    for (int i = level.getMinBuildHeight(); i < level.getMaxBuildHeight(); i ++) {
                        wallPositions.add(new BlockPos(x, i, z));
                    }
                }
            }
        }

        // Compute the max height for the wall and queue the chunk removal
        int maxHeight = 0;
        for (int x = chunkPos.x - range; x <= chunkPos.x + range; x++) {
            for (int z = chunkPos.z- range; z <= chunkPos.z + range; z++) {
                var currentChunkPos = new ChunkPos(x, z);
                LevelChunkSection[] sections = level.getChunk(x, z).getSections();
                // Compute the max height based on chunk sections
                for (LevelChunkSection section : sections) {
                    if (section.hasOnlyAir()) {
                        if (maxHeight < section.bottomBlockY()) {
                            maxHeight = section.bottomBlockY();
                        }
                        break;
                    }
                }

                Predicate<BlockState> finalCustomCheck = customCheck;
                source.getServer().submitAsync(() -> removeChunk(level, currentChunkPos, finalCustomCheck != null ? finalCustomCheck : removalCheck.stateCheck));
                remainingActions.incrementAndGet();
            }
        }

        // Run over all the wall positions and build a wall & lights (randomly placed)
        final int finalMaxHeight = maxHeight;
        Random random = new Random();
        source.getServer().submitAsync(() -> wallPositions.stream()
                .filter(e -> e.getY() < finalMaxHeight)
                .filter(e -> !level.getBlockState(e).is(Blocks.BEDROCK))
                .forEach(e -> {
                    if (random.nextDouble() > .95) {
                        level.setBlock(e, Blocks.GLOWSTONE.defaultBlockState(), 3);
                    } else {
                        level.setBlock(e, Blocks.STONE.defaultBlockState(), 3);
                    }
                })
        );

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
        LevelChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);
        List<LevelChunkSection> sections = Arrays.stream(chunk.getSections()).filter(e -> !e.hasOnlyAir()).toList();

        for (LevelChunkSection section : sections) {
            for (int y = section.bottomBlockY(); y < section.bottomBlockY() + 16; y ++) {
                // For people that don't know what bit shifting is, google it...
                for (int x = chunkPos.x << 4; x < (chunkPos.x << 4) + (1 << 4); x ++) {
                    for (int z = chunkPos.z << 4; z < (chunkPos.z << 4) + (1 << 4); z ++) {
                        final BlockPos pos = new BlockPos(x, y, z);
                        final BlockState state = level.getBlockState(pos);

                        // Don't remove bedrock and skip air, it's a waste of computation
                        if (state.isAir() || state.getBlock() == Blocks.BEDROCK) {
                            continue;
                        }

                        if (blockCheck.test(state)) {
                            continue;
                        }

                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    }
                }
            }
        }

        remainingActions.decrementAndGet();
    }

    private enum RemovalPredicate {
        JUST_ORES(state -> state.is(Tags.Blocks.ORES)),
        ORES_AND_MODDED(state -> state.is(Tags.Blocks.ORES) && state.getBlock().getRegistryName().getNamespace().equals("minecraft"));

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

    private record RangeBounds(
       int minX,
       int maxX,
       int minZ,
       int maxZ
    ) {
      public static RangeBounds from(ChunkPos pos, int range) {
          return new RangeBounds(
            ((pos.x - range) << 4),
            ((pos.x + range) << 4) + (1 << 4),
            ((pos.z - range) << 4),
            ((pos.z + range) << 4) + (1 << 4)
          );
      }
    }
}

