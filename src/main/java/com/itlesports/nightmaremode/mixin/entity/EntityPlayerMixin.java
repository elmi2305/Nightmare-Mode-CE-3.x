package com.itlesports.nightmaremode.mixin.entity;

import api.AddonHandler;
import api.achievement.AchievementEventDispatcher;
import api.item.items.ToolItem;
import api.util.status.StatusEffect;
import api.world.data.DataEntry;
import btw.block.BTWBlocks;
import btw.entity.mob.BTWSquidEntity;
import btw.item.BTWItems;
import btw.util.status.BTWPlayerStatuses;
import com.itlesports.nightmaremode.NightmareModeAddon;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.entity.underworld.IFlowerMob;
import com.itlesports.nightmaremode.util.*;
import com.itlesports.nightmaremode.achievements.NMAchievementEvents;
import com.itlesports.nightmaremode.achievements.NMAchievements;
import com.itlesports.nightmaremode.entity.EntityBloodWither;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.ItemOxygenGear;
import com.itlesports.nightmaremode.mixin.interfaces.EntityAnimalInvoker;
import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.skill.SkillTreeData;
import com.itlesports.nightmaremode.util.elements.NMDamageSource;
import com.itlesports.nightmaremode.util.elements.NMDifficultyParam;
import com.itlesports.nightmaremode.util.interfaces.EntityPlayerExt;
import com.itlesports.nightmaremode.util.interfaces.FoodStatsExt;
import com.itlesports.nightmaremode.mixin.interfaces.EntityFireworkRocketAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;

