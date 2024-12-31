package com.sunekaer.toolkit.commands.player;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.Collection;
import java.util.List;

public class TeleportDimensionSpawnCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("tpdim")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("dimension", DimensionArgument.dimension())
                        .executes(ctx -> teleport(ctx.getSource(), ctx.getSource().getServer(), List.of(ctx.getSource().getPlayerOrException()), DimensionArgument.getDimension(ctx, "dimension")))
                        .then(Commands.argument("target", EntityArgument.entities()).executes(ctx -> teleport(ctx.getSource(), ctx.getSource().getServer(), EntityArgument.getEntities(ctx, "target"), DimensionArgument.getDimension(ctx, "dimension")))));    }

    private static int teleport(CommandSourceStack source, MinecraftServer server, Collection<? extends Entity> entities, ServerLevel dimension) {
        // Find the spawn point of the dimension
        ServerLevel level = server.getLevel(dimension.dimension());
        if (level == null) {
            source.sendFailure(Component.literal("Dimension not found"));
            return 0;
        }

        for (Entity entity : entities) {
            BlockPos pos = entity.blockPosition();
            if (!level.getWorldBorder().isWithinBounds(pos)) {
                pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos((int) level.getWorldBorder().getCenterX(), 0, (int) level.getWorldBorder().getCenterZ()));
            }

            int playerXp = 0;

            if (entity instanceof ServerPlayer serverPlayer) {
                playerXp = serverPlayer.experienceLevel;
                serverPlayer.teleportTo(level, pos.getX(), pos.getY(), pos.getZ(), Relative.ALL, entity.getYRot(), entity.getXRot(), true);
                serverPlayer.setExperienceLevels(playerXp);
            } else {
                entity.teleportTo(pos.getX(), pos.getY(), pos.getZ());
            }

            // Force load the chunk
            if (!level.isLoaded(pos)) {
                level.getChunk(pos);
            }

            // Check if the blocks where the player will spawn are not air
            if (entity instanceof ServerPlayer) {
                BlockState playerFeetBlock = level.getBlockState(pos);
                BlockState playerHeadBlock = level.getBlockState(pos.above());

                if (!playerHeadBlock.isAir() || !playerFeetBlock.isAir()) {
                    BoundingBox box = new BoundingBox(pos).inflatedBy(1);
                    BlockPos.betweenClosedStream(box).forEach(blockPos -> {
                        BlockState state = level.getBlockState(blockPos);
                        if (!state.isAir()) {
                            level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                        }
                    });
                }
            }
        }

        return 1;
    }
}
