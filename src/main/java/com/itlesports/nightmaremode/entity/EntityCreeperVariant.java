package com.itlesports.nightmaremode.entity;

import api.entity.EntityWithCustomPacket;
import api.entity.mob.KickingAnimal;
import api.entity.mob.behavior.SimpleWanderBehavior;
import api.world.difficulty.DifficultyParam;
import btw.community.nightmaremode.NightmareMode;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.AITasks.EntityAICreeperVariantSwell;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.NMDifficultyParam;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Random;

public class EntityCreeperVariant extends EntityMob implements EntityWithCustomPacket {
    private boolean determinedToExplode = false;
    private int lastActiveTime;
    private int timeSinceIgnited;
    public int fuseTime = 30;
    private int explosionRadius = 3;
    private byte patienceCounter = (byte)100;

    // unique
    protected int variantType; // must be overridden by children. this is responsible for the spawn packet, so it must match in the packet register too. corresponds to the CREEPER_[TYPE] fields in NMFields
    protected float soundPitchModifier; // different for every creeper variant
    private boolean isValidForEventLoot; // used for every mob

    public EntityCreeperVariant(World par1World) {
        super(par1World);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAICreeperVariantSwell(this));
        this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityOcelot.class, 6.0f, 1.0, 1.2));
        this.tasks.addTask(4, new EntityAIAttackOnCollide(this, 1.0, false));
        this.tasks.addTask(5, new SimpleWanderBehavior(this, 0.8f));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
        if (par1World != null && ((Boolean)par1World.getDifficultyParameter(DifficultyParam.CanCreepersBreachWalls.class)).booleanValue()) {
            this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, false));
        }

