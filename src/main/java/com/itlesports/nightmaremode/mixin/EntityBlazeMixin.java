package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.entity.mob.BTWSquidEntity;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityBlaze.class)
public class EntityBlazeMixin extends EntityMob{
    @Unique private static boolean areMobsEvolved = NightmareMode.evolvedMobs;

    @Unique private int dashTimer = 0;

    public EntityBlazeMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        if(this.worldObj != null) {
            boolean isVariant = false;
            int progress = NightmareUtils.getWorldProgress(this.worldObj);
            int eclipseBonus = NightmareUtils.getIsMobEclipsed(this) ? (isAquatic(this) ? 20 : 10) : 0;

            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((16 + progress * (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 10 : 4) + eclipseBonus) * NightmareUtils.getNiteMultiplier());
            // 16 -> 26 -> 36 -> 46
            this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(30);

            if(NightmareUtils.getIsMobEclipsed(this)){
                if(rand.nextBoolean()){
                    this.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, 1000000, 0));
                }
                isVariant = true;
            }
            if(!isVariant){
                if ((progress >= (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 0 : 1) || areMobsEvolved) && rand.nextBoolean()) {
                    this.addPotionEffect(new PotionEffect(Potion.invisibility.id, 1000000, 0));
                }
            }
        }
    }

    @ModifyConstant(method = "attackEntity", constant = @Constant(floatValue = 30.0f))
    private float invisibleBlazePassivity(float constant){
        return isInvisible(this) ? 0f : 30f;
    }

    @Inject(method = "attackEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityBlaze;attackEntityAsMob(Lnet/minecraft/src/Entity;)Z"))
    private void manageInvisibleBlazeAttack(Entity par1Entity, float par2, CallbackInfo ci){
        EntityBlaze thisObj = (EntityBlaze)(Object)this;
        if(isInvisible(thisObj)) {
            thisObj.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(4);
            EntityCreeper bomb = new EntityCreeper(thisObj.worldObj);
            bomb.copyLocationAndAnglesFrom(thisObj);
            thisObj.worldObj.spawnEntityInWorld(bomb);
            thisObj.setDead();
        }
    }
    @ModifyConstant(method = "attackEntity", constant = @Constant(intValue = 100))
    private int lowerBlazeAttackCooldown(int constant) {
        if (NightmareUtils.getIsMobEclipsed(this) && this.getActivePotionEffects().isEmpty()){
            return 50;
        }
        return constant;
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void manageBlazeDash(CallbackInfo ci){
        EntityBlaze thisObj = (EntityBlaze)(Object)this;
        if(thisObj.entityToAttack instanceof EntityPlayer target){
            int threshold = NightmareUtils.getIsMobEclipsed(this) ? 80 : 200;
            double distToPlayer = this.getDistanceSqToEntity(target);
            boolean isEclipse = threshold == 80;

            if(distToPlayer < 49 && isEclipse){
                this.dashTimer += 3;
            }

            this.dashTimer++;
            if (this.dashTimer > threshold){
                double var1 = target.posX - thisObj.posX;
                double var2 = target.posY - thisObj.posY;
                double var3 = target.posZ - thisObj.posZ;

                if(this.getDistanceSqToEntity(target) < 49 && isEclipse && !this.isInvisible()){
                    var1 *= -1.5;
                    var2 *= -1;
                    var3 *= -1.5;
                }

                Vec3 vector = Vec3.createVectorHelper(var1, var2, var3);
                vector.normalize();
                thisObj.motionX = vector.xCoord * 0.12;
                thisObj.motionY = vector.yCoord * 0.18 + 0.4 + (this.rand.nextFloat() * 0.2);
                thisObj.motionZ = vector.zCoord * 0.12;
                this.dashTimer = 0;
            } else if(this.dashTimer > threshold - 20){
                thisObj.motionX = 0;
                thisObj.motionY = 0;
                thisObj.motionZ = 0;
            }
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){
        NightmareUtils.manageEclipseChance(this,8);
    }


    @ModifyArg(method = "attackEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;spawnEntityInWorld(Lnet/minecraft/src/Entity;)Z"))
    private Entity manageWaterBlazeAttack(Entity projectile){
        if (this.getEntityToAttack() instanceof EntityPlayer target && !target.capabilities.isCreativeMode && NightmareUtils.getIsMobEclipsed(this)) {
            if(isAquatic(this)){
                BTWSquidEntity squid = new BTWSquidEntity(this.worldObj);

                double posX = this.posX + getRandomOffsetFromPosition(this);
                double posY = this.posY + this.getEyeHeight() + (getRandomOffsetFromPosition(this) / 4);
                double posZ = this.posZ + getRandomOffsetFromPosition(this);

                squid.addPotionEffect(new PotionEffect(Potion.field_76443_y.id, Integer.MAX_VALUE));

                squid.setPositionAndUpdate(posX,posY,posZ);

                return squid;
            } else{
                double deltaX = target.posX - this.posX;
                double deltaY = target.boundingBox.minY + (double) (target.height / 2.0F) - (this.posY + (double) (this.height / 2.0F)) - 0.5;
                double deltaZ = target.posZ - this.posZ;

                EntityLargeFireball largeFireball = new EntityLargeFireball(this.worldObj, this, deltaX, deltaY, deltaZ);
                this.worldObj.playAuxSFXAtEntity(null, 1009, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                largeFireball.posY = this.posY + (double) (this.height / 2.0f) + 0.5;

                return largeFireball;
            }
        }

        return projectile;
    }


    @Unique private static double getRandomOffsetFromPosition(EntityLivingBase entity){
        return ((entity.rand.nextBoolean() ? -1 : 1) * entity.rand.nextInt(3)+2);
    }


    @Unique private static boolean isInvisible(EntityMob blaze){
        return blaze != null && blaze.isPotionActive(Potion.invisibility);
    }
    @Unique private static boolean isAquatic(EntityMob blaze){
        return blaze != null && blaze.isPotionActive(Potion.waterBreathing);
    }

    @Inject(method = "dropFewItems", at = @At("HEAD"))
    private void manageEclipseShardDrops(boolean bKilledByPlayer, int lootingLevel, CallbackInfo ci){
        if (bKilledByPlayer && NightmareUtils.getIsMobEclipsed(this)) {
            for(int i = 0; i < (lootingLevel * 2) + 1; i++) {
                if (this.rand.nextInt(8) == 0) {
                    this.dropItem(NMItems.darksunFragment.itemID, 1);
                    if (this.rand.nextBoolean()) {
                        break;
                    }
                }
            }

            int itemID = isAquatic(this) ? NMItems.waterRod.itemID : isInvisible(this) ? NMItems.shadowRod.itemID : NMItems.fireRod.itemID;

            int var4 = this.rand.nextInt(3);
            if(this.dimension == -1){
                var4 += 4;
            }
            if (lootingLevel > 0) {
                var4 += this.rand.nextInt(lootingLevel + 1);
            }
            for (int var5 = 0; var5 < var4; ++var5) {
                if(this.rand.nextInt(3) == 0) continue;
                this.dropItem(itemID, 1);
            }
        }
    }
}
