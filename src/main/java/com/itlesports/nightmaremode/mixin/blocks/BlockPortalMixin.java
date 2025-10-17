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

    @Inject(method = "tryToCreatePortal", at = @At("HEAD"), cancellable = true)
    private void injectCrudePortal(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (tryToCreateCrudePortal(world, x, y, z)) {
            this.runPortalEffects(world,x,y,z);
            cir.setReturnValue(true);
        }
    }

    @Unique private boolean tryToCreateCrudePortal(World world, int x, int y, int z) {
        int j;
        int i;
        int xDistPos = 0;
        int xDistNeg = 0;
        int zDistPos = 0;
        int zDistNeg = 0;
        for (int i2 = 0; i2 < 22; ++i2) {
            if (world.getBlockId(x + i2, y, z) == NMBlocks.crudeObsidian.blockID && xDistPos == 0) {
                xDistPos = i2;
            }
            if (world.getBlockId(x - i2, y, z) == NMBlocks.crudeObsidian.blockID && xDistNeg == 0) {
                xDistNeg = -i2;
            }
            if (world.getBlockId(x, y, z + i2) == NMBlocks.crudeObsidian.blockID && zDistPos == 0) {
                zDistPos = i2;
            }
            if (world.getBlockId(x, y, z - i2) != NMBlocks.crudeObsidian.blockID || zDistNeg != 0) continue;
            zDistNeg = -i2;
        }
        int xDiff = xDistPos - xDistNeg + 1;
        int zDiff = zDistPos - zDistNeg + 1;
        if (xDiff < 4 && zDiff < 4 || xDiff > 23 && zDiff > 23 || xDiff > 23 && zDiff < 4 || zDiff > 23 && xDiff < 4) {
            return false;
        }
        int isX = 0;
        int isZ = 0;
        if (xDistPos != 0 && xDistNeg != 0) {
            zDistPos = 0;
            zDistNeg = 0;
            isX = 1;
        } else if (zDistPos != 0 && zDistNeg != 0) {
            xDistPos = 0;
            xDistNeg = 0;
            isZ = 1;
        }
        int yDist = 0;
        for (int i3 = 3; i3 < 22; ++i3) {
            if (world.getBlockId(x, y + i3, z) != NMBlocks.crudeObsidian.blockID) continue;
            yDist = i3 + 1;
            break;
        }
        if (yDist == 0) {
            return false;
        }
        int lowerBound = xDistNeg + zDistNeg;
        int upperBound = xDistPos + zDistPos;
        for (i = lowerBound; i <= upperBound; ++i) {
            for (j = -1; j < yDist; ++j) {
                int id = world.getBlockId(x + isX * i, y + j, z + isZ * i);
                if ((i == lowerBound || i == upperBound) && (j == -1 || j == yDist - 1) || !(i == lowerBound || i == upperBound || j == -1 || j == yDist - 1 ? id != NMBlocks.crudeObsidian.blockID : !world.isAirBlock(x + isX * i, y + j, z + isZ * i) && id != Block.fire.blockID && id != BTWBlocks.largeCampfire.blockID && id != BTWBlocks.mediumCampfire.blockID && id != BTWBlocks.smallCampfire.blockID && id != BTWBlocks.unlitCampfire.blockID)) continue;
                return false;
            }
        }
        for (i = lowerBound + 1; i < upperBound; ++i) {
            for (j = 0; j < yDist - 1; ++j) {
                world.setBlock(x + isX * i, y + j, z + isZ * i, Block.portal.blockID, 0, 2);
            }
        }

        return true;
    }

    private void runPortalEffects(World world, int x, int y, int z){
        long targetTime = Math.max(world.worldInfo.getNBTTagCompound().getLong("PortalTime"), NightmareMode.getInstance().portalTime);

        if (!WorldUtils.gameProgressHasNetherBeenAccessedServerOnly() && (targetTime == 0)) {
            if (MinecraftServer.getServer() != null) {
                MinecraftServer.getServer().worldServers[0].setData(BTWWorldData.NETHER_ACCESSED, false);
            }

            double radius = 16.0;
            for (Object obj : world.playerEntities) {
                if (obj instanceof EntityPlayer player) {
                    double dx = player.posX - x;
                    double dy = player.posY - y;
                    double dz = player.posZ - z;
                    double distanceSq = dx * dx + dy * dy + dz * dz;

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

            NightmareMode.getInstance().portalTime = world.getWorldTime() + 72000;
            world.worldInfo.getNBTTagCompound().setLong("PortalTime", world.getWorldTime() + 72000);
            world.playSoundEffect(x,y,z,"mob.wither.death",1f,0.905F);
            // the rest is handled in EntityPlayerMPMixin
        }
    }
}