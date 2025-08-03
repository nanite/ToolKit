package com.sunekaer.toolkit.commands.inventory;

import com.google.gson.Gson;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.sunekaer.toolkit.Toolkit;
import com.sunekaer.toolkit.network.SetCopy;
import com.sunekaer.toolkit.utils.CommandUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.SnbtPrinterTagVisitor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class CopyCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("copy")
                .then(
                        Commands.argument("type", StringArgumentType.word())
                                .suggests(InventoryCollector::suggestions)
                                .executes(ctx -> copy(ctx, null))
                                .then(Commands.argument("outputType", StringArgumentType.word())
                                        .suggests(CopyCommand::outputTypeSuggestions)
                                        .executes(ctx -> copy(ctx, StringArgumentType.getString(ctx, "outputType")))
                                )
                );

    }

    private static CompletableFuture<Suggestions> outputTypeSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder suggestionsBuilder) {
        var sortedTypes = Stream.of(OutputType.values()).sorted(Comparator.comparingInt(a -> a.order)).toList();
        for (OutputType value : sortedTypes) {
            suggestionsBuilder.suggest(value.name);
        }

        return suggestionsBuilder.buildFuture();
    }

    private static int copy(CommandContext<CommandSourceStack> context, @Nullable String outputType) throws CommandSyntaxException {
        var source = context.getSource();
        var type = InventoryCollector.fromString(StringArgumentType.getString(context, "type"));

        var computedOutputType = OutputType.PLAIN;
        if (outputType != null) {
            try {
                computedOutputType = OutputType.valueOf(outputType.toUpperCase());
            } catch (IllegalArgumentException e) {
                source.sendFailure(Component.literal("Invalid output type"));
                return 0;
            }
        }

        if (type == null) {
            source.sendFailure(Component.literal("Invalid type"));
            return 0;
        }

        var player = source.getPlayerOrException();
        var itemCollection = type.itemCollector.apply(player);
        var nonEmptyItems = itemCollection.stream().filter(stack -> !stack.isEmpty()).toList();

        var outputString = computedOutputType.function.apply(nonEmptyItems, source.registryAccess());

        source.sendSuccess(() -> Component.translatable("commands.toolkit.clipboard.copied"), true);
        Toolkit.PLATFORM.sendPacketToPlayer(player, new SetCopy(outputString));

        return 1;
    }

    enum OutputType {
        KUBEJS(4,"kubejs", (items, lookup) -> {
            StringBuilder builder = new StringBuilder();
            builder.append("[").append(CommandUtils.NEW_LINE);

            final String tab = "  ";

            for (ItemStack stack : items) {
                String itemName = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(stack.getItem())).toString();

                String withNBT = "";
                CompoundTag nbt = (CompoundTag) stack.save(lookup);
                if (nbt.contains("components")) {
                    withNBT += nbt.get("components");
                }

                builder.append(tab).append("{").append(CommandUtils.NEW_LINE);
                builder.append(tab).append(tab).append("item: ").append('"').append(itemName).append('"').append(",").append(CommandUtils.NEW_LINE);
                if (!withNBT.isEmpty()) {
                    builder.append(tab).append(tab).append("nbt: ").append('"').append(withNBT).append('"').append(",").append(CommandUtils.NEW_LINE);
                }

                if (stack != items.get(items.size() - 1)) {
                    builder.append(tab).append("},");
                } else {
                    builder.append(tab).append("}");
                }

                builder.append(CommandUtils.NEW_LINE);
            }

            builder.append("]");
            return builder.toString();
        }),
        KUBEJS_NATIVE(5,"kubejs_native", (items, lookup) -> {
            StringBuilder builder = new StringBuilder();
            builder.append("[").append(CommandUtils.NEW_LINE);

            final String tab = "  ";

            for (ItemStack stack : items) {
                String itemName = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(stack.getItem())).toString();

                String withNBT = "";
                CompoundTag nbt = (CompoundTag) stack.save(lookup);
                if (nbt.contains("components")) {
                    withNBT += nbt.get("components");
                }

                String itemString = String.format("%s%s", stack.getCount() > 1 ? stack.getCount() + "x " : "", itemName);
                if (withNBT.isEmpty()) {
                    builder.append(tab).append("\"").append(itemString).append("\"").append(",");
                }

                if (!withNBT.isEmpty()) {
                    builder.append(tab).append("Item.of(\"").append(itemName).append("\").withNbt(").append(withNBT).append("),");
                }

                builder.append(CommandUtils.NEW_LINE);
            }

            builder.append("]");
            return builder.toString();
        }),
        JSON(3, "json", (items, lookup) -> {
            var output = items.stream().map(stack -> {
                String itemName = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(stack.getItem())).toString();

                CompoundTag nbt = (CompoundTag) stack.save(lookup);
                if (nbt.contains("components")) {
                    return Map.of(
                            "item", itemName,
                            "nbt", Objects.requireNonNull(nbt.get("components"))
                    );
                }

                return Map.of("item", itemName);
            }).toList();

            return new Gson().newBuilder().setPrettyPrinting().create().toJson(output);
        }),
        SNBT(2, "snbt", (items, lookup) -> {
            CompoundTag tag = new CompoundTag();
            ListTag list = new ListTag();
            for (ItemStack stack : items) {
                list.add(stack.save(lookup));
            }

            tag.put("items", list);
            return (new SnbtPrinterTagVisitor()).visit(tag);
        }),
        NBT(1, "nbt", (items, lookup) -> {
            CompoundTag tag = new CompoundTag();
            ListTag list = new ListTag();
            for (ItemStack stack : items) {
                list.add(stack.save(lookup));
            }

            tag.put("items", list);
            return tag.toString();
        }),
        /**
         * ZenScript compatible list
         */
        CRAFTTWEAKER(5, "crafttweaker", (items, lookup) -> {
            StringBuilder builder = new StringBuilder();
            builder.append("[").append(CommandUtils.NEW_LINE);

            for (ItemStack stack : items) {
                String itemName = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(stack.getItem())).toString();

                String withNBT = "";
                CompoundTag nbt = (CompoundTag) stack.save(lookup);
                if (nbt.contains("components")) {
                    withNBT += nbt.get("components");
                }

                builder.append("    ").append("<item:").append(itemName).append(">");
                if (!withNBT.isEmpty()) {
                    builder.append(".withTag(").append(withNBT).append(")");
                }
                builder.append(",").append(CommandUtils.NEW_LINE);
            }

            builder.append("]").append(CommandUtils.NEW_LINE);
            return builder.toString();
        }),
        PLAIN(0,"plain", (items, lookup) -> {
            StringBuilder builder = new StringBuilder();

            for (ItemStack stack : items) {
                String itemName = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(stack.getItem())).toString();

                String withNBT = "";
                CompoundTag nbt = (CompoundTag) stack.save(lookup);
                if (nbt.contains("components")) {
                    withNBT += nbt.get("components");
                }

                builder.append(itemName).append(withNBT).append(CommandUtils.NEW_LINE);
            }

            return builder.toString();
        }),
        CSV(6, "csv", (items, lookup) -> {
            StringBuilder builder = new StringBuilder();

            // Header
            builder.append("item,nbt").append(CommandUtils.NEW_LINE);

            for (ItemStack stack : items) {
                String itemName = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(stack.getItem())).toString();

                String withNBT = "";
                CompoundTag nbt = (CompoundTag) stack.save(lookup);
                if (nbt.contains("components")) {
                    withNBT += nbt.get("components");
                }

                builder.append(itemName).append(",").append(withNBT).append(CommandUtils.NEW_LINE);
            }

            return builder.toString();
        });

        final int order;
        final String name;
        final BiFunction<List<ItemStack>, HolderLookup.Provider, String> function;

        OutputType(int order, String name, BiFunction<List<ItemStack>, HolderLookup.Provider, String> function) {
            this.order = order;
            this.name = name;
            this.function = function;
        }
    }
}
