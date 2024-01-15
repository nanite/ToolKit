package com.sunekaer.toolkit.neoforge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ToolkitPlatformImpl {
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static TagKey<Block> getOresTag() {
        return Tags.Blocks.ORES;
    }

    public static Path getGamePath() {
        return FMLPaths.GAMEDIR.get();
    }

    public static List<ItemStack> getInventoryFromBlockEntity(Level level, BlockPos pos, @Nullable Direction direction) {
        List<ItemStack> items = new ArrayList<>();

        var obj = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, Direction.UP);
        if(obj != null) {
            for (int i = 0; i < obj.getSlots(); i++) {
                var stack = obj.getStackInSlot(i);
                if (stack.isEmpty()) {
                    continue;
                }
                items.add(stack);
            }
        }

        return items;
    }
}