import static btw.community.nightmaremode.NightmareMode.*;
import static com.itlesports.nightmaremode.util.NMFields.*;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLivingBase implements EntityPlayerExt {


    @Shadow public abstract ItemStack getHeldItem();
    @Shadow protected abstract boolean isPlayer();
    @Shadow public PlayerCapabilities capabilities;
    @Shadow public FoodStats foodStats;
    @Shadow public abstract void playSound(String par1Str, float par2, float par3);
    @Shadow public int experienceLevel;
    @Shadow protected abstract int decreaseAirSupply(int iAirSupply);
    @Shadow public abstract boolean isPlayerSleeping();
    @Shadow public abstract void wakeUpPlayer(boolean par1, boolean par2, boolean par3);
    @Shadow public abstract boolean isPlayerFullyAsleep();
    @Shadow public abstract <T> void setData(DataEntry.PlayerDataEntry<T> var1, T var2);


    @Shadow public abstract <T> T getData(DataEntry.PlayerDataEntry<T> var1);
    @Shadow protected abstract void detonateCarriedBlastingOil();
    @Shadow public abstract void addStat(StatBase par1StatBase, int par2);
    @Shadow protected abstract boolean isCarryingBlastingOil();
    @Shadow public abstract ItemStack getCurrentArmor(int par1);

    @Shadow public abstract int getGloomLevel();

    @Unique private int ticksInWater;
    @Unique private int ticksSleeping;
    @Unique private int noArmorTicks;
    @Unique private int blinkLength = 0;
    @Unique private float fear = 0f;
    @Unique private int heartCrackLength = 0;
    @Unique private int drowningUnconsciousTicks = -1;
    @Unique private static final int DROWNING_UNCONSCIOUS_BLINK_LENGTH = 80;
    @Unique private static final int DROWNING_UNCONSCIOUS_DEATH_DELAY = 28;
    ;

    public void nightmareMode$setBlinkLength(int target) {
        if(!this.worldObj.isRemote){
            sendBlinkDurationToClient((EntityPlayerMP) (Object) this, target);
        }
        this.blinkLength = target;
    }

    @Override
    public void nightmareMode$setFear(float targetFear) {
        if(!this.worldObj.isRemote){
            sendTargetFearToClient((EntityPlayerMP) (Object) this, targetFear);
        }
        this.fear = targetFear;
    }

    @Override
    public void nightmareMode$setFoodMax(int targetFood) {
        if(!this.worldObj.isRemote){
            sendFoodToClient((EntityPlayerMP) (Object) this, targetFood);
        }
        ((FoodStatsExt)this.foodStats).nightmareMode$setMaxFoodLevel(targetFood);
    }

    @Override
    public float nightmareMode$getFear() {
        return this.fear;
    }

    public int nightmareMode$getBlinkLength() {
        return this.blinkLength;
    }

    @Override
    public float nightmareMode$getSkillBlockBreakSpeedBonus() {
        return this.nightmareMode$getSkillData().blockBreakSpeedBonus;
    }

    @Override
    public float nightmareMode$getSkillMobLootChanceBonus() {
        return this.nightmareMode$getSkillData().mobLootChanceBonus;
    }

    @Override
    public boolean nightmareMode$canSkillHarvestDiamondOre() {
        return this.nightmareMode$getSkillData().canHarvestDiamondOre;
    }

    @Override
    public boolean nightmareMode$canSkillCureVillagers() {
        return this.nightmareMode$getSkillData().canCureVillagers;
    }

    @Override
    public boolean nightmareMode$doesSkillSlowFoodSpoilage() {
        return this.nightmareMode$getSkillData().foodSpoilsSlower;
    }

    @Unique
    private SkillTreeData nightmareMode$getSkillData() {
        return this.getData(SKILL_TREE);
    }

    public EntityPlayerMixin(World par1World) {
        super(par1World);
    }

    // can't jump if you have slowness
    @Inject(method = "canJump", at = @At("RETURN"), cancellable = true)
    private void cantJumpIfSlowness(CallbackInfoReturnable<Boolean> cir){
        if(this.isPotionActive(Potion.moveSlowdown) && this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)){
            cir.setReturnValue(false);
        }
    }
    @Inject(method = "fall", at = @At("HEAD"))
    private void crushBlocksBelow(float fallDistance, CallbackInfo ci){
        if (this.worldObj.isRemote || fallDistance <= 0.9F) {
            return;
        }

        int yBelow = MathHelper.floor_double(this.boundingBox.minY + 0.01D);
        int minX = MathHelper.floor_double(this.posX - 0.5D);
        int maxX = MathHelper.floor_double(this.posX + 0.5D);
        int minZ = MathHelper.floor_double(this.posZ - 0.5D);
        int maxZ = MathHelper.floor_double(this.posZ + 0.5D);

        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                if (!this.canCrushIronAt(x, yBelow, z) || this.rand.nextInt(8) != 0) {
                    continue;
                }

                this.worldObj.playSound(x + 0.5D, yBelow + 0.5D, z + 0.5D, "random.break", 0.5F, 0.9F);
                this.worldObj.setBlockWithNotify(x, yBelow, z, NMBlocks.blockCrushedIronLayer.blockID);
                return;
            }
        }
    }

    @Unique private boolean canCrushIronAt(int x, int y, int z) {
        return this.worldObj.getBlockId(x, y, z) == BTWBlocks.ironOreChunk.blockID;
    }

    @Inject(method = "interactWith", at = @At("HEAD"))
    private void manageCreativeInteractionRegardingMobEquipment(Entity entityInteractedWith, CallbackInfoReturnable<Boolean> cir)
    {
        if(entityInteractedWith instanceof EntityLivingBase living && this.capabilities.isCreativeMode && !this.worldObj.isRemote){
            if (living instanceof EntityLiving) {
                for(int i = 0; i < 4; i++){
                    ((EntityLiving) living).setEquipmentDropChance(i,1.0f);
                }
            }
            if(this.isSneaking()){
                if(living.getHeldItem() != null){
                    living.entityDropItem(living.getHeldItem(), 1);
                    living.setCurrentItemOrArmor(0, null);

                    if(living instanceof EntitySkeleton skeleton){
                        skeleton.setCombatTask();
                    }

                    return;
                }

                for(int i = 1; i <= 4; i++){
                    ItemStack stack = living.getCurrentItemOrArmor(i);

                    if(stack != null){
                        living.entityDropItem(stack, 1);
                        living.setCurrentItemOrArmor(i, null);
                    }
                }

                return;
            }

            ItemStack held = this.getHeldItem();

            if(held == null){
                return;
            }

            if(held.getItem() instanceof ItemArmor armor){
                int slot = 4 - armor.armorType;

                ItemStack existing = living.getCurrentItemOrArmor(slot);

                if(existing != null){
                    living.entityDropItem(existing, 1);
                }

                living.setCurrentItemOrArmor(slot, held.copy());
                return;
            }

            if(held.getItem() instanceof ToolItem || held.getItem() instanceof ItemSword){
                ItemStack existing = living.getHeldItem();

                if(existing != null && existing.isItemEnchanted()){
                    living.entityDropItem(existing, 1);
                }

                living.setCurrentItemOrArmor(0, held.copy());

                if(living instanceof EntitySkeleton skeleton){
                    skeleton.setCombatTask();
                }

                return;
            }
        }
    }
    @Override
    public boolean attackEntityFrom(DamageSource src, float amount) {
        if (this.isEntityInvulnerable()) {
            return false;
        }
        if (this.capabilities.disableDamage && !src.canHarmInCreative()) {
            return false;
        }
        this.entityAge = 0;
        if (this.getHealth() <= 0.0f) {
            return false;
        }
        EntityPlayer self = (EntityPlayer)(Object)this;

        AchievementEventDispatcher.triggerEvent(NMAchievementEvents.DamageSourceEvent.class, self, src);
        AchievementEventDispatcher.triggerEvent(NMAchievementEvents.DamageSourcePlayerEvent.class, self, new NMAchievementEvents.DamageSourcePlayerEvent.DamageSourceData(self, src));

        if (this.manageDrowningUnconsciousDamage(src, amount)) {
            return false;
        }

        if (this.isPlayerSleeping() && !this.worldObj.isRemote) {
            this.wakeUpPlayer(true, true, false);
        }
        if (src.isDifficultyScaled()) {
            if (this.worldObj.difficultySetting == 0) {
                amount = 0.0f;
            }
            if (this.worldObj.difficultySetting == 1) {
                amount = amount / 2.0f + 1.0f;
            }
            if (this.worldObj.difficultySetting == 3) {
                amount = amount * 3.0f / 2.0f;
            }
        }
        if (amount == 0.0f && !(src.getSourceOfDamage() instanceof EntityThrowable)) {
            return false;
        }
        Entity var3 = src.getEntity();
        if (var3 instanceof EntityArrow && ((EntityArrow)var3).shootingEntity != null) {
            var3 = ((EntityArrow)var3).shootingEntity;
        }
        this.addStat(StatList.damageTakenStat, Math.round(amount * 10.0f));
        if (!this.isDead && this.isCarryingBlastingOil()) {
            this.detonateCarriedBlastingOil();
            return false;
        }
        return super.attackEntityFrom(src, amount);
    }

    @Unique
    private boolean manageDrowningUnconsciousDamage(DamageSource src, float amount) {
        if (src != DamageSource.drown || amount <= 0.0F || this.capabilities.disableDamage) {
            return false;
        }

        if (!this.worldObj.isRemote && this.drowningUnconsciousTicks < 0) {
            this.drowningUnconsciousTicks = 0;
            this.nightmareMode$setBlinkLength(DROWNING_UNCONSCIOUS_BLINK_LENGTH);
            this.setAir(0);
        }

        return this.drowningUnconsciousTicks >= 0;
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void manageDrowningUnconsciousDeath(CallbackInfo ci) {
        if (this.drowningUnconsciousTicks < 0 || this.worldObj.isRemote) {
            return;
        }

        if (!this.isEntityAlive() || this.capabilities.disableDamage) {
            this.drowningUnconsciousTicks = -1;
            return;
        }

        this.setAir(0);
        if (++this.drowningUnconsciousTicks >= DROWNING_UNCONSCIOUS_DEATH_DELAY) {
            this.drowningUnconsciousTicks = -1;
            this.setHealth(0.0F);
            this.onDeath(DamageSource.drown);
        }
    }

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void noHitAttributes(CallbackInfo ci){
        if (noHit) {
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(1);
            this.setHealth(1f);
        } else if(nite){
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(this.getHealthForExperience());
            this.setHealth(this.getHealthForExperience());
        }
    }

    @Unique private byte getHealthForExperience(){
        return (byte) Math.min(MathHelper.floor_double((double) this.experienceLevel / 3) * 2 + 6, 20);
    }

    @Inject(method = "doesStatusPreventSprinting", at = @At("HEAD"),cancellable = true)
    private void allowSprintingOnLowHealth(CallbackInfoReturnable<Boolean> cir){
        if(noHit || nite){
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
        if(isAprilFools){
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
            int chance = 12 - NMUtils.getBloodArmorWornCount(this) * 2;
            // 12, 10, 8, 6, 4

            if(rand.nextInt(chance) == 0){
                this.heal(rand.nextInt(chance + 2) == 0 ? 2 : 1);
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
        if(unkillableMobs){
            return 0f;
        }
        return par2;
    }
    @Inject(method = "attackTargetEntityWithCurrentItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayer;triggerAchievement(Lnet/minecraft/src/StatBase;)V"),locals = LocalCapture.CAPTURE_FAILHARD)
    private void manageAchievementsOnPlayerHit(Entity entityHit, CallbackInfo ci, float dmg){
        EntityPlayer player = (EntityPlayer)(Object)this;
        AchievementEventDispatcher.triggerEvent(NMAchievementEvents.PlayerAttackEvent.class, player, new NMAchievementEvents.PlayerAttackEvent.PlayerAttackEventData(player, entityHit, dmg));
    }
    @Inject(method = "onKillEntity", at = @At("TAIL"))
    private void setBloodWitherDataEntry(EntityLivingBase elb, CallbackInfo ci){
        SkillHandler.incrementMobKill((EntityPlayer)(Object)this, elb);
        if(elb instanceof EntityBloodWither){
            this.setData(DEFEATED_BLOODWITHER, true);
        }
    }


    @Override
    public int getMaxInPortalTime() {
        if(this.capabilities.isCreativeMode){return 0;}
        if(NMUtils.getWorldProgress() > HARDMODE) {return 25;}
        if(NMUtils.getWorldProgress() > PREHARDMODE) {return 40;}
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
        float prog = NMUtils.getWorldProgress() * 0.08f;
        return constant + prog + 0.1f;
    }
    @ModifyConstant(method = "addExhaustionForJump", constant = @Constant(floatValue = 1.0f))
    private float reduceExhaustion1(float constant){
        float prog = NMUtils.getWorldProgress() * 0.2f;
        return constant + prog + 0.5f;
    }
    @ModifyConstant(method = "attackTargetEntityWithCurrentItem", constant = @Constant(floatValue = 0.3f))
    private float reduceExhaustion2(float constant){
        float prog = NMUtils.getWorldProgress() * 0.05f;
        return constant + prog + 0.2f;
    }


    @Inject(method = "onItemUseFinish", at = @At("HEAD"))
    private void manageWaterDrinking(CallbackInfo ci){

        EntityPlayer p = (EntityPlayer)(Object)this;

        if(p.getItemInUse() != null){
            int usedItemID = p.getItemInUse().itemID;

            if(usedItemID == Item.appleGold.itemID && !this.worldObj.isRemote){
                int offset = p.getItemInUse().getItemDamage() == 1 ? 1800 : 600;
                this.setData(APPLE_COOLDOWN, this.worldObj.getTotalWorldTime() + offset);
            }

            if (usedItemID == Item.potion.itemID && p.getItemInUse().getItemDamage() == 0) {
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
    }

    @Inject(method = "sleepInBedAt", at = @At("HEAD"),cancellable = true)
    private void preventPlayerFromSleepingIfBlocked(int par1, int par2, int par3, CallbackInfoReturnable<EnumStatus> cir){
        if(this.worldObj.getBlockId(par1, par2 + 1, par3) != 0){
            cir.setReturnValue(EnumStatus.OTHER_PROBLEM);
        }
    }

    @Unique
    private boolean isInvalidConfig(int[] wConf){
        int[] pConf = NMConfUtils.getClientConfigData();
        return !Arrays.equals(pConf, wConf);
    }

    @Unique
    private boolean isWorldUsingConfigs(int[] wConf){
        int[] nullConf = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        return !Arrays.equals(nullConf, wConf);
    }

    @Unique private void invalidateConfig(){
        if(this.worldObj.isRemote || devMode) return;
        int[] zeroConfigs = new int[NMConfUtils.CONFIG_COUNT];
        Arrays.fill(zeroConfigs, 0);

        for(WorldServer serv : MinecraftServer.getServer().worldServers){
            serv.setData(CONFIGS_CREATED, zeroConfigs);
        }
    }




    @Unique private void checkAndValidateConfig(){

        if(this.worldObj.worldInfo.areCommandsAllowed() || this.capabilities.isCreativeMode || this.capabilities.allowFlying || this.worldObj.worldInfo.getGameType() == EnumGameType.CREATIVE){
            // player is currently cheating
//            System.out.println("XX detecting cheating. wouldn't apply");

            EntityPlayer self = (EntityPlayer)(Object)this;

            if (!this.worldObj.isRemote) {
                ChatMessageComponent text1 = new ChatMessageComponent();
                text1.addKey("world.config.cheating");
                text1.setColor(EnumChatFormatting.DARK_RED);
                self.sendChatToPlayer(text1);

                text1 = new ChatMessageComponent();
                text1.addKey("world.config.progress_wiped");
                text1.setColor(EnumChatFormatting.DARK_RED);
                self.sendChatToPlayer(text1);
            }

            invalidateConfig();
            if (!devMode) {
                return;
            };
        }


        for (NMConfUtils.CONFIG conf : NMConfUtils.confList) {
            if(!conf.isActive()) continue;

            // RT is handled explicitly because it directly modifies the world state and is separate from other configs
            if(realTime){
                if(this.shouldActivate(NMConfUtils.CONFIG.REAL_TIME)){
                    this.completeConfig(NMConfUtils.CONFIG.REAL_TIME);
                    this.spawnConfigCompletionEffects();
                }
                break;
            }

            if(this.shouldActivate(conf)){
                this.completeConfig(conf);
                this.spawnConfigCompletionEffects();
            }
        }
    }

    @Unique private void spawnConfigCompletionEffects(){
        // play the level-up sound as a celebratory cue
        this.playSound("random.levelup", 0.75f, 1.0f);

        // spawn a radial burst of happyVillager and note particles around the player
        double cx = this.posX;
        double cy = this.posY + 1.0;
        double cz = this.posZ;
        int particleCount = 24;

        double angle;
        double radius;
        double ox;
        double oz;
        double vy;

        for (int i = 0; i < particleCount; i++) {
            angle = (2.0 * Math.PI * i) / particleCount;
            radius = 2.0;
            ox = Math.cos(angle) * radius;
            oz = Math.sin(angle) * radius;
            vy = 0.15 + this.rand.nextDouble() * 0.2;

            String particleName = "happyVillager";
            this.worldObj.spawnParticle(particleName, cx + ox, cy - 1.75, cz + oz, ox * 0.1, vy, oz * 0.1);
        }

        for (int i = 0; i < particleCount; i++) {
            angle = (2.0 * Math.PI * i) / particleCount;
            radius = 3.0;
            ox = Math.cos(angle) * radius;
            oz = Math.sin(angle) * radius;
            vy = 0.15 + this.rand.nextDouble() * 0.2;

            String particleName = "note";
            this.worldObj.spawnParticle(particleName, cx + ox, cy - 1.5, cz + oz, ox * 0.1, vy, oz * 0.1);
        }

//        for (int i = 0; i < 8; i++) {
//            double ox = (this.rand.nextDouble() - 0.5) * 0.8;
//            double oz = (this.rand.nextDouble() - 0.5) * 0.8;
//            double vy = 0.3 + this.rand.nextDouble() * 0.3;
//            this.worldObj.playAuxSFX(2004, (int) (cx + ox), (int) (cy + vy), (int) (cz + oz), 0);
//        }
//        for (int i = 0; i < 2; i++) {
//            double ox = (this.rand.nextDouble() - 0.5) * 0.8;
//            double oz = (this.rand.nextDouble() - 0.5) * 0.8;
//            double vy = 0.3 + this.rand.nextDouble() * 0.3;
//            this.worldObj.playAuxSFX(2002, (int) (cx + ox), (int) (cy + vy), (int) (cz + oz), 0);
//        }

//        for (int i = 0; i < 1; i++) {
//            double ox = (this.rand.nextDouble() - 0.5) * 0.8;
//            double oz = (this.rand.nextDouble() - 0.5) * 0.8;
//            double vy = 0.3 + this.rand.nextDouble() * 0.3;
//            this.worldObj.playAuxSFX(2003, (int) (cx + ox), (int) (cy + vy), (int) (cz + oz), 0);
//        }


        // firework explosion
//        System.out.println("launching fireworky");
        if (!this.worldObj.isRemote) {
            this.launchCelebrationFirework(cx, cy + 0.5, cz, 14 /* gold */, 11 /* yellow */);
            this.launchCelebrationFirework(cx + 0.5, cy, cz + 0.5, 9 /* cyan */, 5 /* purple */);
        }
    }

    @Unique private void launchCelebrationFirework(double x, double y, double z, int color1, int color2){
        // build fireworks NBT: burst type (3), two colors, trail + twinkle flags
        NBTTagCompound fireworksTag = new NBTTagCompound();
        fireworksTag.setByte("Flight", (byte) 0);

        NBTTagCompound explosion = new NBTTagCompound();
        explosion.setByte("Type", (byte) 3);  // burst shape
        explosion.setBoolean("Trail", true);
        explosion.setBoolean("Flicker", true);
        // encode two dye colors as packed rgb ints
        explosion.setIntArray("Colors", new int[]{ dyeToRGB(color1), dyeToRGB(color2) });
        explosion.setIntArray("FadeColors", new int[]{ dyeToRGB(color2) });

        NBTTagList explosionList = new NBTTagList();
        explosionList.appendTag(explosion);
        fireworksTag.setTag("Explosions", explosionList);

        NBTTagCompound itemTag = new NBTTagCompound();
        itemTag.setTag("Fireworks", fireworksTag);

        ItemStack fireworkItem = new ItemStack(Item.firework, 1);
        fireworkItem.setTagCompound(itemTag);

        EntityFireworkRocket rocket = new EntityFireworkRocket(this.worldObj, x, y + (this.rand.nextFloat() - 0.5f) * 0.5f, z, fireworkItem);
        rocket.motionY = 0;

        ((EntityFireworkRocketAccessor) rocket).setLifetime(0);
        this.worldObj.spawnEntityInWorld(rocket);
    }

    @Unique private static int dyeToRGB(int dyeMeta){
        return switch (dyeMeta) {
            case 0  -> 0x1D1D21; // black
            case 1  -> 0x832532; // red
            case 2  -> 0x5E7C16; // green
            case 3  -> 0x835432; // brown
            case 4  -> 0x3C44AA; // blue
            case 5  -> 0x8932B8; // purple
            case 6  -> 0x169C9C; // cyan
            case 7  -> 0x9D9D97; // light gray
            case 8  -> 0x474F52; // gray
            case 9  -> 0x00AAAA; // light blue (used as cyan)
            case 10 -> 0x80C71F; // lime
            case 11 -> 0xF9801D; // orange
            case 12 -> 0xF38BAA; // pink
            case 13 -> 0x474F52; // gray
            case 14 -> 0xFFD83D; // gold / yellow
            case 15 -> 0xF9FFFE; // white
            default -> 0xFFFFFF;
        };
    }

    @Unique
    private boolean shouldActivate(NMConfUtils.CONFIG conf) {
        EntityPlayer p = (EntityPlayer)(Object)this;

        if(NMConfUtils.isConfigCompleted(conf)) return false;

        return switch (conf.getClearCondition()) {
            case CLEAR_BLOODMOON -> NMUtils.getWorldProgress() > PREHARDMODE
                    && this.getData(DEFEATED_BM);
            case CLEAR_DRAGON -> NMUtils.getWorldProgress() > POSTWITHER;
            case CLEAR_BW -> NMUtils.getWorldProgress() > POSTWITHER
                    && this.getData(DEFEATED_BLOODWITHER);
            case CLEAR_GLOOM -> this.worldObj.getWorldTime() > 120000;
            case CLEAR_HARDMODE -> NMUtils.getWorldProgress() > PREHARDMODE;
            case CLEAR_WEEK -> this.worldObj.getWorldTime() > 140000;
            case CLEAR_GET_ITEM -> p.inventory.hasItemStack(conf.getItemStack());
            default -> false;
        };
    }


    @Unique private void completeConfig(NMConfUtils.CONFIG conf){
        NMConfUtils.setConfig(conf.getId() - 1, 1);
    }

    @Unique private void addonStuff(){
        if(devMode) return;
        if(AddonHandler.modList.keySet().toString().toLowerCase().contains("xray")){
            if(!this.isPotionActive(Potion.blindness)){
                ChatMessageComponent text2 = new ChatMessageComponent();
                text2.addText("<???> Using Xray mod? How pathetic.");
                text2.setColor(EnumChatFormatting.RED);
                MinecraftServer.getServer().getConfigurationManager().sendChatMsg(text2);
            }
            this.addPotionEffect(new PotionEffect(Potion.blindness.id, 100));
            if(this.rand.nextInt(40) == 0){
                this.worldObj.playSoundEffect(this.posX,this.posY,this.posZ,"mob.wither.death",2.0F,0.905F);
            }
            if(this.worldObj.getWorldTime() % 100 == 99){
                Entity lightningbolt = new EntityLightningBolt(this.worldObj, this.posX, this.posY, this.posZ);
                this.worldObj.addWeatherEffect(lightningbolt);
            }
            if(this.worldObj.getWorldTime() % 400 == 399){
                this.attackEntityFrom(DamageSource.outOfWorld, 200f);
            }
        }
    }
    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void onUpdateHookTail(CallbackInfo ci){
        if(this.heartCrackLength > 0){
            this.heartCrackLength--;
        }
        this.addonStuff();

        if(this.worldObj.isRemote && this.ticksExisted % 2 == 0){
            float fear = this.nightmareMode$getFear();
            float brightness = this.worldObj.getLightBrightness((int) ( this.posX + 0.5f), (int) this.posY, (int) (this.posZ + 0.5f));
            float lightModifier = Math.max(1 - Math.min(brightness * 2, 1), 0.5f); // 0 - 1 float clamped to 0.5f - 1.0f

            if(NMUtils.getIsBloodMoon()){
                this.nightmareMode$setFear(Math.max(0.5f, fear));
            }
            else if(this.dimension == 0){
                if(this.posY < 24){
                    this.nightmareMode$setFear(Math.max(0.25f * lightModifier, fear));
                } else if (this.posY < 48){
                    this.nightmareMode$setFear(Math.max(0.2f * lightModifier, fear));
                } else if(this.posY < 60){
                    this.nightmareMode$setFear(Math.max(0.1f * lightModifier, fear));
                }
            }
            if(this.getGloomLevel() > 0){
                this.nightmareMode$setFear(Math.min(fear + 0.05f, 0.8f));
            }
            if(!this.worldObj.isDaytime()){
                this.nightmareMode$setFear(Math.min(fear + 0.01f, 0.1f));
            }

            // unused. entity giving the player fear is more convenient, since there's very few entities that actually do this. until there are more, this will be unused
//            if (this.ticksExisted % 6 == 0) {
//                Entity closestFear = this.worldObj.findNearestEntityWithinAABB(FearSource.class, this.boundingBox.expand(8,6, 8), this);
//                if(closestFear instanceof FearSource){
//                    double maxFear = ((FearSource) closestFear).getFearForThisEntity(this.posX,this.posY,this.posZ);
//                    System.out.println(maxFear);
//                    if(maxFear > 0){
//                        this.nightmareMode$setFear((float) Math.max(this.nightmareMode$getFear(), Math.min(maxFear,1.0f)));
//                    }
//                }
//            }

            float h;
            float m;
            if((h = this.getHealth()) < (m = this.getMaxHealth() / 2)){
                this.nightmareMode$setFear(Math.max(0.07f * (m - h), fear));
            }
        }

        if ((this.ticksExisted % 128 == 5 || NMConfUtils.isClientUsingHelpConfig()) && !MinecraftServer.getIsServer()) {
            EntityPlayer self = (EntityPlayer)(Object)this;

            boolean client = this.worldObj.isRemote;


            int[] wConf = this.worldObj.getData(CONFIGS_CREATED);
            if(!this.isWorldUsingConfigs(wConf)) return;
            // if the configs were invalidated, this returns early and prevents looping
            if (this.isInvalidConfig(wConf)) {
                //                System.out.println("X player config didn't match world config");
                //                System.out.println(Arrays.toString(wConf) + " | " + Arrays.toString(NMConfUtils.getClientConfigData()));
                if (!client) {
                    ChatMessageComponent text1 = new ChatMessageComponent();
                    text1.addKey("world.config.no_match");
                    text1.setColor(EnumChatFormatting.DARK_RED);
                    self.sendChatToPlayer(text1);

                    text1 = new ChatMessageComponent();
                    text1.addKey("world.config.progress_wiped");
                    text1.setColor(EnumChatFormatting.DARK_RED);
                    self.sendChatToPlayer(text1);
                }

                invalidateConfig();
                if (!devMode) {
                    return;
                }

            } else if (NMConfUtils.isClientUsingHelpConfig()) {
                //                System.out.println("X player is using helpful configs");

                if (!client) {
                    ChatMessageComponent text1 = new ChatMessageComponent();

                    text1.addKey("world.config.using_help");
                    text1.setColor(EnumChatFormatting.DARK_RED);
                    self.sendChatToPlayer(text1);

                    text1 = new ChatMessageComponent();
                    text1.addKey("world.config.progress_wiped");
                    text1.setColor(EnumChatFormatting.DARK_RED);
                    self.sendChatToPlayer(text1);
                }

                invalidateConfig();
                if (!devMode) {
                    return;
                }
            } else if (!this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)) {
                if (!client) {
                    ChatMessageComponent text1 = new ChatMessageComponent();
                    text1.addKey("world.config.relaxed");
                    text1.setColor(EnumChatFormatting.DARK_RED);
                    self.sendChatToPlayer(text1);

                    text1 = new ChatMessageComponent();
                    text1.addKey("world.config.progress_wiped");
                    text1.setColor(EnumChatFormatting.DARK_RED);
                    self.sendChatToPlayer(text1);
                }

                invalidateConfig();
                if (!devMode) {
                    return;
                }
            } else if(this.worldObj.getWorldInfo().getTerrainType() != WorldType.DEFAULT){
                if (!client) {
                    ChatMessageComponent text1 = new ChatMessageComponent();
                    text1.addKey("world.config.world_type");
                    text1.setColor(EnumChatFormatting.DARK_RED);
                    self.sendChatToPlayer(text1);

                    text1 = new ChatMessageComponent();
                    text1.addKey("world.config.progress_wiped");
                    text1.setColor(EnumChatFormatting.DARK_RED);
                    self.sendChatToPlayer(text1);
                }

                invalidateConfig();
                if (!devMode) {
                    return;
                }
            }

            this.checkAndValidateConfig();
        }
        // manage underworld events
        if(this.dimension == UNDERWORLD_DIMENSION && devMode){
            if(this.ticksExisted % 10 == 0){
                // recalculate drain amount every 10 ticks
                drainAmount = this.getSanityDrain();
            }
            this.setData(SANITY, this.getData(SANITY) + drainAmount);

            // sanity damage
            if(this.getSanity() > CRITICAL_SANITY * 1.1d && this.ticksExisted % 20 == 0){
                this.damageEntity(NMDamageSource.insanity, 1);
                if(this.getSanity() >= MAX_SANITY){
                    this.damageEntity(NMDamageSource.insanity, 1);
                }
            }

            if(this.ticksExisted % 20 == 0 && this.isSneaking()){
                System.out.println("current sanity is: " + this.getData(SANITY));
            }
        }




        // manage blight effects
        if((this.ticksExisted & 16) == 0) {
            if (!this.capabilities.isCreativeMode && this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY - 1), MathHelper.floor_double(this.posZ)) == BTWBlocks.aestheticEarth.blockID) {
                EntityPlayer thisObj = (EntityPlayer) (Object) this;

                int i = MathHelper.floor_double(this.posX);
                int j = MathHelper.floor_double(this.posY - 1);
                int k = MathHelper.floor_double(this.posZ);

                if (this.worldObj.getBlockMetadata(i, j, k) == 0) {
                    this.addPlayerPotionEffect(thisObj, Potion.weakness.id);
                } else if (this.worldObj.getBlockMetadata(i, j, k) == 1) {
                    this.addPlayerPotionEffect(thisObj, Potion.poison.id);
                } else if (this.worldObj.getBlockMetadata(i, j, k) == 2) {
                    this.addPlayerPotionEffect(thisObj, Potion.wither.id);
                    this.addPlayerPotionEffect(thisObj, Potion.moveSlowdown.id);
                } else if (this.worldObj.getBlockMetadata(i, j, k) == 4) {
                    this.addPlayerPotionEffect(thisObj, Potion.wither.id);
                    this.addPlayerPotionEffect(thisObj, Potion.moveSlowdown.id);
                    this.addPlayerPotionEffect(thisObj, Potion.blindness.id);
                    this.addPlayerPotionEffect(thisObj, Potion.weakness.id);
                }
            }
        }
    }

    @Unique private int getSanity(){
        return this.getData(SANITY).intValue();
    }
    @Unique private WorldServer getWorldServer(){
        return this.worldObj instanceof WorldServer ? (WorldServer) this.worldObj : null;
        // gets the WorldServer instance for this entity, used for blue moon / dimension checking
    }

    @Unique private double drainAmount;

    @Unique
    public double getSanityDrain() {
        return NMSanityUtils.getSanityDrainPerTick((EntityPlayer)(Object)this);
    }



    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void manageLeavingGameDuringBloodWitherFight(CallbackInfo ci){
        if(this.ticksExisted % 100 == 0){
            getInstance().setCanLeaveGame(!EntityBloodWither.isBossActive());
        }
    }
    @Inject(method = "onDeath", at = @At("HEAD"))
    private void manageBloodWitherDeath(DamageSource par1DamageSource, CallbackInfo ci){
        getInstance().setCanLeaveGame(true);
        EntityBloodWither.setBossActive(false);
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void onUpdateHook(CallbackInfo ci){
//        MonoInvertPostProcessor.INSTANCE.setEnabled(this.isSneaking());

        // tracks achievements
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

        if (this.getPlayerOnSkybase()) {
            skybaseTicks = Math.min(skybaseTicks + 2, 12);
            if(skybaseTicks == 12) {
                AchievementEventDispatcher.triggerEvent(NMAchievementEvents.SkybaseScoreEvent.class, thisObj);
            }
        } else{
            skybaseTicks = Math.max(0,--skybaseTicks);
        }
    }

    @Unique private int skybaseTicks = 0;
    @Unique
    private boolean getPlayerOnSkybase() {
        World world = this.worldObj;
        int px = MathHelper.floor_double(this.posX);
        int pz = MathHelper.floor_double(this.posZ);
        int py = MathHelper.floor_double(this.posY);

        int[][] offsets = {
                {5, 0},
                {-5, 0},
                {0, 5},
                {0, -5}
        };

        int totalHeight = 0;

        for (int[] offset : offsets) {
            int x = px + offset[0] + randOffset(world.rand);
            int z = pz + offset[1] + randOffset(world.rand);
            int height = Math.max(world.getPrecipitationHeight(x, z), 63);

            totalHeight += height;
        }

        int avgHeight = totalHeight / 4;

        return py >= avgHeight + 8;
    }
    @Unique
    private static int randOffset(Random r){return r.nextInt(3) - 1;}

    @Override
    public void nightmareMode$incrementHealth(int amount) {
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(this.getMaxHealth() - 1);
        if(this.getHealth() > this.getMaxHealth()){
            this.setHealth(this.getMaxHealth());
        }
        if (this.getMaxHealth() % 2 == 0) {
            this.nightmareMode$setHeartCrack(2);
            NMUtils.playUISound(NightmareModeAddon.NM_CRACK.sound(), 1.0f, 1.0f);
        }
    }

    @Override
    public boolean nightmareMode$getHeartCrack() {
        return this.heartCrackLength >= 1;
    }

    @Override
    public void nightmareMode$setHeartCrack(int heartCrackLength) {
        if(!this.worldObj.isRemote){
            sendHeartCrackingToPlayer((EntityPlayerMP) (Object) this, heartCrackLength);
        }
        this.heartCrackLength = heartCrackLength;
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void manageHighPoisonInvincibilityFrames(CallbackInfo ci){
        if(this.isPotionActive(Potion.poison) && this.getActivePotionEffect(Potion.poison).getAmplifier() >= 128){
            this.hurtResistantTime = 0;
        }
    }



    @Unique private void addPlayerPotionEffect(EntityPlayer player, int potionID){
        if(!player.isPotionActive(potionID) || potionID == Potion.blindness.id){
            player.addPotionEffect(new PotionEffect(potionID,81,0));
        }
    }

    @Unique private static final Collection<StatusEffect> collection = List.of(
            BTWPlayerStatuses.PECKISH,
            BTWPlayerStatuses.STARVING,
            BTWPlayerStatuses.EMACIATED,
            BTWPlayerStatuses.HUNGRY,
            BTWPlayerStatuses.HURT,
            BTWPlayerStatuses.INJURED,
            BTWPlayerStatuses.STARVING,
            BTWPlayerStatuses.WOUNDED,
            BTWPlayerStatuses.DYING,
            BTWPlayerStatuses.CRIPPLED,
            BTWPlayerStatuses.FAMISHED
    );

    @Inject(method = "getAllActiveStatusEffects", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void niteAndNoHitEffectManager(CallbackInfoReturnable<ArrayList<StatusEffect>> cir, ArrayList activeEffects){
        if (nite || noHit) {
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
        if (this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) && !this.worldObj.isRemote && this.ticksExisted % 10 == 0) {
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
            addWebPotion(Potion.moveSlowdown, 3);
            addWebPotion(Potion.weakness, 0);
        }
    }

    @Unique private void addWebPotion(Potion potion, int amplifier) {
        if(!this.isPotionActive(potion)){
            this.addPotionEffect(new PotionEffect(potion.id,10,amplifier));
        }
    }
    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void manageDeepCaveOxygenLoss(CallbackInfo ci) {
        if (!this.shouldLoseOxygenInDeepCave()) {
            return;
        }

        int drainInterval = this.getDeepCaveOxygenDrainInterval();
        if (drainInterval <= 0 || this.ticksExisted % drainInterval != 0) {
            return;
        }

        this.setAir(this.getAir() - 1);
        if (this.getAir() <= -20) {
            this.setAir(0);
            this.attackEntityFrom(DamageSource.drown, 2.0F);
        }
    }

    @Inject(method = "recoverAirSupply", at = @At(value = "HEAD"), cancellable = true)
    private void preventAirRecoveryInDeepCaves(CallbackInfo ci) {
        if (this.shouldLoseOxygenInDeepCave()) {
            ci.cancel();
        }
    }

    @Unique
    private boolean shouldLoseOxygenInDeepCave() {
        return this.dimension == 0
                && this.posY < 54.0D
                && !this.capabilities.disableDamage
                && this.isEntityAlive()
                && !this.isInsideOfMaterial(Material.water);
    }

    @Unique
    private int getDeepCaveOxygenDrainInterval() {
        double y = Math.max(24.0D, this.posY);
        double depthRatio = Math.max(0.0D, Math.min(1.0D, (54.0D - y) / 30.0D));
        int baseInterval = Math.max(1, (int)Math.round(8.0D - depthRatio * 7.0D));
        float reduction = Math.min(this.getOxygenGearReduction(), 0.8F);
        return Math.max(1, (int)Math.ceil(baseInterval / (1.0F - reduction)));
    }

    @Unique
    private float getOxygenGearReduction() {
        float reduction = 0.0F;
        ItemStack mask = this.getCurrentArmor(3);
        if (mask != null && mask.getItem() instanceof ItemOxygenGear) {
            reduction += ((ItemOxygenGear)mask.getItem()).getOxygenDrainReduction();
        }
        ItemStack tank = this.getCurrentArmor(2);
        if (tank != null && tank.getItem() instanceof ItemOxygenGear) {
            reduction += ((ItemOxygenGear)tank.getItem()).getOxygenDrainReduction();
        }
        return reduction;
    }


    @Override
    protected float applyArmorCalculations(DamageSource damageSource, float damageAmount) {
        if(damageSource.getSourceOfDamage() instanceof IFlowerMob){
            return (damageAmount / 2) + super.applyArmorCalculations(damageSource, damageAmount);
        }
        return super.applyArmorCalculations(damageSource, damageAmount);
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void manageSeaOfDeath(CallbackInfo ci){
        if(bloodmare){
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
//        if(this.ticksExisted % 100 == 0){
//            for(Object ac : TAB_GETTING_STARTED.achievementList){
//                AchievementHandler.triggerAchievement(((EntityPlayer)(Object)this), (Achievement) ac);
//            }
//            for(Object ac : TAB_IRON_AGE.achievementList){
//                AchievementHandler.triggerAchievement(((EntityPlayer)(Object)this), (Achievement) ac);
//            }
//            for(Object ac : TAB_AUTOMATION.achievementList){
//                AchievementHandler.triggerAchievement(((EntityPlayer)(Object)this), (Achievement) ac);
//            }
//            for(Object ac : TAB_END_GAME.achievementList){
//                AchievementHandler.triggerAchievement(((EntityPlayer)(Object)this), (Achievement) ac);
//            }
//            for(Object ac : TAB_EXTRAS.achievementList){
//                AchievementHandler.triggerAchievement(((EntityPlayer)(Object)this), (Achievement) ac);
//            }
//        }
    }


    @Inject(method = "addHarvestBlockExhaustion", at = @At("HEAD"))
    private void manageBlockBrokenAchievements(int iBlockID, int iBlockI, int iBlockJ, int iBlockK, int iBlockMetadata, CallbackInfo ci){
        EntityPlayer self = (EntityPlayer)(Object)this;
        SkillHandler.incrementBlocksMined(self, iBlockID);
        AchievementEventDispatcher.triggerEvent(NMAchievementEvents.BlockBrokenEvent.class, self, new NMAchievementEvents.BlockBrokenEvent.BlockBrokenData(iBlockID, iBlockMetadata));
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
            // BTWItems crashes when attempted to be loaded here
    ));
}
