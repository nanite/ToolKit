package com.sunekaer.mods.toolkit.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.command.Commands.literal;

public class CommandSlayer {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return literal("slayer")
                .requires(cs -> cs.hasPermissionLevel(2)) //permission
                .executes(ctx -> giveItem(
                        ctx.getSource(),
                        ctx.getSource().asPlayer()
                        )
                );
    }

    private static int giveItem(CommandSource source, ServerPlayerEntity player) {
        System.out.println(source.toString());
        ItemStack itemstack = new ItemStack(Items.DIAMOND_SWORD);
        itemstack.setDisplayName(new TranslationTextComponent("commands.dragonslayer.name"));
        Map<Enchantment, Integer> map = new HashMap<>();
        map.put(Enchantments.SHARPNESS, 32767);
        EnchantmentHelper.setEnchantments(map, itemstack);

        int count = 1;
        int i = count;

            while(i > 0) {
                int j = Math.min(Items.CLAY.getItem().getMaxStackSize(), i);
                i -= j;
                boolean flag = player.inventory.addItemStackToInventory(itemstack);
                if (flag && itemstack.isEmpty()) {
                    itemstack.setCount(1);
                    ItemEntity itementity1 = player.dropItem(itemstack, false);
                    if (itementity1 != null) {
                        itementity1.makeFakeItem();
                    }

                    player.world.playSound((PlayerEntity)null, player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    player.container.detectAndSendChanges();
                } else {
                    ItemEntity itementity = player.dropItem(itemstack, false);
                    if (itementity != null) {
                        itementity.setNoPickupDelay();
                        itementity.setOwnerId(player.getUniqueID());
                    }
                }

        }
        return 1;
    }
}
