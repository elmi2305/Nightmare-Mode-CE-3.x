package com.itlesports.nightmaremode.mixin;

import btw.BTWMod;
import btw.block.BTWBlocks;
import btw.util.hardcorespawn.HardcoreSpawnUtils;
import btw.world.util.WorldUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// MODIFY TIME TO n * 24000 + 18000 UPON DEATH where n is current day
// code is sloppy, there's probably a better way to do this.

@Mixin(HardcoreSpawnUtils.class)
public abstract class HardcoreSpawnUtilsMixin{

    @Inject(method = "assignNewHardcoreSpawnLocation", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;setTimeOfLastSpawnAssignment(J)V"))
    private static void nightSetterUponDeath(World world, MinecraftServer server, EntityPlayerMP player, CallbackInfoReturnable<Boolean> cir) {
        long overworldTime = WorldUtils.getOverworldTimeServerOnly();
        if (BTWMod.isSinglePlayerNonLan() || MinecraftServer.getServer().getCurrentPlayerCount() == 0) {
            overworldTime += 18000L;
            if(overworldTime % 192000 == 114000){
                ItemStack var1 = new ItemStack(BTWBlocks.finiteBurningTorch);
                player.inventory.addItemStackToInventory(var1);
            }
            for(int i = 0; i < MinecraftServer.getServer().worldServers.length; ++i) {
                WorldServer tempServer = MinecraftServer.getServer().worldServers[i];
                tempServer.setWorldTime(overworldTime);
            }
        }
    }
}