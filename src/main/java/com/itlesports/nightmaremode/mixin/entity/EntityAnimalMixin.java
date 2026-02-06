package com.itlesports.nightmaremode.mixin.entity;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.NMUtils;
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
    @Unique private int timeOfLastAttack;

    public EntityAnimalMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "isSecondaryTargetForSquid", at = @At("HEAD"),cancellable = true)
    private void squidAvoidAnimalsOnEclipse(CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(!NMUtils.getIsMobEclipsed(this));
    }

    @Inject(method = "updateHealing", at = @At("TAIL"))
    private void manageHealingOverTime(CallbackInfo ci){
        boolean shouldIncreaseHealth = false;
        if (this.worldObj != null && NMUtils.getIsEclipse()) {
            if(this.ticksExisted % 20 == 0 && this.timeOfLastAttack < (this.ticksExisted - 400)){
                shouldIncreaseHealth = true;
            }
        }
        if(shouldIncreaseHealth){
            this.heal(1f);
        }
    }
    @Inject(method = "attackEntityFrom", at = @At("TAIL"))
    private void timeEntityWasRecentlyHit(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        if (NMUtils.getIsEclipse()) {
            this.timeOfLastAttack = this.ticksExisted;
        }
    }
    @Inject(method = "isSubjectToHunger", at = @At("HEAD"), cancellable = true)
    private void nonSubjectToHungerInUnderworld(CallbackInfoReturnable<Boolean> cir){
        if(this.dimension == NightmareMode.UNDERWORLD_DIMENSION){
            cir.setReturnValue(false);
        }
    }
}
