package com.itlesports.nightmaremode.mixin;

import btw.block.blocks.BedrollBlock;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLivingBase implements EntityAccess{
    @Shadow public abstract ItemStack getHeldItem();

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

}
