package com.itlesports.nightmaremode.mixin;

import btw.world.util.WorldUtils;
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
    @Unique boolean runOnce = true;
    @Unique boolean runAgain = true;
    @Unique boolean runEffects = false;
    @Unique long testTime = 2147483647;


    @Redirect(method = "updateTick(Lnet/minecraft/src/World;IIILjava/util/Random;)V", at = @At(value = "INVOKE", target = "Lbtw/world/util/WorldUtils;gameProgressSetNetherBeenAccessedServerOnly()V"))
    private void doNothing(){}

    @Redirect(method = "tryToCreatePortal", at = @At(value = "INVOKE", target = "Lbtw/world/util/WorldUtils;gameProgressSetNetherBeenAccessedServerOnly()V"))
    private void runHardmodeInitialisation() {
        if(runAgain) {
            if(!WorldUtils.gameProgressHasNetherBeenAccessedServerOnly()) {
                ChatMessageComponent text1 = new ChatMessageComponent();
                text1.addText("3 days remain.");
                text1.setColor(EnumChatFormatting.DARK_RED);
                Minecraft.getMinecraft().thePlayer.sendChatToPlayer(text1);
                testTime = Minecraft.getMinecraft().theWorld.getWorldTime() + 72000;
                runAgain = false;
                runEffects = true;
            }
        }
    }
    @Inject(method = "tryToCreatePortal", at = @At(value = "INVOKE", target = "Lbtw/world/util/WorldUtils;gameProgressSetNetherBeenAccessedServerOnly()V"))
    private void applyPlayerEffects(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir){
        if (runEffects) {
            world.getClosestPlayer(x,y,z,-1).addPotionEffect(new PotionEffect(Potion.blindness.id, 100, 0));
            Minecraft.getMinecraft().thePlayer.playSound("mob.wither.death",2.0F, 0.905F);
            runEffects = false;
        }
    }

    @Inject(method = "randomDisplayTick", at = @At("TAIL"))
    private void displayHardmodeStuffInChat(World par1World, int par2, int par3, int par4, Random par5Random, CallbackInfo ci){
        if (runOnce) {
            if(Minecraft.getMinecraft().theWorld.getWorldTime() > testTime){
                ChatMessageComponent text2 = new ChatMessageComponent();
                text2.addText("Hardmode has begun.");
                text2.setColor(EnumChatFormatting.DARK_RED);
                Minecraft.getMinecraft().thePlayer.sendChatToPlayer(text2);
                runOnce = false;
                WorldUtils.gameProgressSetNetherBeenAccessedServerOnly();
            }
        }
    }
}
