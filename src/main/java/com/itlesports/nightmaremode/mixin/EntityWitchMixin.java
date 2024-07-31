package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityWitch.class)
public abstract class EntityWitchMixin extends EntityMob implements EntityWitchAccess{
    public EntityWitchMixin(World par1World) {
        super(par1World);
    }

    @Unique private int minionCountdown = 0;

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        EntityWitch thisObj = (EntityWitch)(Object)this;
        int progress = NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj);
        thisObj.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(20.0 + progress*4);
        // 20 -> 24 -> 28 -> 32
        thisObj.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(40);
        thisObj.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.4);
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityWitch;setAggressive(Z)V",ordinal = 1,shift = At.Shift.AFTER))
    private void healFast(CallbackInfo ci){
        this.setWitchAttackTimer(10);
    }

    @ModifyConstant(method = "attackEntityWithRangedAttack", constant = @Constant(floatValue = 8.0f))
    private float increaseThrowingRange(float constant){
        return 16f;
    }
    @ModifyConstant(method = "<init>", constant = @Constant(floatValue = 8.0f))
    private float lookAtPlayer(float constant){
        return 16f;
    }
    @ModifyConstant(method = "<init>", constant = @Constant(floatValue = 10.0f))
    private float increaseThrowingRange1(float constant){
        return 20f;
    }

    @ModifyConstant(method = "attackEntityWithRangedAttack", constant = @Constant(floatValue = 0.75f))
    private float modifyPotionVelocity(float constant){
        EntityWitch thisObj = (EntityWitch)(Object)this;
        double dist = thisObj.getDistanceSqToEntity(this.getAttackTarget());
        float velocityTuning = 0;
        if(Math.sqrt(dist)>15){
            velocityTuning = (float)Math.sqrt(dist)/27;
            return 0.6f+velocityTuning;
        }
        if (Math.sqrt(dist)>8) {
            velocityTuning = (float)Math.sqrt(dist)/30;
            return 0.6f+velocityTuning;
        }
        return 0.75f +velocityTuning;
    }

    @Inject(method = "attackEntityWithRangedAttack", at = @At("TAIL"))
    private void chanceToTeleport(EntityLivingBase par1EntityLivingBase, float par2, CallbackInfo ci){
        EntityWitch thisObj = (EntityWitch)(Object)this;
        if(thisObj.getAttackTarget() instanceof EntityPlayer targetPlayer && getDistanceSqToEntity(targetPlayer)>256 && this.rand.nextFloat()<0.95){
            thisObj.getLookHelper().setLookPosition(targetPlayer.posX,targetPlayer.posY+1,targetPlayer.posZ,30,20f);
            System.out.println(thisObj.rotationYaw);
            thisObj.worldObj.spawnEntityInWorld(new EntityEnderPearl(thisObj.worldObj, thisObj));
        }
    }
    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void manageSilverfish(CallbackInfo ci){
        int progress = NightmareUtils.getGameProgressMobsLevel(this.worldObj);
        EntityWitch thisObj = (EntityWitch)(Object)this;
        minionCountdown += thisObj.rand.nextInt(3+progress);
        if(minionCountdown > 600){
            if(thisObj.getAttackTarget() instanceof EntityPlayer){
                this.summonMinion(thisObj);
                minionCountdown = this.rand.nextInt(15)*10;
                // this formula produces 3 silverfish around every 300 ticks spent targeting the player
            }
        }
    }


    @Unique private void summonMinion(EntityWitch witch){
        for(int i = 0; i<3; i++){
            if(NightmareUtils.getGameProgressMobsLevel(this.worldObj) < 1 || NightmareUtils.getGameProgressMobsLevel(this.worldObj) == 3) {
                EntitySilverfish tempMinion = new EntitySilverfish(this.worldObj);
                tempMinion.copyLocationAndAnglesFrom(witch);
                this.worldObj.spawnEntityInWorld(tempMinion);
            } else if(this.dimension != 1){
                EntitySpider tempMinion = new EntitySpider(this.worldObj);
                tempMinion.copyLocationAndAnglesFrom(witch);
                this.worldObj.spawnEntityInWorld(tempMinion);
            }
        }
    }
}
