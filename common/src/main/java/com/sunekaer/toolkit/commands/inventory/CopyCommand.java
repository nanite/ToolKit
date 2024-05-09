package com.sunekaer.toolkit.commands.inventory;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.sunekaer.toolkit.network.Handler;
import com.sunekaer.toolkit.network.SetCopy;
import com.sunekaer.toolkit.utils.CommandUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CopyCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("copy")
                .then(Commands.argument("type", StringArgumentType.word()).suggests(InventoryCollector::suggestions).executes(CopyCommand::copy));
    }

    private static int copy(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();
        var type = InventoryCollector.fromString(StringArgumentType.getString(context, "type"));

        if (type == null) {
            // TODO: Move to correct exception
            source.sendFailure(Component.literal("Invalid type"));
            return 0;
        }

        var player = source.getPlayerOrException();
        var itemCollection = type.itemCollector.apply(player);

        StringBuilder clipboard = new StringBuilder();
        for (ItemStack stack : itemCollection) {
            if (stack.isEmpty()) {
                continue;
            }

            String itemName = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(stack.getItem())).toString();

            String withNBT = "";
            CompoundTag nbt = (CompoundTag) stack.save(context.getSource().registryAccess());
            if (nbt.contains("tag")) {
                withNBT += nbt.get("tag");
            }

            clipboard.append(itemName).append(withNBT).append(CommandUtils.NEW_LINE);
        }

        source.sendSuccess(() -> Component.translatable("commands.toolkit.clipboard.copied"), true);
        Handler.CHANNEL.sendToPlayer(player, new SetCopy(clipboard.toString()));

        return 1;
    }
}
