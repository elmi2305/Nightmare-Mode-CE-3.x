package com.itlesports.nightmaremode.mixin;

import btw.entity.mob.JungleSpiderEntity;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EntitySpider.class)
public abstract class EntitySpiderMixin extends EntityMob{

    public EntitySpiderMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "shouldContinueAttacking", at = @At("RETURN"), cancellable = true)
    private void doNotAttackWitches(float fDistanceToTarget, CallbackInfoReturnable<Boolean> cir){
        if(this.entityToAttack instanceof EntityWitch){
            cir.setReturnValue(false);
        }
    }

    @ModifyConstant(method = "spawnerInitCreature", constant = @Constant(intValue = 24000))
    private int lowerSpiderWebCooldown(int constant){
        if (this.worldObj != null) {
            return 16000 - NightmareUtils.getWorldProgress(this.worldObj)*3000;
        } else return 24000;
    }
    @ModifyConstant(method = "spitWeb", constant = @Constant(intValue = 24000))
    private int lowerSpiderWebCooldown1(int constant){
        if (this.worldObj != null) {
            if(this.rand.nextFloat() < 0.1){
                return 10;
            }
            return 16000 - NightmareUtils.getWorldProgress(this.worldObj)*3000;
        }
        return constant;
    }

    @Inject(method = "dropFewItems", at = @At("HEAD"))
    private void dropVenomSacks(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        EntitySpider thisObj = (EntitySpider)(Object)this;

        if(thisObj.hasWeb() || thisObj.rand.nextInt(10) <= NightmareUtils.getWorldProgress(thisObj.worldObj) * 2){
            thisObj.dropItem(Item.fermentedSpiderEye.itemID,1);
        }
    }
    @ModifyArg(method = "dropFewItems", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
    private int increaseSpiderEyeRates(int bound){
        return 4;
    }

    @Inject(method = "attackEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntitySpider;entityMobAttackEntity(Lnet/minecraft/src/Entity;F)V"))
    private void injectVenom(Entity targetEntity, float fDistanceToTarget, CallbackInfo ci){
        if(targetEntity instanceof EntityLivingBase target && target.rand.nextFloat() < 0.4 + NightmareUtils.getWorldProgress(target.worldObj)*0.2){
            if (NightmareUtils.getWorldProgress(target.worldObj)<=1) {
                target.addPotionEffect(new PotionEffect(Potion.poison.id, 40,0));
            } else if (target.worldObj.getDifficulty() == Difficulties.HOSTILE){
                target.addPotionEffect(new PotionEffect(Potion.poison.id, 40,1));
                target.addPotionEffect(new PotionEffect(Potion.hunger.id, 80,0));
            }

            if (target.worldObj.getDifficulty() == Difficulties.HOSTILE && target instanceof EntityPlayer player) {
                this.alertNearbySpiders((EntitySpider)(Object)this,player);
            }
        }
    }

    @Inject(method = "spitWeb", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;spawnEntityInWorld(Lnet/minecraft/src/Entity;)Z"))
    private void chanceToShootFireball(Entity targetEntity, CallbackInfo ci){
        if(targetEntity.worldObj.getDifficulty() == Difficulties.HOSTILE && targetEntity.rand.nextFloat()<0.005 && NightmareUtils.getWorldProgress(targetEntity.worldObj) <= 1){
            double var3 = targetEntity.posX - this.posX;
            double var5 = targetEntity.boundingBox.minY + (double) (targetEntity.height / 2.0F) - (this.posY + (double) (this.height / 2.0F));
            double var7 = targetEntity.posZ - this.posZ;

            EntityLargeFireball var11 = new EntityLargeFireball(this.worldObj, this, var3, var5, var7);
            this.worldObj.playAuxSFXAtEntity(null, 1009, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
            var11.posY = this.posY + (double) (this.height / 2.0f) + 0.5;
            this.worldObj.spawnEntityInWorld(var11);
        }
    }

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        EntitySpider thisObj = (EntitySpider)(Object)this;

        if (thisObj.worldObj != null) {
            int progress = NightmareUtils.getWorldProgress(thisObj.worldObj);
            double bloodMoonModifier = NightmareUtils.getIsBloodMoon(thisObj.worldObj) ? 1.5 : 1;
            boolean isHostile = thisObj.worldObj.getDifficulty() == Difficulties.HOSTILE;
            boolean isBloodMoon = bloodMoonModifier > 1;

            if(progress==0) {
                thisObj.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(16.0 * bloodMoonModifier);
                thisObj.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.825f);
            } else {
                thisObj.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((13.0 + progress * (isHostile ? 7 : 5)) * bloodMoonModifier);
                // 13 -> 20 -> 27 -> 34
                thisObj.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(MathHelper.floor_double((4.0 + progress * 2) * (isBloodMoon ? 1.25 : 1)));
                // 4 -> 6 -> 8 -> 10
                thisObj.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.85f); // slightly increases move speed
            }
            if(thisObj.rand.nextInt(20 - progress) == 0 && !(thisObj instanceof JungleSpiderEntity)){
                thisObj.addPotionEffect(new PotionEffect(Potion.invisibility.id, 1000000,0));
            }
            if (isHostile) {
                thisObj.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(32.0 * bloodMoonModifier);
            }

            if(thisObj instanceof JungleSpiderEntity){
                thisObj.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((12.0 + progress*6) * (isBloodMoon ? 1.25 : 1));
                // 12 -> 18 -> 24 -> 30
            }
        }
    }


    @Unique
    private void alertNearbySpiders(EntitySpider spider, EntityPlayer targetPlayer){
        if (spider.worldObj != null) {
            List list = spider.worldObj.getEntitiesWithinAABBExcludingEntity(spider, spider.boundingBox.expand(32.0, 32.0, 32.0));
            for (Object tempEntity : list) {
                if (!(tempEntity instanceof EntitySpider tempSpider)) continue;
                if (tempSpider.entityToAttack != null) continue;
                tempSpider.entityToAttack = targetPlayer;
            }
        }
    }
}
