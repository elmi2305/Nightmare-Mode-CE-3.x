package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.SpawnerAnimals;
import net.minecraft.src.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpawnerAnimals.class)
public class SpawnerAnimalsMixin {
    @Redirect(method = "findChunksForSpawning", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldServer;getClosestPlayer(DDDD)Lnet/minecraft/src/EntityPlayer;"))
    private EntityPlayer allowSpawningCloseToPlayerInBloodMoon(WorldServer worldServer, double spawnPosX, double spawnPosY, double spawnPosZ, double exclusionRadius){
        if(NightmareUtils.getIsBloodMoon(worldServer)){
            return worldServer.getClosestPlayer(spawnPosX,spawnPosY + 0.01f, spawnPosZ, 8);
        }
        return worldServer.getClosestPlayer(spawnPosX,spawnPosY + 0.01f, spawnPosZ,exclusionRadius);
    }
}
