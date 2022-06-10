package com.sunekaer.mods.toolkit.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.concurrent.atomic.AtomicInteger;

public class CommandKillAllItems {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("killAllItems")
                .requires(cs -> cs.hasPermission(2))
                .executes(ctx -> remove(
                                ctx.getSource()
                        )
                );
    }

    private static int remove(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        AtomicInteger i = new AtomicInteger();

        source.sendSuccess(Component.translatable("commands.toolkit.killall.items.start"), true);

        for (Entity entity : level.getEntities().getAll()) {
            if (entity instanceof ItemEntity) {
                i.addAndGet(1);
                entity.remove(Entity.RemovalReason.KILLED);
            }
        }


        if (i.get() > 0) {
            source.sendSuccess(Component.translatable("commands.toolkit.killall.items.done", i.get()), true);
        } else {
            source.sendSuccess(Component.translatable("commands.toolkit.killall.items.no"), true);
        }
        return 1;
    }
}
