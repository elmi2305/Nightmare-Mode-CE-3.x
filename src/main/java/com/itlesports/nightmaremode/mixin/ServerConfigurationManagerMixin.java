package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NetherItemHelper;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.interfaces.EntityPlayerExt;
import com.itlesports.nightmaremode.util.interfaces.FoodStatsExt;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ServerConfigurationManager;
import net.minecraft.src.Teleporter;
import org.spongepowered.asm.mixin.Mixin;
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
            instance.placeInExistingPortal(d, e, f, g, v);
            return;
        }

        instance.placeInPortal(d,e,f,g,v);
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
