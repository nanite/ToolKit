package com.sunekaer.mods.toolkit.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.sunekaer.mods.toolkit.network.Handler;
import com.sunekaer.mods.toolkit.network.SetCopy;
import com.sunekaer.mods.toolkit.utils.CommandUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

import java.util.Objects;


public class CommandHotbar {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("hotbar")
                .requires(cs -> cs.hasPermission(0)) //permission
                .executes(ctx -> getHotbar(
                        ctx.getSource(),
                        ctx.getSource().getPlayerOrException()
                        )
                );
    }

    private static int getHotbar(CommandSourceStack source, Player player) {
        StringBuilder clipboard = new StringBuilder();
        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = player.getInventory().getItem(slot); //.inventory.mainInventory.get(slot)

            if (stack.isEmpty()) {
                continue;
            }

            String itemName = Objects.requireNonNull(Registry.ITEM.getKey(stack.getItem())).toString();

            String withNBT = "";
            CompoundTag nbt = stack.serializeNBT();
            if (nbt.contains("tag")) {
                withNBT += nbt.get("tag");
            }

            clipboard.append(itemName).append(withNBT).append(CommandUtils.NEW_LINE);
        }

        source.sendSuccess(Component.translatable("commands.toolkit.clipboard.copied"), true);
        Handler.MAIN.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new SetCopy(clipboard.toString()));
        return 1;
    }
}
