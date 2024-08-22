package com.itlesports.nightmaremode.mixin;

import btw.entity.mob.JungleSpiderEntity;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Random;

@Mixin(EntitySpider.class)
public abstract class EntitySpiderMixin {

    @Inject(method = "shouldContinueAttacking", at = @At("RETURN"), cancellable = true)
    private void doNotAttackWitches(float fDistanceToTarget, CallbackInfoReturnable<Boolean> cir){
        EntitySpider thisObj = (EntitySpider)(Object)this;
        if(thisObj.entityToAttack instanceof EntityWitch){
            cir.setReturnValue(false);
        }
    }

    @ModifyConstant(method = "spawnerInitCreature", constant = @Constant(intValue = 24000))
    private int lowerSpiderWebCooldown(int constant){
        EntitySpider thisObj = (EntitySpider)(Object)this;
        if (thisObj.worldObj != null) {
            return 16000 - NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj)*3000;
        } else return 24000;
    }
    @ModifyConstant(method = "spitWeb", constant = @Constant(intValue = 24000))
    private int lowerSpiderWebCooldown1(int constant){
        EntitySpider thisObj = (EntitySpider)(Object)this;
        if (thisObj.worldObj != null) {
            if(new Random().nextFloat() < 0.1){
                return 10;
            }
            return 16000 - NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj)*3000;
        }
        return constant;
    }

    @Inject(method = "dropFewItems", at = @At("HEAD"))
    private void dropVenomSacks(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        EntitySpider thisObj = (EntitySpider)(Object)this;
        if(thisObj.hasWeb() || thisObj.rand.nextInt(10)<= NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj) * 2){
            thisObj.dropItem(Item.fermentedSpiderEye.itemID,1);
        }
    }
    @ModifyArg(method = "dropFewItems", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
    private int increaseSpiderEyeRates(int bound){
        return 4;
    }
    @Inject(method = "attackEntity", at  = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntitySpider;entityMobAttackEntity(Lnet/minecraft/src/Entity;F)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void injectVenom(Entity targetEntity, float fDistanceToTarget, CallbackInfo ci){
        if(targetEntity instanceof EntityLivingBase target && target.rand.nextFloat()<0.4+ NightmareUtils.getGameProgressMobsLevel(target.worldObj)*0.2){
            if (NightmareUtils.getGameProgressMobsLevel(target.worldObj)<=1) {
                target.addPotionEffect(new PotionEffect(Potion.poison.id, 40,0));
            } else {
                target.addPotionEffect(new PotionEffect(Potion.poison.id, 40,1));
                target.addPotionEffect(new PotionEffect(Potion.hunger.id, 80,0));
            }
            EntitySpider thisObj = (EntitySpider)(Object)this;

            if (target instanceof EntityPlayer player) {
                this.alertNearbySpiders(thisObj,player);
            }
        }
    }

    @Inject(method = "spitWeb", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;spawnEntityInWorld(Lnet/minecraft/src/Entity;)Z"))
    private void chanceToShootFireball(Entity targetEntity, CallbackInfo ci){
        EntitySpider thisObj = (EntitySpider)(Object)this;
        if(targetEntity.rand.nextFloat()<0.005 && NightmareUtils.getGameProgressMobsLevel(targetEntity.worldObj) <= 1){
            double var3 = targetEntity.posX - thisObj.posX;
            double var5 = targetEntity.boundingBox.minY + (double) (targetEntity.height / 2.0F) - (thisObj.posY + (double) (thisObj.height / 2.0F));
            double var7 = targetEntity.posZ - thisObj.posZ;

            EntityLargeFireball var11 = new EntityLargeFireball(thisObj.worldObj, thisObj, var3, var5, var7);
            thisObj.worldObj.playAuxSFXAtEntity(null, 1009, (int)thisObj.posX, (int)thisObj.posY, (int)thisObj.posZ, 0);
            var11.posY = thisObj.posY + (double) (thisObj.height / 2.0f) + 0.5;
            thisObj.worldObj.spawnEntityInWorld(var11);
        }
    }

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        EntitySpider thisObj = (EntitySpider)(Object)this;
        if (thisObj.worldObj != null) {
            int progress = NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj);
            if(progress==0) {
                thisObj.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(16.0);
                thisObj.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.825f);
            } else {
                thisObj.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(13.0 + progress*7);
                // 16 -> 20 -> 27 -> 34
                thisObj.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(4.0 + progress*2);
                // 4 -> 6 -> 8 -> 10
                thisObj.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.85f); // slightly increases move speed
            }
            if(thisObj.rand.nextFloat()<0.05 && !(thisObj instanceof JungleSpiderEntity)){
                thisObj.addPotionEffect(new PotionEffect(Potion.invisibility.id, 1000000,0));
            }
            thisObj.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(32.0);

            if(thisObj instanceof JungleSpiderEntity){
                thisObj.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(12.0 + progress*6);
                // 12 -> 18 -> 24 -> 30
            }
            if(thisObj.rand.nextFloat()<0.0125 && !(thisObj instanceof JungleSpiderEntity) && thisObj.riddenByEntity == null){
                if(thisObj.rand.nextFloat()<0.3){
                    EntityCreeper rider = new EntityCreeper(thisObj.worldObj);
                    rider.copyLocationAndAnglesFrom(thisObj);
                    thisObj.worldObj.spawnEntityInWorld(rider);
                    rider.mountEntity(thisObj);
                } else {
                    EntityZombie rider = new EntityZombie(thisObj.worldObj);
                    rider.copyLocationAndAnglesFrom(thisObj);
                    thisObj.worldObj.spawnEntityInWorld(rider);
                    rider.mountEntity(thisObj);
                }
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
