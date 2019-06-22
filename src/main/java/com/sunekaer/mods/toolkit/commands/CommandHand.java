package com.sunekaer.mods.toolkit.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import static net.minecraft.realms.Realms.setClipboard;

public class CommandHand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return literal("hand")
                .requires(cs -> cs.hasPermissionLevel(0)) //permission
                .executes(ctx -> getHand(
                        ctx.getSource(),
                        ctx.getSource().asPlayer(),
                        ""
                        )
                )
                .then(argument("nocopy", StringArgumentType.string())
                        .executes(ctx -> getHand(
                                ctx.getSource(),
                                ctx.getSource().asPlayer(),
                                StringArgumentType.getString(ctx, "nocopy")
                                )
                        )
                );
    }

    private static int getHand(CommandSource source, PlayerEntity player, String clip) {
        ItemStack stack = player.getHeldItemMainhand();

        if (stack.isEmpty()) {
            source.sendErrorMessage(new TranslationTextComponent("commands.toolkit.hand.handempty"));
            return 0;
        }

        String itemName = stack.getItem().getRegistryName().toString();

        String withNBT = "";
        CompoundNBT nbt = stack.serializeNBT();
        if (nbt.contains("tag")) {
            withNBT += nbt.get("tag");
        }

        String combinedItemNBT = itemName + withNBT;

        if (clip.isEmpty()) {
            source.sendFeedback(new TranslationTextComponent(TextFormatting.YELLOW + combinedItemNBT), true);
            setClipboard(combinedItemNBT);
            return 1;
        }
        if (clip.contentEquals("nocopy")) {
            source.sendFeedback(new TranslationTextComponent(TextFormatting.YELLOW + combinedItemNBT), true);
            return 1;
        }else{
            source.sendErrorMessage(new TranslationTextComponent("commands.unknown.argument"));
            return 0;
        }
    }
}
