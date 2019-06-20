package com.sunekaer.mods.toolkit.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public class CommandLookingAt {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return literal("lookingat")
                .requires(cs -> cs.hasPermissionLevel(0)) //permission
                .executes(ctx -> {
                    ctx.getSource().sendErrorMessage(new TranslationTextComponent("commands.toolkit.la.missing"));
                    return 1;
                })
                .then(argument("0=block/1=fluid", IntegerArgumentType.integer())
                        .executes(ctx -> getRay(
                                ctx.getSource(),
                                ctx.getSource().asPlayer(),
                                IntegerArgumentType.getInteger(ctx, "0=block/1=fluid")
                                )
                        )
                );
    }

    private static int getRay(CommandSource source, PlayerEntity player, int boolenstate) {
        RayTraceResult rayTrace;
        BlockState blockstate;

        if (!(player instanceof PlayerEntity)) {
            return 0;
        }

        if (boolenstate == 0) {
            rayTrace = player.func_213324_a(20.0D, 0.0F, false);
            if (rayTrace.getType() == RayTraceResult.Type.BLOCK) {
                BlockPos blockpos = ((BlockRayTraceResult) rayTrace).getPos();
                blockstate = source.getWorld().getBlockState(blockpos);
                source.sendFeedback(new TranslationTextComponent("Block \u00A72[" + blockstate.getBlock().getRegistryName() + "] \u00A7rat \u00A79[" + blockpos.getX() + ", " + blockpos.getY() + ", " + blockpos.getZ() + "]"), true);

                TileEntity te = source.getWorld().getTileEntity(blockpos);
                if (te != null) {
                    source.sendFeedback(new TranslationTextComponent(te.serializeNBT().toString()), true);
                }
                return 1;
            }
        } else if (boolenstate == 1) {
            rayTrace = player.func_213324_a(20.0D, 0.0F, true);
            if (rayTrace.getType() == RayTraceResult.Type.BLOCK) {
                BlockPos blockpos = ((BlockRayTraceResult) rayTrace).getPos();
                IFluidState ifluidstate = source.getWorld().getFluidState(blockpos);
                if (ifluidstate.isEmpty()) {
                    return 0;
                } else {
                    source.sendFeedback(new TranslationTextComponent("Fluid \u00A72[" + ifluidstate.getFluid().getRegistryName() + "] \u00A7rat \u00A79[" + blockpos.getX() + ", " + blockpos.getY() + ", " + blockpos.getZ() + "]"), true);
                    return 1;
                }
            }
        }else {
            source.sendErrorMessage(new TranslationTextComponent("commands.unknown.argument"));
            return 1;
        }
        return 0;
    }
}
