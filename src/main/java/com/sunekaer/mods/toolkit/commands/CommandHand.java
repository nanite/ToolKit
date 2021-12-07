package com.sunekaer.mods.toolkit.commands;


import com.mojang.brigadier.builder.ArgumentBuilder;
import com.sunekaer.mods.toolkit.network.Handler;
import com.sunekaer.mods.toolkit.network.SetCopy;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class CommandHand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("hand")
                .requires(cs -> cs.hasPermission(0)) //permission
                .executes(ctx -> getHand(
                                ctx.getSource(),
                                ctx.getSource().getPlayerOrException()
                        )
                );
    }

    private static int getHand(CommandSourceStack source, Player player) {
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.isEmpty()) {
            source.sendFailure(new TranslatableComponent("commands.toolkit.hand.handempty"));
            return 0;
        }

        String itemName = Objects.requireNonNull(stack.getItem().getRegistryName()).toString();
        List<ResourceLocation> tags = new ArrayList<>(stack.getItem().getTags());

        String withNBT = "";
        CompoundTag nbt = stack.serializeNBT();
        if (nbt.contains("tag")) {
            withNBT += nbt.get("tag");
        }

        String combinedItemNBT = itemName + withNBT;


        source.sendSuccess(new TextComponent(combinedItemNBT).withStyle(ChatFormatting.YELLOW), true);
        Handler.MAIN.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new SetCopy(combinedItemNBT));
        if (!tags.isEmpty()) {
            TextComponent tagText = new TextComponent("Tags: ");
            TextComponent tagsText = new TextComponent(tags.stream().map(ResourceLocation::toString).collect(Collectors.joining(", ")));
            tagsText.withStyle(ChatFormatting.RED);
            source.sendSuccess(tagText.append(tagsText), true);
        }
        return 1;

    }
}
