package com.sunekaer.toolkit.commands.items;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.*;
import com.sunekaer.toolkit.utils.EnchantmentHacks;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.Nullable;

public class EnchantCommand {
    public static final SimpleCommandExceptionType ERROR_MISSING_PLAYER = new SimpleCommandExceptionType(Component.translatable("commands.toolkit.failed.missing_player"));
    private static final Dynamic2CommandExceptionType ERROR_MISSING_ENCHANTMENT = new Dynamic2CommandExceptionType((a, b) -> Component.translatable("commands.toolkit.enchant.failed.missing_enchant", a, b));
    private static final DynamicCommandExceptionType ERROR_INCOMPATIBLE = new DynamicCommandExceptionType(object -> Component.translatable("commands.enchant.failed.incompatible", object));
    private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType(object -> Component.translatable("commands.enchant.failed.itemless", object));

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext arg) {
        return Commands.literal("enchant")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.literal("add")
                        .then(Commands.argument("enchantment", ResourceArgument.resource(arg, Registries.ENCHANTMENT))
                                .then(Commands.argument("level", IntegerArgumentType.integer(0, 255))
                                        .executes(context -> enchant(
                                                context,
                                                ResourceArgument.getEnchantment(context, "enchantment"),
                                                IntegerArgumentType.getInteger(context, "level")
                                        ))
                                )
                        )
                )
                .then(Commands.literal("remove")
                        .then(Commands.argument("enchantment", ResourceArgument.resource(arg, Registries.ENCHANTMENT))
                                .executes(context -> removeEnchantment(context, ResourceArgument.getEnchantment(context, "enchantment"))))
                );
    }

    private static int enchant(CommandContext<CommandSourceStack> context, Holder.Reference<Enchantment> arg2, int level) throws CommandSyntaxException {
         CommandSourceStack source = context.getSource();
         Enchantment enchantment = arg2.value();

         var player = source.getPlayer();
         var mainHandItem = getItemInHand(player);
         if (mainHandItem == null) {
             throw ERROR_MISSING_PLAYER.create();
         }

         if (!enchantment.canEnchant(mainHandItem) || !EnchantmentHelper.isEnchantmentCompatible(EnchantmentHelper.getEnchantmentsForCrafting(mainHandItem).keySet(), enchantment)) {
             throw ERROR_INCOMPATIBLE.create(mainHandItem.getItem().getName(mainHandItem).getString());
         }

         EnchantmentHacks.enchantItem(mainHandItem, enchantment, (short) level);
         source.sendSuccess(() -> Component.translatable("commands.toolkit.enchant.success", mainHandItem.getItem().getName(mainHandItem).getString(), enchantment.getFullname(level).getString()), false);
        return 1;
    }

    private static int removeEnchantment(CommandContext<CommandSourceStack> context, Holder.Reference<Enchantment> arg2) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayer();
        var mainHandItem = getItemInHand(player);
        if (mainHandItem == null) {
            throw ERROR_MISSING_PLAYER.create();
        }

        Enchantment enchantment = arg2.value();

        var enchantments = EnchantmentHelper.getEnchantmentsForCrafting(mainHandItem);
        var hasEnchantment = enchantments.getLevel(enchantment) > 0;

        if (!hasEnchantment) {
            throw ERROR_MISSING_ENCHANTMENT.create(mainHandItem.getItem().getName(mainHandItem).getString(), enchantment.getFullname(1));
        }

        boolean success = EnchantmentHacks.removeEnchantment(mainHandItem, enchantment);
        if (success) {
            source.sendSuccess(() -> Component.translatable("commands.toolkit.remove_enchant.success", mainHandItem.getItem().getName(mainHandItem).getString(), enchantment.getFullname(1).getString()), false);
        } else {
            source.sendFailure(Component.translatable("commands.toolkit.remove_enchant.failed", mainHandItem.getItem().getName(mainHandItem).getString(), enchantment.getFullname(1).getString()));
        }
        return success ? 1 : 0;
    }

    @Nullable
    public static ItemStack getItemInHand(@Nullable ServerPlayer player) throws CommandSyntaxException {
        if (player == null) {
            return null;
        }

        ItemStack mainHandItem = player.getMainHandItem();
        if (mainHandItem.isEmpty()) {
            throw ERROR_NO_ITEM.create(player.getName().getString());
        }

        return mainHandItem;
    }
}

