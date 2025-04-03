package com.itlesports.nightmaremode.mixin.blocks;

import btw.community.nightmaremode.NightmareMode;
import btw.world.util.WorldUtils;
import btw.world.util.data.BTWWorldData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(BlockPortal.class)
public class BlockPortalMixin{
    @Redirect(method = "updateTick(Lnet/minecraft/src/World;IIILjava/util/Random;)V", at = @At(value = "INVOKE", target = "Lbtw/world/util/WorldUtils;gameProgressSetNetherBeenAccessedServerOnly()V"))
    private void doNothing(){} // doesn't update the nether flag to be set every tick

    @Redirect(method = "tryToCreatePortal", at = @At(value = "INVOKE", target = "Lbtw/world/util/WorldUtils;gameProgressSetNetherBeenAccessedServerOnly()V"))
    private void doNothing1(){} // makes sure the nether flag isn't set as soon as the portal is created

    @Inject(method = "tryToCreatePortal", at = @At("TAIL"))
    private void applyPlayerEffects(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir){
        long targetTime = Math.max(world.worldInfo.getNBTTagCompound().getLong("PortalTime"), NightmareMode.getInstance().portalTime);
        if (!WorldUtils.gameProgressHasNetherBeenAccessedServerOnly() && (targetTime == 0 || targetTime == Long.MAX_VALUE)) {
            if (MinecraftServer.getServer() != null) {
                MinecraftServer.getServer().worldServers[0].setData(BTWWorldData.NETHER_ACCESSED, false);
            }
            EntityPlayer nearestPlayer = world.getClosestPlayer(x, y, z, -1);

            ChatMessageComponent text1 = new ChatMessageComponent();
            text1.addText("<???> 3 days remain.");
            text1.setColor(EnumChatFormatting.DARK_RED);
            nearestPlayer.sendChatToPlayer(text1);
            nearestPlayer.addPotionEffect(new PotionEffect(Potion.blindness.id, 100, 0));

            NightmareMode.getInstance().portalTime = world.getWorldTime() + 72000;
            world.worldInfo.getNBTTagCompound().setLong("PortalTime", world.getWorldTime() + 72000);
            world.playSoundEffect(x,y,z,"mob.wither.death",1f,0.905F);
            // the rest is handled in EntityPlayerMPMixin
        }
    }
}
