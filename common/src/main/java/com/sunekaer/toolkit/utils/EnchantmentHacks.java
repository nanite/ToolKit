package com.sunekaer.toolkit.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class EnchantmentHacks {
    public static void enchantItem(ItemStack stack, Enchantment enchantment, short level) {
        // CompoundTag tag = stack.getOrCreateTag();
        // if (!tag.contains(TAG_ENCH, 9)) {
        //     tag.put(TAG_ENCH, new ListTag());
        // }
        // ListTag listtag = tag.getList(TAG_ENCH, 10);
        // listtag.add(EnchantmentHelper.storeEnchantment(EnchantmentHelper.getEnchantmentId(enchantment), level));
    }

    public static boolean removeEnchantment(ItemStack stack, Enchantment enchantment) {
        //CompoundTag tag = stack.getOrCreateTag();
        //if (!tag.contains(TAG_ENCH)) {
        //    return false;
        //}
//
        //ListTag listTag = tag.getList(TAG_ENCH, 10);
//
        //ResourceLocation enchantmentId = EnchantmentHelper.getEnchantmentId(enchantment);
        //if (enchantmentId == null) {
        //    return false;
        //}
//
        //// Find the enchant
        //CompoundTag foundCompound = null;
        //for (int i = 0; i < listTag.size(); i++) {
        //    CompoundTag innerTag = listTag.getCompound(i);
        //    if (innerTag.contains("id") && innerTag.getString("id").equals(enchantmentId.toString())) {
        //        foundCompound = innerTag;
        //    }
        //}
//
        //if (foundCompound == null) {
        //    return false;
        //}
//
        //return listTag.remove(foundCompound);
        return false;
    }
}
