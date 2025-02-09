package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.AITasks.EntityAIChasePlayer;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntitySheep.class)
public abstract class EntitySheepMixin extends EntityAnimal {
    public EntitySheepMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){
        NightmareUtils.manageEclipseChance(this,4);
        this.targetTasks.addTask(12, new EntityAIChasePlayer(this, 1.35f));
    }
    @Inject(method = "onLivingUpdate", at = @At("TAIL"))
    private void manageJumpAttackAtPlayer(CallbackInfo ci){
        if (NightmareUtils.getIsMobEclipsed(this)) {
            if(this.getAttackTarget() instanceof EntityPlayer player){
                double dist = Math.sqrt(this.getDistanceSqToEntity(player));
                if(dist < 1.3){
                    player.attackEntityFrom(DamageSource.causeMobDamage(this), 6f);
                    player.flingAwayFromEntity(this,1f);
                }
                else if(dist < 7){
                    this.rotationPitch = 90f;
                }
            }

        }
        if(this.ticksExisted % 120 != 0) return;
        int originalHealth = 8;
        double eclipseModifier = NightmareUtils.getIsMobEclipsed(this) ? 2.5 : 1;
        if(this.getMaxHealth() != originalHealth * NightmareUtils.getNiteMultiplier() * eclipseModifier){
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(originalHealth * NightmareUtils.getNiteMultiplier() * eclipseModifier);
        }
    }

    @ModifyConstant(method = "updateHungerState", constant = @Constant(intValue = 24000))
    private int eclipseSheepFurRegen(int constant){
        if(NightmareUtils.getIsMobEclipsed(this)){
            return 20;
        }
        return constant;
    }
    @Inject(method = "isSubjectToHunger", at = @At("HEAD"),cancellable = true)
    private void manageEclipseHunger(CallbackInfoReturnable<Boolean> cir){
        if(NightmareUtils.getIsMobEclipsed(this)){
            cir.setReturnValue(false);
        }
    }
    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(8d * NightmareUtils.getNiteMultiplier());
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.23F * (1 + (NightmareUtils.getNiteMultiplier() - 1) / 20));
    }
}
