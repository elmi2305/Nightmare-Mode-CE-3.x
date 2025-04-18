package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(SpawnerAnimals.class)
public class SpawnerAnimalsMixin {
    @Unique
    private static Random rand = new Random();

    @Redirect(method = "findChunksForSpawning", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldServer;getClosestPlayer(DDDD)Lnet/minecraft/src/EntityPlayer;"))
    private EntityPlayer allowSpawningCloseToPlayerInBloodMoon(WorldServer worldServer, double spawnPosX, double spawnPosY, double spawnPosZ, double exclusionRadius){
        if(NightmareUtils.getIsBloodMoon()){
            return worldServer.getClosestPlayer(spawnPosX,spawnPosY + 0.01f, spawnPosZ, 8);
        }
        return worldServer.getClosestPlayer(spawnPosX,spawnPosY + 0.01f, spawnPosZ,exclusionRadius);
    }

    @Inject(method = "canCreatureTypeSpawnAtLocation", at = @At(value = "RETURN",ordinal = 0),cancellable = true)
    private static void manageSquidSpawningInEclipse(EnumCreatureType type, World world, int i, int j, int k, CallbackInfoReturnable<Boolean> cir){
        if(NightmareUtils.getIsEclipse()){
            cir.setReturnValue(true);
        }
    }
    @Inject(method = "getVerticalOffsetForPos", at = @At("HEAD"),cancellable = true)
    private static void manageSquidSpawnHeight(EnumCreatureType type, World world, int i, int j, int k, CallbackInfoReturnable<Float> cir) {
        if(NightmareUtils.getIsEclipse() && type == EnumCreatureType.waterCreature){
            cir.setReturnValue((float) (8 + world.rand.nextInt(8)));
        }
    }
    @Inject(method = "canCreatureTypeSpawnInMaterial", at = @At("HEAD"),cancellable = true)
    private static void manageSquidSpawnInAir(EnumCreatureType type, Material material, CallbackInfoReturnable<Boolean> cir){
        if(type == EnumCreatureType.waterCreature && NightmareUtils.getIsEclipse()){
            cir.setReturnValue(true);
        }
    }

    @Redirect(method = "findChunksForSpawning", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EnumCreatureType;getMaxNumberOfCreature()I"))
    private int increaseSquidMobCap(EnumCreatureType instance){
        if(instance == EnumCreatureType.waterCreature && NightmareUtils.getIsEclipse()){
            return 8;
        }
        return instance.getMaxNumberOfCreature();
    }

    @Redirect(method = "canCreatureTypeSpawnAtLocation", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Material;isLiquid()Z",ordinal = 1))
    private static boolean allowSquidsToSpawnIn1BlockHoles(Material instance){
        if(rand.nextInt(16) == 0 && NightmareMode.worldState > 0) {
            return true;
        }
        return instance.isLiquid();
    }
}
