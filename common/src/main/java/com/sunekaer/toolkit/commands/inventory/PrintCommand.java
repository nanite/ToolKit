package com.sunekaer.toolkit.commands.inventory;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sunekaer.toolkit.network.Handler;
import com.sunekaer.toolkit.network.SetCopy;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PrintCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("print")
                .then(Commands.argument("type", StringArgumentType.word()).suggests(InventoryCollector::suggestions).executes(context -> print(context, StringArgumentType.getString(context, "type"), false)));
    }

    private static int print(CommandContext<CommandSourceStack> context, String typeInput, boolean copyOnReply) throws CommandSyntaxException {
        var source = context.getSource();
        var type = InventoryCollector.fromString(typeInput);

        if (type == null) {
            // TODO: Move to correct exception
            source.sendFailure(Component.literal("Invalid type"));
            return 0;
        }

        var player = source.getPlayerOrException();
        var itemCollection = type.itemCollector.apply(player);

        for (ItemStack stack : itemCollection) {
            String itemName = Objects.requireNonNull(stack.getItem().arch$registryName()).toString();
            List<TagKey<?>> tags = stack.getTags().collect(Collectors.toList());

            String withNBT = "";
            var saveData = stack.save(context.getSource().registryAccess());
            if (saveData instanceof CompoundTag && ((CompoundTag) saveData).contains("components")) {
                Tag components = ((CompoundTag) saveData).get("components");
                assert components != null;
                withNBT = components.toString();
            }

            String combinedItemNBT = itemName + withNBT;

            source.sendSuccess(() -> Component.literal(combinedItemNBT).withStyle(Style.EMPTY
                    .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, combinedItemNBT))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Copy tag")))
                    .withColor(ChatFormatting.YELLOW)), false);

            if (copyOnReply) {
                Handler.CHANNEL.sendToPlayer(player, new SetCopy(combinedItemNBT));
            }

            if (tags.isEmpty()) {
                continue;
            }

            for (TagKey<?> tag : tags) {
                var tagString = String.format("#%s", tag.location());
                source.sendSuccess(() -> Component.literal("- ").append(Component.literal(tagString).withStyle(Style.EMPTY.withColor(ChatFormatting.RED)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, tagString))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Copy tag")))
                )), false);
            }
        }

        return 1;
    }

    public static ArgumentBuilder<CommandSourceStack, ?> registerHandCommand() {
        return Commands.literal("hand").executes(context -> print(context, "hand", true));
    }
}
