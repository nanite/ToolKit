package com.sunekaer.toolkit.fabric;

import com.sunekaer.toolkit.XPlatShim;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class XPlatShimFabricImpl implements XPlatShim {
    @Override
    public TagKey<Block> oresTag() {
        return ConventionalBlockTags.ORES;
    }

    @Override
    public List<ItemStack> itemsInBlockEntity(Level level, BlockPos pos, @Nullable Direction direction) {
        List<ItemStack> items = new ArrayList<>();

        Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, pos, direction);

        if (storage == null) {
            return items;
        }

        for (StorageView<ItemVariant> view : storage) {
            ItemStack stack = view.getResource().toStack();
            if (stack.isEmpty()) {
                continue;
            }

            items.add(stack);
        }

        return items;
    }
}
