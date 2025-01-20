package com.itlesports.nightmaremode.mixin.blocks;

import btw.world.util.WorldUtils;
import btw.world.util.data.BTWWorldData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(BlockPortal.class)
public class BlockPortalMixin{
    // this code is slightly less awful than before, but still pretty bad
    @Unique boolean runOnce = true;
    @Unique boolean runAgain = true;
    @Unique long targetTime = 2147483647;


    @Redirect(method = "updateTick(Lnet/minecraft/src/World;IIILjava/util/Random;)V", at = @At(value = "INVOKE", target = "Lbtw/world/util/WorldUtils;gameProgressSetNetherBeenAccessedServerOnly()V"))
    private void doNothing(){} // doesn't update the nether flag to be set every tick

    @Redirect(method = "tryToCreatePortal", at = @At(value = "INVOKE", target = "Lbtw/world/util/WorldUtils;gameProgressSetNetherBeenAccessedServerOnly()V"))
    private void doNothing1(){} // makes sure the nether flag isn't set as soon as the portal is created

    @Inject(method = "tryToCreatePortal", at = @At("TAIL"), cancellable = true)
    private void applyPlayerEffects(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir){
        if (this.runAgain && !WorldUtils.gameProgressHasNetherBeenAccessedServerOnly()) {
            if (MinecraftServer.getServer() != null) {
                MinecraftServer.getServer().worldServers[0].setData(BTWWorldData.NETHER_ACCESSED, false);
            }
            if (MinecraftServer.getIsServer()){
                ChatMessageComponent text2 = new ChatMessageComponent();
                text2.addText("<???> Hardmode has begun.");
                text2.setColor(EnumChatFormatting.DARK_RED);
                world.getClosestPlayer(x,y,z,-1).sendChatToPlayer(text2);
                this.runOnce = false;
                WorldUtils.gameProgressSetNetherBeenAccessedServerOnly();
                cir.setReturnValue(null);
            } else {
                EntityPlayer nearestPlayer = world.getClosestPlayer(x, y, z, -1);

                ChatMessageComponent text1 = new ChatMessageComponent();
                text1.addText("<???> 3 days remain.");
                text1.setColor(EnumChatFormatting.DARK_RED);
                nearestPlayer.sendChatToPlayer(text1);
                nearestPlayer.addPotionEffect(new PotionEffect(Potion.blindness.id, 100, 0));

                this.targetTime = world.getWorldTime() + 72000;
                this.runAgain = false;
            }
            world.playSoundEffect(x,y,z,"mob.wither.death",2.0F,0.905F);
        }
    }
    @Inject(method = "randomDisplayTick", at = @At("TAIL"))
    private void displayHardmodeStuffInChat(World world, int x, int y, int z, Random par5Random, CallbackInfo ci){
        if (this.runOnce) {
            if(world.getWorldTime() > this.targetTime && !WorldUtils.gameProgressHasNetherBeenAccessedServerOnly()){
                EntityPlayer nearestPlayer = world.getClosestPlayer(x, y, z, -1);
                ChatMessageComponent text2 = new ChatMessageComponent();
                text2.addText("<???> Hardmode has begun.");
                text2.setColor(EnumChatFormatting.DARK_RED);
                nearestPlayer.sendChatToPlayer(text2);
                this.runOnce = false;
                world.playSoundEffect(x,y,z,"mob.wither.death",2.0F,0.905F);
                WorldUtils.gameProgressSetNetherBeenAccessedServerOnly();
            }
        }
    }
}
