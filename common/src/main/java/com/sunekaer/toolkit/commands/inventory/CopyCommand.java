package com.sunekaer.toolkit.commands.inventory;

import com.google.gson.Gson;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.sunekaer.toolkit.network.SetCopy;
import com.sunekaer.toolkit.utils.CommandUtils;
import dev.nanite.library.platform.Platform;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CopyCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("copy")
                .then(
                        Commands.argument("type", StringArgumentType.word())
                                .suggests(InventoryCollector::suggestions)
                                .executes(ctx -> copy(ctx, null, true))
                                .then(Commands.argument("outputType", StringArgumentType.word())
                                        .suggests(CopyCommand::outputTypeSuggestions)
                                        .then(Commands.argument("includeItemNBT", BoolArgumentType.bool())
                                                .executes(ctx -> copy(ctx, StringArgumentType.getString(ctx, "outputType"), BoolArgumentType.getBool(ctx, "includeItemNBT")))
                                        )
                                        .executes(ctx -> copy(ctx, StringArgumentType.getString(ctx, "outputType"), true))
                                )
                );

    }

    private static CompletableFuture<Suggestions> outputTypeSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder suggestionsBuilder) {
        for (OutputType value : OutputType.VALUES) {
            suggestionsBuilder.suggest(value.name().toLowerCase());
        }

        return suggestionsBuilder.buildFuture();
    }

    private static int copy(CommandContext<CommandSourceStack> context, @Nullable String outputType, boolean includeDataComponent) throws CommandSyntaxException {
        var source = context.getSource();
        var type = InventoryCollector.fromString(StringArgumentType.getString(context, "type"));

        var computedOutputType = OutputType.PLAIN;
        if (outputType != null) {
            computedOutputType = OutputType.fromString(outputType);
            if (computedOutputType == null) {
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

        // Get distinct items without a count.
        var seen = new ArrayList<ItemStack>();
        var nonEmptyItems = itemCollection.stream()
                .filter(stack -> !stack.isEmpty())
                .map(e -> e.copyWithCount(1))
                .filter(stack -> {
                    if (seen.stream().anyMatch(s -> ItemStack.isSameItemSameComponents(s, stack))) {
                        return false;
                    }
                    seen.add(stack);
                    return true;
                })
                .toList();

        var outputString = computedOutputType.outputter.apply(nonEmptyItems, source.registryAccess(), includeDataComponent);

        source.sendSuccess(() -> Component.translatable("commands.toolkit.clipboard.copied"), true);
        Platform.INSTANCE.sendPacketToPlayer(player, new SetCopy(outputString));

        return 1;
    }

    enum OutputType {
        LIST(((items, lookup, includeDataComponents) -> {
            StringBuilder builder = new StringBuilder("[").append(CommandUtils.NEW_LINE);
            for (var item : items) {
                builder.append("  \"").append(item.typeHolder().getRegisteredName());
                if (includeDataComponents) {
                    var withNBT = getNbtFromItemStack(item, lookup, true);
                    builder.append(withNBT.replace("\"", "\\\""));
                }
                builder.append("\"").append(CommandUtils.NEW_LINE);
            }
            return builder.append("]").toString();
        })),
        KUBEJS((items, lookup, includeDataComponent) -> {
            StringBuilder builder = new StringBuilder();
            builder.append("[").append(CommandUtils.NEW_LINE);

            final String tab = "  ";

            for (ItemStack stack : items) {
                String itemName = stack.typeHolder().getRegisteredName();

                builder.append(tab).append("{").append(CommandUtils.NEW_LINE);
                builder.append(tab).append(tab).append("item: ").append('"').append(itemName).append('"').append(",").append(CommandUtils.NEW_LINE);

                if (includeDataComponent) {
                    var withNBT = getNbtFromItemStack(stack, lookup, true);
                    if (!withNBT.isEmpty()) {
                        builder.append(tab).append(tab).append("nbt: ").append('"').append(withNBT).append('"').append(",").append(CommandUtils.NEW_LINE);
                    }
                }

                if (stack != items.getLast()) {
                    builder.append(tab).append("},");
                } else {
                    builder.append(tab).append("}");
                }

                builder.append(CommandUtils.NEW_LINE);
            }

            builder.append("]");
            return builder.toString();
        }),
        KUBEJS_NATIVE((items, lookup, includeDataComponent) -> {
            StringBuilder builder = new StringBuilder();
            builder.append("[").append(CommandUtils.NEW_LINE);

            final String tab = "  ";

            for (ItemStack stack : items) {
                String itemName = stack.typeHolder().getRegisteredName();

                String itemString = String.format("%s%s", stack.getCount() > 1 ? stack.getCount() + "x " : "", itemName);

                var withNBT = getNbtFromItemStack(stack, lookup, true);
                if (withNBT.isEmpty()) {
                    builder.append(tab).append("\"").append(itemString).append("\"").append(",");
                }

                if (!withNBT.isEmpty() && includeDataComponent) {
                    builder.append(tab).append("Item.of(\"").append(itemName).append(withNBT.replace("\"", "\\\"")).append(",");
                }

                builder.append(CommandUtils.NEW_LINE);
            }

            builder.append("]");
            return builder.toString();
        }),
        JSON((items, lookup, includeDataComponent) -> {
            var output = items.stream().map(stack -> {
                String itemName = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(stack.getItem())).toString();

                var nbtData = getNbtFromItemStack(stack, lookup, true);
                if (!nbtData.isEmpty() && includeDataComponent) {
                    return Map.of(
                            "item", itemName,
                            "nbt", nbtData
                    );
                }

                return Map.of("item", itemName);
            }).toList();

            return new Gson().newBuilder().setPrettyPrinting().create().toJson(output);
        }),
        SNBT((items, lookup, includeDataComponent) -> {
            CompoundTag tag = new CompoundTag();
            ListTag list = new ListTag();
            for (ItemStack stack : items) {
                var itemNbt = ItemStack.CODEC.encodeStart(includeDataComponent ? lookup.createSerializationContext(NbtOps.INSTANCE) : NbtOps.INSTANCE, stack)
                        .mapOrElse(Function.identity(), (error) -> new CompoundTag());

                list.add(itemNbt);
            }

            tag.put("items", list);
            return (new SnbtPrinterTagVisitor()).visit(tag);
        }),
        NBT((items, lookup, includeDataComponent) -> {
            CompoundTag tag = new CompoundTag();
            ListTag list = new ListTag();
            for (ItemStack stack : items) {
                var itemNbt = ItemStack.CODEC.encodeStart(includeDataComponent ? lookup.createSerializationContext(NbtOps.INSTANCE) : NbtOps.INSTANCE, stack)
                        .mapOrElse(Function.identity(), (error) -> new CompoundTag());

                list.add(itemNbt);
            }

            tag.put("items", list);
            return tag.toString();
        }),
        PLAIN((items, lookup, includeDataComponent) -> {
            StringBuilder builder = new StringBuilder();

            for (ItemStack stack : items) {
                String itemName = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(stack.getItem())).toString();

                builder.append(itemName);
                if (includeDataComponent) {
                    String withNBT = getNbtFromItemStack(stack, lookup, true);
                    builder.append(withNBT);
                }
                builder.append(CommandUtils.NEW_LINE);
            }

            return builder.toString();
        }),
        CSV((items, lookup, includeDataComponent) -> {
            StringBuilder builder = new StringBuilder();

            // Header
            builder.append("item");
            if (includeDataComponent) {
                builder.append(",data_components");
            }
            builder.append(CommandUtils.NEW_LINE);

            for (ItemStack stack : items) {
                String itemName = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(stack.getItem())).toString();


                StringBuilder append = builder.append(itemName);
                if (includeDataComponent) {
                    String dataComponents = getNbtFromItemStack(stack, lookup, true);
                    append.append(",").append(dataComponents);
                }

                builder.append(CommandUtils.NEW_LINE);
            }

            return builder.toString();
        });

        private static final List<OutputType> VALUES = List.of(values());
        final OutputFunction outputter;

        OutputType(OutputFunction outputter) {
            this.outputter = outputter;
        }

        @Nullable
        public static OutputType fromString(String s) {
            for (OutputType type : VALUES) {
                if (type.toString().equalsIgnoreCase(s)) {
                    return type;
                }
            }

            return null;
        }
    }

    @FunctionalInterface
    interface OutputFunction {
        String apply(List<ItemStack> items, HolderLookup.Provider lookup, boolean includeDataComponents);
    }

    public static String getNbtFromItemStack(ItemStack stack, HolderLookup.Provider lookup, boolean removeName) {
        Holder<Item> itemHolder = stack.typeHolder();
        String itemName = itemHolder.getRegisteredName();

        var ops = lookup.createSerializationContext(NbtOps.INSTANCE);
        DataComponentPatch patch = stack.getComponentsPatch();
        String componentStr = patch.entrySet().stream().flatMap(entry -> {
            DataComponentType<?> type = entry.getKey();
            Identifier id = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(type);
            if (id == null) return Stream.empty();
            Optional<?> value = entry.getValue();
            if (value.isPresent()) {
                TypedDataComponent<?> typed = TypedDataComponent.createUnchecked(type, value.get());
                return typed.encodeValue(ops).result().stream()
                        .map(tag -> id + "=" + tag);
            } else {
                return Stream.of("!" + id);
            }
        }).collect(Collectors.joining(","));

        var value = componentStr.isEmpty() ? itemName : itemName + "[" + componentStr + "]";
        if (removeName) {
            if (value.startsWith(itemName)) {
                value = value.substring(itemName.length());
            }
        }

        return value;
    }
}
