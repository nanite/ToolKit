package com.sunekaer.toolkit;

import com.mojang.brigadier.arguments.ArgumentType;
import com.sunekaer.toolkit.commands.CommandClear;
import com.sunekaer.toolkit.commands.TKCommand;
import com.sunekaer.toolkit.event.PlayerEvents;
import com.sunekaer.toolkit.network.Handler;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

public class Toolkit {
    private static final Logger LOGGER = LoggerFactory.getLogger(Toolkit.class);
    public static final String MODID = "toolkit";

    public static final DefaultedValue<Boolean> SHOW_ON_JOIN_MESSAGE = new DefaultedValue<>(true);
    public static final DefaultedValue<String> JOIN_MESSAGE = new DefaultedValue<>("Hello from ToolKit, this message can be change or disabled in config.");

    public static void init() {
        PlayerEvent.PLAYER_JOIN.register(PlayerEvents::playerJoined);
        CommandRegistrationEvent.EVENT.register(TKCommand::register);

        Handler.init();
        LifecycleEvent.SERVER_STOPPING.register(Toolkit::onServerStopping);
        LifecycleEvent.SETUP.register(Toolkit::setup);

    }

    // Poor mans basic config system :cry:
    private static void setup() {
        Path configPath = ToolkitPlatform.getConfigDirectory();
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

    private static void onServerStopping(MinecraftServer minecraftServer) {
        CommandClear.EXECUTOR.shutdownNow();
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
