package com.sunekaer.toolkit.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.Iterator;

/**
 * ChunkRangeIterator takes in a starting chunkpos and expands the chunk selection by a give size. We'll then iterate
 * over all of the chunks in the range and return every blockpos in each chunk in the range.
 */
public class ChunkRangeIterator implements Iterator<BlockPos> {
    private final Level level;

    private final int minX;
    private final int minZ;

    private final int maxX;
    private final int maxZ;

    private int currentX;
    private int currentY;
    private int currentZ;

    private final boolean reverseY;

    public ChunkRangeIterator(Level level, ChunkPos startPos, int size) {
        this(level, startPos, size, false);
    }

    public ChunkRangeIterator(Level level, ChunkPos startPos, int size, boolean reverseY) {
        this.level = level;
        int rangeSize = Math.max(0, size / 2);

        this.minX = (startPos.x - rangeSize) << 4;
        this.minZ = (startPos.z - rangeSize) << 4;

        this.maxX = ((startPos.x + rangeSize) << 4) + (1 << 4);
        this.maxZ = ((startPos.z + rangeSize) << 4) + (1 << 4);

        this.currentX = minX;
        this.currentZ = minZ;
        this.reverseY = reverseY;
        this.currentY = reverseY ? level.getMaxBuildHeight() : level.getMinBuildHeight();
    }

    @Override
    public boolean hasNext() {
        return (this.reverseY ? currentY > level.getMinBuildHeight() : currentY < level.getMaxBuildHeight())
                && currentZ < maxZ
                && currentX < maxX;
    }

    @Override
    public BlockPos next() {
        BlockPos pos = new BlockPos(currentX, currentY, currentZ);

        currentX++;
        if (currentX >= maxX) {
            currentX = minX;
            currentZ++;
            if (currentZ >= maxZ) {
                currentZ = minZ;
                if (reverseY) {
                    currentY--;
                } else {
                    currentY++;
                }
            }
        }

        return pos;
    }
}
