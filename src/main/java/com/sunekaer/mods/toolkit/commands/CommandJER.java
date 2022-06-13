package com.sunekaer.mods.toolkit.commands;


import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.sunekaer.mods.toolkit.utils.jer.DimData;
import com.sunekaer.mods.toolkit.utils.jer.Scanner;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CommandJER {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("jer")
            .requires(cs -> cs.hasPermission(0))
            .then(Commands.literal("start")
                .executes(context -> jerStart(context.getSource(), false))
                    .then(Commands.argument("drops", BoolArgumentType.bool())
                        .executes(context -> jerStart(context.getSource(), BoolArgumentType.getBool(context, "drops")))
                    )
            )
            .then(Commands.literal("stop")
                .executes(context -> jerStop(context.getSource()))
            );
    }

    private static int jerStart(CommandSourceStack source, boolean drops) {
        if (Scanner.current != null) {
            source.sendSuccess(Component.literal("JER Scanner is already running!"), false);
            return 0;
        }

        Path config = FMLPaths.CONFIGDIR.get().resolve("jer-world-gen-config.json");

        if (!Files.exists(config)) {
            try {
                JsonObject json = new JsonObject();

                JsonArray blocks = new JsonArray();
                blocks.add("#forge:ores");
                json.add("block_whitelist", blocks);

                JsonObject dimensions = new JsonObject();

                for (ServerLevel world : source.getServer().getAllLevels()) {
                    dimensions.addProperty(world.dimension().location().toString(), true);
                }

                json.add("dimensions", dimensions);

                json.addProperty("height", 256);
                json.addProperty("scan_radius", 25);

                Files.write(config, Collections.singleton(new GsonBuilder().setPrettyPrinting().create().toJson(json)));
                source.sendSuccess(Component.literal("config/jer-world-gen-config.json created! After you've configured it, run this command again!"), false);
                return 0;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            try (Reader reader = Files.newBufferedReader(config)) {
                JsonObject json = new GsonBuilder().setLenient().create().fromJson(reader, JsonObject.class);

                int height = Mth.clamp(json.get("height").getAsInt(), 16, 320);
                int radius = Mth.clamp(json.get("scan_radius").getAsInt(), 1, 200);
                int startX = Mth.floor(source.getPosition().x) >> 4;
                int startZ = Mth.floor(source.getPosition().z) >> 4;

                Set<Block> blocks = new HashSet<>();

                for (JsonElement e : json.get("block_whitelist").getAsJsonArray()) {
                    String s = e.getAsString();

                    if (s.startsWith("#")) {
                        TagKey<Block> tag = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(s.substring(1)));

                        if (!tag.toString().isEmpty()) {
                            ForgeRegistries.BLOCKS.tags().getTag(tag).stream().forEach(blocks::add);
                        }
                    } else {
                        blocks.add(Registry.BLOCK.get(new ResourceLocation(s)));
                    }
                }

                blocks.remove(Blocks.AIR);

                Scanner.current = new Scanner(height, radius, startX, startZ, blocks, drops);

                JsonObject dimensions = json.get("dimensions").getAsJsonObject();

                for (ServerLevel world : source.getServer().getAllLevels()) {
                    String id = world.dimension().location().toString();

                    if (dimensions.has(id) && dimensions.get(id).getAsBoolean()) {
                        Scanner.current.dimensions.add(new DimData(Scanner.current, world));
                    }
                }

                Scanner.current.stop = false;

                if (source.getEntity() instanceof Player) {
                    Player p = (Player) source.getEntity();
                    Scanner.current.callback = text -> p.displayClientMessage(text, true);
                } else {
                    Scanner.current.callback = text -> source.sendSuccess(text, false);
                }

                Util.ioPool().execute(Scanner.current);
                source.sendSuccess(Component.literal("JER Scanner started!"), false);
                return 1;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return 0;
    }

    private static int jerStop(CommandSourceStack source) {
        if (Scanner.current != null) {
            Scanner.current.stop();
            Scanner.current = null;
            source.sendSuccess(Component.literal("JER Scanner stopped!"), false);
            return 1;
        } else {
            source.sendFailure(Component.literal("JER Scanner isn't running!"));
            return 0;
        }
    }

}
