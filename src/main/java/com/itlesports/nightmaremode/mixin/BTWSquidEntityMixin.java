package com.itlesports.nightmaremode.mixin;

import btw.entity.mob.BTWSquidEntity;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(BTWSquidEntity.class)
public abstract class BTWSquidEntityMixin extends EntityWaterMob{
    @Shadow(remap = false) private int tentacleAttackCooldownTimer;
    @Unique int squidOnHeadTimer = 0;

    public BTWSquidEntityMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "updateTentacleAttack",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/BTWSquidEntity;tentacleAttackFlingTarget(Lnet/minecraft/src/Entity;Z)V",
                    shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILHARD)

    private void teleportPlayer(CallbackInfo ci, Vec3 tentacleTip, AxisAlignedBB tipBox, List potentialCollisionList, Iterator collisionIterator, EntityLivingBase tempEntity){
        tempEntity.setPositionAndUpdate(this.posX,this.posY-1,this.posZ);
        if (tempEntity instanceof EntityPlayer) {
            this.playSound("mob.endermen.portal",2.0F,1.0F);
        } else{
            this.worldObj.playSoundAtEntity(this,"mob.endermen.portal",1.0F,this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }
    }
    @ModifyConstant(method = "dropFewItems", constant = @Constant(intValue = 8))
    private int increaseMysteriousGlandDropRate(int constant){return 2;}
    // makes the random function roll a number between 0 and 2 instead of 0 and 8

    @Inject(method = "dropFewItems", at = @At("TAIL"))
    private void allowBloodOrbDrops(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        int bloodOrbID = NightmareUtils.getIsBloodMoon() ? NMItems.bloodOrb.itemID : 0;
        if (bloodOrbID > 0) {
            int var4 = this.rand.nextInt(4)+2;
            // 2 - 5
            if (iLootingModifier > 0) {
                var4 += this.rand.nextInt(iLootingModifier + 1);
            }
            for (int var5 = 0; var5 < var4; ++var5) {
                this.dropItem(bloodOrbID, 1);
            }
        }
    }

    // adds a flat +1 to inc sac drops
    @Inject(method = "dropFewItems", at = @At(value = "FIELD", target = "Lbtw/entity/mob/BTWSquidEntity;rand:Ljava/util/Random;",ordinal = 0))
    private void dropMoreSacs(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        this.entityDropItem(new ItemStack(Item.dyePowder, 1, 0), 0.0f);
    }
    @Redirect(method = "updateTentacleAttack",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/BTWSquidEntity;tentacleAttackFlingTarget(Lnet/minecraft/src/Entity;Z)V"))
    private void doNothing(BTWSquidEntity instance, Entity iFXJ, boolean iFXK){}

//    @Inject(method = "updateHeadCrab",
//            at = @At(value = "FIELD",
//                    target = "Lbtw/entity/mob/BTWSquidEntity;headCrabDamageCounter:I",
//                    ordinal = 3,
//                    shift = At.Shift.AFTER))
//    private void resetHeadCrabCounter(CallbackInfo ci){
//        if (this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
//            this.headCrabDamageCounter = 11;
//        }
//    }
    @ModifyConstant(method = "updateHeadCrab", constant = @Constant(intValue = 40),remap = false)
    private int reduceSquidDamageInterval(int constant){
        return 11;
    }

    @ModifyArg(method = "checkForScrollDrop", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
    private int reduceScrollDropChance(int bound){
        return 100;
    }

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void increaseSquidRangeDuringBloodMoon(CallbackInfo ci){
        if(NightmareUtils.getIsBloodMoon()){
            this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(20d);
        }
    }


    @Inject(method = "updateHeadCrab",
            at = @At("HEAD"),remap = false)
    private void doScaryThingsOnHead(CallbackInfo ci) {
        squidOnHeadTimer++;
        if (rand.nextInt(60)==0) {
            this.playSound("mob.ghast.scream",0.3F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }

        if(this.worldObj.getDifficulty() == Difficulties.HOSTILE && this.ridingEntity instanceof EntityPlayer player) {
            if (squidOnHeadTimer > 100) {
                player.addPotionEffect(new PotionEffect(Potion.blindness.id, 200, 0));
            }
            switch (NightmareUtils.getWorldProgress(this.worldObj)) {
                case 0:
                    break;
                case 1:
                    if (!player.isPotionActive(Potion.poison)) {
                        player.addPotionEffect(new PotionEffect(Potion.poison.id,120,0));
                    }
                    break;
                case 2:
                    if (!player.isPotionActive(Potion.wither)) {
                        player.addPotionEffect(new PotionEffect(Potion.wither.id, 120,0));
                    }
                    break;
                case 3:
                    if (!player.isPotionActive(Potion.wither)) {
                        player.addPotionEffect(new PotionEffect(Potion.wither.id, 300,0));
                    }
                    player.addPotionEffect(new PotionEffect(Potion.hunger.id, 160,0));
                    break;
            }
        }
    }


// sets the time until the squid starts dealing damage to n ticks
//    @Inject(method = "checkForHeadCrab", at = @At(value = "FIELD", target = "Lbtw/entity/mob/BTWSquidEntity;headCrabDamageCounter:I", shift = At.Shift.AFTER))
//    private void firstHeadCrabInterval(CallbackInfo ci){
//        this.headCrabDamageCounter = 15;
//    }
    @ModifyConstant(method = "checkForHeadCrab", constant = @Constant(intValue = 40),remap = false)
    private int reduceDamageInterval(int constant){
        return 15;
    }

    @ModifyArg(method = "applyEntityAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/AttributeInstance;setAttribute(D)V"))
    private double modifySquidHP(double d) {
        if (this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            return NightmareUtils.getWorldProgress(this.worldObj) > 0 ? 12*(NightmareUtils.getWorldProgress(this.worldObj)+1) : 18;
            // pre nether 18, hardmode 24, post wither 36, post dragon 48
        }
        return d;
    }
    // TENTACLES
    @Inject(method = "updateEntityActionState", at = @At("HEAD"))
    private void lowerTentacleCooldown(CallbackInfo ci) {
        if (this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            --this.tentacleAttackCooldownTimer;
            if (NightmareUtils.getWorldProgress(this.worldObj) > 1) {
                this.tentacleAttackCooldownTimer -= 2;
            }
        }
    }
    @ModifyConstant(method = "launchTentacleAttackInDirection", constant = @Constant(intValue = 100),remap = false)
    private int lowerTentacleAttackCooldownTimer(int constant){
        if (this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            return 100-(NightmareUtils.getWorldProgress(this.worldObj)*30);
        }
        return constant;
        // cooldown has a degree of randomness, so it's not like it'll fire every 10 ticks post dragon. it has some variance.
        // this purely insures the cooldown condition is true whenever it is checked
    }

    // increasing the squid range

    @ModifyConstant(method = "launchTentacleAttackInDirection", constant = @Constant(doubleValue = 6.0d),remap = false)
    private double increaseCalculatedTentacleRange(double constant){
        return 8.0d;
    }

    @ModifyConstant(method = "attemptTentacleAttackOnTarget", constant = @Constant(doubleValue = 36.0),remap = false)
    private double manageRange(double constant){
        return this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 81.0 : 36.0;
    }
    // let squid see through walls
    @Redirect(method = "attemptTentacleAttackOnTarget", at = @At(value = "INVOKE", target = "Lbtw/entity/mob/BTWSquidEntity;canEntityBeSeen(Lnet/minecraft/src/Entity;)Z"))
    private boolean canSeeThroughWalls1(BTWSquidEntity instance, Entity entity){
        return true;
    }
    @Redirect(method = "attemptTentacleAttackOnTarget", at = @At(value = "INVOKE", target = "Lbtw/entity/mob/BTWSquidEntity;canEntityCenterOfMassBeSeen(Lnet/minecraft/src/Entity;)Z"))
    private boolean canSeeThroughWalls2(BTWSquidEntity instance, Entity entity){
        return true;
    }

    // sets the squid to be permanently in darkness if post nether. this is so the squids are always hostile
    @ModifyVariable(method = "updateEntityActionState", at = @At(value = "STORE"),ordinal = 0)
    private boolean hostilePostNether(boolean bIsInDarkness) {
        if (NightmareUtils.getWorldProgress(this.worldObj) > 0 && this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            return true;
        }
        return bIsInDarkness;
    }
    @Redirect(method = "findClosestValidAttackTargetWithinRange", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;isDaytime()Z"))
    private boolean squidAlwaysNightPostNether(World instance){
        if(NightmareUtils.getWorldProgress(this.worldObj) > 0){
            return false;
        } else return this.worldObj.isDaytime();
    }

    @Redirect(method = "findClosestValidAttackTargetWithinRange", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayer;getBrightness(F)F"))
    private float playerPermanentlyInDarknessAfterNether(EntityPlayer instance, float v){
        if(NightmareUtils.getWorldProgress(this.worldObj)>0 && this.worldObj.getDifficulty() == Difficulties.HOSTILE){
            return 0f;
        }
        return instance.getBrightness(1.0f);
    }

    @Redirect(method = "findClosestValidAttackTargetWithinRange", at = @At(value = "INVOKE", target = "Lbtw/entity/mob/BTWSquidEntity;canEntityBeSeen(Lnet/minecraft/src/Entity;)Z"))
    private boolean canSeeThroughObstacles(BTWSquidEntity instance, Entity entity){
        return true;
    }

    // making the squid launch tentacles even if it cannot see the player, even if its on land, even if the player is not in water
    @Redirect(method = "updateEntityActionState", at = @At(value = "FIELD", target = "Lbtw/entity/mob/BTWSquidEntity;inWater:Z",ordinal = 0))
    private boolean tentacleEvenIfBeached(BTWSquidEntity instance){
        return true;
    }
}
