package com.itlesports.nightmaremode.mixin;

import btw.block.blocks.BedrollBlock;
import btw.item.BTWItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLivingBase implements EntityAccess{
    @Shadow public abstract ItemStack getHeldItem();

    @Shadow protected abstract boolean isPlayer();

    public EntityPlayerMixin(World par1World) {
        super(par1World);
    }

                    // can't jump if you have slowness
    @Inject(method = "canJump", at = @At("RETURN"), cancellable = true)
    private void cantJumpIfSlowness(CallbackInfoReturnable<Boolean> cir){
        if(this.isPotionActive(Potion.moveSlowdown)){
            cir.setReturnValue(false);
        }
    }
    @ModifyConstant(method = "addExhaustionForJump", constant = @Constant(floatValue = 0.2f))
    private float reduceExhaustion(float constant){
        return 0.17f; // jump
    }
    @ModifyConstant(method = "addExhaustionForJump", constant = @Constant(floatValue = 1.0f))
    private float reduceExhaustion1(float constant){
        return 0.75f; // sprint jump
    }
    @ModifyConstant(method = "attackTargetEntityWithCurrentItem", constant = @Constant(floatValue = 0.3f))
    private float reduceExhaustion2(float constant){
        return 0.2f; // punch
    }

    @Inject(method = "onItemUseFinish", at = @At("HEAD"))
    private void manageWaterDrinking(CallbackInfo ci){
        EntityPlayer thisObj = (EntityPlayer)(Object)this;
        if(thisObj.getItemInUse() != null && (thisObj.getItemInUse().itemID == Item.potion.itemID && thisObj.getItemInUse().getItemDamage() == 0)){
            if (this.getFire()>=1) {
                this.invokeSetFire(0);
            }
            if(this.isPotionActive(Potion.confusion.id)){
                this.removePotionEffect(Potion.confusion.id);
            }
            if(this.isPotionActive(Potion.blindness.id)){
                this.removePotionEffect(Potion.blindness.id);
            }
            if(this.isPotionActive(Potion.weakness.id)){
                this.removePotionEffect(Potion.weakness.id);
            }
            if(this.isPotionActive(Potion.moveSlowdown.id)){
                this.removePotionEffect(Potion.moveSlowdown.id);
            }
        }
    }



    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void slowIfInWeb(CallbackInfo ci){
        if(this.isInWeb) {
            this.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 10, 3));
            this.addPotionEffect(new PotionEffect(Potion.weakness.id, 10, 1));
        }
    }

    // removes the check for daytime and kicking the player out of the bed if it turns day. this enables infinite sleeping
    @Redirect(method = "sleepInBedAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;isDaytime()Z"))
    private boolean doNotCareIfDay(World instance){
        if (!(Block.blocksList[this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))] instanceof BedrollBlock)) {
            return false;
        } else {
            return this.worldObj.skylightSubtracted < 4;
        }
    }

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;isDaytime()Z"))
    private boolean doNotCareIfDay1(World instance) {
        if (!(Block.blocksList[this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))] instanceof BedrollBlock)) {
            return false;
        } else {
            return this.worldObj.skylightSubtracted < 4;
        }
    }

    @Redirect(method = "sleepInBedAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldProvider;isSurfaceWorld()Z"))
    private boolean canSleepInNether(WorldProvider instance){
        return true;
    }

    @ModifyConstant(method = "movementModifierWhenRidingBoat", constant = @Constant(doubleValue = 0.35))
    private double windmillSpeedBoat(double constant){
        EntityPlayer thisObj = (EntityPlayer)(Object)this;
        if(isPlayerHoldingWindmill(thisObj)){
            return 5.0;
        }
        return constant;
    }

    @Unique private boolean isPlayerHoldingWindmill(EntityPlayer player) {
        ItemStack currentItemStack = player.inventory.mainInventory[player.inventory.currentItem];
        if (currentItemStack != null) {
            return currentItemStack.itemID == BTWItems.windMill.itemID;
        }
        return false;
    }
}
