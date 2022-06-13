package com.sunekaer.mods.toolkit.utils.jer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.lang3.mutable.MutableLong;

import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class Scanner implements Runnable {
    private static final Function<Block, MutableLong> BLOCK_TO_COUNT_FUNCTION = k -> new MutableLong(0L);

    public static Scanner current;

    public final List<DimData> dimensions;
    public boolean stop;
    public final int height;
    public final int radius;
    public final int startX, startZ;
    public final Set<Block> whitelist;
    public final boolean drops;
    public long blocksScanned;
    public Consumer<Component> callback;

    public int addToHeight;

    public Scanner(int h, int r, int sx, int sz, Set<Block> w, boolean d) {
        dimensions = new ArrayList<>();
        height = h;
        radius = r;
        startX = sx;
        startZ = sz;
        whitelist = w;
        drops = d;
        blocksScanned = 0L;
        stop = true;
    }

    @Override
    public void run() {
        long diameter = 1L + radius * 2L;
        long area = diameter * diameter;
        long blocks = area * 256L;
        long progress = 0L;
        long percent = 0L;

        for (DimData data : dimensions) {
            if (stop) {
                return;
            }
            addToHeight = Math.abs(data.dimension.getMinBuildHeight());


            data.distribution = new HashMap[height + addToHeight];


            for (int i = 0; i < height + addToHeight; i++) {
                data.distribution[i] = new HashMap<>();
            }

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (stop) {
                        return;
                    }

                    int cx = startX + x;
                    int cz = startZ + z;
                    LevelChunk chunk = data.dimension.getChunk(cx, cz);

                    for (int bx = 0; bx < 16; bx++) {
                        for (int bz = 0; bz < 16; bz++) {
                            int h = Math.min(height, chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.MOTION_BLOCKING).getFirstAvailable(bx, bz));

                            for (int by = data.dimension.getMinBuildHeight(); by < h; by++) {
                                BlockState state = chunk.getBlockState(new BlockPos(cx * 16 + bx, by, cz * 16 + bz));
//                                System.out.println(new BlockPos(cx * 16 + bx, by, cz * 16 + bz));
                                int y = by + addToHeight;
                                if (!(state.getBlock() instanceof AirBlock) && whitelist.contains(state.getBlock())) {
                                    data.distribution[y].computeIfAbsent(state.getBlock(), BLOCK_TO_COUNT_FUNCTION).increment();
                                }

                                blocksScanned++;
                            }

                            progress++;
                        }
                    }

                    long p = percent;
                    percent = progress * 1000L / (blocks * dimensions.size());

                    if (p != percent) {
                        callback.accept(Component.literal("JER Scanner is running [" + (percent / 10D) + "%]"));
                    }
                }
            }
        }

        if (!stop) {
            JsonArray array = new JsonArray();

            for (DimData data : dimensions) {
                Set<Block> dimBlocks = new HashSet<>();
                addToHeight = Math.abs(data.dimension.getMinBuildHeight());

                for (int y = 0; y < height + addToHeight ; y++) {
                    dimBlocks.addAll(data.distribution[y].keySet());
                }

                LootContext.Builder lootContext = new LootContext.Builder(data.dimension).withRandom(data.dimension.random).withLuck(1F);

                for (Block block : dimBlocks) {
                    StringBuilder sb = new StringBuilder();

                    for (int y = data.dimension.getMinBuildHeight(); y < height; y++) {
                        if (y > data.dimension.getMinBuildHeight()) {
                            sb.append(';');
                        }

                        sb.append(y);
                        sb.append(',');

                        int count = y + addToHeight;
                        System.out.println(y + " " + count);
                        MutableLong m = data.distribution[y + addToHeight].get(block);

                        String b = m == null ? "0.0" : Double.toString(m.getValue().doubleValue() / (double) blocks);

                        sb.append(b.endsWith(".0") ? b.substring(0, b.length() - 2) : b);
                    }

                    JsonObject json = new JsonObject();
                    json.addProperty("block", Registry.BLOCK.getKey(block).toString());
                    // json.addProperty("silktouch", false);
                    json.addProperty("dim", data.dimension.dimension().location().toString());
                    json.addProperty("distrib", sb.toString());
                    array.add(json);
                }
            }

            try {
                Files.write(FMLPaths.CONFIGDIR.get().resolve("world-gen.json"), Collections.singleton(array.toString()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        current = null;
    }

    public void stop() {
        stop = true;
    }
}