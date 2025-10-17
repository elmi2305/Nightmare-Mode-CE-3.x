package com.itlesports.nightmaremode.mixin;

import btw.BTWMod;
import btw.achievement.event.AchievementEventDispatcher;
import btw.block.BTWBlocks;
import btw.block.blocks.BedrollBlock;
import btw.community.nightmaremode.NightmareMode;
import btw.entity.mob.BTWSquidEntity;
import btw.entity.mob.KickingAnimal;
import btw.item.BTWItems;
import btw.util.status.PlayerStatusEffects;
import btw.util.status.StatusEffect;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.achievements.NMAchievementEvents;
import com.itlesports.nightmaremode.achievements.NMAchievements;
import com.itlesports.nightmaremode.entity.EntityBloodWither;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.network.IHorseTamingClient;
import com.itlesports.nightmaremode.network.IPlayerDirectionTracker;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;

import static com.itlesports.nightmaremode.NMUtils.chainArmor;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLivingBase implements EntityAccessor, IPlayerDirectionTracker {


    @Shadow public abstract ItemStack getHeldItem();
    @Shadow protected abstract boolean isPlayer();
    @Shadow public PlayerCapabilities capabilities;
    @Shadow public FoodStats foodStats;
    @Shadow public abstract boolean attackEntityFrom(DamageSource par1DamageSource, float par2);
    @Shadow public abstract void playSound(String par1Str, float par2, float par3);
    @Shadow public int experienceLevel;
    @Shadow protected abstract int decreaseAirSupply(int iAirSupply);
    @Shadow public abstract boolean isPlayerSleeping();
    @Shadow public abstract void wakeUpPlayer(boolean par1, boolean par2, boolean par3);
    @Shadow public abstract boolean isPlayerFullyAsleep();



    @Unique private int ticksInWater;
    @Unique private int ticksSleeping;
    @Unique private int noArmorTicks;

    public EntityPlayerMixin(World par1World) {
        super(par1World);
    }

    // can't jump if you have slowness
    @Inject(method = "canJump", at = @At("RETURN"), cancellable = true)
    private void cantJumpIfSlowness(CallbackInfoReturnable<Boolean> cir){
        if(this.isPotionActive(Potion.moveSlowdown) && this.worldObj.getDifficulty() == Difficulties.HOSTILE){
            cir.setReturnValue(false);
        }
    }
    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void noHitAttributes(CallbackInfo ci){
        if (NightmareMode.noHit) {
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(1);
            this.setHealth(1f);
//            this.getDataWatcher().updateObject(6, 1);
        } else if(NightmareMode.nite){
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(this.getHealthForExperience());
//            this.getDataWatcher().updateObject(6, this.getHealthForExperience());
            this.setHealth(this.getHealthForExperience());
        }
    }
    @Unique private byte getHealthForExperience(){
        return (byte) Math.min(MathHelper.floor_double((double) this.experienceLevel / 3) * 2 + 6, 20);
    }
    @Inject(method = "doesStatusPreventSprinting", at = @At("HEAD"),cancellable = true)
    private void allowSprintingOnLowHealth(CallbackInfoReturnable<Boolean> cir){
        if(NightmareMode.noHit || NightmareMode.nite){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isImmuneToHeadCrabDamage", at = @At("HEAD"),cancellable = true)
    private void notImmuneToSquidsEclipse(CallbackInfoReturnable<Boolean> cir){
        if(this.riddenByEntity instanceof BTWSquidEntity && NMUtils.getIsMobEclipsed((BTWSquidEntity) this.riddenByEntity)){
            cir.setReturnValue(false);
        }
    }
    @Inject(method = "jump", at = @At("TAIL"))
    private void aprilFoolsJumpHeight(CallbackInfo ci){
        if(NightmareMode.isAprilFools){
            if (this.rand.nextInt(6) == 0) {
                this.motionY += this.rand.nextFloat() * 0.2f * (this.rand.nextBoolean() ? 1 : -1);
            }
        }
    }

    @Inject(method = "attackTargetEntityWithCurrentItem", at = @At("HEAD"))
    private void manageLifeSteal(Entity entity, CallbackInfo ci){
        if(entity instanceof EntityLiving &&
                NMUtils.isHoldingBloodSword(this) &&
                entity.hurtResistantTime == 0 &&
                !this.isPotionActive(Potion.weakness) &&
                !(entity instanceof EntityWither)){
            int chance = 20 - NMUtils.getBloodArmorWornCount(this) * 3;
            // 20, 16, 12, 8, 4

            if(rand.nextInt(chance) == 0){
                this.heal(rand.nextInt(chance) == 0 ? 2 : 1);
            }

            if(rand.nextInt((int) (chance / 1.5)) == 0 && this.foodStats.getFoodLevel() < 57){
                this.foodStats.setFoodLevel(this.foodStats.getFoodLevel() + 3);
            }


            if(NMUtils.isWearingFullBloodArmor(this)){
                if((this.rand.nextInt(3) == 0) && this.fallDistance > 0.0F){
                    this.heal(1f);
                }
            }
        }
    }
    @ModifyArg(method = "attackTargetEntityWithCurrentItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Entity;attackEntityFrom(Lnet/minecraft/src/DamageSource;F)Z"),index = 1)
    private float unkillableMobs(float par2){
        if(NightmareMode.unkillableMobs){
            return 0f;
        }
        return par2;
    }
    @Inject(method = "attackTargetEntityWithCurrentItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayer;triggerAchievement(Lnet/minecraft/src/StatBase;)V"),locals = LocalCapture.CAPTURE_FAILHARD)
    private void manageAchievementsOnPlayerHit(Entity entityHit, CallbackInfo ci, float dmg){
        EntityPlayer player = (EntityPlayer)(Object)this;
        AchievementEventDispatcher.triggerEvent(NMAchievementEvents.PlayerAttackEvent.class, player, new NMAchievementEvents.PlayerAttackEvent.PlayerAttackEventData(player, entityHit, dmg));
    }


    @Override
    public int getMaxInPortalTime() {
        if(NMUtils.getWorldProgress() > 1) {return 25;}
        if(NMUtils.getWorldProgress() > 0) {return 40;}
        return 60;
    }

    @Inject(method = "dropPlayerItemWithRandomChoice", at = @At("TAIL"),locals = LocalCapture.CAPTURE_FAILHARD)
    private void manageDroppingItemsDuringBloodWither(ItemStack par1ItemStack, boolean par2, CallbackInfoReturnable<EntityItem> cir, EntityItem var3, float var4, float var5){
        if(EntityBloodWither.isBossActive()){
            var3.setFire(100);
            var3.dealFireDamage(3);
        }
    }
    @ModifyConstant(method = "addExhaustionForJump", constant = @Constant(floatValue = 0.2f))
    private float reduceExhaustion(float constant) {
        EntityPlayer thisObj = (EntityPlayer)(Object)this;

        if(NightmareMode.noHit){
            return 0.09f;
        }
        if(NightmareMode.nite){
            return 0.2f * NMUtils.getFoodShanksFromLevel(thisObj) / 60f;
        }
        if (NightmareMode.bloodmare) {
            return 0.15f;
        }
        return 0.17f; // jump
    }
    @ModifyConstant(method = "addExhaustionForJump", constant = @Constant(floatValue = 1.0f))
    private float reduceExhaustion1(float constant){
        EntityPlayer thisObj = (EntityPlayer)(Object)this;

        if(NightmareMode.noHit){
            return 0.2f;
        }
        if(NightmareMode.nite){
            return 0.75f * NMUtils.getFoodShanksFromLevel(thisObj) / 60f;
        }
        if(NightmareMode.bloodmare){
            return 0.5f;
        }
        return 0.75f; // sprint jump
    }
    @ModifyConstant(method = "attackTargetEntityWithCurrentItem", constant = @Constant(floatValue = 0.3f))
    private float reduceExhaustion2(float constant){
        EntityPlayer thisObj = (EntityPlayer)(Object)this;

        if(NightmareMode.noHit){
            return 0.1f;
        }
        if(NightmareMode.nite){
            return 0.2f * NMUtils.getFoodShanksFromLevel(thisObj) / 60f;
        }
        if(NightmareMode.bloodmare){
            return 0.15f;
        }
        return 0.2f; // punch
    }

    @Inject(method = "onItemUseFinish", at = @At("HEAD"))
    private void manageWaterDrinking(CallbackInfo ci){
        EntityPlayer thisObj = (EntityPlayer)(Object)this;
        if(thisObj.getItemInUse() != null && (thisObj.getItemInUse().itemID == Item.potion.itemID && thisObj.getItemInUse().getItemDamage() == 0)){
            if (this.isBurning()) {
                this.extinguish();
            }
            if(this.isPotionActive(Potion.confusion.id)){
                this.removePotionEffect(Potion.confusion.id);
            }
            if(this.isPotionActive(Potion.blindness.id)){
                this.removePotionEffect(Potion.blindness.id);
            }
            if(this.isPotionActive(Potion.weakness.id)){
                this.removePotionEffect(Potion.weakness.id);
            }
            if(this.isPotionActive(Potion.moveSlowdown.id)){
                this.removePotionEffect(Potion.moveSlowdown.id);
            }
        }
    }

    @Inject(method = "sleepInBedAt", at = @At("HEAD"),cancellable = true)
    private void preventPlayerFromSleepingIfBlocked(int par1, int par2, int par3, CallbackInfoReturnable<EnumStatus> cir){
        if(this.worldObj.getBlockId(par1, par2 + 1, par3) != 0){
            cir.setReturnValue(EnumStatus.OTHER_PROBLEM);
        }
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void manageBlightMovement(CallbackInfo ci){
        if (this.worldObj.getBlockId(MathHelper.floor_double(this.posX),MathHelper.floor_double(this.posY-1),MathHelper.floor_double(this.posZ)) == BTWBlocks.aestheticEarth.blockID && !this.capabilities.isCreativeMode){
            EntityPlayer thisObj = (EntityPlayer)(Object)this;

            int i = MathHelper.floor_double(this.posX);
            int j = MathHelper.floor_double(this.posY-1);
            int k = MathHelper.floor_double(this.posZ);

            if(this.worldObj.getBlockMetadata(i,j,k) == 0){
                this.addPlayerPotionEffect(thisObj,Potion.weakness.id);
            } else if (this.worldObj.getBlockMetadata(i,j,k) == 1){
                this.addPlayerPotionEffect(thisObj,Potion.poison.id);
            } else if (this.worldObj.getBlockMetadata(i,j,k) == 2){
                this.addPlayerPotionEffect(thisObj,Potion.wither.id);
                this.addPlayerPotionEffect(thisObj,Potion.moveSlowdown.id);
            } else if (this.worldObj.getBlockMetadata(i,j,k) == 4){
                this.addPlayerPotionEffect(thisObj,Potion.wither.id);
                this.addPlayerPotionEffect(thisObj,Potion.moveSlowdown.id);
                this.addPlayerPotionEffect(thisObj,Potion.blindness.id);
                this.addPlayerPotionEffect(thisObj,Potion.weakness.id);
            }
        }
    }
    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void manageLeavingGameDuringBloodWitherFight(CallbackInfo ci){
        if(this.ticksExisted % 100 == 0){
            NightmareMode.getInstance().setCanLeaveGame(!EntityBloodWither.isBossActive());
        }
    }
    @Inject(method = "onDeath", at = @At("HEAD"))
    private void manageBloodWitherDeath(DamageSource par1DamageSource, CallbackInfo ci){
        NightmareMode.getInstance().setCanLeaveGame(true);
        EntityBloodWither.setBossActive(false);
    }
    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void managePotionsDuringBloodArmor(CallbackInfo ci){
        Collection activePotions = this.getActivePotionEffects();
        boolean hasWither = false;
        for (Object obj : this.worldObj.loadedEntityList) {
            if (obj instanceof EntityBloodWither) {
                hasWither = true;
                break;
            }
        }
        if(!hasWither){
            NightmareMode.getInstance().setCanLeaveGame(true);
            EntityBloodWither.setBossActive(false);
        }
        if (NMUtils.isWearingFullBloodArmorWithoutSword(this)) {
            for(Object activePotion : activePotions){
                if(activePotion == null) continue;
                PotionEffect tempPotion = (PotionEffect) activePotion;
                tempPotion.duration = Math.max(tempPotion.duration - 1, 0);
            }
        }
        if(this.isPlayerFullyAsleep()){
            this.ticksSleeping ++;
        } else{
            this.ticksSleeping = 0;
        }
        if(NMAchievements.isWearingNoArmor(this)){
            this.noArmorTicks ++;
        } else{
            this.noArmorTicks = 0;
        }
        if(this.ticksExisted % 80 != 0) return;
        EntityPlayer thisObj = (EntityPlayer)(Object)this;

        AchievementEventDispatcher.triggerEvent(NMAchievementEvents.PlayerSleepEvent.class, thisObj, this.ticksSleeping);
        if (this.posY < 16 && this.dimension == 0) {
            AchievementEventDispatcher.triggerEvent(NMAchievementEvents.ArmorLessEvent.class, thisObj, this.noArmorTicks);
        }

        if(NMUtils.getIsEclipse() && !NightmareMode.getInstance().shouldStackSizesIncrease){
            NMUtils.setItemStackSizes(32);
        }
    }
    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void manageHealthOnNoHitAndNite(CallbackInfo ci){
        if(NightmareMode.noHit){
            if(this.getMaxHealth() > 1){
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(1);
                this.getDataWatcher().updateObject(6, 0x1);
            }
        } else if(NightmareMode.nite){
            if(this.getMaxHealth() != this.getHealthForExperience()){
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(this.getHealthForExperience());
            }
        }
    }
    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void manageChainArmor(CallbackInfo ci){
        if(isWearingFullChainArmor(this) && !areChainPotionsActive(this)){
            this.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 110,0));
            if(this.rand.nextInt(16) == 0){
                this.addPotionEffect(new PotionEffect(BTWMod.potionFortune.id, 600, 0));
            }
            if(this.rand.nextInt(16) == 0){
                this.addPotionEffect(new PotionEffect(BTWMod.potionLooting.id, 600, 0));
            }
            if(this.rand.nextInt(16) == 0){
                this.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 110,1));
            } else{
                this.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 110,0));
            }
        }
    }
    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void manageHighPoisonInvincibilityFrames(CallbackInfo ci){
        if(this.isPotionActive(Potion.poison) && this.getActivePotionEffect(Potion.poison).getAmplifier() >= 128){
            this.hurtResistantTime = 0;
        }
    }
    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void manageDarkStormyNight(CallbackInfo ci){
        if (NightmareMode.darkStormyNightmare) {
            this.worldObj.worldInfo.setRaining(true);
            this.worldObj.worldInfo.setThundering(true);
            if (this.worldObj.worldInfo.getThunderTime() < 200) {
                this.worldObj.worldInfo.setThunderTime(2000);
                this.worldObj.worldInfo.setRainTime(2000);
            }
        }
    }

    @Unique private int soundInterval = 30;

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void manageAprilFools(CallbackInfo ci){
        if (NightmareMode.isAprilFools) {
            if(this.ticksExisted % soundInterval == (soundInterval - 1)){
                this.playRandomMobOrItemSound();
            } else{
                return;
            }

            if(this.isPlayerFullyAsleep()){
                int xPos = (int) (this.posX + (this.rand.nextBoolean() ? 1 : -1) * this.rand.nextInt(300));
                int zPos = (int) (this.posZ + (this.rand.nextBoolean() ? 1 : -1) * this.rand.nextInt(300));
                this.wakeUpPlayer(true, true, true);
                this.setPositionAndUpdate(xPos, 250, zPos);
                this.addPotionEffect(new PotionEffect(Potion.resistance.id,300,100));
            }

            if(this.rand.nextInt(32) == 0){
                soundInterval = Math.max(
                        Math.min(
                                soundInterval + (this.rand.nextBoolean() ? 1 : -1),
                                40
                        ),
                        10
                );
            }

            if(this.rand.nextInt(600) == 0){
                EntityPlayer thisObj = (EntityPlayer)(Object)this;

                ItemStack[] hotbar = new ItemStack[9];
                System.arraycopy(thisObj.inventory.mainInventory, 0, hotbar, 0, 9);
                for(int i = 0; i < 9; i++){
                    int j = this.rand.nextInt(i + 1);
                    ItemStack temp = hotbar[i];
                    hotbar[i] = hotbar[j];
                    hotbar[j] = temp;
                }
                System.arraycopy(hotbar, 0, (thisObj).inventory.mainInventory, 0, 9);

                thisObj.worldObj.playSoundEffect(thisObj.posX, thisObj.posY, thisObj.posZ, "mob.endermen.portal", 1.0f, 1.0f);
            }
            // runs once every 30 ticks
            if(this.rand.nextInt(800) == 0){
                EntityWither wither = new EntityWither(this.worldObj);
                wither.setPositionAndUpdate(this.posX + (this.rand.nextBoolean() ? 1 : -1) * (this.rand.nextInt(30)), 150, this.posZ + (this.rand.nextBoolean() ? 1 : -1) * (this.rand.nextInt(30)));
                this.worldObj.spawnEntityInWorld(wither);
            }

            if(this.rand.nextInt(10000) == 0){
                EntityDragon dragon = new EntityDragon(this.worldObj);
                dragon.setHealth(20 + this.rand.nextInt(80));
                dragon.setPositionAndUpdate(this.posX + (this.rand.nextBoolean() ? 1 : -1) * (this.rand.nextInt(30)), 200, this.posZ + (this.rand.nextBoolean() ? 1 : -1) * (this.rand.nextInt(30)));
                this.worldObj.spawnEntityInWorld(dragon);
            }
        }
    }
    @Unique private static final List<String> sounds = new ArrayList<>(Arrays.asList(
            "mob.zombie.say",
            "mob.zombie.hurt",
            "mob.zombie.death",
            "mob.creeper.say",
            "mob.creeper.hurt",
            "mob.creeper.death",
            "mob.spider.say",
            "mob.spider.hurt",
            "mob.spider.death",
            "mob.skeleton.say",
            "mob.skeleton.hurt",
            "mob.skeleton.death",
            "mob.ghast.moan",
            "mob.ghast.scream",
            "mob.ghast.death",
            "mob.enderman.death",
            "mob.enderman.hit",
            "random.break",
            "random.drink",
            "random.eat",
            "random.levelup",
            "random.classic_hurt",
            "liquid.splash",
            "mob.slime.big",
            "random.anvil.land",
            "random.anvil.use",
            "random.anvil.break",
            "mob.ghast.fireball",
            "mob.zombie.wood",
            "mob.zombie.woodbreak",
            "random.fizz",
            "fire.ignite",
            "ambient.cave.cave4",
            "mob.pig.say",
            "mob.pig.death",
            "mob.cow.say",
            "mob.cow.hurt",
            "mob.cow.death",
            "mob.chicken.say",
            "mob.chicken.hurt",
            "random.break"
    ));

    @Unique private void playRandomMobOrItemSound(){
        this.playSound(sounds.get(this.rand.nextInt(sounds.size())), 0.1f + this.rand.nextFloat() * 0.2f, 0.6f + this.rand.nextFloat() * 0.4f);
    }

    @Unique private static boolean areChainPotionsActive(EntityLivingBase player){
        return player.isPotionActive(Potion.digSpeed) || player.isPotionActive(Potion.moveSpeed);
    }
    @Unique private static boolean isWearingFullChainArmor(EntityLivingBase entity){
        for(int i = 1; i < 5; i++){
            if(entity.getCurrentItemOrArmor(i) == null){return false;}
            if(entity.getCurrentItemOrArmor(i).itemID == chainArmor.get(i - 1)) continue;
            return false;
        }
        return true;
    }

    @Unique private void addPlayerPotionEffect(EntityPlayer player, int potionID){
        if(!player.isPotionActive(potionID) || potionID == Potion.blindness.id){
            player.addPotionEffect(new PotionEffect(potionID,81,0));
        }
    }

    @Unique private static final Collection<StatusEffect> collection = List.of(
            PlayerStatusEffects.PECKISH,
            PlayerStatusEffects.STARVING,
            PlayerStatusEffects.EMACIATED,
            PlayerStatusEffects.HUNGRY,
            PlayerStatusEffects.HURT,
            PlayerStatusEffects.INJURED,
            PlayerStatusEffects.STARVING,
            PlayerStatusEffects.WOUNDED,
            PlayerStatusEffects.DYING,
            PlayerStatusEffects.CRIPPLED,
            PlayerStatusEffects.FAMISHED
    );

    @Inject(method = "getAllActiveStatusEffects", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void niteAndNoHitEffectManager(CallbackInfoReturnable<ArrayList<StatusEffect>> cir, ArrayList activeEffects){
        if (NightmareMode.nite || NightmareMode.noHit) {
            activeEffects.removeAll(collection);
        }
    }

    @Inject(method = "dismountEntity", at = @At("TAIL"))
    private void manageHorseDismount(Entity riddenEntity, CallbackInfo ci){
        if(riddenEntity instanceof EntityHorse horse && !horse.isTame()) {
            ((EntityAnimalInvoker)horse).invokeOnNearbyPlayerStartles((EntityPlayer)(Object)this);
            // that's a lot of casting
        }
    }


    @Inject(method = "onLivingUpdate", at = @At("TAIL"))
    private void manageRunningFromPlayer(CallbackInfo ci){
        if (this.worldObj.getDifficulty() == Difficulties.HOSTILE && !this.worldObj.isRemote && this.ticksExisted % 4 == 3) {
            double range = NMUtils.getIsEclipse() ? 3 : 5;

            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(range, range, range));

            for (Object tempEntity : list) {
                if (!(tempEntity instanceof EntityAnimal tempAnimal)) continue;
                if(tempAnimal == this.ridingEntity) continue;
                if (tempAnimal.isSprinting() || tempAnimal instanceof EntityWolf) continue;
                if (NMUtils.getIsMobEclipsed(tempAnimal)) {
                    if(tempAnimal instanceof EntityChicken) continue;
                    if(tempAnimal instanceof EntityPig) continue;
                    if(tempAnimal instanceof EntitySheep) continue;
                }
                boolean isNotSneaking = !this.isSneaking();
                boolean shouldHorseStayClose = tempAnimal instanceof EntityHorse horse
                        && ((this.getHeldItem() != null
                        && this.getHeldItem().itemID == BTWItems.wheat.itemID)
                        || horse.getInLove() > 0
                        || horse.isTame()
                );
                if(shouldHorseStayClose) continue;
                boolean isHoldingItemToRunFrom = this.getHeldItem() != null && itemsToRunFrom.contains(this.getHeldItem().itemID);
                boolean shouldRun = (isNotSneaking || isHoldingItemToRunFrom) && !tempAnimal.getLeashed();

                if (!shouldRun) continue;
                ((EntityAnimalInvoker) tempAnimal).invokeOnNearbyPlayerStartles((EntityPlayer)(Object)this);
                break;
            }
        }
    }



    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void slowIfInWeb(CallbackInfo ci){
        if(this.isInWeb) {
            this.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 10, 3));
            this.addPotionEffect(new PotionEffect(Potion.weakness.id, 10, 1));
        }
    }
