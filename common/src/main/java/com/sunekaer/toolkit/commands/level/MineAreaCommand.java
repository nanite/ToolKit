// TODO: Finish this command implementation
//package com.sunekaer.toolkit.commands.level;
//
//import com.google.common.collect.HashMultiset;
//import com.google.common.collect.Multiset;
//import com.mojang.brigadier.arguments.IntegerArgumentType;
//import com.mojang.brigadier.builder.ArgumentBuilder;
//import com.mojang.brigadier.exceptions.CommandSyntaxException;
//import com.sunekaer.toolkit.utils.ChunkRangeIterator;
//import net.minecraft.commands.CommandSourceStack;
//import net.minecraft.commands.Commands;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Registry;
//import net.minecraft.core.registries.Registries;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.item.enchantment.Enchantment;
//import net.minecraft.world.item.enchantment.Enchantments;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraft.world.level.block.entity.BarrelBlockEntity;
//import net.minecraft.world.level.block.state.BlockState;
//
//import java.util.*;
//
///**
// * Relatively simple but useful command to simulate or actually mine an area based on an item tag
// * <p>
// * Optionally you can use a tool to mine the area which will use the items enchantments
// */
//public class MineAreaCommand {
//    public static ArgumentBuilder<CommandSourceStack, ?> register() {
//        return (Commands.literal("minearea")
//                .requires(cs -> cs.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
//                .executes(context -> mine(context.getSource(), 1, ""))
//                .then(Commands.argument("range", IntegerArgumentType.integer()).executes(ctx -> mine(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "range"), ""))));
////                        .then(Commands.argument("filter", StringArgumentType.string()).suggests((commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(ClearCommand.RemovalPredicate.NAMES, suggestionsBuilder)).executes(ctx -> remove(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "range"), StringArgumentType.getString(ctx, "filter"))))));
//    }
//
//    private static int mine(CommandSourceStack source, int size, String filter) throws CommandSyntaxException {
//        try {
//            ItemStack breaker = new ItemStack(Items.NETHERITE_PICKAXE);
//
//            Registry<Enchantment> enchantmentRegistry = source.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
////            breaker.enchant(Enchantments.BLOCK_FORTUNE, 3);
//            breaker.enchant(enchantmentRegistry.get(Enchantments.SILK_TOUCH).orElseThrow(), 1);
//
//            MinecraftServer server = source.getServer();
//            ServerLevel level = source.getLevel();
//            ServerPlayer player = source.getPlayerOrException();
//
//            int range = Math.max(0, size / 2);
//            ChunkRangeIterator iterator = new ChunkRangeIterator(level, player.chunkPosition(), range);
//
//            // Use a multiset to back the drops list as we don't care for the order nor do we care for duplicates
//            Multiset<Item> drops = HashMultiset.create();
//
//            while (iterator.hasNext()) {
//                var blockPos = iterator.next();
//
//                BlockState state = level.getBlockState(blockPos);
//                if (state.isAir() || state.is(Blocks.BEDROCK)) {
//                    continue;
//                }
//
//                if (!state.is(ToolkitPlatform.getOresTag())) {
//                    continue;
//                }
//
//                // Now for the fun bit, let's simulate the block being mined
//                // Drops
//                List<ItemStack> blockDrops = Block.getDrops(state, level, blockPos, level.getBlockEntity(blockPos), player, breaker);
//                for (ItemStack drop : blockDrops) {
//                    drops.add(drop.getItem(), drop.getCount());
//                }
//            }
//
////            boolean smeltDrops = true;
//
////            HashMap<Item, ItemStack> cookingLookupCache = new HashMap<>();
////            Multiset<Item> smeltingMultiSet = HashMultiset.create();
////            if (smeltDrops) {
////                for (Item item : drops.elementSet()) {
////                    int itemCount = drops.count(item);
////                    // Split into stacks of 64
////                    int itemsToProcess = (int) Math.round(Math.ceil(itemCount / 64.0));
////
////                    for (int i = 0; i < itemsToProcess; i++) {
////                        // Get the item stack
////                        ItemStack stack = new ItemStack(item, 64);
////                        if (i == itemsToProcess - 1) {
////                            stack = new ItemStack(item, itemCount % 64);
////                        }
////
////                        // Get the recipe
////                        final ItemStack finalStack = stack;
////                        ItemStack smeltingResult = cookingLookupCache.computeIfAbsent(item, (key) -> {
////                            Optional<SmeltingRecipe> recipeFor = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(finalStack), level);
////                            return recipeFor.map(r -> r.getResultItem().copy()).orElse(ItemStack.EMPTY);
////                        });
////
////                        if (smeltingResult.isEmpty()) {
////                            // If it can't be smelted then just add it to the multiset
////                            smeltingMultiSet.add(item, stack.getCount());
////                            continue;
////                        }
////
////                        // Add the result to the multiset
////                        smeltingMultiSet.add(smeltingResult.getItem(), smeltingResult.getCount());
////                    }
////                }
////            }
//
////            drops = smeltDrops ? smeltingMultiSet : drops;
//            System.out.println(drops);
//
//            int chests = drops.size() / 27;
//            BlockPos playerPos = player.blockPosition();
//
//            boolean finished = false;
//            int rows = 0;
//            int cols = 0;
//            for (int i = 0; i < chests; i++) {
//                if (finished) {
//                    break;
//                }
//
//                if (rows > 4) {
//                    rows = 0;
//                    cols++;
//                }
//
//                // Create a chest
//                BlockPos chestPos = playerPos.offset(cols, 0, rows);
//                level.setBlockAndUpdate(chestPos, Blocks.BARREL.defaultBlockState());
//
//                // Get the chest entity
//                BarrelBlockEntity chest = (BarrelBlockEntity) level.getBlockEntity(chestPos);
//                if (chest == null) {
//                    continue;
//                }
//
//                // Now fill the chest with as many items as we can
//                Iterator<Item> iterator1 = drops.iterator();
//                for (int j = 0; j < 27; j++) {
//                    if (!iterator1.hasNext()) {
//                        finished = true;
//                        break;
//                    }
//
//                    // Get the next item
//                    Item item = iterator1.next();
//
//                    int count = drops.count(item);
//                    int taken = Math.min(count, 64);
//
//                    // Add the item to the chest
//                    chest.setItem(j, new ItemStack(item, taken));
//
//                    // Remove the item from the multiset
//                    drops.remove(item, taken);
//                }
//
//                rows++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return 0;
//    }
//}
