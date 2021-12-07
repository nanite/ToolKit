package com.sunekaer.mods.toolkit.commands;


import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.HashMap;
import java.util.Map;


public class CommandSlayer {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("slayer")
                .requires(cs -> cs.hasPermission(2)) //permission
                .executes(ctx -> giveItem(
                        ctx.getSource().getPlayerOrException()
                        )
                );
    }

    private static int giveItem(ServerPlayer player) {
        ItemStack itemstack = new ItemStack(Items.DIAMOND_SWORD);
        itemstack.setHoverName(new TranslatableComponent("commands.dragonslayer.name"));
        Map<Enchantment, Integer> map = new HashMap<>();
        map.put(Enchantments.SHARPNESS, 255);
        EnchantmentHelper.setEnchantments(map, itemstack);

        int i = 1;
            while(i > 0) {
                int j = i;
                i -= j;
                boolean flag = player.getInventory().add(itemstack);
                if (flag && itemstack.isEmpty()) {
                    itemstack.setCount(1);
                    ItemEntity itementity1 = player.drop(itemstack, false);
                    if (itementity1 != null) {
                        itementity1.makeFakeItem();
                    }

                    player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    player.containerMenu.broadcastChanges();
                } else {
                    ItemEntity itementity = player.drop(itemstack, false);
                    if (itementity != null) {
                        itementity.setNoPickUpDelay();
                        itementity.setOwner(player.getUUID());
                    }
                }

        }
        return 1;
    }
}