//    @Inject(method = "onUpdate", at = @At("TAIL"))
//    private void oxygenLossOnTooHigh(CallbackInfo ci){
//        if(this.posY > 120) {
//            this.setAir(Math.max(this.getAir() - 1, 0));
//        }
//    }
//
//    @Inject(method = "recoverAirSupply", at = @At(value = "HEAD"),cancellable = true)
//    private void oxygenLossOnTooHigh0(CallbackInfo ci){
//        if (this.posY > 120) {
//            ci.cancel();
//        }
//    }
    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void manageSeaOfDeath(CallbackInfo ci){
        if(NightmareMode.bloodmare){
            if (this.isInWater()) {
                this.ticksInWater = Math.min(this.ticksInWater + 1, 30);
                if(this.ticksInWater == 30){
                    this.attackEntityFrom(DamageSource.drown, 1f);
                    this.ticksInWater = 0;
                }
            } else{
                this.ticksInWater = Math.max(this.ticksInWater - 1, 0);
            }
        }
    }
    @Inject(method = "addHarvestBlockExhaustion", at = @At("HEAD"))
    private void manageBlockBrokenAchievements(int iBlockID, int iBlockI, int iBlockJ, int iBlockK, int iBlockMetadata, CallbackInfo ci){
        EntityPlayer self = (EntityPlayer)(Object)this;
        AchievementEventDispatcher.triggerEvent(NMAchievementEvents.BlockBrokenEvent.class, self, new NMAchievementEvents.BlockBrokenEvent.BlockBrokenData(iBlockID, iBlockMetadata));
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void manageAchievementDamageSource(DamageSource source, float par2, CallbackInfoReturnable<Boolean> cir){
        EntityPlayer self = (EntityPlayer)(Object)this;

        AchievementEventDispatcher.triggerEvent(NMAchievementEvents.DamageSourceEvent.class, self, source);
        AchievementEventDispatcher.triggerEvent(NMAchievementEvents.DamageSourcePlayerEvent.class, self, new NMAchievementEvents.DamageSourcePlayerEvent.DamageSourceData(self, source));
    }

    // removes the check for daytime and kicking the player out of the bed if it turns day. this enables infinite sleeping
    @Redirect(method = "sleepInBedAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;isDaytime()Z"))
    private boolean doNotCareIfDay(World instance){
        if (!(Block.blocksList[this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))] instanceof BedrollBlock)) {
            return false;
        } else {
            return this.worldObj.skylightSubtracted < 4;
        }
    }

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;isDaytime()Z"))
    private boolean doNotCareIfDay1(World instance) {
        if (!(Block.blocksList[this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))] instanceof BedrollBlock)) {
            return false;
        } else {
            return this.worldObj.skylightSubtracted < 4;
        }
    }

    @Redirect(method = "sleepInBedAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldProvider;isSurfaceWorld()Z"))
    private boolean canSleepInNether(WorldProvider instance){
        return true;
    }

    @ModifyConstant(method = "movementModifierWhenRidingBoat", constant = @Constant(doubleValue = 0.35))
    private double windmillSpeedBoat(double constant){
        EntityPlayer thisObj = (EntityPlayer)(Object)this;
        if(isPlayerHoldingWindmill(thisObj)){
            return 5.0;
        }
        return constant;
    }


