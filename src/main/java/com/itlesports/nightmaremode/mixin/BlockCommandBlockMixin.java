package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.BlockCommandBlock;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(BlockCommandBlock.class)
public abstract class BlockCommandBlockMixin {
    @Inject(method = "updateTick", at = @At("HEAD"), cancellable = true)
    private void nightmareMode$disableCommandBlock(World world, int x, int y, int z, Random random, CallbackInfo ci) {
        if (NightmareMode.lockDownCreative) ci.cancel();
    }

    @Inject(method = "onBlockPlacedBy", at = @At("HEAD"), cancellable = true)
    private void nightmareMode$rejectCommandBlockPlacement(World world, int x, int y, int z,
                                                            EntityLivingBase placer, ItemStack stack, CallbackInfo ci) {
        if (!NightmareMode.lockDownCreative) return;
        if (!world.isRemote) world.setBlockToAir(x, y, z);
        ci.cancel();
    }
}
