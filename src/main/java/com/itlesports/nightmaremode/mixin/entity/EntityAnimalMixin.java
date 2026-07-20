package com.itlesports.nightmaremode.mixin.entity;

import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.item.items.template.ItemKnife;
import com.itlesports.nightmaremode.util.CarcassHarvesting;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.util.interfaces.CarcassAnimal;
import net.minecraft.src.DamageSource;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityAgeable;
import net.minecraft.src.EntityAnimal;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityAnimal.class)
public abstract class EntityAnimalMixin extends EntityAgeable implements CarcassAnimal {
    @Unique private static final int CARCASS_LIFETIME = 2400;
    @Unique private static final int HARVESTER_WATCHER_ID = 28;
    @Unique private static final int HARVEST_PROGRESS_WATCHER_ID = 29;

    @Unique private int timeOfLastAttack;
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

    public EntityAnimalMixin(World par1World) {
        super(par1World);
    }

    @Override
    public boolean nm$isCarcass() {
        return this.dataWatcher.getWatchableObjectByte(27) != 0;
    }

    @Override
    public int nm$getHarvesterId() {
        return this.dataWatcher.getWatchableObjectInt(HARVESTER_WATCHER_ID);
    }

    @Override
    public int nm$getHarvestProgress() {
        return this.dataWatcher.getWatchableObjectInt(HARVEST_PROGRESS_WATCHER_ID);
    }

    @Override
    public void nm$becomeCarcass(DamageSource source) {

        if (this.nm$isCarcass()) {
            return;
        }

        this.setHealth(1.0F);
        this.dataWatcher.updateObject(27, (byte)1);
        this.dataWatcher.updateObject(HARVESTER_WATCHER_ID, -1);
        this.dataWatcher.updateObject(HARVEST_PROGRESS_WATCHER_ID, 0);
        this.carcassAge = 0;
        this.deathTime = 20;
        this.carcassX = this.posX;
        this.carcassY = this.posY;
        this.carcassZ = this.posZ;
        this.carcassPositionInitialized = true;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.limbSwing = 0; // sets the limb swing initially, but the limbs continue client swinging
        this.limbSwingAmount = 0;
        this.prevLimbSwingAmount = 0;

        if (this.riddenByEntity != null) {
            this.riddenByEntity.mountEntity(null);
        }
        if (this.ridingEntity != null) {
            this.mountEntity(null);
        }

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

        this.lastHarvestHeartbeat = this.ticksExisted;
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

        if (!this.carcassPositionInitialized) {
            this.carcassX = this.posX;
            this.carcassY = this.posY;
            this.carcassZ = this.posZ;
            this.carcassPositionInitialized = true;
        }

        this.deathTime = 20;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.limbSwing = 0.0F;
        this.limbSwingAmount = 0.0F;
        this.prevLimbSwingAmount = 0.0F;
        this.setPosition(this.carcassX, this.carcassY, this.carcassZ);

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
                || this.ticksExisted - this.lastHarvestHeartbeat > 10
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
            CarcassHarvesting.completeHarvest((EntityAnimal)(Object)this, player, this.harvestTier);
            this.nm$spawnCarcassPoof();
            this.setDead();
        }
    }

    @Override
    public void nm$writeCarcassToNBT(NBTTagCompound tag) {
        tag.setBoolean("nmCarcass", this.nm$isCarcass());
        tag.setInteger("nmCarcassAge", this.carcassAge);
    }

    @Override
    public void nm$readCarcassFromNBT(NBTTagCompound tag) {
        if (!tag.getBoolean("nmCarcass")) {
            return;
        }
        this.dataWatcher.updateObject(27, (byte)1);
        this.dataWatcher.updateObject(HARVESTER_WATCHER_ID, -1);
        this.dataWatcher.updateObject(HARVEST_PROGRESS_WATCHER_ID, 0);
        this.carcassAge = tag.getInteger("nmCarcassAge");
        this.deathTime = 20;
        this.setHealth(1.0F);
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

    @Inject(method = "attackEntityFrom", at = @At("HEAD"), cancellable = true)
    private void preventCarcassDamage(DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (this.nm$isCarcass()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isSecondaryTargetForSquid", at = @At("HEAD"),cancellable = true)
    private void squidAvoidAnimalsOnEclipse(CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(!NMUtils.getIsMobEclipsed(this));
    }

    @Inject(method = "updateHealing", at = @At("TAIL"))
    private void manageHealingOverTime(CallbackInfo ci){
        boolean shouldIncreaseHealth = false;
        if (this.worldObj != null && NMUtils.getIsEclipse()) {
            if(this.ticksExisted % 20 == 0 && this.timeOfLastAttack < (this.ticksExisted - 400)){
                shouldIncreaseHealth = true;
            }
        }
        if(shouldIncreaseHealth){
            this.heal(1f);
        }
    }
    @Inject(method = "attackEntityFrom", at = @At("TAIL"))
    private void timeEntityWasRecentlyHit(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        if (NMUtils.getIsEclipse()) {
            this.timeOfLastAttack = this.ticksExisted;
        }
    }
    @Inject(method = "isSubjectToHunger", at = @At("HEAD"), cancellable = true)
    private void nonSubjectToHungerInUnderworld(CallbackInfoReturnable<Boolean> cir){
        if(this.dimension == NMFields.UNDERWORLD_DIMENSION){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "procreate", at = @At("TAIL"))
    private void trackSkillAnimalBreeding(EntityAnimal targetMate, CallbackInfo ci) {
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }

        EntityPlayer player = this.worldObj.getClosestPlayer(this.posX, this.posY, this.posZ, 16.0D);
        SkillHandler.incrementAnimalsBred(player);
    }
}
