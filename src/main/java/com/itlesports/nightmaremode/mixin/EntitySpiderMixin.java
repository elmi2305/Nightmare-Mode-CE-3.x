package com.itlesports.nightmaremode.mixin;

import btw.entity.mob.JungleSpiderEntity;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EntitySpider.class)
public abstract class EntitySpiderMixin extends EntityMob{

    @Shadow protected abstract void entityInit();

    public EntitySpiderMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "shouldContinueAttacking", at = @At("RETURN"), cancellable = true)
    private void doNotAttackWitches(float fDistanceToTarget, CallbackInfoReturnable<Boolean> cir){
        if(this.entityToAttack instanceof EntityWitch){
            cir.setReturnValue(false);
        }
    }

    @Redirect(method = "findPlayerToAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntitySpider;getBrightness(F)F"))
    private float alwaysAggressiveOnEclipse(EntitySpider instance, float v){
        if(NightmareUtils.getIsEclipse()){
            return 0f;
        }
        return instance.getBrightness(v);
    }

    @Inject(method = "dropFewItems", at = @At("TAIL"))
    private void allowBloodOrbDrops(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        int bloodOrbID = NightmareUtils.getIsBloodMoon() ? NMItems.bloodOrb.itemID : 0;
        if (bloodOrbID > 0) {
            int var4 = this.rand.nextInt(3);
            // 0 - 2
            if (iLootingModifier > 0) {
                var4 += this.rand.nextInt(iLootingModifier + 1);
            }
            for (int var5 = 0; var5 < var4; ++var5) {
                this.dropItem(bloodOrbID, 1);
            }
        }
    }

    @Inject(method = "onSpawnWithEgg", at = @At("TAIL"))
    private void manageHardmodeSpiderSpawns(EntityLivingData entityData, CallbackInfoReturnable<EntityLivingData> cir){
        if(NightmareUtils.getWorldProgress(this.worldObj) >= 1 && this.rand.nextInt(6) == 0){
            JungleSpiderEntity caveSpider = new JungleSpiderEntity(this.worldObj);
            caveSpider.copyLocationAndAnglesFrom(this);
            this.setDead();
            this.worldObj.spawnEntityInWorld(caveSpider);
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
            } else if(NightmareUtils.getIsEclipse()){
                return 200;
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
        boolean isHostile = targetEntity.worldObj.getDifficulty() == Difficulties.HOSTILE;
        boolean isEclipse = NightmareUtils.getIsEclipse();

        if (isHostile) {
            double deltaX = targetEntity.posX - this.posX;
            double deltaY = targetEntity.boundingBox.minY + (double) (targetEntity.height / 2.0F) - (this.posY + (double) (this.height / 2.0F)) - 0.5;
            double deltaZ = targetEntity.posZ - this.posZ;

            Entity var11 = null;

            if(targetEntity.rand.nextInt(isEclipse ? 4 : 200) == 0){
                var11 = new EntityLargeFireball(this.worldObj, this, deltaX, deltaY, deltaZ);
                this.worldObj.playAuxSFXAtEntity(null, 1009, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                var11.posY = this.posY + (double) (this.height / 2.0f) + 0.5;
            } else{
                if(isEclipse){

                    int i = rand.nextInt(3);
                    switch(i){
                        case 0:
                            var11 = new EntitySmallFireball(this.worldObj, this, deltaX, deltaY, deltaZ);
                            this.worldObj.playAuxSFXAtEntity(null, 1009, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                            var11.posY = this.posY + (double) (this.height / 2.0f) + 0.5;
                            break;
                        case 1:
                            var11 = new EntityLargeFireball(this.worldObj, this, deltaX, deltaY - 0.5, deltaZ);
                            JungleSpiderEntity spider = new JungleSpiderEntity(this.worldObj);
                            spider.copyLocationAndAnglesFrom(this);
                            this.worldObj.spawnEntityInWorld(spider);
                            spider.mountEntity(var11);

                            this.worldObj.playAuxSFXAtEntity(null, 1009, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                            var11.posY = this.posY + (double) (this.height / 2.0f) + 0.5;
                            break;
                        case 2:
                            var11 = new EntityLargeFireball(this.worldObj, this, deltaX, deltaY - 0.5, deltaZ);
                            EntityCreeper creeper = new EntityCreeper(this.worldObj);
                            creeper.copyLocationAndAnglesFrom(this);
                            this.worldObj.spawnEntityInWorld(creeper);
                            creeper.mountEntity(var11);

                            this.worldObj.playAuxSFXAtEntity(null, 1009, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                            var11.posY = this.posY + (double) (this.height / 2.0f) + 0.5;
                            break;
                    }
                }
            }
            if (var11 != null) {
                this.worldObj.spawnEntityInWorld(var11);
            }
        }
    }

    @Inject(method = "spitWeb", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;spawnEntityInWorld(Lnet/minecraft/src/Entity;)Z"),cancellable = true)
    private void doNotShootWebInEclipse(Entity targetEntity, CallbackInfo ci){
        if (NightmareUtils.getIsEclipse()) {
            ci.cancel();
        }
    }

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        if (this.worldObj != null) {
            EntitySpider thisObj = (EntitySpider)(Object)this;

            int progress = NightmareUtils.getWorldProgress(this.worldObj);
            int eclipseModifier = NightmareUtils.getIsEclipse() ? 20 : 0;
            boolean isEclipse = eclipseModifier > 1;
            double bloodMoonModifier = NightmareUtils.getIsBloodMoon() ? 1.5 : 1;
            boolean isHostile = this.worldObj.getDifficulty() == Difficulties.HOSTILE;
            boolean isBloodMoon = bloodMoonModifier > 1;

            if(progress==0) {
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(16.0 * bloodMoonModifier + eclipseModifier);
                this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.825f);
            } else {
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((13.0 + progress * (isHostile ? 7 : 5)) * bloodMoonModifier + eclipseModifier);
                // 13 -> 20 -> 27 -> 34
                this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(MathHelper.floor_double((4.0 + progress * 2) * (isBloodMoon ? 1.25 : 1)) + (isEclipse ? 1 : 0));
                // 4 -> 6 -> 8 -> 10
                this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(isEclipse ? 0.9f : 0.85f); // slightly increases move speed
            }
            if(this.rand.nextInt(20 - progress) == 0 && !(thisObj instanceof JungleSpiderEntity)){
                this.addPotionEffect(new PotionEffect(Potion.invisibility.id, 1000000,0));
            }

            if(thisObj instanceof JungleSpiderEntity){
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((12.0 + progress*6) * (isBloodMoon ? 1.25 : 1));
                // 12 -> 18 -> 24 -> 30
            }
        }
    }



    @Unique
    private void alertNearbySpiders(EntitySpider spider, EntityPlayer targetPlayer){
        if (spider.worldObj != null) {
            List list = spider.worldObj.getEntitiesWithinAABBExcludingEntity(spider, spider.boundingBox.expand(32.0, 16.0, 32.0));
            for (Object tempEntity : list) {
                if (!(tempEntity instanceof EntitySpider tempSpider)) continue;
                if (tempSpider.entityToAttack != null) continue;
                tempSpider.entityToAttack = targetPlayer;
            }
        }
    }
}
