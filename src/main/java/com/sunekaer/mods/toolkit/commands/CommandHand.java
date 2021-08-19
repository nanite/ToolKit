package com.sunekaer.mods.toolkit.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.sunekaer.mods.toolkit.network.Handler;
import com.sunekaer.mods.toolkit.network.SetCopy;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.minecraft.command.Commands.literal;

public class CommandHand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return literal("hand")
                .requires(cs -> cs.hasPermissionLevel(0)) //permission
                .executes(ctx -> getHand(
                                ctx.getSource(),
                                ctx.getSource().asPlayer()
                        )
                );
    }

    private static int getHand(CommandSource source, PlayerEntity player) {
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.isEmpty()) {
            source.sendErrorMessage(new TranslationTextComponent("commands.toolkit.hand.handempty"));
            return 0;
        }

        String itemName = stack.getItem().getRegistryName().toString();
        List<ResourceLocation> tags = new ArrayList<>(stack.getItem().getTags());

        String withNBT = "";
        CompoundNBT nbt = stack.serializeNBT();
        if (nbt.contains("tag")) {
            withNBT += nbt.get("tag");
        }

        String combinedItemNBT = itemName + withNBT;


        source.sendFeedback(new TranslationTextComponent(TextFormatting.YELLOW + combinedItemNBT), true);
        Handler.MAIN.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player.getEntity()), new SetCopy(combinedItemNBT));
        if (!tags.isEmpty()) {
            source.sendFeedback(new TranslationTextComponent("Tags: " + TextFormatting.RED + tags.stream().map(ResourceLocation::toString).collect(Collectors.joining(", "))), true);
        }
        return 1;

    }
}
