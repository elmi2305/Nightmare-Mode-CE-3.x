package com.itlesports.nightmaremode.mixin;

import btw.entity.mob.KickingAnimal;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityCow.class)
public abstract class EntityCowMixin extends KickingAnimal {
    public EntityCowMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){
        NightmareUtils.manageEclipseChance(this,4);
    }
    @Inject(method = "interact", at = @At("HEAD"),cancellable = true)
    private void manageGatheringBloodMilk(EntityPlayer player, CallbackInfoReturnable<Boolean> cir){
        EntityCow thisObj = (EntityCow)(Object)this;
        if(NightmareUtils.getIsMobEclipsed(thisObj)){
            ItemStack stack = player.inventory.getCurrentItem();
            if (stack != null && stack.itemID == Item.bucketEmpty.itemID) {
                if (thisObj.gotMilk()) {
                    --stack.stackSize;
                    if (stack.stackSize <= 0) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(NMItems.bloodMilk));
                    } else if (!player.inventory.addItemStackToInventory(new ItemStack(NMItems.bloodMilk))) {
                        player.dropPlayerItem(new ItemStack(NMItems.bloodMilk.itemID, 1, 0));
                    }

                    this.attackEntityFrom(DamageSource.generic, 0.0F);
                } else if (this.worldObj.getDifficulty().canMilkingStartleCows()) {
                    this.attackEntityFrom(DamageSource.causePlayerDamage(player), 0.0F);
                }

                cir.setReturnValue(true);
            } else {
                cir.setReturnValue(this.entityAnimalInteract(player));
            }
        }
    }

    @Inject(method = "isSubjectToHunger", at = @At("HEAD"),cancellable = true)
    private void manageEclipseHunger(CallbackInfoReturnable<Boolean> cir){
        if(NightmareUtils.getIsMobEclipsed(this)){
            cir.setReturnValue(false);
        }
    }
    @ModifyConstant(method = "updateHungerState", constant = @Constant(intValue = 24000))
    private int getMilkFaster(int constant){
        return 12000;
    }
}
