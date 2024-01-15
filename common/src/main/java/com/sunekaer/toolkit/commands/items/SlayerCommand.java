package com.sunekaer.toolkit.commands.items;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;

import static net.minecraft.world.item.ItemStack.TAG_ENCH;


public class SlayerCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("slayer")
                .requires(cs -> cs.hasPermission(2)) //permission
                .executes(ctx -> giveItem(ctx.getSource().getPlayerOrException()));
    }

    private static int giveItem(ServerPlayer player) {
        ItemStack itemstack = new ItemStack(Items.NETHERITE_SWORD);
        itemstack.setHoverName(Component.translatable("commands.dragonslayer.name"));

        var enchantments = List.of(
                Enchantments.SHARPNESS, Enchantments.KNOCKBACK, Enchantments.UNBREAKING, Enchantments.BANE_OF_ARTHROPODS,
                Enchantments.SMITE, Enchantments.SWEEPING_EDGE
        );

        itemstack.getOrCreateTag().put(TAG_ENCH, new ListTag());
        ListTag listtag = itemstack.getOrCreateTag().getList(TAG_ENCH, 10);
        enchantments.forEach(e ->
                listtag.add(EnchantmentHelper.storeEnchantment(EnchantmentHelper.getEnchantmentId(e), Short.MAX_VALUE)));

        boolean added = player.getInventory().add(itemstack.copy());
        if (!added) {
            ItemEntity drop = player.drop(itemstack.copy(), false);
            if (drop != null) {
                drop.setNoPickUpDelay();
                drop.setTarget(player.getUUID());
            }
        }

        return 1;
    }
}
