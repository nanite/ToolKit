package com.sunekaer.toolkit.neoforge;

import com.sunekaer.toolkit.XPlatShim;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class XPlatShimNeoImpl implements XPlatShim {
    @Override
    public Supplier<Path> configDirectory() {
        return FMLPaths.CONFIGDIR::get;
    }

    @Override
    public TagKey<Block> oresTag() {
        return Tags.Blocks.ORES;
    }

    @Override
    public List<ItemStack> itemsInBlockEntity(Level level, BlockPos pos, @Nullable Direction direction) {
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

    @Override
    public void sendPacketToPlayer(ServerPlayer player, CustomPacketPayload packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }
}
