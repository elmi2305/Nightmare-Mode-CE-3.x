package com.itlesports.nightmaremode.mixin;

import btw.block.blocks.BedBlockBase;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.World;
import net.minecraft.src.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BedBlockBase.class)
public class BedBlockBaseMixin {
    @Redirect(method = "onBlockActivated", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getBiomeGenForCoords(II)Lnet/minecraft/src/BiomeGenBase;"))
    private BiomeGenBase allowSleepingInNether(World instance, int i, int par1){
        return BiomeGenBase.plains;
    }
    @Redirect(method = "onBlockActivated", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldProvider;canRespawnHere()Z"))
    private boolean allowSleepingInNether1(WorldProvider instance){
        return true;
    }
}
