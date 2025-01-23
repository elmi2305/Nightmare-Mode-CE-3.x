package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import btw.item.BTWItems;
import btw.world.util.difficulty.Difficulties;
import btw.world.util.difficulty.Difficulty;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// SETS THE TIME TO NIGHT UPON WORLD CREATION

@Mixin(WorldInfo.class)
public abstract class WorldInfoMixin implements WorldInfoAccessor{
    @Shadow private long worldTime;
    @Shadow private GameRules theGameRules;
    @Shadow private long totalTime;
    @Shadow public abstract Difficulty getDifficulty();

    @Unique private boolean botherChecking = true;

    @Inject(method = "getWorldTime()J", at = @At("HEAD"))
    private void nightSetter(CallbackInfoReturnable<Long> cir) {
        if (!NightmareMode.perfectStart) {
            if (botherChecking && this.getDifficulty() == Difficulties.HOSTILE) {
                if (this.totalTime == 0L) {
                    worldTime = 18000L;
                    theGameRules.addGameRule("doMobSpawning", "false");
                } else if(worldTime >= 20000 && !theGameRules.getGameRuleBooleanValue("doMobSpawning")){
                    theGameRules.addGameRule("doMobSpawning", "true");
                    botherChecking = false;
                } // 1:40 grace period
            }
        } else{
            worldTime = 24000L;
            if(!MinecraftServer.getIsServer()){
                Minecraft.getMinecraft().thePlayer.foodStats.setFoodLevel(45);
                Minecraft.getMinecraft().thePlayer.setHealth(16);
                Minecraft.getMinecraft().thePlayer.inventory.addItemStackToInventory(new ItemStack(BTWBlocks.idleOven));
                Minecraft.getMinecraft().thePlayer.inventory.addItemStackToInventory(new ItemStack(Item.axeStone));
                Minecraft.getMinecraft().thePlayer.inventory.addItemStackToInventory(new ItemStack(BTWItems.tangledWeb));
            } else{
                for (Object playerEntity : MinecraftServer.getServer().worldServers[0].playerEntities) {
                    ((EntityPlayer)playerEntity).inventory.addItemStackToInventory(new ItemStack(Item.axeStone));
                    if(((EntityPlayer)playerEntity).rand.nextInt(3) == 0) continue;
                    ((EntityPlayer)playerEntity).inventory.addItemStackToInventory(new ItemStack(BTWItems.tangledWeb));
                    if(((EntityPlayer)playerEntity).rand.nextInt(3) == 0) continue;
                    ((EntityPlayer)playerEntity).inventory.addItemStackToInventory(new ItemStack(BTWBlocks.idleOven));

                }

            }
        }
    }

    @ModifyArg(method = "updateTagCompound", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/NBTTagCompound;setBoolean(Ljava/lang/String;Z)V",ordinal = 4),index = 0)
    private String javaCompatibility(String string){
        return "jvmArgsOverride";
    }
    @Inject(method = "<init>(Lnet/minecraft/src/NBTTagCompound;)V", at = @At("TAIL"))
    private void addCompatibility(NBTTagCompound par1NBTTagCompound, CallbackInfo ci){
        if (par1NBTTagCompound.hasKey("jvmArgsOverride")) {
            this.setJavaCompatibilityLevel(par1NBTTagCompound.getBoolean("jvmArgsOverride"));
        }
    }
    
    @ModifyArg(method = "updateTagCompound", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/NBTTagCompound;setInteger(Ljava/lang/String;I)V",ordinal = 1),index = 0)
    private String implementDeathCounter(String string){
        return "DeathCount";
    }
    @Inject(method = "<init>(Lnet/minecraft/src/NBTTagCompound;)V", at = @At("TAIL"))
    private void countDeaths(NBTTagCompound par1NBTTagCompound, CallbackInfo ci){
        this.setDeathCounter(EnumGameType.getByID(par1NBTTagCompound.getInteger("DeathCount")));
    }
}
