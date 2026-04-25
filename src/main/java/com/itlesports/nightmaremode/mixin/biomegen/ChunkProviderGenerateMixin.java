package com.itlesports.nightmaremode.mixin.biomegen;

import btw.community.nightmaremode.NightmareMode;
import btw.entity.mob.villager.PriestVillagerEntity;
import net.minecraft.src.Block;
import net.minecraft.src.ChunkProviderGenerate;
import net.minecraft.src.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(ChunkProviderGenerate.class)
public class ChunkProviderGenerateMixin {
    @Unique private static Random rand = new Random();
    @Redirect(method = "generateTerrain", at = @At(value = "FIELD", target = "Lnet/minecraft/src/Block;waterStill:Lnet/minecraft/src/Block;", opcode = Opcodes.GETSTATIC))
    private Block funnyLavaOcean(){
        if(NightmareMode.isAprilFools && rand.nextInt(8) == 0){
            return Block.lavaStill;
        }
        return Block.waterStill;
    }

    @Inject(method = "generateAdditionalBrownMushrooms", at = @At("HEAD"), cancellable = true)
    private void cancelOverworld(World worldObj, int iChunkX, int iChunkZ, CallbackInfo ci){
        if(worldObj.provider.dimensionId == 0) {
            ci.cancel();
        }
    }
}
