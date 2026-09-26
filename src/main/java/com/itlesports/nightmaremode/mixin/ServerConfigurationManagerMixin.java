package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.mixin.interfaces.TeleporterAccess;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NetherRecall;
import com.itlesports.nightmaremode.util.NetherPostProgress;
import com.itlesports.nightmaremode.util.interfaces.PhaseTransitEntity;
import net.minecraft.src.NBTTagCompound;
import com.itlesports.nightmaremode.util.NetherItemHelper;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.interfaces.EntityPlayerExt;
import com.itlesports.nightmaremode.util.interfaces.FoodStatsExt;
import com.itlesports.nightmaremode.world.JourneyProfile;
import com.itlesports.nightmaremode.world.PhasePortalData;
import com.itlesports.nightmaremode.world.PhasePortalManager;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Block;
import net.minecraft.src.MathHelper;
import net.minecraft.src.ServerConfigurationManager;
import net.minecraft.src.Teleporter;
import net.minecraft.src.WorldServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerConfigurationManager.class)
public class ServerConfigurationManagerMixin {
    @Unique private final java.util.Map<EntityPlayerMP, ItemStack> pendingRecalls = new java.util.IdentityHashMap<>();
    @Unique private long deathWorldTime;

    @Inject(method = "respawnPlayer", at = @At("HEAD"))
    private void rememberDeathWorldTime(EntityPlayerMP oldPlayer, int dimension, boolean leavingEnd,
                                        CallbackInfoReturnable<EntityPlayerMP> cir) {
        if (!leavingEnd) deathWorldTime = MinecraftServer.getServer().worldServerForDimension(0).getWorldTime();
    }

    @Inject(method = "respawnPlayer", at = @At("RETURN"))
    private void setMorningAfterDeath(EntityPlayerMP oldPlayer, int dimension, boolean leavingEnd,
                                      CallbackInfoReturnable<EntityPlayerMP> cir) {
        if (leavingEnd) return;
        WorldServer world = MinecraftServer.getServer().worldServerForDimension(0);
        long time = world.getWorldTime();
        world.setWorldTime(deathWorldTime < 120000L ? 0L
                : ((time / 24000L) + (time % 24000L == 0L ? 0L : 1L)) * 24000L);
    }


    @Inject(method = "transferEntityToWorld", at = @At("HEAD"), cancellable = true)
    private void transferPhaseEntityToWorld(Entity entity, int sourceDimension, WorldServer sourceWorld,
                                            WorldServer destinationWorld, CallbackInfo ci) {
        PhasePortalData.Endpoint phaseTarget = PhasePortalManager.getTransferTarget();
        NBTTagCompound recall = NetherRecall.getTransferTarget();
        if (phaseTarget == null && recall == null) return;

        // vanilla skips placement entirely when the source is the end.
        // phase portals have an explicit destination, so bypass its coordinate scaling,
        // end-return handling, and portal generation for every source dimension.
        double x = recall != null ? recall.getDouble("X") : phaseTarget.x + (phaseTarget.axis == 0 ? 1.0D : 0.5D);
        double y = recall != null ? recall.getDouble("Y") : phaseTarget.y + 0.1D;
        double z = recall != null ? recall.getDouble("Z") : phaseTarget.z + (phaseTarget.axis == 1 ? 1.0D : 0.5D);
        int chunkX = MathHelper.floor_double(x / 16.0D);
        int chunkZ = MathHelper.floor_double(z / 16.0D);

        destinationWorld.theChunkProviderServer.loadChunk(chunkX, chunkZ);
        destinationWorld.addChunkRangeToCheckForUnloadList(chunkX - 9, chunkZ - 9, chunkX + 9, chunkZ + 9);
        entity.setLocationAndAngles(x, y, z, recall != null ? recall.getFloat("Yaw") : entity.rotationYaw,
                recall != null ? recall.getFloat("Pitch") : entity.rotationPitch);
        entity.motionX = 0.0D;
        entity.motionY = 0.0D;
        entity.motionZ = 0.0D;

        if (entity.isEntityAlive()) {
            destinationWorld.spawnEntityInWorld(entity);
            destinationWorld.updateEntityWithOptionalForce(entity, false);
        }
        entity.setWorld(destinationWorld);
        ci.cancel();
    }

