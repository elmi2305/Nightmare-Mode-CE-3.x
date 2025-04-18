package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityAgeable;
import net.minecraft.src.EntityAnimal;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityAnimal.class)
public abstract class EntityAnimalMixin extends EntityAgeable {
    public EntityAnimalMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "isSecondaryTargetForSquid", at = @At("HEAD"),cancellable = true)
    private void squidAvoidAnimalsOnEclipse(CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(!NightmareUtils.getIsMobEclipsed(this));
    }
    @Unique
    private int timeOfLastAttack;

    @Inject(method = "onLivingUpdate", at = @At("TAIL"))
    private void manageHealingOverTime(CallbackInfo ci){
        boolean shouldIncreaseHealth = false;
        if (this.worldObj != null && this.worldObj.isRemote && NightmareUtils.getIsEclipse()) {
            if(this.ticksExisted % 30 == 0 && this.timeOfLastAttack + 800 < this.ticksExisted){
                shouldIncreaseHealth = true;
            }
        }
        if(shouldIncreaseHealth){
            this.heal(1f);
        }
    }
    @Inject(method = "attackEntityFrom", at = @At("TAIL"))
    private void timeEntityWasRecentlyHit(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        if (this.worldObj.isRemote && NightmareUtils.getIsEclipse()) {
            this.timeOfLastAttack = this.ticksExisted;
        }
    }
}
