package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import btw.world.util.WorldUtils;
import btw.world.util.data.BTWWorldData;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.blocks.NMBlock;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockPortal.class)
public class BlockPortalMixin{
    @Redirect(method = "updateTick(Lnet/minecraft/src/World;IIILjava/util/Random;)V", at = @At(value = "INVOKE", target = "Lbtw/world/util/WorldUtils;gameProgressSetNetherBeenAccessedServerOnly()V", remap = false))
    private void doNothing(){} // doesn't update the nether flag to be set every tick

    @Redirect(method = "tryToCreatePortal", at = @At(value = "INVOKE", target = "Lbtw/world/util/WorldUtils;gameProgressSetNetherBeenAccessedServerOnly()V", remap = false))
    private void doNothing1(){} // makes sure the nether flag isn't set as soon as the portal is created

    @Inject(method = "tryToCreatePortal", at = @At("TAIL"))
    private void applyPlayerEffects(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir){
        this.runPortalEffects(world,x,y,z);
    }



    @Unique
    private void runPortalEffects(World world, int x, int y, int z){
        long targetTime = world.getData(NightmareMode.PORTAL_TIME);

        if (!WorldUtils.gameProgressHasNetherBeenAccessedServerOnly() && (targetTime == 0)) {
            if (MinecraftServer.getServer() != null) {
                MinecraftServer.getServer().worldServers[0].setData(BTWWorldData.NETHER_ACCESSED, false);
            }

            double radius = 32;
            for (Object obj : world.playerEntities) {
                if (obj instanceof EntityPlayer player) {
                    double dx = player.posX - x;
                    double dy = player.posY - y;
                    double dz = player.posZ - z;
                    double distanceSq = dx * dx + dy * dy + dz * dz;

                    if (player.dimension == -1){
                        return;
                    }

                    if (distanceSq <= radius * radius) {
                        ChatMessageComponent text1 = new ChatMessageComponent();
                        text1.addText("<???> ");
                        text1.addKey("world.portal_warning");
                        text1.setColor(EnumChatFormatting.DARK_RED);
                        player.sendChatToPlayer(text1);

                        player.addPotionEffect(new PotionEffect(Potion.blindness.id, 100, 0));
                    }
                }
            }

            world.setData(NightmareMode.PORTAL_TIME, world.getWorldTime() + 72000);

            world.worldInfo.getNBTTagCompound().setLong("PortalTime", world.getWorldTime() + 72000);
            world.playSoundEffect(x,y,z,"mob.wither.death",1f,0.905F);
            // the rest is handled in EntityPlayerMPMixin
        }
    }
}