package com.sunekaer.mods.toolkit.commands;


import com.mojang.brigadier.builder.ArgumentBuilder;
import com.sunekaer.mods.toolkit.network.Handler;
import com.sunekaer.mods.toolkit.network.SetCopy;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
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
            source.sendFailure(Component.translatable("commands.toolkit.hand.handempty"));
            return 0;
        }

        String itemName = Objects.requireNonNull(Registry.ITEM.getKey(stack.getItem())).toString();
        List<TagKey> tags = new ArrayList<>(stack.getTags().collect(Collectors.toList()));

        String withNBT = "";
        CompoundTag nbt = stack.serializeNBT();
        if (nbt.contains("tag")) {
            withNBT += nbt.get("tag");
        }

        String combinedItemNBT = itemName + withNBT;


        source.sendSuccess(Component.literal(combinedItemNBT).withStyle(ChatFormatting.YELLOW), true);
        Handler.MAIN.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new SetCopy(combinedItemNBT));
        if (!tags.isEmpty()) {
            MutableComponent tagText = Component.literal("Tags: ");
            MutableComponent tagsText = Component.literal(tags.stream().map(TagKey::toString).collect(Collectors.joining(", ")));
            tagsText.withStyle(ChatFormatting.RED);
            source.sendSuccess(tagText.append(tagsText), true);
        }
        return 1;

    }
}
