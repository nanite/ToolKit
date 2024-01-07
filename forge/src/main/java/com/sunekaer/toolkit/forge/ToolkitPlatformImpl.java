package com.sunekaer.toolkit.forge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fml.loading.FMLPaths;
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

        BlockEntity entity = level.getBlockEntity(pos);
        if (entity == null) {
            return items;
        }

        entity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            for (int i = 0; i < handler.getSlots(); i++) {
                var stack = handler.getStackInSlot(i);
                if (stack.isEmpty()) {
                    continue;
                }
                items.add(stack);
            }
        });

        return items;
    }
}
