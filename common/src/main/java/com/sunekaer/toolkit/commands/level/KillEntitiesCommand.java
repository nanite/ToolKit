package com.sunekaer.toolkit.commands.level;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.StringRepresentableArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class KillEntitiesCommand {
    enum KillType implements StringRepresentable {
        all((p, entity) -> !(entity instanceof AbstractMinecart) && !entity.getUUID().equals(p.getUUID())),
        animals((p, entity) -> entity instanceof Animal),
        monsters((p, entity) -> entity instanceof Monster),
        items((p, entity) -> entity instanceof ItemEntity),
        xp((p, entity) -> entity instanceof ExperienceOrb),
        players((p, entity) -> entity instanceof Player),
        me((p, entity) -> entity instanceof Player && entity.getUUID().equals(p.getUUID()));

        final BiPredicate<Player, Entity> checker;
        KillType(BiPredicate<Player, Entity> checker) {
            this.checker = checker;
        }

        public static final Codec<KillType> CODEC = StringRepresentable.fromEnum(KillType::values);

        @Override
        @NotNull
        public String getSerializedName() {
            return this.name();
        }
    }

    public static class KillTypeArgument extends StringRepresentableArgument<KillType> {
        protected KillTypeArgument() {
            super(KillType.CODEC, KillType::values);
        }

        public static KillTypeArgument killType() {
            return new KillTypeArgument();
        }

        public static KillType getKillType(CommandContext<CommandSourceStack> source, String name) {
            return source.getArgument(name, KillType.class);
        }
    }

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext arg) {
        return Commands.literal("kill")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("type", KillTypeArgument.killType())
                        .executes(context -> kill(KillTypeArgument.getKillType(context, "type"), context.getSource())))
                .then(Commands.literal("by").then(
                        Commands.argument("entity", ResourceArgument.resource(arg, Registries.ENTITY_TYPE)).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes(context -> killByEntity(context, ResourceArgument.getSummonableEntityType(context, "entity")))));
    }

    private static int killByEntity(CommandContext<CommandSourceStack> context, Holder.Reference<EntityType<?>> reference) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        var level = source.getLevel();

        EntityType<?> entityType = reference.value();

        source.sendSuccess(() -> Component.translatable("commands.toolkit.kill.start", entityType), true);
        var entitiesKilled = yeetEntities((player, entity) -> entity.getType().equals(entityType), level, source.getPlayerOrException());
        yeetedEntitiesMessage(source, entitiesKilled, entityType.toShortString());

        return 0;
    }

    private static int kill(KillType type, CommandSourceStack source) throws CommandSyntaxException {
        ServerLevel level = source.getLevel();
        int entitiesKilled = 0;

        String typeName = Component.translatable("commands.toolkit.kill.type." + type.name()).getString();
        source.sendSuccess(() -> Component.translatable("commands.toolkit.kill.start", typeName), true);

        if (type == KillType.me || type == KillType.players) {
            for (Player player : level.getPlayers(e -> type.checker.test(e, e))) {
                player.kill();
                entitiesKilled++;
            }
        } else {
            entitiesKilled += yeetEntities(type.checker, level, source.getPlayerOrException());
        }

        yeetedEntitiesMessage(source, entitiesKilled, typeName);

        return 1;
    }

    private static void yeetedEntitiesMessage(CommandSourceStack source, int yeetedAmount, String typeName) {
        if (yeetedAmount > 0) {
            source.sendSuccess(() -> Component.translatable("commands.toolkit.kill.done", yeetedAmount), true);
        } else {
            source.sendSuccess(() -> Component.translatable("commands.toolkit.kill.no", typeName), true);
        }
    }

    private static int yeetEntities(BiPredicate<Player, Entity> tester, ServerLevel level, Player player) {
        int entitiesKilled = 0;
        Iterable<Entity> entities = level.getAllEntities();

        // Copy the entities to a list to avoid concurrent modification
        List<Entity> entityList = new ArrayList<>();
        entities.forEach(entityList::add);

        for (Entity entity : entityList) {
            if (entity == null) {
                continue;
            }

            if (tester.test(player, entity)) {
                entity.remove(Entity.RemovalReason.KILLED);
                entitiesKilled ++;
            }
        }

        return entitiesKilled;
    }
}

