package com.itlesports.nightmaremode.mixin.blocks;

import com.itlesports.nightmaremode.util.HammerAnvilHelper;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class HammerAnvilDropsMixin {
    @Inject(method = "canBlockBePushedByPiston", at = @At("HEAD"), cancellable = true)
    private void preventPistonWearReset(World world, int x, int y, int z, int facing, CallbackInfoReturnable<Boolean> cir) {
        if (HammerAnvilHelper.isAnvil(((Block)(Object)this).blockID)) cir.setReturnValue(false);
    }

    @Inject(method = "breakBlock", at = @At("HEAD"))
    private void dropAnvilBeforeRemovingTile(World world, int x, int y, int z, int id, int metadata, CallbackInfo ci) {
        if (HammerAnvilHelper.isAnvil(id) && world.getBlockId(x, y, z) != id) {
            HammerAnvilHelper.dropOnRemoval(world, x, y, z, id);
            if (!world.isRemote) world.removeBlockTileEntity(x, y, z);
        }
    }

    @Inject(method = "dropBlockAsItemWithChance", at = @At("HEAD"), cancellable = true)
    private void avoidDuplicateAnvilDrops(World world, int x, int y, int z, int metadata, float chance, int fortune, CallbackInfo ci) {
        // removal owns the drop, even when an explosion rolls a zero drop chance.
        if (HammerAnvilHelper.isAnvil(((Block)(Object)this).blockID)) ci.cancel();
    }

    @Inject(method = "getStackRetrievedByBlockDispenser", at = @At("HEAD"), cancellable = true)
    private void retrieveAnvil(World world, int x, int y, int z, CallbackInfoReturnable<ItemStack> cir) {
        int id = ((Block)(Object)this).blockID;
        if (!HammerAnvilHelper.isAnvil(id)) return;
        if (HammerAnvilHelper.isPristine(world, x, y, z)) {
            cir.setReturnValue(new ItemStack(id, 1, 0));
        } else {
            if (!world.isRemote) {
                world.playAuxSFX(2001, x, y, z, id);
                world.setBlockToAir(x, y, z);
            }
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "onRemovedByBlockDispenser", at = @At("HEAD"))
    private void suppressCollectedAnvilDrop(World world, int x, int y, int z, CallbackInfo ci) {
        if (HammerAnvilHelper.isAnvil(((Block)(Object)this).blockID)) {
            HammerAnvilHelper.collectPristineAnvil(world, x, y, z);
        }
    }

    @Inject(method = "canSilkHarvest(I)Z", at = @At("HEAD"), cancellable = true)
    private void preventSilkTouchWearReset(int metadata, CallbackInfoReturnable<Boolean> cir) {
        if (HammerAnvilHelper.isAnvil(((Block)(Object)this).blockID)) cir.setReturnValue(false);
    }
}
