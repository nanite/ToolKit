package com.sunekaer.toolkit.commands.items;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.List;




public class SlayerCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("slayer")
                .requires(cs -> cs.hasPermission(2)) //permission
                .executes(ctx -> giveItem(ctx.getSource().getPlayerOrException()));
    }

    private static int giveItem(ServerPlayer player) {
        ItemStack itemstack = new ItemStack(Items.NETHERITE_SWORD);
        itemstack.set(DataComponents.CUSTOM_NAME, Component.translatable("commands.dragonslayer.name"));

        var enchantments = List.of(
                Enchantments.SHARPNESS, Enchantments.KNOCKBACK, Enchantments.UNBREAKING, Enchantments.BANE_OF_ARTHROPODS,
                Enchantments.SMITE, Enchantments.SWEEPING_EDGE
        );

        var enchants = itemstack.get(DataComponents.ENCHANTMENTS);
        var mutableEnchants = new ItemEnchantments.Mutable(enchants);
        Registry<Enchantment> enchantmentRegistry = player.server.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        enchantments.forEach(e -> mutableEnchants.set(enchantmentRegistry.getHolderOrThrow(e), Short.MAX_VALUE));
        itemstack.set(DataComponents.ENCHANTMENTS, mutableEnchants.toImmutable());

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
