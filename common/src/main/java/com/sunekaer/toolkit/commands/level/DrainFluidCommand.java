package com.sunekaer.toolkit.commands.level;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.sunekaer.toolkit.jobs.ServerTickJobRunner;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.FluidState;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class DrainFluidCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("drain")
                .requires(cs -> cs.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .then(Commands.argument("area-size", IntegerArgumentType.integer(1, 300))
                        .executes(ctx -> drainFluids(
                                ctx.getSource(),
                                ctx.getSource().getPlayerOrException().blockPosition(),
                                IntegerArgumentType.getInteger(ctx, "area-size"),
                                false
                        )))
                .then(Commands.argument("location", BlockPosArgument.blockPos())
                        .then(Commands.argument("area-size", IntegerArgumentType.integer(1, 300))
                        .executes(ctx -> drainFluids(
                                ctx.getSource(),
                                BlockPosArgument.getLoadedBlockPos(ctx, "location"),
                                IntegerArgumentType.getInteger(ctx, "area-size"),
                                true
                        ))
                ));
    }

    private static int drainFluids(CommandSourceStack source, BlockPos location, int areaSize, boolean shift) {
        // Get the fluid below the player
        ServerLevel level = source.getLevel();
        BlockPos pos = location;

        if (shift) {
            // Look all around the block for a fluid
            for (Direction dir : Direction.values()) {
                BlockPos offset = pos.relative(dir);
                FluidState currentState = level.getFluidState(offset);

                if (currentState.isEmpty()) {
                    continue;
                }

                pos = offset;
                break;
            }
        }

        BlockState state = level.getBlockState(pos);

        if (state.isAir()) {
            source.sendFailure(Component.literal("Go closer to the fluid source"));
            return 1;
        }

        if (state.getFluidState().isEmpty()) {
            source.sendFailure(Component.literal("No fluid found"));
            return 1;
        }

        FluidState fluidState = state.getFluidState();

        Deque<BlockPos> scanQueue = new ArrayDeque<>();
        scanQueue.add(pos);

        int maxSize = Math.min(areaSize, 300);
        var box = new BoundingBox(pos).inflatedBy(maxSize);

        Set<BlockPos> locationsToRemove = new HashSet<>();

        while (!scanQueue.isEmpty()) {
            BlockPos currentPos = scanQueue.pop();

            locationsToRemove.add(currentPos);
            for (Direction dir : Direction.values()) {
                BlockPos offset = currentPos.relative(dir);
                FluidState currentState = level.getFluidState(offset);

                if (currentState.isEmpty()) {
                    continue;
                }

                if (!fluidState.getType().isSame(currentState.getType())) {
                    continue;
                }

                if (!locationsToRemove.contains(offset)) {
                    if (!box.isInside(offset)) {
                        continue;
                    }

                    scanQueue.add(offset);
                    locationsToRemove.add(offset);
                }
            }
        }

        ServerTickJobRunner.get().add(() -> {
            for (BlockPos blockPos : locationsToRemove) {
                level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                level.updateNeighborsAt(blockPos, Blocks.AIR);
            }
        });

        return 0;
    }
}
