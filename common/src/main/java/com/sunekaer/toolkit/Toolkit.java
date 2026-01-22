package com.sunekaer.toolkit;

import com.mojang.brigadier.CommandDispatcher;
import com.sunekaer.toolkit.commands.TKCommand;
import com.sunekaer.toolkit.commands.level.ClearCommand;
import com.sunekaer.toolkit.jobs.ServerTickJobRunner;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ServiceLoader;
import java.util.function.Supplier;

public class Toolkit {
    private static final Logger LOGGER = LoggerFactory.getLogger(Toolkit.class);
    public static final String MOD_ID = "toolkit";

    public static final XPlatShim PLATFORM = ServiceLoader.load(XPlatShim.class).findFirst().orElseThrow();

    public static final DefaultedValue<Boolean> SHOW_ON_JOIN_MESSAGE = new DefaultedValue<>(true);
    public static final DefaultedValue<String> JOIN_MESSAGE = new DefaultedValue<>("Hello from ToolKit, this message can be change or disabled in config.");

    public Toolkit() {
    }

    public void onSetup() {
        setup();
    }

    public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
        TKCommand.register(dispatcher, commandBuildContext, commandSelection);
    }

    public void onServerStopping() {
        ClearCommand.EXECUTOR.shutdownNow();
    }

    public void onServerPostTick(MinecraftServer server) {
        ServerTickJobRunner.get().onTick(server);
    }

    public void onPlayerJoin(ServerPlayer player) {
        if (player != null && Toolkit.SHOW_ON_JOIN_MESSAGE.get()) {
            if (!player.level().isClientSide()) {
                player.displayClientMessage(Component.literal(Toolkit.JOIN_MESSAGE.get()), false);
            }
        }
    }

    // Poor persons basic config system :cry:
    private static void setup() {
        Path configPath = PLATFORM.configDirectory().get();
        Path ourConfig = configPath.resolve("toolkit.txt");

        if (Files.notExists(ourConfig)) {
            String output = "# Config for Toolkit\n\n";
            output += "# Show join message on player join will display a custom message when the player joins a server or single player world\n";
            output += "show_on_join_message=" + SHOW_ON_JOIN_MESSAGE.get() + "\n\n";
            output += "# The custom message to show to players\n";
            output += "join_message=" + JOIN_MESSAGE.get() + "\n";

            try {
                Files.writeString(ourConfig, output);
            } catch (IOException e) {
                LOGGER.error("Failed to create {} for toolkit's configuration", ourConfig, e);
            }
        } else {
            try (var reader = Files.newBufferedReader(ourConfig)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("#")) {
                        continue;
                    }

                    if (!line.contains("=")) {
                        continue;
                    }

                    line = line.trim();
                    var remainingString = line.substring(line.indexOf("=") + 1);
                    if (line.startsWith("show_on_join_message")) {
                        SHOW_ON_JOIN_MESSAGE.setValue(remainingString.equals("true"));
                    }

                    if (line.startsWith("join_message")) {
                        JOIN_MESSAGE.setValue(remainingString);
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Failed to read {} for toolkit's configuration", ourConfig, e);
            }
        }
    }

    public static class DefaultedValue<T> implements Supplier<T> {
        private T value;
        private final T defaultValue;

        public DefaultedValue(T defaultValue) {
            this.defaultValue = defaultValue;
        }

        public void setValue(T value) {
            this.value = value;
        }

        @Override
        public T get() {
            if (value == null) {
                return this.defaultValue;
            }

            return this.value;
        }
    }
}
