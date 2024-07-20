package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

//    @Inject(method = "onUpdate", at = @At("TAIL"))
//    private void displayHeldItem(CallbackInfo ci){
//        System.out.println(this.getHeldItem());
//    }
}
