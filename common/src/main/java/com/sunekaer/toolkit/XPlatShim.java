package com.sunekaer.toolkit;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

public interface XPlatShim {
    Supplier<Path> configDirectory();

    TagKey<Block> oresTag();

    List<ItemStack> itemsInBlockEntity(Level level, BlockPos pos, @Nullable Direction direction);

    void sendPacketToPlayer(ServerPlayer player, CustomPacketPayload packet);
}
