package com.itlesports.nightmaremode.mixin;

import btw.BTWMod;
import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import btw.item.BTWItems;
import btw.util.hardcorespawn.HardcoreSpawnUtils;
import btw.world.util.WorldUtils;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
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
//                ItemStack var1 = new ItemStack(BTWBlocks.finiteBurningTorch,3);
//                ItemStack var2 = new ItemStack(ItemPotion.potion,1,16422);
//                player.inventory.addItemStackToInventory(var1);
//                player.inventory.addItemStackToInventory(var2);
                overworldTime += 24000L;
            }
            for(int i = 0; i < MinecraftServer.getServer().worldServers.length; ++i) {
                WorldServer tempServer = MinecraftServer.getServer().worldServers[i];
                tempServer.setWorldTime(overworldTime);
            }
            if (NightmareUtils.getWorldProgress(player.worldObj) >= 2) {
                ItemStack var1 = new ItemStack(BTWBlocks.finiteBurningTorch,3);
                ItemStack var3 = new ItemStack(ItemPotion.potion,1,16422);
                player.inventory.addItemStackToInventory(var1);
                player.inventory.addItemStackToInventory(var3);
            }
            if (NightmareUtils.getWorldProgress(player.worldObj) >= 1){
                ItemStack var3 = new ItemStack(Item.compass,1);
                ItemStack var2 = new ItemStack(BTWItems.corpseEye,1);
                player.inventory.addItemStackToInventory(var2);
                player.inventory.addItemStackToInventory(var3);
            }
            // gives a few bonus items after you die
        }
    }

    @Redirect(method = "assignNewHardcoreSpawnLocation", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;contains(Ljava/lang/Object;)Z"))
    private static boolean excludeJungle(ArrayList list, Object o){
        if((o.equals(BiomeGenBase.jungle) || o.equals(BiomeGenBase.jungleHills)) && NightmareMode.bloodNightmare){
            return true;
        }
        return list.contains(o);
    }

    @Redirect(method = "handleHardcoreSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;sendChatToPlayer(Lnet/minecraft/src/ChatMessageComponent;)V",ordinal = 0))
    private static void doNothing(EntityPlayerMP instance, ChatMessageComponent par1ChatMessageComponent){}
}