    @Redirect(method = "transferEntityToWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Teleporter;placeInPortal(Lnet/minecraft/src/Entity;DDDF)V"))
    private void doNotGenerateNetherPortalForUnderworld(Teleporter instance, Entity d, double e, double f, double g, float v){
        if(d.dimension == NMFields.UNDERWORLD_DIMENSION) return;
        if (d.dimension == -1) {
            if (!instance.placeInExistingPortal(d, e, f, g, v)) {
                this.createNetherArrivalPlatform(instance, d);
            }
            return;
        }

        instance.placeInPortal(d,e,f,g,v);
    }

    @Unique
    private void createNetherArrivalPlatform(Teleporter teleporter, Entity entity) {
        int centerX = MathHelper.floor_double(entity.posX);
        int platformY = Math.max(1, Math.min(MathHelper.floor_double(entity.posY) - 1,
                ((TeleporterAccess)teleporter).getWorld().getActualHeight() - 4));
        int centerZ = MathHelper.floor_double(entity.posZ);
        WorldServer world = ((TeleporterAccess)teleporter).getWorld();

        for (int x = centerX - 2; x <= centerX + 2; ++x) {
            for (int z = centerZ - 2; z <= centerZ + 2; ++z) {
                world.setBlock(x, platformY, z, Block.netherrack.blockID, 0, 2);
                for (int y = platformY + 1; y <= platformY + 3; ++y) {
                    world.setBlockToAir(x, y, z);
                }
            }
        }
        entity.setLocationAndAngles(centerX + 0.5D, platformY + 1.0D, centerZ + 0.5D,
                entity.rotationYaw, entity.rotationPitch);
        entity.motionX = 0.0D;
        entity.motionY = 0.0D;
        entity.motionZ = 0.0D;
    }

    @Inject(method = "transferPlayerToDimension", at = @At("HEAD"), cancellable = true)
    private void incinerateInventoryOnNetherEntry(EntityPlayerMP player, int dimensionID, CallbackInfo ci) {
        if (NetherRecall.getTransferTarget() == null && PhasePortalManager.getTransferTarget() == null
                && ((PhaseTransitEntity)player).nm$mustLeavePhasePortal()) {
            ci.cancel();
            return;
        }
        if (PhasePortalManager.getTransferTarget() != null || NetherRecall.getTransferTarget() != null) return;
        if (player.dimension != 0 || dimensionID != -1) {
            return;
        }
        if ((NetherPostProgress.completedTiers(player) & 1) != 0) {
            this.pendingRecalls.remove(player);
            return;
        }
        this.pendingRecalls.put(player, NetherRecall.create(player));
        for (int slot = 0; slot < player.inventory.mainInventory.length; ++slot) {
            ItemStack stack = player.inventory.mainInventory[slot];
            if (stack != null && !NetherItemHelper.survivesNetherEntry(stack, player)) {
                player.inventory.mainInventory[slot] = new ItemStack(NMItems.ash);
            }
        }
        player.inventory.onInventoryChanged();
    }

    @Inject(method = "playerLoggedIn", at = @At("TAIL"))
    private void sendFoodPacketToJoinedPlayer(EntityPlayerMP player, CallbackInfo ci){
        if (!player.worldObj.isRemote) {
            JourneyProfile profile = JourneyProfile.getOrCreate(player.worldObj);
            profile.joins++;
            com.itlesports.nightmaremode.world.SandboxRules.inheritSkills(player);
            profile.recordSkillState(player, player.worldObj);
            player.worldObj.setData(btw.community.nightmaremode.NightmareMode.JOURNEY_PROFILE, profile);
        }
        if (player instanceof EntityPlayerExt ext){
            ext.nightmareMode$setFoodMax(((FoodStatsExt)player.getFoodStats()).nightmareMode$getMaxFoodLevel());
        }
    }
    @Inject(method = "transferPlayerToDimension", at = @At("TAIL"))
    private void sendFoodPacketToDimensionChangedPlayer(EntityPlayerMP player, int dimensionID, CallbackInfo ci){
        ItemStack recall = this.pendingRecalls.remove(player);
        if (recall != null && player.dimension == -1) {
            recall.getTagCompound().setLong("Expires", System.currentTimeMillis() + 15000L);
            int slot = player.inventory.currentItem;
            ItemStack displaced = player.inventory.mainInventory[slot];
            player.inventory.mainInventory[slot] = recall;
            if (displaced != null && !player.inventory.addItemStackToInventory(displaced)) player.dropPlayerItem(displaced);
            player.inventory.onInventoryChanged();
            player.inventoryContainer.detectAndSendChanges();
        }
        if (player instanceof EntityPlayerExt ext){
            ext.nightmareMode$setFoodMax(((FoodStatsExt)player.getFoodStats()).nightmareMode$getMaxFoodLevel());
        }
    }
}
