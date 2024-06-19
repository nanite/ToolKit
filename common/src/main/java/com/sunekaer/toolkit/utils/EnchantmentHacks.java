package com.sunekaer.toolkit.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Objects;

public class EnchantmentHacks {
    public static void enchantItem(ItemStack stack, Holder.Reference<Enchantment> enchantment, short level) {
        var enchantments = stack.get(DataComponents.ENCHANTMENTS);
        var mutableEnchants = new ItemEnchantments.Mutable(Objects.requireNonNullElse(enchantments, ItemEnchantments.EMPTY));

        mutableEnchants.set(enchantment, level);
        stack.set(DataComponents.ENCHANTMENTS, mutableEnchants.toImmutable());
    }

    public static boolean removeEnchantment(ItemStack stack, Holder.Reference<Enchantment> enchantment) {
        var enchantments = stack.get(DataComponents.ENCHANTMENTS);
        if (enchantments == null) {
            return false;
        }

        var mutableEnchants = new ItemEnchantments.Mutable(enchantments);
        for (Holder<Enchantment> enchant : mutableEnchants.keySet()) {
            if (enchant.value().equals(enchantment.value())) {
                mutableEnchants.set(enchantment, 0);
                stack.set(DataComponents.ENCHANTMENTS, mutableEnchants.toImmutable());
                return true;
            }
        }

        return false;
    }
}
