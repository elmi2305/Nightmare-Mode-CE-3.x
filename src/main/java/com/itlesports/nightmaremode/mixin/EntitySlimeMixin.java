package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.entity.SpiderWebEntity;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.NMDifficultyParam;
import com.itlesports.nightmaremode.NMUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(EntitySlime.class)
public abstract class EntitySlimeMixin extends EntityLiving{
    @Unique private static final List<Integer> foodItems = new ArrayList<>(Arrays.asList(
            Item.cake.itemID,
            Item.pumpkinPie.itemID,
            Item.cookie.itemID,
            Item.porkCooked.itemID,
            Item.beefCooked.itemID,
            Item.chickenCooked.itemID,
            Item.fishCooked.itemID,
            BTWItems.cookedCheval.itemID,
            BTWItems.cookedKebab.itemID,
            BTWItems.cookedMutton.itemID,
            BTWItems.cookedMysteryMeat.itemID,
            BTWItems.cookedScrambledEggs.itemID,
            BTWItems.cookedWolfChop.itemID,
            BTWItems.donut.itemID,
            BTWItems.cookedCarrot.itemID,
            BTWItems.chocolate.itemID,
            Item.bakedPotato.itemID,
            BTWItems.porkDinner.itemID,
            BTWItems.steakDinner.itemID,
            BTWItems.wolfDinner.itemID,
            BTWItems.hamAndEggs.itemID,
            BTWItems.chowder.itemID,
            BTWItems.heartyStew.itemID,
            BTWItems.chickenSoup.itemID,
            Item.goldenCarrot.itemID,
            Item.bread.itemID,
            BTWItems.tastySandwich.itemID,
            BTWItems.steakAndPotatoes.itemID
    ));


    public EntitySlimeMixin(World par1World) {
        super(par1World);
    }

    @Shadow public abstract void setSlimeSize(int iSize);
    @Shadow public abstract int getSlimeSize();

    @Shadow public abstract void setDead();

    @Unique private int timeSpentTargeting = 60;
    @Unique private float streakModifier = 1;
    @Unique private float splitCounter = 0;

    @Inject(method = "checkForScrollDrop", at = @At("HEAD"),cancellable = true)
    private void noScrollDrops(CallbackInfo ci){
        ci.cancel();
    }

    @Inject(method = "updateEntityActionState",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/EntitySlime;faceEntity(Lnet/minecraft/src/Entity;FF)V",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void checkIfShouldShootPlayer(CallbackInfo ci, EntityPlayer targetPlayer){
        this.timeSpentTargeting++;
        EntitySlime thisObj = (EntitySlime)(Object)this;
        if (thisObj.ridingEntity == null) {
            if(this.timeSpentTargeting >= 110){
                if (!thisObj.worldObj.isRemote && thisObj.getSlimeSize()>=2) {
                    if(thisObj instanceof EntityMagmaCube && thisObj.dimension != 0){
                        EntityLivingBase target = thisObj.getAITarget();
                        if (target != null) {
                            double var3 = target.posX - thisObj.posX;
                            double var5 = target.boundingBox.minY + (double) (target.height / 2.0F) - (thisObj.posY + (double) (thisObj.height / 2.0F));
                            double var7 = target.posZ - thisObj.posZ;

                            EntitySmallFireball var11 = new EntitySmallFireball(thisObj.worldObj, thisObj, var3, var5, var7);
                            thisObj.worldObj.playAuxSFXAtEntity(null, 1009, (int)thisObj.posX, (int)thisObj.posY, (int)thisObj.posZ, 0);
                            var11.posY = thisObj.posY + (double) (thisObj.height / 2.0f) + 0.5;

                            thisObj.worldObj.spawnEntityInWorld(var11);
                        }
                    } else {
                        thisObj.worldObj.spawnEntityInWorld(new SpiderWebEntity(thisObj.worldObj, thisObj, targetPlayer));
                    }
                    this.timeSpentTargeting = thisObj.rand.nextInt(40);
                }
            }
        }
    }
    // unused despawn timer
//    @Inject(method = "updateEntityActionState", at = @At("HEAD"))
//    private void manageDespawningOnEclipse(CallbackInfo ci){
//        if(this.ticksExisted % 2000 == 1999 && this.worldObj.getClosestPlayer(this.posX,this.posY,this.posZ,40) == null){
//            this.setDead();
//        }
//    }

    @Redirect(method = "getCanSpawnHere", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getCurrentMoonPhaseFactor()F"))
    private float slimeBloodMoon(World world){
        if(NMUtils.getIsBloodMoon()){
            return 0;
        }
        return world.getCurrentMoonPhaseFactor();
    }

    @Inject(method = "jump", at = @At("TAIL"))
    private void chanceToSpawnSlimeOnJump(CallbackInfo ci){
        EntitySlime thisObj = (EntitySlime)(Object)this;
        boolean isEclipsed = NMUtils.getIsMobEclipsed(this);
        int maxSplits = isEclipsed ? 1 : 3;
        int baseChance = isEclipsed ? 4 : 2;

        if (thisObj.getSlimeSize() >= 2 && this.splitCounter < maxSplits){
            if(thisObj.rand.nextInt((int) (baseChance * this.streakModifier)) == 0){
                EntitySlime baby = new EntitySlime(thisObj.worldObj);
                if (isEclipsed) {
                    baby.getDataWatcher().updateObject(16, (byte)(thisObj.getSlimeSize() - 1)); // makes the newly spawned slime half the size of the current one
                } else{
                    baby.getDataWatcher().updateObject(16, (byte)(Math.floor((double) thisObj.getSlimeSize() / 2))); // makes the newly spawned slime half the size of the current one
                }
                baby.setHealth(baby.getSlimeSize());
                if(this.isPotionActive(Potion.field_76443_y)){
                    baby.addPotionEffect(new PotionEffect(Potion.field_76443_y.id, 1000000,0));
                }
                baby.setPositionAndUpdate(thisObj.posX,thisObj.posY,thisObj.posZ);
                thisObj.worldObj.spawnEntityInWorld(baby);
                this.streakModifier += 1 + (float)thisObj.getSlimeSize() + (thisObj.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) ? 0 : 2);
                this.splitCounter += 1;
            }
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseSlimeSize(World par1World, CallbackInfo ci){
        if(NMUtils.getIsEclipse() || (NightmareMode.evolvedMobs && this.rand.nextInt(8) == 0)){
            this.addPotionEffect(new PotionEffect(Potion.field_76443_y.id, 1000000,0));
            this.setSlimeSize(this.getSlimeSize() + this.rand.nextInt(5));
        }
    }

    @Override
    protected void dropFewItems(boolean par1, int par2) {
        if(NMUtils.getIsMobEclipsed(this) && par1){
            for(int i = 0; i < this.getSlimeSize() * 2; i++){
                if(this.rand.nextInt(12) == 0){
                    this.dropItem(foodItems.get(this.rand.nextInt(foodItems.size())), 1);
                }
            }
        }
        super.dropFewItems(par1, par2);
    }
}
