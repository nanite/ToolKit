package com.sunekaer.toolkit.commands.inventory;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sunekaer.toolkit.Toolkit;
import com.sunekaer.toolkit.network.SetCopy;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

import static com.sunekaer.toolkit.commands.inventory.CopyCommand.getNbtFromItemStack;

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
            List<TagKey<?>> tags = stack.getTags().collect(Collectors.toList());
            var value = new ItemInput(stack.getItemHolder(), stack.getComponentsPatch()).serialize(context.getSource().registryAccess());

            source.sendSuccess(() -> Component.literal(value).withStyle(Style.EMPTY
                    .withClickEvent(new ClickEvent.CopyToClipboard(value))
                    .withHoverEvent(new HoverEvent.ShowText(Component.literal("Copy tag")))
                    .withColor(ChatFormatting.YELLOW)), false);

            if (copyOnReply) {
                Toolkit.PLATFORM.sendPacketToPlayer(player, new SetCopy(value));
            }

            if (tags.isEmpty()) {
                continue;
            }

            for (TagKey<?> tag : tags) {
                var tagString = String.format("#%s", tag.location());
                source.sendSuccess(() -> Component.literal("- ").append(Component.literal(tagString).withStyle(Style.EMPTY.withColor(ChatFormatting.RED)
                        .withClickEvent(new ClickEvent.CopyToClipboard(tagString))
                        .withHoverEvent(new HoverEvent.ShowText(Component.literal("Copy tag")))
                )), false);
            }
        }

        return 1;
    }

    public static ArgumentBuilder<CommandSourceStack, ?> registerHandCommand() {
        return Commands.literal("hand").executes(context -> print(context, "hand", true));
    }
}
