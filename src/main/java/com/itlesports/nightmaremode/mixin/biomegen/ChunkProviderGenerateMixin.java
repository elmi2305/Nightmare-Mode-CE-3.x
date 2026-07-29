package com.itlesports.nightmaremode.mixin.biomegen;

import btw.community.nightmaremode.NightmareMode;
import btw.entity.mob.villager.PriestVillagerEntity;
import com.itlesports.nightmaremode.agriculture.ChunkAttributeManager;
import com.itlesports.nightmaremode.structure.MapGenOceanDesertTemple;
import net.minecraft.src.Block;
import net.minecraft.src.Chunk;
import net.minecraft.src.ChunkProviderGenerate;
import net.minecraft.src.EnumCreatureType;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;
import java.util.List;

@Mixin(ChunkProviderGenerate.class)
public class ChunkProviderGenerateMixin {
    @Unique private static Random rand = new Random();
    @Unique private final MapGenOceanDesertTemple oceanDesertTempleGenerator = new MapGenOceanDesertTemple();
    @Shadow private World worldObj;
    @Shadow private Random structureRand;
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

    @Inject(method = "provideChunk", at = @At("RETURN"))
    private void initializeChunkAttributes(int chunkX, int chunkZ, CallbackInfoReturnable<Chunk> cir) {
        ChunkAttributeManager.initialize(cir.getReturnValue());
    }

    @Inject(method = "provideChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/MapGenVillage;generate(Lnet/minecraft/src/IChunkProvider;Lnet/minecraft/src/World;II[S[B)V"))
    private void prepareOceanDesertTemples(int chunkX, int chunkZ, CallbackInfoReturnable<Chunk> cir) {
        oceanDesertTempleGenerator.generate((ChunkProviderGenerate) (Object) this, worldObj, chunkX, chunkZ, null, null);
    }

    @Inject(method = "populate",  at = @At(value = "INVOKE", target = "Lnet/minecraft/src/MapGenMineshaft;generateStructuresInChunk(Lnet/minecraft/src/World;Ljava/util/Random;II)Z"))
    private void generateOceanDesertTemples(IChunkProvider provider, int chunkX, int chunkZ, CallbackInfo ci) {
        oceanDesertTempleGenerator.generateStructuresInChunk(worldObj, structureRand, chunkX, chunkZ);
    }

    @Inject(method = "getPossibleCreatures", at = @At("HEAD"), cancellable = true)
    private void useOceanTempleSpawnTable(EnumCreatureType creatureType, int x, int y, int z, CallbackInfoReturnable<List> cir) {
        if (creatureType == EnumCreatureType.monster && oceanDesertTempleGenerator.hasTempleAt(x, y, z)) {
            cir.setReturnValue(oceanDesertTempleGenerator.getSpawnList());
        }
    }
}