//    @Inject(method = "canHarvestBlock", at = @At("HEAD"),cancellable = true)
//    private void hardCodeSpecificBlocksToBeMinableBySpecificTools(Block block, int x, int y, int z, CallbackInfoReturnable<Boolean> cir){
//        ItemStack held = this.getHeldItem();
//        if (held != null && held.getItem() instanceof ItemBloodPickaxe && block == Block.netherrack) {
//            cir.setReturnValue(true);
//        }
//    }

    @Unique private boolean isPlayerHoldingWindmill(EntityPlayer player) {
        ItemStack currentItemStack = player.inventory.mainInventory[player.inventory.currentItem];
        if (currentItemStack != null) {
            return currentItemStack.itemID == BTWItems.windMill.itemID;
        }
        return false;
    }

    @Unique
    private static final List<Integer> itemsToRunFrom = new ArrayList<>(Arrays.asList(
            Item.axeStone.itemID,
            Item.swordIron.itemID,
            Item.axeIron.itemID,
            Item.swordDiamond.itemID,
            Item.axeDiamond.itemID,
            22580, // bone club
            22568, // wood club
            541, // steel axe
            511, // steel sword
            510, // battleaxe
            NMItems.bloodSword.itemID,
            NMItems.bloodAxe.itemID
    ));
}
