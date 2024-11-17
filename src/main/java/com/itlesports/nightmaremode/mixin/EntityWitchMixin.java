package com.itlesports.nightmaremode.mixin;

import btw.item.BTWItems;
import btw.world.util.WorldUtils;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.EntityAIWitchLightningStrike;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityWitch.class)
public abstract class EntityWitchMixin extends EntityMob {

    @Shadow private int witchAttackTimer;

    public EntityWitchMixin(World par1World) {
        super(par1World);
    }

    @Unique private int minionCountdown = 0;
    @Unique private void summonMinion(EntityWitch witch, EntityPlayer player){
        for(int i = 0; i<3; i++){
            if(!WorldUtils.gameProgressHasNetherBeenAccessedServerOnly() || this.dimension == 1) {
                EntitySilverfish tempMinion = new EntitySilverfish(this.worldObj);
                tempMinion.copyLocationAndAnglesFrom(witch);
                tempMinion.setAttackTarget(player);
                this.worldObj.spawnEntityInWorld(tempMinion);
                // silverfish pre nether and in the end
            } else {
                EntitySpider tempMinion = new EntitySpider(this.worldObj);
                tempMinion.copyLocationAndAnglesFrom(witch);
                tempMinion.setAttackTarget(player);
                this.worldObj.spawnEntityInWorld(tempMinion);
                break;
            }
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addWitchSpecificAITasks(World par1World, CallbackInfo ci){
        this.targetTasks.addTask(1, new EntityAIWitchLightningStrike(this));
    }

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        if (this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            int progress = NightmareUtils.getGameProgressMobsLevel(this.worldObj);
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(20.0 + progress*4);
            // 20 -> 24 -> 28 -> 32
            this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(40);
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.4);
        }
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityWitch;setAggressive(Z)V",ordinal = 1,shift = At.Shift.AFTER))
    private void healFast(CallbackInfo ci){
        if (this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            this.witchAttackTimer = 10;
        }
    }

    @Inject(method = "dropFewItems", at = @At("TAIL"))
    private void chanceToDropSpecialItems(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        if(this.rand.nextInt(50)==0){
            this.dropItem(Item.expBottle.itemID, 1);
        }
        if(this.rand.nextInt(6)==0){
            this.dropItem(BTWItems.witchWart.itemID, this.rand.nextInt(2));
        }
    }

    @ModifyConstant(method = "attackEntityWithRangedAttack", constant = @Constant(floatValue = 8.0f))
    private float increaseThrowingRange(float constant){
        return this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 16f : constant;
    }
    @ModifyConstant(method = "<init>", constant = @Constant(floatValue = 8.0f))
    private float lookAtPlayer(float constant){
        return this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 16f : constant;
    }
    @ModifyConstant(method = "<init>", constant = @Constant(floatValue = 10.0f))
    private float increaseThrowingRange1(float constant){
        return this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 20f : constant;
    }

    @ModifyConstant(method = "attackEntityWithRangedAttack", constant = @Constant(floatValue = 0.75f))
    private float modifyPotionVelocity(float constant){
        if(this.worldObj.getDifficulty() != Difficulties.HOSTILE){
            return constant;
        }
        if (this.getAttackTarget() != null) {
            double dist = this.getDistanceSqToEntity(this.getAttackTarget());
            float velocityTuning = 0;
            if(Math.sqrt(dist) > 15){
                velocityTuning = (float)Math.sqrt(dist)/27;
                return 0.6f+velocityTuning;
            }
            if (Math.sqrt(dist) > 8) {
                velocityTuning = (float)Math.sqrt(dist)/30;
                return 0.6f+velocityTuning;
            }
            if (Math.sqrt(dist) < 5){
                return 0.6f;
            }
            return 0.75f +velocityTuning;
        }
        return constant;
    }

    @Inject(method = "attackEntityWithRangedAttack", at = @At("TAIL"))
    private void chanceToTeleport(EntityLivingBase par1EntityLivingBase, float par2, CallbackInfo ci){
        if(this.worldObj.getDifficulty() == Difficulties.HOSTILE && this.getAttackTarget() instanceof EntityPlayer targetPlayer && getDistanceSqToEntity(targetPlayer)>256){
            EntityEnderPearl pearl = new EntityEnderPearl(this.worldObj, this);
            this.worldObj.spawnEntityInWorld(pearl);
            double var1 = targetPlayer.posX - this.posX;
            double var2 = targetPlayer.posZ - this.posZ;
            Vec3 vector = Vec3.createVectorHelper(var1, 0, var2);
            vector.normalize();
            pearl.motionX = vector.xCoord * 0.1;
            pearl.motionZ = vector.zCoord * 0.1;
        }
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void manageSilverfish(CallbackInfo ci){
        EntityWitch thisObj = (EntityWitch)(Object)this;
        minionCountdown += thisObj.rand.nextInt(3 + NightmareUtils.getGameProgressMobsLevel(this.worldObj));
        if(minionCountdown >  (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 600 : 1600)){
            if(thisObj.getAttackTarget() instanceof EntityPlayer player && !player.capabilities.isCreativeMode){
                this.summonMinion(thisObj, player);
                minionCountdown = this.rand.nextInt(15) * (10 - (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 0 : 10));
                // this formula produces 3 silverfish around every 300 ticks spent targeting the player
            }
        }
    }
}
