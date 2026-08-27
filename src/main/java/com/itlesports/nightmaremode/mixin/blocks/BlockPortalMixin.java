package com.itlesports.nightmaremode.mixin.blocks;

import btw.community.nightmaremode.NightmareMode;
import btw.world.BTWWorldData;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.util.interfaces.EntityPlayerExt;
import com.itlesports.nightmaremode.worldgen.OverworldTierHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockPortal.class)
public class BlockPortalMixin{
    @Inject(method = "tryToCreatePortal", at = @At("HEAD"), cancellable = true)
    private void preventRemotePortalIgnition(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (OverworldTierHelper.isPortalBlocked(world, x, z)) cir.setReturnValue(false);
    }
    @Redirect(method = "updateTick(Lnet/minecraft/src/World;IIILjava/util/Random;)V", at = @At(value = "INVOKE", target = "Lapi/world/WorldUtils;gameProgressSetNetherBeenAccessedServerOnly()V", remap = false))
    private void doNothing(){} // doesn't update the nether flag to be set every tick

    @Redirect(method = "tryToCreatePortal", at = @At(value = "INVOKE", target = "Lapi/world/WorldUtils;gameProgressSetNetherBeenAccessedServerOnly()V", remap = false))
    private void doNothing1(){} // makes sure the nether flag isn't set as soon as the portal is created

    @Redirect(method = "tryToCreatePortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getBlockId(III)I"))
    private int rejectCrudeObsidianInNetherFrames(World world, int x, int y, int z) {
        int blockID = world.getBlockId(x, y, z);
        if (world.provider.dimensionId == -1
                && blockID == Block.obsidian.blockID
                && world.getBlockMetadata(x, y, z) == 1) {
            return -1;
        }
        return blockID;
    }

    @Inject(method = "tryToCreatePortal", at = @At("TAIL"))
    private void applyPlayerEffects(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir){
        this.runPortalEffects(world,x,y,z);
    }

    @Inject(method = "onEntityCollidedWithBlock", at = @At("HEAD"), cancellable = true)
    private void preventItemPortalTravel(World world, int x, int y, int z, Entity entity, CallbackInfo ci) {
        if (entity instanceof EntityItem) {
            ci.cancel();
        }
    }



    @Unique
    private void runPortalEffects(World world, int x, int y, int z){
        long targetTime = world.getData(NightmareMode.PORTAL_TIME);

        if (!(NMUtils.getWorldProgress() == NMFields.HARDMODE) && (targetTime == 0)) {
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

                    if(player instanceof EntityPlayerExt epe){
                        epe.nightmareMode$setFear(1.0f);

                    }

                    if (distanceSq <= radius * radius) {
                        ChatMessageComponent text1 = new ChatMessageComponent();
                        text1.addText("<???> ");
                        text1.addKey("world.portal_warning");
                        text1.setColor(EnumChatFormatting.DARK_RED);
                        player.sendChatToPlayer(text1);

                        text1 = new ChatMessageComponent();
                        text1.addKey("world.portal_heat_suit_warning");
                        player.sendChatToPlayer(text1);

                        player.addPotionEffect(new PotionEffect(Potion.blindness.id, 100, 0));
                    }
                }
            }

            world.setData(NightmareMode.PORTAL_TIME, world.getWorldTime() + 2880000);

            world.worldInfo.getNBTTagCompound().setLong("PortalTime", world.getWorldTime() + 2880000);
            world.playSoundEffect(x,y,z,"mob.wither.death",1f,0.905F);
            // the rest is handled in EntityPlayerMPMixin
        }
    }
}
