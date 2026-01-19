package com.itlesports.nightmaremode.mixin.biomegen;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.Block;
import net.minecraft.src.ChunkProviderGenerate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

import java.util.Random;

@Mixin(ChunkProviderGenerate.class)
public class ChunkProviderGenerateMixin {
    @Unique private static Random rand = new Random();
    @Redirect(method = "generateTerrain", at = @At(value = "FIELD", target = "Lnet/minecraft/src/Block;waterStill:Lnet/minecraft/src/Block;"))
    private Block funnyLavaOcean(){
        if(NightmareMode.isAprilFools && rand.nextInt(8) == 0){
            return Block.lavaStill;
        }
        return Block.waterStill;
    }
}
