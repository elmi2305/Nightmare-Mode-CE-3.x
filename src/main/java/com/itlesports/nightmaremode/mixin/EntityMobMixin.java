package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import btw.world.util.WorldUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityMob.class)
public class EntityMobMixin{

    @Inject(method = "entityMobAttackEntityFrom", at = @At("HEAD"),cancellable = true)
    private void mobMagicImmunity(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        EntityMob thisObj = (EntityMob)(Object)this;
        if((par1DamageSource == DamageSource.magic || par1DamageSource == DamageSource.wither || par1DamageSource == DamageSource.fallingBlock) && (thisObj instanceof EntityWitch || thisObj instanceof EntitySpider || thisObj instanceof EntitySilverfish)){
            cir.setReturnValue(false);
        }
        if (par1DamageSource == DamageSource.fall && (thisObj instanceof EntityCreeper || thisObj instanceof EntitySkeleton) && thisObj.dimension == 1){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void avoidAttackingWitches(CallbackInfo ci){
        EntityMob thisObj = (EntityMob)(Object)this;
        if(thisObj.getAttackTarget() instanceof EntityWitch){
            thisObj.setAttackTarget(null);
        }
    }

    @Inject(method = "entityMobOnLivingUpdate", at = @At("TAIL"))
    private void manageBlightPowerUp(CallbackInfo ci){
        EntityMob thisObj = (EntityMob)(Object)this;
        if (WorldUtils.gameProgressHasWitherBeenSummonedServerOnly()) {
            if(thisObj.worldObj.getBlockId(MathHelper.floor_double(thisObj.posX),MathHelper.floor_double(thisObj.posY-1),MathHelper.floor_double(thisObj.posZ)) == BTWBlocks.aestheticEarth.blockID){
                int i = MathHelper.floor_double(thisObj.posX);
                int j = MathHelper.floor_double(thisObj.posY-1);
                int k = MathHelper.floor_double(thisObj.posZ);

                if(thisObj.worldObj.getBlockMetadata(i,j,k) == 0){
                    this.addMobPotionEffect(thisObj,Potion.regeneration.id);
                } else if (thisObj.worldObj.getBlockMetadata(i,j,k) == 1){
                    this.addMobPotionEffect(thisObj,Potion.regeneration.id);
                    this.addMobPotionEffect(thisObj,Potion.resistance.id);
                } else if (thisObj.worldObj.getBlockMetadata(i,j,k) == 2){
                    this.addMobPotionEffect(thisObj,Potion.moveSpeed.id);
                    this.addMobPotionEffect(thisObj,Potion.damageBoost.id);
                    this.addMobPotionEffect(thisObj,Potion.resistance.id);
                } else{
                    this.addMobPotionEffect(thisObj,Potion.moveSpeed.id);
                    this.addMobPotionEffect(thisObj,Potion.damageBoost.id);
                    this.addMobPotionEffect(thisObj,Potion.resistance.id);
                    this.addMobPotionEffect(thisObj,Potion.invisibility.id);
                }
            }
        }
    }

    @Unique private void addMobPotionEffect(EntityMob mob, int potionID){
        if(!mob.isPotionActive(potionID)){
            mob.addPotionEffect(new PotionEffect(potionID,100,0));
        }
    }
//
//    @Inject(method = "entityMobAttackEntityFrom", at = @At("HEAD"),cancellable = true)
//    private void arrowsIgnoreInvincibility(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
//        if(par1DamageSource.getEntity() instanceof InfiniteArrowEntity){
//            cir.setReturnValue(true);
//        }
//    }
}
