package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import btw.item.BTWItems;
import btw.world.util.WorldUtils;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(EntityMob.class)
public class EntityMobMixin extends EntityCreature{

    public EntityMobMixin(World par1World) {
        super(par1World);
    }

    @Unique private int timeOfLastAttack;

    @Unique
    private static final List<Integer> itemsToAvoidDropping = new ArrayList<>(Arrays.asList(
            Item.swordWood.itemID,
            Item.helmetLeather.itemID,
            Item.plateLeather.itemID,
            Item.legsLeather.itemID,
            Item.bootsLeather.itemID,
            BTWItems.boneClub.itemID,
            BTWItems.steelSword.itemID,
            Item.axeGold.itemID,
            BTWItems.woolBoots.itemID,
            BTWItems.woolChest.itemID,
            BTWItems.woolHelmet.itemID,
            BTWItems.woolLeggings.itemID,
            Item.swordDiamond.itemID
    ));


    @Inject(method = "isValidLightLevel", at = @At(value = "RETURN", ordinal = 2),cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void ensureSpawnsOnEclipse(CallbackInfoReturnable<Boolean> cir, int x, int y, int z, int blockLightValue, int naturalLightValue){
        if(NightmareUtils.getIsEclipse()){
            cir.setReturnValue(naturalLightValue <= this.rand.nextInt(8) + 8);
        }
    }

    @ModifyArg(method = "isValidLightLevel", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"), index = 1)
    private int increaseMobSpawningOnStormChance(int a){
        return 4;
    }

    @Inject(method = "entityMobOnLivingUpdate", at = @At("TAIL"))
    private void manageHealingOverTime(CallbackInfo ci){
        if(this.worldObj != null && this.ticksExisted % (120 - NightmareUtils.getWorldProgress(this.worldObj) * 10) == 0 && (this.timeOfLastAttack + 100) < this.ticksExisted){
            this.heal(1f);
        }
    }

    @Inject(method = "entityMobAttackEntityFrom", at = @At("TAIL"))
    private void timeEntityWasRecentlyHit(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        this.timeOfLastAttack = this.ticksExisted;
    }

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
        if(thisObj.getAttackTarget() instanceof EntityWitch || thisObj.getAttackTarget() instanceof EntityWither || (thisObj.getAttackTarget() instanceof EntitySpider && thisObj instanceof EntitySkeleton)){
            thisObj.setAttackTarget(null);
        }
    }
    @Inject(method = "isValidLightLevel", at = @At("HEAD"), cancellable = true)
    private void allowBloodMoonSpawnsInLight(CallbackInfoReturnable<Boolean> cir){
        EntityMob thisObj = (EntityMob)(Object)this;
        if (thisObj.worldObj != null) {
            if(NightmareUtils.getIsBloodMoon()){
                cir.setReturnValue(true);
            }
        }
    }
    @Inject(method = "canSpawnOnBlockBelow", at = @At("HEAD"),cancellable = true)
    private void manageBloodmareSpawning(CallbackInfoReturnable<Boolean> cir){
        if(NightmareUtils.getIsBloodMoon() || NightmareUtils.getIsMobEclipsed(this)){
            int i = MathHelper.floor_double(this.posX);
            int j = (int)this.boundingBox.minY - 1;
            int k = MathHelper.floor_double(this.posZ);
            if(this.worldObj != null && this.worldObj.getBlockId(i,j,k) != 0 && this.worldObj.getBlockMaterial(i,j,k) != Material.water){
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "attackEntityFrom", at = @At("TAIL"))
    private void ensureExperienceGain(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        if(NightmareUtils.getIsBloodMoon()){
            boolean bIsPostWither = WorldUtils.gameProgressHasWitherBeenSummonedServerOnly();
            this.experienceValue = bIsPostWither ? 40 : 20;
        } else{
            this.experienceValue = NightmareMode.nite ? 10 : 5;
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
                    this.addBlightPotionEffect(thisObj,Potion.regeneration.id);
                } else if (thisObj.worldObj.getBlockMetadata(i,j,k) == 1){
                    this.addBlightPotionEffect(thisObj,Potion.regeneration.id);
                    this.addBlightPotionEffect(thisObj,Potion.resistance.id);
                } else if (thisObj.worldObj.getBlockMetadata(i,j,k) == 2){
                    this.addBlightPotionEffect(thisObj,Potion.moveSpeed.id);
                    this.addBlightPotionEffect(thisObj,Potion.damageBoost.id);
                    this.addBlightPotionEffect(thisObj,Potion.resistance.id);
                } else if (thisObj.worldObj.getBlockMetadata(i,j,k) == 3){
                    this.addBlightPotionEffect(thisObj,Potion.moveSpeed.id);
                    this.addBlightPotionEffect(thisObj,Potion.damageBoost.id);
                    this.addBlightPotionEffect(thisObj,Potion.resistance.id);
                    this.addBlightPotionEffect(thisObj,Potion.invisibility.id);
                }
            }
        }
    }

    @Unique private void addBlightPotionEffect(EntityMob mob, int potionID){
        if(!mob.isPotionActive(potionID)){
            mob.addPotionEffect(new PotionEffect(potionID,100,0));
        }
    }
}