//        if (this.variantType == NMFields.CREEPER_OBSIDIAN) {
//            this.setFuseTime(60);
//        } else if (this.variantType == NMFields.CREEPER_SUPERCRITICAL) {
//            this.setFuseTime(15);
//        } else if (this.variantType == NMFields.CREEPER_LIGHTNING) {
//            this.setFuseTime(90 - NMUtils.getWorldProgress() * 10);
//        }

        NMUtils.manageEclipseChance(this,12);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        double followDistance = 16.0;
        if (this.worldObj != null) {
            followDistance *= (double)((Float)this.worldObj.getDifficultyParameter(DifficultyParam.CreeperFollowDistanceMultiplier.class)).floatValue();
        }
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(followDistance);

        int progress = NMUtils.getWorldProgress();
        double bloodMoonModifier = NMUtils.getIsBloodMoon() ? 1.25 : 1;
        int eclipseModifier = NMUtils.getIsMobEclipsed(this) ? 20 : 0;
        boolean isHostile = this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class);

        if (this.rand.nextInt(NMUtils.divByNiteMultiplier(8 - progress * 2, 2)) == 0 && isHostile) {
            this.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 10000000,0));
        }
        if (this.rand.nextInt(NMUtils.divByNiteMultiplier(3, 2)) == 0 && eclipseModifier > 1) {
            this.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 10000000,0));
        }
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(((20 + progress * 6) * bloodMoonModifier + eclipseModifier) * NMUtils.getNiteMultiplier());
        // 20 -> 26 -> 32 -> 38
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute((0.28 + eclipseModifier * 0.005) * ((((NMUtils.getNiteMultiplier() - 1) / 20)) + 1));
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    public int getMaxSafePointTries() {
        return this.getAttackTarget() == null ? 3 : 3 + (int)(this.getHealth() - 1.0f);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, (byte)-1);
        this.dataWatcher.addObject(17, (byte)shouldSpawnCharged());
        this.dataWatcher.addObject(25, (byte)0);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);
        if (this.dataWatcher.getWatchableObjectByte(17) == 1) {
            par1NBTTagCompound.setBoolean("powered", true);
        }
        par1NBTTagCompound.setShort("Fuse", (short)this.fuseTime);
        par1NBTTagCompound.setByte("ExplosionRadius", (byte)this.explosionRadius);
        par1NBTTagCompound.setByte("fcNeuteredState", (byte)this.getNeuteredState());
        par1NBTTagCompound.setShort("timeSinceIgnited", (short)this.timeSinceIgnited);
        par1NBTTagCompound.setByte("patienceCounter", this.patienceCounter);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
        this.dataWatcher.updateObject(17, (byte)(par1NBTTagCompound.getBoolean("powered") ? 1 : 0));
        if (par1NBTTagCompound.hasKey("Fuse")) {
            this.fuseTime = par1NBTTagCompound.getShort("Fuse");
        }
        if (par1NBTTagCompound.hasKey("ExplosionRadius")) {
            this.explosionRadius = par1NBTTagCompound.getByte("ExplosionRadius");
        }
        if (par1NBTTagCompound.hasKey("fcNeuteredState")) {
            this.setNeuteredState(par1NBTTagCompound.getByte("fcNeuteredState"));
        }
        if (par1NBTTagCompound.hasKey("timeSinceIgnited")) {
            this.timeSinceIgnited = par1NBTTagCompound.getShort("timeSinceIgnited");
        }
        if (par1NBTTagCompound.hasKey("patienceCounter")) {
            this.patienceCounter = par1NBTTagCompound.getByte("patienceCounter");
        }
    }

    @Override
    public void onUpdate() {
        if (this.isEntityAlive()) {
            if (NightmareMode.isAprilFools && this.getAttackTarget() instanceof EntityPlayer player && this.getDistanceSqToEntity(player) < 81) {
                Vec3 lookVec = player.getLookVec(); // Player's look direction

                Vec3 directionToSquid = Vec3.createVectorHelper(
                        this.posX - player.posX,
                        this.posY - (player.posY + player.getEyeHeight()), // From eye level
                        this.posZ - player.posZ
                ).normalize(); // Normalize to get direction

                double dotProduct = lookVec.dotProduct(directionToSquid); // How aligned the vectors are

                if (dotProduct < -0.2) {
                    this.copyLocationAndAnglesFrom(player);
                }
            }


            this.lastActiveTime = this.timeSinceIgnited;
            int var1 = this.getCreeperState();
            if (var1 > 0 && this.timeSinceIgnited == 0) {
                this.playSound("random.fuse", 1.0f, 0.5f + soundPitchModifier);
            }
            if (((Boolean)this.worldObj.getDifficultyParameter(DifficultyParam.CanCreepersBreachWalls.class)).booleanValue() && this.variantType != NMFields.CREEPER_LIGHTNING) {

                if (this.getAttackTarget() == null) {
                    if (this.worldObj.rand.nextInt(20) == 0) {
                        this.patienceCounter = (byte)Math.min(this.patienceCounter + 1, 100);
                    }
                } else if (this.getDistanceSqToEntity(this.getAttackTarget()) < getCreeperBreachRange(36.0) && !this.canEntityBeSeen(this.getAttackTarget()) && this.getNavigator().noPath()) {
                    this.patienceCounter = (byte)Math.max(this.patienceCounter - 1, 0);
                }
                if (this.patienceCounter == 0) {
                    this.determinedToExplode = true;
                }
            }
            this.timeSinceIgnited += var1;
            if (this.timeSinceIgnited < 0) {
                this.timeSinceIgnited = 0;
            }
            if (this.timeSinceIgnited >= this.fuseTime) {
                this.timeSinceIgnited = this.fuseTime;
                if (!this.worldObj.isRemote) {

                    if(this.variantType == NMFields.CREEPER_LIGHTNING){
                        EntityPlayer player;
                        if(this.getAttackTarget() instanceof EntityPlayer targetPlayer){
                            player = targetPlayer;
                        } else {
                            player = this.worldObj.getClosestPlayerToEntity(this, 16);
                        }

                        if (player != null) {
                            int worldState = NMUtils.getWorldProgress();
                            double xOffset = (this.rand.nextFloat() * (3 - worldState)) / 4 * (this.rand.nextBoolean() ? 1 : -1);
                            double zOffset = (this.rand.nextFloat() * (3 - worldState)) / 4 * (this.rand.nextBoolean() ? 1 : -1);
                            Entity lightningbolt = new EntityLightningBolt(this.worldObj, player.posX + xOffset, player.posY - 0.5, player.posZ + zOffset);
                            this.worldObj.addWeatherEffect(lightningbolt);
                        }
                        this.setDead();
                        super.onUpdate();
                        return;
                    }


                    boolean var2 = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
                    if (this.getPowered()) {
                        this.worldObj.createExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, this.explosionRadius * 2, var2);
                    } else {
                        this.worldObj.createExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, this.explosionRadius, var2);
                    }

                    if(NightmareMode.isAprilFools){
                        this.worldObj.spawnEntityInWorld(new EntityTNTPrimed(this.worldObj,this.posX,this.posY,this.posZ,this));
                    }

                    if(this.variantType == NMFields.CREEPER_DUNG){
                        int amount = NightmareMode.isAprilFools ? 12 : 4;
                        for(int i = 0; i < amount; i++){
                            spawnItemExplosion(this.worldObj,this, new ItemStack(BTWItems.dung), amount / 4,this.rand);
                        }
                    }

                    this.setDead();
                }
            }
        }
        super.onUpdate();
    }
    @Override
    public boolean isImmuneToHeadCrabDamage() {
        return true;
    }

    @Override
    public boolean isSecondaryTargetForSquid() {
        return NMUtils.getIsBloodMoon() && isCharged() && !this.inWater;
    }

    @Override
    public Entity getHeadCrabSharedAttackTarget() {
        return this.getAttackTarget();
    }

    @Override
    protected String getHurtSound() {
        return "mob.creeper.say";
    }

    @Override
    protected String getDeathSound() {
        return "mob.creeper.death";
    }

    @Override
    public void onDeath(DamageSource par1DamageSource) {
        super.onDeath(par1DamageSource);
        if (par1DamageSource.getEntity() instanceof EntitySkeleton && this.getNeuteredState() == 0) {
            int var2 = Item.record13.itemID + this.rand.nextInt(Item.recordWait.itemID - Item.record13.itemID + 1);
            this.dropItem(var2, 1);
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity par1Entity) {
        return true;
    }

    public boolean getPowered() {
        return this.dataWatcher.getWatchableObjectByte(17) == 1;
    }

    public float getCreeperFlashIntensity(float par1) {
        return ((float)this.lastActiveTime + (float)(this.timeSinceIgnited - this.lastActiveTime) * par1) / (float)(this.fuseTime - 2);
    }

    @Override
    protected int getDropItemId() {
        return BTWItems.nitre.itemID;
    }

    public int getCreeperState() {
        return this.dataWatcher.getWatchableObjectByte(16);
    }

    public void setCreeperState(int par1) {
        this.dataWatcher.updateObject(16, (byte)par1);
    }

    @Override
    public boolean attackEntityFrom(DamageSource src, float amount) {
        this.isValidForEventLoot = src.getEntity() instanceof EntityPlayer;

        if (NMUtils.getIsBloodMoon() && src == DamageSource.drown) {
            return false;
        }

        if (shouldDetonateOnFire(src)){
            this.onKickedByAnimal(null); // primes the creeper instantly. lightning creeper overrides and skips this
        }

        if (((Boolean)this.worldObj.getDifficultyParameter(DifficultyParam.CanCreepersBreachWalls.class)).booleanValue() && src.isExplosion()) {
            amount /= 5.0f;
        }
        return super.attackEntityFrom(src, amount);
    }

    @Override
    protected void dropHead() {
        this.entityDropItem(new ItemStack(Item.skull.itemID, 1, 4), 0.0f);
    }

    @Override
    protected void dropFewItems(boolean bKilledByPlayer, int iLootingModifier) {
        super.dropFewItems(bKilledByPlayer, iLootingModifier);
        // ghast tear for charged
        if(isCharged()) {
            this.dropItem(Item.ghastTear.itemID, 1);
            if (rand.nextInt(3) == 0) {
                this.dropItem(BTWItems.creeperOysters.itemID, 1);
            }
        }
        // eclipse drops
        if (bKilledByPlayer && NMUtils.getIsMobEclipsed(this) && isValidForEventLoot) {
            for(int i = 0; i < (iLootingModifier * 2) + 1; i++) {
                if (this.rand.nextInt(8) == 0) {
                    this.dropItem(NMItems.darksunFragment.itemID, 1);
                    if (this.rand.nextBoolean()) {
                        break;
                    }
                }
            }

            int itemID = NMItems.sulfur.itemID;

            int var4 = this.rand.nextInt(3);
            if (iLootingModifier > 0) {
                var4 += this.rand.nextInt(iLootingModifier + 1);
            }
            for (int var5 = 0; var5 < var4; ++var5) {
                if(this.rand.nextInt(3) == 0) continue;
                this.dropItem(itemID, 1);
            }
        }

        // blood orb drops
        int bloodOrbID = NMUtils.getIsBloodMoon() ? NMItems.bloodOrb.itemID : 0;
        if (bloodOrbID > 0 && bKilledByPlayer && isValidForEventLoot) {
            int var4 = this.rand.nextInt(3);
            // 0 - 2
            if (iLootingModifier > 0) {
                var4 += this.rand.nextInt(iLootingModifier + 1);
            }
            for (int var5 = 0; var5 < var4; ++var5) {
                this.dropItem(bloodOrbID, 1);
            }
        }

        if (this.getNeuteredState() == 0 && (this.rand.nextInt(3) == 0 || this.rand.nextInt(1 + iLootingModifier) > 0)) {
            this.dropItem(BTWItems.creeperOysters.itemID, 1);
        }
    }

    @Override
    public boolean interact(EntityPlayer player) {
        ItemStack playersCurrentItem = player.inventory.getCurrentItem();
        boolean isHostile = this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class);
        float bloodMoonModifier = NMUtils.getIsBloodMoon() ? 1.25f : 1;

        if (playersCurrentItem != null && playersCurrentItem.getItem() instanceof ItemShears && this.getNeuteredState() == 0) {
            if (!this.worldObj.isRemote && !this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)) {
                if (isHostile || playersCurrentItem.getItem().itemID == Item.shears.itemID) {
                    boolean var2 = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
                    if (this.getPowered()) {
                        this.worldObj.createExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, 8 * bloodMoonModifier, var2);
                    } else {
                        this.worldObj.createExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, 3 * bloodMoonModifier, var2);
                    }
                    if (!NMUtils.getIsMobEclipsed(this)) {
                        this.setDead();
                    } else{
                        if (this.getAttackTarget() instanceof EntityPlayer target) {
                            double deltaX = this.posX - target.posX;
                            double deltaZ = this.posZ - target.posZ;
                            Vec3 vector = Vec3.createVectorHelper(deltaX, 0, deltaZ);
                            vector.normalize();
                            this.motionX = vector.xCoord * 0.2;
                            this.motionZ = vector.zCoord * 0.2;
                            this.timeSinceIgnited = 0;
                            this.fuseTime = 20;
                        }
                        this.motionY = 0.5f;
                    }
                    return true;
                }
            }
        }
        return super.interact(player);
    }

    @Override
    public int getTalkInterval() {
        return 120;
    }

    @Override
    public void playLivingSound() {
        if (this.getNeuteredState() > 0) {
            String var1 = this.getLivingSound();
            if (var1 != null) {
                this.playSound(var1, 0.25f, this.getSoundPitch() + 0.25f);
            }
        } else {
            super.playLivingSound();
        }
    }

    @Override
    protected String getLivingSound() {
        if (this.getNeuteredState() > 0) {
            return "mob.creeper.say";
        }
        return super.getLivingSound();
    }

    @Override
    public void onKickedByAnimal(KickingAnimal kickingAnimal) {
        this.determinedToExplode = true;
    }

    @Override
    public void checkForScrollDrop() {
        if (this.rand.nextInt(1000) == 0) {
            ItemStack itemstack = new ItemStack(BTWItems.arcaneScroll, 1, Enchantment.blastProtection.effectId);
            this.entityDropItem(itemstack, 0.0f);
        }
    }

    @Override
    public void onStruckByLightning(EntityLightningBolt entityBolt) {
        this.dataWatcher.updateObject(17, (byte)1);
    }

    public boolean getIsDeterminedToExplode() {
        return this.determinedToExplode;
    }

    public int getNeuteredState() {
        return this.dataWatcher.getWatchableObjectByte(25);
    }

    public void setNeuteredState(int iNeuteredState) {
        this.dataWatcher.updateObject(25, (byte)iNeuteredState);
    }

    @Override
    public Packet getSpawnPacketForThisEntity() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);
        try {
            EntityCreeperVariant par1EntityLivingBase = this;
            dataStream.writeInt(variantType);
            dataStream.writeInt(this.entityId);
            new Packet24MobSpawn(par1EntityLivingBase).writePacketData(dataStream);
            dataStream.writeInt(this.timeSinceIgnited);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new Packet250CustomPayload("btw|SE", byteStream.toByteArray());
    }

    @Override
    public int getTrackerViewDistance() {
        return 80;
    }

    @Override
    public int getTrackerUpdateFrequency() {
        return 3;
    }

    @Override
    public boolean getTrackMotion() {
        return true;
    }

    @Override
    public boolean shouldServerTreatAsOversized() {
        return false;
    }

    public void setTimeSinceIgnited(int timeSinceIgnited) {
        this.timeSinceIgnited = timeSinceIgnited;
    }





    // HELPER METHODS

    private float getExplosionSize() {
        float aprilFoolsExplosionModifier = NightmareMode.isAprilFools ? 1.05f + 0.15f * this.rand.nextFloat() : 1f;
        float variantExplosionModifier = this.variantType == NMFields.CREEPER_OBSIDIAN ? 1.5f : (this.variantType == NMFields.CREEPER_SUPERCRITICAL ? 1.4f : 1f);
        float bloodmoonModifier = NMUtils.getIsBloodMoon() ? 0.25f : 0;
        float eclipseModifier = NMUtils.getIsMobEclipsed(this) ? 0.15f : 0;
        float niteModifier = (float) NMUtils.getNiteMultiplier();

        if(!this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)){
            return (3f + bloodmoonModifier) * niteModifier * variantExplosionModifier * aprilFoolsExplosionModifier;
        }
        if(NMUtils.getWorldProgress() >= 2){
            return (4.2f + bloodmoonModifier + eclipseModifier) * niteModifier * variantExplosionModifier * aprilFoolsExplosionModifier;
        } else if(NMUtils.getWorldProgress() == 1){
            return (3.6f + bloodmoonModifier + eclipseModifier) * niteModifier * variantExplosionModifier * aprilFoolsExplosionModifier;
        }
        return (3.375f + bloodmoonModifier + eclipseModifier) * niteModifier * variantExplosionModifier * aprilFoolsExplosionModifier;
    }

    private static void spawnItemExplosion(World world, Entity entity, ItemStack itemStack, int amount, Random random) {
        for (int i = 0; i < amount; i++) {
            double theta = random.nextDouble() * 2 * Math.PI; // Horizontal angle
            double phi = random.nextDouble() * Math.PI;       // Vertical angle
            double radius = 0.5 + random.nextDouble() * 0.5; // Sphere size (0.5 - 1 block radius)

            double xOffset = radius * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * Math.cos(phi);
            double zOffset = radius * Math.sin(phi) * Math.sin(theta);

            double spawnX = entity.posX + xOffset;
            double spawnY = entity.posY + yOffset;
            double spawnZ = entity.posZ + zOffset;

            // Create item entity
            EntityItem itemEntity = new EntityItem(world, spawnX, spawnY, spawnZ, itemStack.copy());

            // Outward momentum (normalized direction vector)
            itemEntity.motionX = xOffset * 0.5;
            itemEntity.motionY = yOffset * 0.5;
            itemEntity.motionZ = zOffset * 0.5;

            // Spawn the item entity
            world.spawnEntityInWorld(itemEntity);
        }
    }


    private boolean shouldDetonateOnFire(DamageSource src){
        return (src == DamageSource.inFire ||
                src == DamageSource.onFire ||
                src == DamageSource.lava) && this.dimension != -1 &&
                !NMUtils.getIsBloodMoon() &&
                !NMUtils.getIsMobEclipsed(this) &&
                !this.isPotionActive(Potion.fireResistance.id);
    }

    private double getCreeperBreachRange(double constant) {
        boolean isHostile = this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class);
        if(!isHostile){
            return constant;
        }
        int bloodMoonModifier = NMUtils.getIsBloodMoon() || NMUtils.getIsMobEclipsed(this) ? 3 : 1;
        int i = NMUtils.getWorldProgress();

        return switch (i) {
            case 0 -> 36 * bloodMoonModifier * NMUtils.getNiteMultiplier();  // 6b   10.4b
            case 1 -> 81 * bloodMoonModifier * NMUtils.getNiteMultiplier();  // 9b   15.57b
            case 2, 3 -> 121 * bloodMoonModifier * NMUtils.getNiteMultiplier(); // 11b  19.03b
            default -> constant;
        };
    }
    private boolean isCharged() {
        return this.getDataWatcher().getWatchableObjectByte(17) == 1;
    }
    private int shouldSpawnCharged() {
        EntityCreeper self = (EntityCreeper)(Object)this;
        int progress = NMUtils.getWorldProgress();
        boolean isBloodMoon = NMUtils.getIsBloodMoon();
        boolean isEclipse = NMUtils.getIsMobEclipsed(this);

        if(self instanceof EntityLightningCreeper){
            return 1;
        }
        if((progress > 0 || NightmareMode.evolvedMobs) && (self.rand.nextFloat() * NMUtils.getNiteMultiplier()) < 0.15 + (progress - 1) * 0.03){
            if(self.rand.nextInt(10) == 0 && self.dimension == 0) {
                self.setCustomNameTag("Terrence");
            }
            return 1;
        } else if((self.dimension == -1 && !(self instanceof EntityFireCreeper)) && (progress > 0 || NightmareMode.evolvedMobs)){
            return 1;
        } else if(self.dimension == 1 && self.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)){
            return 1;
        }
        if(isBloodMoon || isEclipse){
            return rand.nextInt(6) == 0 ? 1 : 0;
        }
        return 0;
    }
}
