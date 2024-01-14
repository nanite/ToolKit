package com.sunekaer.toolkit;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;

public class ToolkitPlatform {
    @ExpectPlatform
    public static Path getConfigDirectory() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static TagKey<Block> getOresTag() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Path getGamePath() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static List<ItemStack> getInventoryFromBlockEntity(Level level, BlockPos pos, @Nullable Direction direction) {
        throw new AssertionError();
    }
}
