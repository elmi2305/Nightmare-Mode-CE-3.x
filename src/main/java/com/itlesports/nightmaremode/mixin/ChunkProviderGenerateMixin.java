package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.Block;
import net.minecraft.src.ChunkProviderGenerate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(ChunkProviderGenerate.class)
public class ChunkProviderGenerateMixin {
    @Unique private static Random rand = new Random();
    @Unique private long startTime;
    private int value = 60; // Start at 60
    private final int min = 50;
    private final int max = 70;

//    @ModifyConstant(method = "generateTerrain", constant = @Constant(intValue = 63))
//    private int changeWaterLevel(int constant){
//        if(false){
//
//            double chanceUp = (max - value) / 20.0 * 0.2;  // Scale by 0.2 to fit within 20%
//            double chanceDown = (value - min) / 20.0 * 0.2; // Scale by 0.2 to fit within 20%
//            double roll = rand.nextDouble();
//
//            if (roll < chanceUp) {
//                value = Math.min(value + 1, max);
//            } else if (roll < chanceUp + chanceDown) {
//                value = Math.max(value - 1, min);
//            }
//            return value;
//        }
//        return constant;
//    }

    @Redirect(method = "generateTerrain", at = @At(value = "FIELD", target = "Lnet/minecraft/src/Block;waterStill:Lnet/minecraft/src/Block;"))
    private Block funnyLavaOcean(){
        if(NightmareMode.isAprilFools && rand.nextInt(8) == 0){
            return Block.lavaStill;
        }
        return Block.waterStill;
    }
}
