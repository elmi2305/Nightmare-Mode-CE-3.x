package com.itlesports.nightmaremode.mixin.blocks;

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
        // roundabout way to allow sleeping in the nether. the game checks which biome we are in
    }
    @Redirect(method = "onBlockActivated", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldProvider;canRespawnHere()Z"))
    private boolean allowSleepingInNether1(WorldProvider instance){
        return true;
        // game checks if player canRespawnHere(). this is only true for the overworld. I make it true regardless of dimension
    }
}
