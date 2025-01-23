package com.itlesports.nightmaremode.mixin;

import btw.BTWMod;
import btw.community.nightmaremode.NightmareMode;
import btw.util.hardcorespawn.HardcoreSpawnUtils;
import btw.world.util.WorldUtils;
import btw.world.util.difficulty.Difficulties;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

// code is sloppy, there's probably a better way to do this.

@Mixin(HardcoreSpawnUtils.class)
public abstract class HardcoreSpawnUtilsMixin{

    @Inject(method = "assignNewHardcoreSpawnLocation", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;setTimeOfLastSpawnAssignment(J)V"))
    private static void nightSetterUponDeath(World world, MinecraftServer server, EntityPlayerMP player, CallbackInfoReturnable<Boolean> cir) {
        long overworldTime = WorldUtils.getOverworldTimeServerOnly();
        if ((BTWMod.isSinglePlayerNonLan() || MinecraftServer.getServer().getCurrentPlayerCount() == 0) && world.getDifficulty() == Difficulties.HOSTILE) {
            overworldTime += 18000L;


            if(world.getMoonPhase() == 4 && (!WorldUtils.gameProgressHasWitherBeenSummonedServerOnly() || WorldUtils.gameProgressHasEndDimensionBeenAccessedServerOnly())){
                overworldTime += 24000L;
            }
            for(int i = 0; i < MinecraftServer.getServer().worldServers.length; ++i) {
                WorldServer tempServer = MinecraftServer.getServer().worldServers[i];
                tempServer.setWorldTime(overworldTime);
            }
        }
    }

    @Redirect(method = "assignNewHardcoreSpawnLocation", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;contains(Ljava/lang/Object;)Z"))
    private static boolean excludeJungle(ArrayList list, Object o){
        if((o.equals(BiomeGenBase.jungle) || o.equals(BiomeGenBase.jungleHills)) && NightmareMode.bloodmare){
            return true;
        }
        if((o.equals(BiomeGenBase.icePlains) || o.equals(BiomeGenBase.iceMountains))){
            return true;
        }
        return list.contains(o);
    }

    @Redirect(method = "handleHardcoreSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;sendChatToPlayer(Lnet/minecraft/src/ChatMessageComponent;)V",ordinal = 0))
    private static void doNothing(EntityPlayerMP instance, ChatMessageComponent par1ChatMessageComponent){}
}