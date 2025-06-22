package com.itlesports.nightmaremode.entity;

import btw.community.nightmaremode.NightmareMode;
import btw.entity.EntityWithCustomPacket;
import btw.entity.LightningBoltEntity;
import btw.entity.mob.KickingAnimal;
import btw.entity.mob.behavior.SimpleWanderBehavior;
import btw.item.BTWItems;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.AITasks.EntityAIChaseTargetSmart;
import com.itlesports.nightmaremode.AITasks.EntityAICreeperVariantSwell;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Unique;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Random;

public class EntityCreeperVariant extends EntityMob implements EntityWithCustomPacket {

    // note: I'm potentially gonna move all creeper variants to this class. one day. surely.

    private static final int NEUTERED_STATE_DATA_WATCHER_ID = 25;
    private boolean determinedToExplode = false;
    private int lastActiveTime;
    private int timeSinceIgnited;
    public int fuseTime = 30;
    public int variant;
    private static final int FIRE = 1;
    private static final int METAL = 2;
    private static final int DUNG = 3;
    private static final int LIGHTNING = 4;
    private int explosionRadius = 3;
    private byte patienceCounter = (byte)100;

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
        if (par1World != null && par1World.getDifficulty().canCreepersBreachWalls()) {
            this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, false));
        }
        if (NightmareMode.hordeMode) {
            this.tasks.removeAllTasksOfClass(EntityAIAttackOnCollide.class);
            this.tasks.addTask(4, new EntityAIChaseTargetSmart(this, 1.0D));
        }
    }



    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        double followDistance = 16.0;
        if (this.worldObj != null) {
            followDistance *= (double)this.worldObj.getDifficulty().getCreeperFollowDistanceMultiplier();
        }
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(followDistance);

        int progress = NightmareUtils.getWorldProgress(this.worldObj);
        double bloodMoonModifier = NightmareUtils.getIsBloodMoon() ? 1.25 : 1;
        int eclipseModifier = NightmareUtils.getIsMobEclipsed(this) ? 20 : 0;
        boolean isHostile = this.worldObj.getDifficulty() == Difficulties.HOSTILE;

        if (this.rand.nextInt(NightmareUtils.divByNiteMultiplier(8 - progress * 2, 2)) == 0 && isHostile) {
            this.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 10000000,0));
        }
        if (this.rand.nextInt(NightmareUtils.divByNiteMultiplier(3, 2)) == 0 && eclipseModifier > 1) {
            this.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 10000000,0));
        }
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(((20 + progress * 6) * bloodMoonModifier + eclipseModifier) * NightmareUtils.getNiteMultiplier());
        // 20 -> 26 -> 32 -> 38
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute((0.28 + eclipseModifier * 0.005) * ((((NightmareUtils.getNiteMultiplier() - 1) / 20)) + 1));

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
        this.dataWatcher.addObject(17, (byte)0);
        this.dataWatcher.addObject(25, (byte)0);
        boolean areMobsEvolved = NightmareMode.evolvedMobs;


        int progress = NightmareUtils.getWorldProgress(this.worldObj);
        boolean isBloodMoon = NightmareUtils.getIsBloodMoon();
        boolean isEclipse = NightmareUtils.getIsMobEclipsed(this);
        boolean result = false;

        boolean evolved = progress > 0 || areMobsEvolved;
        boolean bloodOrEclipse = isBloodMoon || isEclipse;

        if (bloodOrEclipse) {
            result = rand.nextInt(6) == 0;
        } else if (evolved) {
            if (dimension == 0) {
                double chance = rand.nextDouble() * NightmareUtils.getNiteMultiplier();
                float threshold = 0.15f + (progress - 1) * 0.03f;

                if (chance < threshold) {
                    if (rand.nextInt(10) == 0) {
                        this.setCustomNameTag("Terrence");
                    }
                    result = true;
                }
            } else if (dimension == -1 && variant != FIRE) {
                result = true;
            }
        } else if (dimension == 1 && worldObj.getDifficulty() == Difficulties.HOSTILE) {
            result = true;
        }

        this.setCharged(result);

    }

    public void setVariant(int variant) {
        this.variant = variant;
    }

    public int getVariant() {
        return variant;
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

            // cancer mode
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
            // cancer mode


            double creeperBreachRange = this.getCreeperBreachRange();

            this.lastActiveTime = this.timeSinceIgnited;
            int var1 = this.getCreeperState();
            if (var1 > 0 && this.timeSinceIgnited == 0) {
                this.playSound("random.fuse", 1.0f, 0.5f);
            }
            if (this.worldObj.getDifficulty().canCreepersBreachWalls()) {
                if (this.getAttackTarget() == null) {
                    if (this.worldObj.rand.nextInt(20) == 0) {
                        this.patienceCounter = (byte)Math.min(this.patienceCounter + 1, 100);
                    }
                } else if (this.getDistanceSqToEntity(this.getAttackTarget()) < creeperBreachRange && !this.canEntityBeSeen(this.getAttackTarget()) && this.getNavigator().noPath()) {
                    this.patienceCounter = (byte)Math.max(this.patienceCounter - 1, 0);
                }
                if (this.patienceCounter == 0) {
                    this.determinedToExplode = true;
                }
            }
            this.timeSinceIgnited += var1;

            if (this.timeSinceIgnited == (this.fuseTime - 8) && this.getCreeperState() == 1 && this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
                EntityPlayer target = this.worldObj.getClosestVulnerablePlayerToEntity(this,6);
                this.motionY = 0.38F;
                if(target != null) {
                    double var8 = target.posX - this.posX;
                    double var2 = target.posZ - this.posZ;
                    Vec3 vector = Vec3.createVectorHelper(var8, 0, var2);
                    vector.normalize();
                    this.motionX = vector.xCoord * 0.18;
                    this.motionZ = vector.zCoord * 0.18;
                    this.faceEntity(target,100f,100f);
                }
            }

            if (this.timeSinceIgnited < 0) {
                this.timeSinceIgnited = 0;
            }
            if (this.timeSinceIgnited >= this.fuseTime) {
                this.timeSinceIgnited = this.fuseTime;
                if (!this.worldObj.isRemote) {
                    boolean var2 = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
                    if (this.getPowered()) {
                        this.worldObj.createExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, this.explosionRadius * 2, var2);
                    } else {
                        this.worldObj.createExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, this.explosionRadius, var2);
                    }
                    // cancer mode
                    if(NightmareMode.isAprilFools){
                        this.worldObj.spawnEntityInWorld(new EntityTNTPrimed(this.worldObj,this.posX,this.posY,this.posZ,this));
                    }
                    // cancer mode

                    // dung creeper
                    if(this.variant == DUNG){
                        for(int i = 0; i < 24; i++){
                            spawnItemExplosion(this.worldObj,this, new ItemStack(BTWItems.dung),3,this.rand);
                        }
                    }
                    // dung creeper
                    this.setDead();
                }
            }
        }
        super.onUpdate();
    }

    private boolean getIsCharged(){
        return this.getDataWatcher().getWatchableObjectByte(17) == 1;
    }
    private void setCharged(boolean par1){
        int value = par1 ? 1 : 0;
        this.getDataWatcher().updateObject(17, value);
    }

    private double getCreeperBreachRange(){
        boolean isHostile = this.worldObj.getDifficulty() == Difficulties.HOSTILE;
        if(!isHostile){return 36;}
        int bloodMoonModifier = NightmareUtils.getIsBloodMoon() || NightmareUtils.getIsMobEclipsed(this) ? 3 : 1;
        int i = NightmareUtils.getWorldProgress(this.worldObj);

        return switch (i) {
            case 0 ->  36 * bloodMoonModifier * NightmareUtils.getNiteMultiplier();  // 6b   10.4b
            case 1 ->  81 * bloodMoonModifier * NightmareUtils.getNiteMultiplier();  // 9b   15.57b
            case 2 -> 121 * bloodMoonModifier * NightmareUtils.getNiteMultiplier(); // 11b  19.03b
            case 3 -> 196 * bloodMoonModifier * NightmareUtils.getNiteMultiplier(); // 14b  24.2b
            default -> 36;
        };
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
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        if ((par1DamageSource == DamageSource.inFire || par1DamageSource == DamageSource.onFire || par1DamageSource == DamageSource.lava) && this.dimension != -1 && !NightmareUtils.getIsBloodMoon() && !NightmareUtils.getIsMobEclipsed(this) && !this.isPotionActive(Potion.fireResistance.id)){
            this.determinedToExplode = true;
        }
        if (this.worldObj.getDifficulty().canCreepersBreachWalls() && par1DamageSource.isExplosion()) {
            par2 /= 5.0f;
        }
        if (NightmareUtils.getIsBloodMoon() && par1DamageSource == DamageSource.drown) {
            return false;
        }
        return super.attackEntityFrom(par1DamageSource, par2);
    }

    @Override
    protected void dropHead() {
        this.entityDropItem(new ItemStack(Item.skull.itemID, 1, 4), 0.0f);
    }

    @Override
    protected void dropFewItems(boolean bKilledByPlayer, int iFortuneModifier) {
        super.dropFewItems(bKilledByPlayer, iFortuneModifier);

        if(this.getIsCharged()){
            this.dropItem(Item.ghastTear.itemID, 1);
            if (rand.nextInt(3) == 0) {
                this.dropItem(BTWItems.creeperOysters.itemID, 1);
            }
        }

        if (this.getNeuteredState() == 0 && (this.rand.nextInt(3) == 0 || this.rand.nextInt(1 + iFortuneModifier) > 0)) {
            this.dropItem(BTWItems.creeperOysters.itemID, 1);
        }
        if (bKilledByPlayer && NightmareUtils.getIsMobEclipsed(this)) {
            for(int i = 0; i < (iFortuneModifier * 2) + 1; i++) {
                if (this.rand.nextInt(8) == 0) {
                    this.dropItem(NMItems.darksunFragment.itemID, 1);
                    if (this.rand.nextBoolean()) {
                        break;
                    }
                }
            }

            int itemID = NMItems.sulfur.itemID;

            int var4 = this.rand.nextInt(3);
            if (iFortuneModifier > 0) {
                var4 += this.rand.nextInt(iFortuneModifier + 1);
            }
            for (int var5 = 0; var5 < var4; ++var5) {
                if(this.rand.nextInt(3) == 0) continue;
                this.dropItem(itemID, 1);
            }
        }

        int bloodOrbID = NightmareUtils.getIsBloodMoon() ? NMItems.bloodOrb.itemID : 0;
        if (bloodOrbID > 0 && bKilledByPlayer) {
            int var4 = this.rand.nextInt(3);
            // 0 - 2
            if (iFortuneModifier > 0) {
                var4 += this.rand.nextInt(iFortuneModifier + 1);
            }
            for (int var5 = 0; var5 < var4; ++var5) {
                this.dropItem(bloodOrbID, 1);
            }
        }
    }

    @Override
    public boolean interact(EntityPlayer player) {
        ItemStack playersCurrentItem = player.inventory.getCurrentItem();
        boolean isHostile = this.worldObj.getDifficulty() == Difficulties.HOSTILE;
        float bloodMoonModifier = NightmareUtils.getIsBloodMoon() ? 1.25f : 1;

        if (playersCurrentItem != null && playersCurrentItem.getItem() instanceof ItemShears && this.getNeuteredState() == 0) {
            if (!this.worldObj.isRemote) {
                if (isHostile || playersCurrentItem.getItem().itemID == Item.shears.itemID) {
                    boolean var2 = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
                    if (this.getPowered()) {
                        this.worldObj.createExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, 8 * bloodMoonModifier, var2);
                    } else {
                        this.worldObj.createExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, 3 * bloodMoonModifier, var2);
                    }
                    if(NightmareUtils.getIsMobEclipsed(this)){
                        if(this.getAttackTarget() instanceof EntityPlayer target){
                            double var1 = this.posX - target.posX;
                            double var3 = this.posZ - target.posZ;
                            Vec3 vector = Vec3.createVectorHelper(var1, 0, var3);
                            vector.normalize();
                            this.motionX = vector.xCoord * 0.2;
                            this.motionZ = vector.zCoord * 0.2;
                            this.timeSinceIgnited = 0;
                            this.fuseTime = 20;
                        }
                        this.motionY = 0.5f;
                    } else{
                        this.setDead();
                    }
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
    public void checkForScrollDrop() {}

    @Override
    public void onStruckByLightning(LightningBoltEntity entityBolt) {
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
            dataStream.writeInt(23);
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




    @Unique
    private static void spawnItemExplosion(World world, Entity entity, ItemStack itemStack, int amount, Random random) {
        for (int i = 0; i < amount; i++) {
            // Random spherical coordinates
            double theta = random.nextDouble() * 2 * Math.PI; // Horizontal angle
            double phi = random.nextDouble() * Math.PI;       // Vertical angle
            double radius = 0.5 + random.nextDouble() * 0.5; // Sphere size (0.5 - 1 block radius)

            // Convert spherical coordinates to Cartesian
            double xOffset = radius * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * Math.cos(phi);
            double zOffset = radius * Math.sin(phi) * Math.sin(theta);

            // Spawn position relative to the entity
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


}

