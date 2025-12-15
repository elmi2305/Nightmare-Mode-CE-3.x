package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.Block;
import net.minecraft.src.ChunkProviderGenerate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Random;

@Mixin(ChunkProviderGenerate.class)
public class ChunkProviderGenerateMixin {
    @Shadow private BiomeGenBase[] biomesForGeneration;
    @Unique private static Random rand = new Random();
    @Redirect(method = "generateTerrain", at = @At(value = "FIELD", target = "Lnet/minecraft/src/Block;waterStill:Lnet/minecraft/src/Block;"))
    private Block funnyLavaOcean(){
        if(NightmareMode.isAprilFools && rand.nextInt(8) == 0){
            return Block.lavaStill;
        }
        return Block.waterStill;
    }
    @Inject(method = "generateTerrain", at = @At(value = "FIELD", target = "Lnet/minecraft/src/ChunkProviderGenerate;biomesForGeneration:[Lnet/minecraft/src/BiomeGenBase;", shift = At.Shift.AFTER, ordinal = 0))
    private void printSize(int par1, int par2, short[] blockIDs, byte[] metadata, CallbackInfo ci){
//        System.out.println(Arrays.toString(this.biomesForGeneration));
//        System.out.println("");
//        System.out.println("");
//        System.out.println("");
//        System.out.println("");
//        System.out.println("");
//        System.out.println("");
    }
}
