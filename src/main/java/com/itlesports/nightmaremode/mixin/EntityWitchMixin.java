package com.itlesports.nightmaremode.mixin;

import btw.entity.mob.BTWSquidEntity;
import btw.item.BTWItems;
import btw.world.util.WorldUtils;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.AITasks.EntityAIWitchLightningStrike;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityWitch.class)
public abstract class EntityWitchMixin extends EntityMob {
    @Shadow private int witchAttackTimer;
    @Unique private int minionCountdown = 0;
    public EntityWitchMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){
        NightmareUtils.manageEclipseChance(this,10);
    }

    @Unique private void summonMinion(EntityWitch witch, EntityPlayer player){
        for(int i = 0; i<3; i++){

            if(NightmareUtils.getIsMobEclipsed(this)){
                int xValue = MathHelper.floor_double(this.posX) + this.rand.nextInt(-7, 8);
                int zValue = MathHelper.floor_double(this.posZ) + this.rand.nextInt(-7, 8);
                int yValue = this.worldObj.getPrecipitationHeight(MathHelper.floor_double(xValue), MathHelper.floor_double(zValue));

                if(this.posY + 5 < yValue){
                    yValue = (int) (this.posY + 2);
                    xValue = (int) this.posX;
                    zValue = (int) this.posZ;
                }

                int minionIndex = rand.nextInt(4);
                EntityLiving minion = null;
                switch(minionIndex){
                    case 0:
                        minion = new EntitySkeleton(this.worldObj);
                        ((EntitySkeleton)minion).setSkeletonType(1);
                        minion.setLocationAndAngles(xValue, yValue, zValue, this.rotationYaw, this.rotationPitch);
                        minion.setCurrentItemOrArmor(0, new ItemStack(Item.bow));
                        this.worldObj.spawnEntityInWorld(minion);
                        break;
                    case 1:
                        minion = new EntitySlime(this.worldObj);
                        minion.setLocationAndAngles(xValue, yValue, zValue, this.rotationYaw, this.rotationPitch);
                        ((EntitySlime)minion).setSlimeSize(this.rand.nextInt(4) * 2 + 2);
                        this.worldObj.spawnEntityInWorld(minion);
                        break;
                    case 2:
                        minion = new EntitySpider(this.worldObj);
                        EntitySkeleton skeleton = new EntitySkeleton(this.worldObj);
                        skeleton.setSkeletonType(1);
                        skeleton.setCurrentItemOrArmor(0,new ItemStack(Item.swordStone));
                        BTWSquidEntity squid = new BTWSquidEntity(this.worldObj);

                        minion.setLocationAndAngles(xValue, yValue, zValue, this.rotationYaw, this.rotationPitch);
                        skeleton.setLocationAndAngles(xValue, yValue, zValue, this.rotationYaw, this.rotationPitch);
                        squid.setLocationAndAngles(xValue, yValue, zValue, this.rotationYaw, this.rotationPitch);

                        this.worldObj.spawnEntityInWorld(squid);
                        this.worldObj.spawnEntityInWorld(skeleton);
                        this.worldObj.spawnEntityInWorld(minion);

                        squid.mountEntity(skeleton);
                        skeleton.mountEntity(minion);
                        break;
                    case 3:
                        minion = new EntityBlaze(this.worldObj);
                        minion.setLocationAndAngles(xValue, yValue, zValue, this.rotationYaw, this.rotationPitch);
                        minion.motionY = (rand.nextFloat() + 0.5) / 4;
                        this.worldObj.spawnEntityInWorld(minion);
                        break;
                }

                if (this.getAttackTarget() != null) {
                    minion.setAttackTarget(this.getAttackTarget());
                }
            } else {
                if (NightmareUtils.getIsBloodMoon()) {
                    EntityCreeper tempMinion = new EntityCreeper(this.worldObj);
                    tempMinion.copyLocationAndAnglesFrom(witch);
                    tempMinion.entityToAttack = player;
                    this.worldObj.spawnEntityInWorld(tempMinion);
                    break;
                }
                if (!WorldUtils.gameProgressHasNetherBeenAccessedServerOnly() || this.dimension == 1) {
                    EntitySilverfish tempMinion = new EntitySilverfish(this.worldObj);
                    tempMinion.copyLocationAndAnglesFrom(witch);
                    tempMinion.entityToAttack = player;
                    this.worldObj.spawnEntityInWorld(tempMinion);
                    // silverfish pre nether and in the end
                } else {
                    EntitySpider tempMinion = new EntitySpider(this.worldObj);
                    tempMinion.entityToAttack = player;
                    tempMinion.copyLocationAndAnglesFrom(witch);
                    this.worldObj.spawnEntityInWorld(tempMinion);
                    break;
                }
            }
        }
    }


    @Inject(method = "dropFewItems", at = @At("TAIL"))
    private void allowBloodOrbDrops(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        int bloodOrbID = NightmareUtils.getIsBloodMoon() ? NMItems.bloodOrb.itemID : 0;
        if (bloodOrbID > 0 && bKilledByPlayer) {
            int var4 = this.rand.nextInt(9)+4;
            // 4 - 12
            if (iLootingModifier > 0) {
                var4 += this.rand.nextInt(iLootingModifier + 1);
            }
            for (int var5 = 0; var5 < var4; ++var5) {
                this.dropItem(bloodOrbID, 1);
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
            int progress = NightmareUtils.getWorldProgress(this.worldObj);
            double bloodMoonModifier = NightmareUtils.getIsBloodMoon() ? 1.5 : 1;
            int eclipseModifier = NightmareUtils.getIsMobEclipsed(this) ? 30 : 0;

            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((20.0 + progress * 4) * bloodMoonModifier + eclipseModifier);
            // 20 -> 24 -> 28 -> 32
            this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(40);
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.4);
        }
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityWitch;setAggressive(Z)V",ordinal = 1,shift = At.Shift.AFTER))
    private void healFast(CallbackInfo ci){
        if (this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            this.witchAttackTimer = NightmareUtils.getIsMobEclipsed(this) ? 5 : 10;
        }
    }

    @Inject(method = "dropFewItems", at = @At("TAIL"))
    private void chanceToDropSpecialItems(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        if(this.rand.nextInt(50)==0){
            this.dropItem(Item.expBottle.itemID, 1);
        }
        if(this.rand.nextInt(NightmareUtils.getIsBloodMoon() ? 2 : 6) == 0){
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
    private void manageMinionSummons(CallbackInfo ci){
        EntityWitch thisObj = (EntityWitch)(Object)this;
        this.minionCountdown += thisObj.rand.nextInt(3 + NightmareUtils.getWorldProgress(this.worldObj));
        if(this.minionCountdown >  (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 600 : 1600) - (NightmareUtils.getIsMobEclipsed(this) ? 300 : 0)){
            if(thisObj.getAttackTarget() instanceof EntityPlayer player && !player.capabilities.isCreativeMode){
                this.summonMinion(thisObj, player);
                this.minionCountdown = this.rand.nextInt(15) * (10 - (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 0 : 10));
                // this formula produces 3 silverfish around every 300 ticks spent targeting the player
            }
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        if(this.rand.nextInt(4) == 0 && NightmareUtils.getIsMobEclipsed(this)){
            int xOffset = (this.rand.nextBoolean() ? -1 : 1) * (this.rand.nextInt(3)+3);
            int zOffset = (this.rand.nextBoolean() ? -1 : 1) * (this.rand.nextInt(3)+3);

            int xValue = MathHelper.floor_double(this.posX) + xOffset;
            int zValue = MathHelper.floor_double(this.posZ) + zOffset;
            int yValue = MathHelper.floor_double(this.posY) + this.rand.nextInt(-2,2);
            if(this.worldObj.getBlockId(xValue,yValue,zValue) != 0){
                yValue = this.worldObj.getPrecipitationHeight(xValue,zValue);
                if (Math.abs(yValue - this.posY) > 5){
                    yValue = (int) this.posY;
                }
            }
            this.setPositionAndUpdate(xValue,yValue,zValue);
        }
        return super.attackEntityFrom(par1DamageSource, par2);
    }
}
