package com.itlesports.nightmaremode.mixin.entity;

import api.achievement.AchievementEventDispatcher;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.achievements.NMAchievementEvents;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.template.ItemKnife;
import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.util.CarcassHarvesting;
import com.itlesports.nightmaremode.util.interfaces.CarcassAnimal;
import net.minecraft.src.*;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin extends Entity implements CarcassAnimal {
    @Unique private static final int CARCASS_WATCHER_ID = 27;
    @Unique private static final int HARVESTER_WATCHER_ID = 28;
    @Unique private static final int HARVEST_PROGRESS_WATCHER_ID = 29;
    @Unique private static final int CARCASS_LIFETIME = 2400;

    @Unique private int lastTimeWasSnowballed;
    @Unique private EntityPlayer lastSnowBaller;
    @Unique private int carcassAge;
    @Unique private int harvestTicks;
    @Unique private int harvestRequiredTicks;
    @Unique private int harvestTier;
    @Unique private int harvestToolItemId = -1;
    @Unique private int lastHarvestHeartbeat;
    @Unique private double harvestStartX;
    @Unique private double harvestStartY;
    @Unique private double harvestStartZ;
    @Unique private double carcassX;
    @Unique private double carcassY;
    @Unique private double carcassZ;
    @Unique private boolean carcassPositionInitialized;
    @Unique private DamageSource carcassDamageSource;

    @Shadow public abstract boolean isEntityAlive();

    @Shadow public abstract void addPotionEffect(PotionEffect par1PotionEffect);
    @Shadow protected EntityPlayer attackingPlayer;
    @Shadow protected abstract String getDeathSound();
    @Shadow protected abstract float getSoundVolume();
    @Shadow protected abstract int getExperiencePoints(EntityPlayer player);

    public EntityLivingBaseMixin(World par1World) {
        super(par1World);
    }

    @ModifyArg(method = "entityLivingOnDeath", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/EntityLivingBase;dropFewItems(ZI)V"), index = 1)
    private int applySkillMobLootChance(int lootingModifier) {
        if (this.attackingPlayer != null
                && this.rand.nextFloat() < SkillHandler.getPlayerData(this.attackingPlayer).mobLootChanceBonus
                        + SkillHandler.getWorldData(this.worldObj).globalMobLootChanceBonus) {
            return lootingModifier + 1;
        }
        return lootingModifier;
    }

    @Redirect(method = "onDeathUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLivingBase;isChild()Z"))
    private boolean babyZombiesInMvDropTheirItems(EntityLivingBase e){
        if(NightmareMode.moreVariants){
            if(e instanceof EntityZombie) return false; // baby zombies drop items like adult zombies do
        }
        return e.isChild();
    }
    @Inject(method = "<init>", at = @At("TAIL"))
    private void increaseStepHeightSlightlyForAsphalt(World par1World, CallbackInfo ci){
        this.stepHeight = 0.6f;
    }
    @ModifyConstant(method = "getEyeHeight", constant = @Constant(floatValue = 0.85f))
    private float modifyWitherSkeletonSight(float constant){
        EntityLivingBase thisObj = (EntityLivingBase)(Object)this;
        if(this.worldObj == null) return constant;
        if(thisObj instanceof EntitySkeleton skeleton && skeleton.getSkeletonType().id() == 1){
            return 0.6f;
        }
        return constant;
    }
    @ModifyConstant(method = "moveEntityWithHeading", constant = @Constant(doubleValue = 0.2))
    private double modifyLadderClimbRateBasedOnLadder(double constant){
        int blockX = MathHelper.floor_double(this.posX);
        int blockY = MathHelper.floor_double(this.boundingBox.minY);
        int blockZ = MathHelper.floor_double(this.posZ);

        int blockID = this.worldObj.getBlockId(blockX, blockY, blockZ);
        if(blockID == NMBlocks.stoneLadder.blockID){
            return NMBlocks.stoneLadder.getSpeedModifier();
        } else if (blockID == NMBlocks.ironLadder.blockID){
            return NMBlocks.ironLadder.getSpeedModifier();
        }
        return constant;
    }

    @Redirect(method = "updatePotionEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;spawnParticle(Ljava/lang/String;DDDDDD)V"))
    private void removePotionParticlesConfig(World world, String s, double d, double par2, double par4, double par6, double par8, double par10){
        if(NightmareMode.potionParticles){
            world.spawnParticle(s,d,par2,par4,par6,par8,par10);
        }
    }

    @Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
    private void manageBloodMoonKills(DamageSource source, CallbackInfo ci){
        if(source.getEntity() instanceof EntityPlayer player && Objects.equals(source.damageType, "player")){
            if(NMUtils.isWearingFullBloodArmor(player)){
                int chance = NMUtils.getIsBloodMoon() ? 2 : 3;
                if(this.rand.nextInt(chance) == 0){
                    PotionEffect activeStrengthPotion = player.getActivePotionEffect(Potion.damageBoost);
                    if(activeStrengthPotion != null && activeStrengthPotion.getAmplifier() == 0){

                        if(rand.nextInt(5) == 0 && activeStrengthPotion.getDuration() <= 100){
                            this.addPotion(player,Potion.damageBoost.id, 1);
                        }

                        this.addPotion(player,Potion.regeneration.id, 0);
                    } else {
                        this.addPotion(player, Potion.damageBoost.id, 0);
                    }
                }
                player.heal(rand.nextInt(2)+1);
            }
            if(NMUtils.isHoldingBloodSword(player)){
                player.getHeldItem().setItemDamage(Math.max(player.getHeldItem().getItemDamage() - this.rand.nextInt(4) - 1, 0));
                this.mendTools(player);
                if (this.rand.nextInt(24) == 0) {
                    this.dropItem(NMItems.bloodOrb.itemID,1);
                }
                this.increaseArmorDurabilityRandomly(player);
            }
        }
        EntityLivingBase thisObj = (EntityLivingBase)(Object)this;
        if (NMUtils.canBecomeCarcass(thisObj) && NMUtils.isDamageSourceAllowedToCreateCarcass(thisObj, source)) {
            this.nm$becomeCarcass(source);
            ci.cancel();
        }
    }

    @Inject(method = "entityInit", at = @At("TAIL"))
    private void addCarcassWatchers(CallbackInfo ci) {
        if (NMUtils.canBecomeCarcass((EntityLivingBase)(Object)this)) {
            this.dataWatcher.addObject(CARCASS_WATCHER_ID, (byte)0);
            this.dataWatcher.addObject(HARVESTER_WATCHER_ID, -1);
            this.dataWatcher.addObject(HARVEST_PROGRESS_WATCHER_ID, 0);
        }
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void tickAnimalCarcass(CallbackInfo ci) {
        this.nm$tickCarcass();
    }

    @Inject(method = "moveEntityWithHeading", at = @At("HEAD"), cancellable = true)
    private void preventCarcassMovement(float strafe, float forward, CallbackInfo ci) {
        if (this.nm$isCarcass()) {
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
            ci.cancel();
        }
    }

    @Inject(method = "writeEntityToNBT", at = @At("TAIL"))
    private void writeCarcassData(NBTTagCompound tag, CallbackInfo ci) {
        this.nm$writeCarcassToNBT(tag);
    }

    @Inject(method = "readEntityFromNBT", at = @At("TAIL"))
    private void readCarcassData(NBTTagCompound tag, CallbackInfo ci) {
        this.nm$readCarcassFromNBT(tag);
    }

    @Inject(method = "handleHealthUpdate", at = @At("HEAD"), cancellable = true)
    private void handleCarcassPoof(byte state, CallbackInfo ci) {
        if (state == 3 && this.nm$isCarcass()) {
            ci.cancel();
        } else if (state == 30 && this.nm$isCarcass()) {
            this.nm$spawnCarcassPoof();
            ci.cancel();
        }
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"), cancellable = true)
    private void preventCarcassDamage(DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (this.nm$isCarcass()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isEntityAlive", at = @At("HEAD"), cancellable = true)
    private void reportCarcassAsDead(CallbackInfoReturnable<Boolean> cir) {
        if (this.nm$isCarcass()) {
            cir.setReturnValue(false);
        }
    }

    @Override
    public boolean nm$isCarcass() {
        EntityLivingBase self = (EntityLivingBase)(Object)this;
        return NMUtils.canBecomeCarcass(self)
                && this.dataWatcher.getWatchableObjectByte(CARCASS_WATCHER_ID) != 0;
    }

    @Override
    public int nm$getHarvesterId() {
        return this.nm$isCarcass() ? this.dataWatcher.getWatchableObjectInt(HARVESTER_WATCHER_ID) : -1;
    }

    @Override
    public int nm$getHarvestProgress() {
        return this.nm$isCarcass() ? this.dataWatcher.getWatchableObjectInt(HARVEST_PROGRESS_WATCHER_ID) : 0;
    }

    @Override
    public void nm$becomeCarcass(DamageSource source) {
        EntityLivingBase self = (EntityLivingBase)(Object)this;
        if (this.nm$isCarcass() || !NMUtils.canBecomeCarcass(self)) {
            return;
        }

        // Dismount before changing any carcass state. In particular, a headcrab
        // may cause this transition from inside its own damage update.
        if (this.riddenByEntity != null) {
            this.riddenByEntity.mountEntity(null);
        }
        if (this.ridingEntity != null) {
            this.mountEntity(null);
        }

        self.setHealth(1.0F);
        this.dataWatcher.updateObject(CARCASS_WATCHER_ID, (byte)1);
        this.dataWatcher.updateObject(HARVESTER_WATCHER_ID, -1);
        this.dataWatcher.updateObject(HARVEST_PROGRESS_WATCHER_ID, 0);
        this.carcassDamageSource = source;
        this.carcassAge = 0;
        self.deathTime = 20;
        this.carcassX = this.posX;
        this.carcassY = this.posY;
        this.carcassZ = this.posZ;
        this.carcassPositionInitialized = true;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        self.limbSwing = 0.0F;
        self.limbSwingAmount = 0.0F;
        self.prevLimbSwingAmount = 0.0F;

        if (!this.worldObj.isRemote) {
            String deathSound = this.getDeathSound();
            if (deathSound != null) {
                this.worldObj.playSoundAtEntity(this, deathSound, this.getSoundVolume(), 1.45F);
            }
        }
    }

    @Override
    public void nm$continueHarvest(EntityPlayer player) {
        if (this.worldObj.isRemote || !this.nm$isCarcass() || player == null
                || player.getDistanceSqToEntity(this) > 25.0D) {
            return;
        }

        int harvesterId = this.nm$getHarvesterId();
        if (harvesterId == -1) {
            ItemStack held = player.getHeldItem();
            if (!CarcassHarvesting.isValidTool(held)) {
                return;
            }

            this.harvestTier = CarcassHarvesting.getHarvestTier(held);
            this.harvestRequiredTicks = CarcassHarvesting.getProcessingTicks(held);
            this.harvestToolItemId = held == null ? -1 : held.itemID;
            this.harvestTicks = 0;
            this.harvestStartX = player.posX;
            this.harvestStartY = player.posY;
            this.harvestStartZ = player.posZ;
            this.dataWatcher.updateObject(HARVESTER_WATCHER_ID, player.entityId);
            this.dataWatcher.updateObject(HARVEST_PROGRESS_WATCHER_ID, 0);
        } else if (harvesterId != player.entityId) {
            return;
        }

        this.lastHarvestHeartbeat = this.carcassAge;
    }

    @Override
    public void nm$cancelHarvest(EntityPlayer player) {
        if (!this.worldObj.isRemote && player != null && this.nm$getHarvesterId() == player.entityId) {
            this.nm$resetHarvest();
        }
    }

    @Override
    public void nm$tickCarcass() {
        if (!this.nm$isCarcass()) {
            return;
        }

        EntityLivingBase self = (EntityLivingBase)(Object)this;
        if (!this.carcassPositionInitialized) {
            this.carcassX = this.posX;
            this.carcassY = this.posY;
            this.carcassZ = this.posZ;
            this.carcassPositionInitialized = true;
        }

        self.deathTime = 20;
        this.motionX = 0.0D;
        this.motionZ = 0.0D;
        self.limbSwing = 0.0F;
        self.limbSwingAmount = 0.0F;
        self.prevLimbSwingAmount = 0.0F;
        this.setPosition(this.carcassX, this.posY, this.carcassZ);
        this.nm$applyCarcassGravity();
        this.carcassY = this.posY;

        if (this.worldObj.isRemote) {
            return;
        }

        ++this.carcassAge;
        if (this.carcassAge % 40 == 0) {
            String deathSound = this.getDeathSound();
            if (deathSound != null) {
                this.worldObj.playSoundAtEntity(this, deathSound, this.getSoundVolume(), 1.6F + this.rand.nextFloat() * 0.2F);
            }
        }

        if (this.carcassAge >= CARCASS_LIFETIME) {
            this.nm$spawnCarcassPoof();
            this.setDead();
            return;
        }

        int harvesterId = this.nm$getHarvesterId();
        if (harvesterId == -1) {
            return;
        }

        Entity entity = this.worldObj.getEntityByID(harvesterId);
        if (!(entity instanceof EntityPlayer player)
                || this.carcassAge - this.lastHarvestHeartbeat > 10
                || player.getDistanceSqToEntity(this) > 25.0D
                || this.nm$playerMoved(player)
                || !this.nm$isHoldingStartedTool(player)) {
            this.nm$resetHarvest();
            return;
        }

        ++this.harvestTicks;
        int progress = Math.min(1000, this.harvestTicks * 1000 / this.harvestRequiredTicks);
        if (progress != this.nm$getHarvestProgress()) {
            this.dataWatcher.updateObject(HARVEST_PROGRESS_WATCHER_ID, progress);
        }

        if (this.harvestTicks >= this.harvestRequiredTicks) {
            DamageSource source = this.carcassDamageSource == null ? DamageSource.generic : this.carcassDamageSource;
            CarcassHarvesting.completeHarvest(self, player, this.harvestTier, source);
            player.addExperience(Math.max(0, this.getExperiencePoints(player)));
            this.nm$spawnCarcassPoof();
            this.setDead();
        }
    }

    @Override
    public void nm$writeCarcassToNBT(NBTTagCompound tag) {
        if (!NMUtils.canBecomeCarcass((EntityLivingBase)(Object)this)) {
            return;
        }
        tag.setBoolean("nmCarcass", this.nm$isCarcass());
        tag.setInteger("nmCarcassAge", this.carcassAge);
    }

    @Override
    public void nm$readCarcassFromNBT(NBTTagCompound tag) {
        EntityLivingBase self = (EntityLivingBase)(Object)this;
        if (!NMUtils.canBecomeCarcass(self) || !tag.getBoolean("nmCarcass")) {
            return;
        }
        this.dataWatcher.updateObject(CARCASS_WATCHER_ID, (byte)1);
        this.dataWatcher.updateObject(HARVESTER_WATCHER_ID, -1);
        this.dataWatcher.updateObject(HARVEST_PROGRESS_WATCHER_ID, 0);
        this.carcassAge = tag.getInteger("nmCarcassAge");
        this.carcassDamageSource = DamageSource.generic;
        self.deathTime = 20;
        self.setHealth(1.0F);
        this.carcassX = this.posX;
        this.carcassY = this.posY;
        this.carcassZ = this.posZ;
        this.carcassPositionInitialized = true;
    }

    @Override
    public void nm$spawnCarcassPoof() {
        if (!this.worldObj.isRemote) {
            this.worldObj.setEntityState(this, (byte)30);
            return;
        }
        for (int i = 0; i < 20; ++i) {
            double x = this.posX + (this.rand.nextDouble() - 0.5D) * this.width;
            double y = this.posY + this.rand.nextDouble() * this.height;
            double z = this.posZ + (this.rand.nextDouble() - 0.5D) * this.width;
            this.worldObj.spawnParticle("explode", x, y, z, 0.0D, 0.03D, 0.0D);
        }
    }

    @Unique
    private void nm$applyCarcassGravity() {
        if (this.onGround) {
            this.motionY = -0.08D;
        } else {
            this.motionY -= 0.08D;
        }
        this.moveEntity(0.0D, this.motionY, 0.0D);
        if (this.onGround) {
            this.motionY = 0.0D;
        } else {
            this.motionY *= 0.98D;
        }
    }

    @Unique
    private boolean nm$playerMoved(EntityPlayer player) {
        double x = player.posX - this.harvestStartX;
        double y = player.posY - this.harvestStartY;
        double z = player.posZ - this.harvestStartZ;
        return x * x + y * y + z * z > 0.0025D;
    }

    @Unique
    private boolean nm$isHoldingStartedTool(EntityPlayer player) {
        ItemStack held = player.getHeldItem();
        if (this.harvestToolItemId == -1) {
            return held == null;
        }
        ItemKnife knife = ItemKnife.fromStack(held);
        return held != null && held.itemID == this.harvestToolItemId
                && knife != null && knife.getHarvestTier() == this.harvestTier;
    }

    @Unique
    private void nm$resetHarvest() {
        this.harvestTicks = 0;
        this.harvestRequiredTicks = 0;
        this.harvestTier = ItemKnife.TIER_FISTS;
        this.harvestToolItemId = -1;
        this.dataWatcher.updateObject(HARVESTER_WATCHER_ID, -1);
        this.dataWatcher.updateObject(HARVEST_PROGRESS_WATCHER_ID, 0);
    }

    @Unique private void increaseArmorDurabilityRandomly(EntityLivingBase player){
        int j = rand.nextInt(5);
        for (int a = 0; a < 2; a++) {
            int i = rand.nextInt(4) + 1;
            if(player.getCurrentItemOrArmor(i) == null) continue;
            player.getCurrentItemOrArmor(i).setItemDamage(Math.max(player.getCurrentItemOrArmor(i).getItemDamage() - j,0));
        }
    }


    @Unique private void addPotion(EntityLivingBase entity, int potionID, int amp){
        entity.addPotionEffect(new PotionEffect(potionID, 100, amp));
    }
    @Unique private void mendTools(EntityPlayer player){
        for(ItemStack stack : player.inventory.mainInventory){
            if (stack == null) continue;
            if (stack.getMaxStackSize() != 1) continue;
            if(NMUtils.bloodTools.contains(stack.itemID)){
                if(rand.nextInt(4) == 0) continue;
                this.increaseDurabilityIfPossible(stack);
            }
        }
    }

    @Unique private void increaseDurabilityIfPossible(ItemStack stack){
        int i = rand.nextInt(4) + 1;
        stack.setItemDamage(Math.max(stack.getItemDamage() - i,0));
    }



}
