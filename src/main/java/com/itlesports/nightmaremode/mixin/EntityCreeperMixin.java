package com.itlesports.nightmaremode.mixin;

import btw.item.BTWItems;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(EntityCreeper.class)
public class EntityCreeperMixin {
    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void chanceToSpawnWithSpeed(CallbackInfo ci){
        EntityCreeper thisObj = (EntityCreeper)(Object)this;
        if (new Random().nextFloat() < 0.05 + (NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj)*0.02)) {
            thisObj.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 10000000,0));
        }
    }
    @Inject(method = "interact",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/InventoryPlayer;getCurrentItem()Lnet/minecraft/src/ItemStack;",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void explodeIfShorn(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        EntityCreeper thisObj = (EntityCreeper)(Object)this;
        ItemStack playersCurrentItem = player.inventory.getCurrentItem();
        if (playersCurrentItem != null && playersCurrentItem.getItem() instanceof ItemShears && thisObj.getNeuteredState() == 0) {
            if (!thisObj.worldObj.isRemote) {
                boolean var2 = thisObj.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
                if (thisObj.getPowered()) {
                    thisObj.worldObj.createExplosion(thisObj, thisObj.posX, thisObj.posY + (double)thisObj.getEyeHeight(), thisObj.posZ, 6, var2);
                } else {
                    thisObj.worldObj.createExplosion(thisObj, thisObj.posX, thisObj.posY + (double)thisObj.getEyeHeight(), thisObj.posZ, 3, var2);
                }
                thisObj.setDead();
            }
        }
    }
}
