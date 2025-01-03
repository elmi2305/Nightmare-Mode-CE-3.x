package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import btw.world.util.WorldUtils;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(EntityMob.class)
public class EntityMobMixin extends EntityCreature{
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


    public EntityMobMixin(World par1World) {
        super(par1World);
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

    @Override
    protected void dropEquipment(boolean par1, int par2) {
        for(int var3 = 0; var3 < this.getLastActiveItems().length; ++var3) {
            ItemStack var4 = this.getCurrentItemOrArmor(var3);
            if(var4 != null && itemsToAvoidDropping.contains(var4.itemID)) continue;

            boolean var5 = this.equipmentDropChances[var3] > 1.0F;
            if (var4 != null && (par1 || var5) && this.rand.nextFloat() - (float)par2 * 0.01F < this.equipmentDropChances[var3]) {
                if (!var5 && var4.isItemStackDamageable()) {
                    int var6 = Math.max((int)((float)var4.getMaxDamage() * 0.95F), 1);
                    int var7 = var4.getMaxDamage() - this.rand.nextInt(this.rand.nextInt(var6) + 1);
                    if (var7 > var6) {
                        var7 = var6;
                    }

                    if (var7 < 1) {
                        var7 = 1;
                    }

                    var4.setItemDamage(var7);
                }

                this.entityDropItem(var4, 0.0F);
            }
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
        if(NightmareUtils.getIsBloodMoon() || NightmareUtils.getIsEclipse()){
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
            this.experienceValue = 5;
        }
    }

//    @Inject(method = "entityMobOnLivingUpdate", at = @At("HEAD"))
//    private void playerRadar(CallbackInfo ci){
//        if(!this.hasAttackTarget() && this.ticksExisted % 200 == 0){
////        if(NightmareUtils.getIsBloodMoon() && !this.hasAttackTarget() && this.ticksExisted % 200 == 0){
//            EntityPlayer tempTarget = this.worldObj.getClosestVulnerablePlayer(this.posX,this.posY,this.posZ,128);
//            if(tempTarget != null){
//                System.out.println("check!");
//                this.getNavigator().tryMoveToEntity(tempTarget,this.getAIMoveSpeed());
//                this.setAttackTarget(tempTarget);
//            }
//        }
//    }

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
                } else if (thisObj.worldObj.getBlockMetadata(i,j,k) == 3){
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
