package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.mixin.interfaces.TeleporterAccess;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NetherItemHelper;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.interfaces.EntityPlayerExt;
import com.itlesports.nightmaremode.util.interfaces.FoodStatsExt;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Block;
import net.minecraft.src.MathHelper;
import net.minecraft.src.ServerConfigurationManager;
import net.minecraft.src.Teleporter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerConfigurationManager.class)
public class ServerConfigurationManagerMixin {

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
                ((TeleporterAccess)teleporter).getWorld().getActualHeight() - 3));
        int centerZ = MathHelper.floor_double(entity.posZ);

        for (int x = centerX - 2; x <= centerX + 2; ++x) {
            for (int z = centerZ - 2; z <= centerZ + 2; ++z) {
                ((TeleporterAccess)teleporter).getWorld().setBlock(x, platformY, z, Block.netherrack.blockID, 0, 2);
            }
        }
        entity.setLocationAndAngles(centerX + 0.5D, platformY + 1.0D, centerZ + 0.5D,
                entity.rotationYaw, entity.rotationPitch);
        entity.motionX = 0.0D;
        entity.motionY = 0.0D;
        entity.motionZ = 0.0D;
    }

    @Inject(method = "transferPlayerToDimension", at = @At("HEAD"))
    private void incinerateInventoryOnNetherEntry(EntityPlayerMP player, int dimensionID, CallbackInfo ci) {
        if (player.dimension != 0 || dimensionID != -1) {
            return;
        }
        for (int slot = 0; slot < player.inventory.mainInventory.length; ++slot) {
            ItemStack stack = player.inventory.mainInventory[slot];
            if (stack != null && !NetherItemHelper.survivesNetherEntry(stack)) {
                player.inventory.mainInventory[slot] = new ItemStack(NMItems.ash);
            }
        }
        player.inventory.onInventoryChanged();
    }

    @Inject(method = "playerLoggedIn", at = @At("TAIL"))
    private void sendFoodPacketToJoinedPlayer(EntityPlayerMP player, CallbackInfo ci){
        if (player instanceof EntityPlayerExt ext){
            ext.nightmareMode$setFoodMax(((FoodStatsExt)player.getFoodStats()).nightmareMode$getMaxFoodLevel());
        }
    }
    @Inject(method = "transferPlayerToDimension", at = @At("TAIL"))
    private void sendFoodPacketToDimensionChangedPlayer(EntityPlayerMP player, int dimensionID, CallbackInfo ci){
        if (player instanceof EntityPlayerExt ext){
            ext.nightmareMode$setFoodMax(((FoodStatsExt)player.getFoodStats()).nightmareMode$getMaxFoodLevel());
        }
    }
}
