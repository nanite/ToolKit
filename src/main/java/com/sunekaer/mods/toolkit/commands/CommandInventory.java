package com.sunekaer.mods.toolkit.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.sunekaer.mods.toolkit.utils.CommandUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;

import static net.minecraft.command.Commands.literal;
import static net.minecraft.realms.Realms.setClipboard;

public class CommandInventory {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return literal("inventory")
                .requires(cs -> cs.hasPermissionLevel(0)) //permission
                .executes(ctx -> getHotbar(
                        ctx.getSource(),
                        ctx.getSource().asPlayer()
                        )
                );
    }

    private static int getHotbar(CommandSource source, PlayerEntity player) {
        String clipboard = "";
        for (int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
            ItemStack stack = player.inventory.mainInventory.get(slot);

            if (stack.isEmpty()) {
                continue;
            }

            String itemName = stack.getItem().getRegistryName().toString();

            String withNBT = "";
            CompoundNBT nbt = stack.serializeNBT();
            if (nbt.contains("tag")) {
                withNBT += nbt.get("tag");
            }

            clipboard += itemName + withNBT + CommandUtils.NEW_LINE;
        }

        source.sendFeedback(new TranslationTextComponent("commands.toolkit.clipboard.copied"), true);
        setClipboard(clipboard);
        return 1;
    }
}
