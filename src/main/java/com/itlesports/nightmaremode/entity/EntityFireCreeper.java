package com.itlesports.nightmaremode.entity;

import btw.entity.EntityWithCustomPacket;
import btw.entity.mob.KickingAnimal;
import btw.entity.mob.behavior.SimpleWanderBehavior;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class EntityFireCreeper extends EntityCreeper implements EntityWithCustomPacket {
    private boolean determinedToExplode = false;
    private int lastActiveTime;
    private int timeSinceIgnited;
    private int rangedAttackCooldown;
    private int fuseTime = 25;
    private final int explosionRadius = 3;
    private byte patienceCounter = 60;


    public EntityFireCreeper(World par1World) {
        super(par1World);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAICreeperSwell(this));
        this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityOcelot.class, 6.0F, 1.0, 1.2));
        this.tasks.addTask(4, new EntityAIAttackOnCollide(this, 1.0, false));
        this.tasks.addTask(5, new SimpleWanderBehavior(this, 0.8F));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 20f));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, false));

        NightmareUtils.manageEclipseChance(this,10);
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.29 * (1 + (NightmareUtils.getNiteMultiplier() - 1) / 20));
        if (this.worldObj != null) {
            int postNetherBoost = NightmareUtils.getWorldProgress(this.worldObj) >= 1 ? 12 : 0;
            this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(24.0 + postNetherBoost);
        }
    }

    protected void entityInit() {
        super.entityInit();
    }

    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);

        par1NBTTagCompound.setShort("Fuse", (short)this.fuseTime);
        par1NBTTagCompound.setByte("ExplosionRadius", (byte)this.explosionRadius);
        par1NBTTagCompound.setByte("fcNeuteredState", (byte)this.getNeuteredState());
        par1NBTTagCompound.setShort("timeSinceIgnited", (short)this.timeSinceIgnited);
        par1NBTTagCompound.setByte("patienceCounter", this.patienceCounter);
    }

    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
        if (par1NBTTagCompound.hasKey("Fuse")) {
            this.fuseTime = par1NBTTagCompound.getShort("Fuse");
        }

        if (par1NBTTagCompound.hasKey("timeSinceIgnited")) {
            this.timeSinceIgnited = par1NBTTagCompound.getShort("timeSinceIgnited");
        }

        if (par1NBTTagCompound.hasKey("patienceCounter")) {
            this.patienceCounter = par1NBTTagCompound.getByte("patienceCounter");
        }

    }
    public void onUpdate() {
        if (this.isEntityAlive()) {
            this.lastActiveTime = this.timeSinceIgnited;
            int var1 = this.getCreeperState();
            if (var1 > 0 && this.timeSinceIgnited == 0) {
                this.playSound("random.fuse", 1.0F, 0.5F);
            }

            if (this.getAttackTarget() == null) {
                if (this.worldObj.rand.nextInt(20) == 0) {
                    this.patienceCounter = (byte)Math.min(this.patienceCounter + 1, 100);
                }
            } else {
                if (this.getDistanceSqToEntity(this.getAttackTarget()) < 36.0 && !this.canEntityBeSeen(this.getAttackTarget()) && this.getNavigator().noPath()) {
                    this.patienceCounter = (byte)Math.max(this.patienceCounter - 1, 0);
                } else if(this.getDistanceSqToEntity(this.getAttackTarget()) > 64.0 && this.canEntityBeSeen(this.getAttackTarget()) && this.rangedAttackCooldown == 0 && NightmareUtils.getIsBloodMoon()){
                    EntityLivingBase target = this.getAttackTarget();
                    double var3 = target.posX - this.posX;
                    double var5 = target.boundingBox.minY + (double) (target.height / 2.0F) - (this.posY + (double) (this.height / 2.0F)) - 0.5;
                    double var7 = target.posZ - this.posZ;

                    EntitySmallFireball var11 = new EntitySmallFireball(this.worldObj, this, var3, var5, var7);
                    this.worldObj.playAuxSFXAtEntity(null, 1009, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                    var11.posY = this.posY + (double) (this.height / 2.0f) + 0.5;

                    this.worldObj.spawnEntityInWorld(var11);
                    this.rangedAttackCooldown = 50 + rand.nextInt(30);
                }
                this.rangedAttackCooldown = Math.max(--this.rangedAttackCooldown,0);

            }
            if (this.patienceCounter == 0) {
                this.determinedToExplode = true;
            }

            this.timeSinceIgnited += var1;
            if (this.timeSinceIgnited < 0) {
                this.timeSinceIgnited = 0;
            }

            if (this.timeSinceIgnited >= this.fuseTime) {
                this.timeSinceIgnited = this.fuseTime;
                if (!this.worldObj.isRemote) {
                    boolean var2 = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
                    if(this.getPowered()){
                        if(this.isBurning() && this.worldObj.getDifficulty() == Difficulties.HOSTILE){
                            this.worldObj.newExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, (float)(this.explosionRadius * 2.5),true, var2);
                        } else {
                            this.worldObj.newExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, (float)(this.explosionRadius * 2),true, var2);
                        }
                    } else {
                        this.worldObj.newExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, (float)this.explosionRadius, true, var2);
                    }
                    if(NightmareUtils.getIsMobEclipsed(this)){
                        EntityLivingBase target = this.getAttackTarget();
                        if (target instanceof EntityPlayer) {
                            for (int i = -1; i < 2 ; i++) {
                                for (int j = -1; j < 2 ; j++) {
                                    for (int k = 0; k < 2; k++) {
                                        if(rand.nextInt(4) == 0){
                                            continue;
                                        }
                                        EntityMagmaCube cube = new EntityMagmaCube(this.worldObj);
                                        cube.setLocationAndAngles(this.posX + (i * 0.2), this.posY + (k * 0.5), this.posZ + (j * 0.2),this.rotationYaw,this.rotationPitch);
                                        ((EntitySlime)cube).setSlimeSize(1);
                                        this.worldObj.spawnEntityInWorld(cube);
                                    }
                                }
                            }
                        }
                    }
                    this.setDead();
                }
            }
        }
        super.onUpdate();
    }

    @Override
    public Packet getSpawnPacketForThisEntity() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);
        try {
            EntityFireCreeper par1EntityLivingBase = this;
            dataStream.writeInt(18);

            dataStream.writeInt(this.entityId);
            new Packet24MobSpawn(par1EntityLivingBase).writePacketData(dataStream);
            dataStream.writeInt(this.timeSinceIgnited);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new Packet250CustomPayload("btw|SE", byteStream.toByteArray());

    }

    public void onDeath(DamageSource par1DamageSource) {
        super.onDeath(par1DamageSource);
        if (par1DamageSource.getEntity() instanceof EntitySkeleton && this.getNeuteredState() == 0) {
            int var2 = Item.record13.itemID + this.rand.nextInt(Item.recordWait.itemID - Item.record13.itemID + 1);
            this.dropItem(var2, 1);
        }

    }

    public float getCreeperFlashIntensity(float par1) {
        return ((float)this.lastActiveTime + (float)(this.timeSinceIgnited - this.lastActiveTime) * par1) / (float)(this.fuseTime - 2);
    }

    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        if (par1DamageSource.isExplosion()) {
            par2 /= 2.0F;
        }

        return super.attackEntityFrom(par1DamageSource, par2);
    }

    protected void dropFewItems(boolean bKilledByPlayer, int iLootingModifier) {
        super.dropFewItems(bKilledByPlayer, iLootingModifier);
        if (this.getNeuteredState() == 0 && this.rand.nextInt(8) == 0) {
            this.dropItem(Item.fireballCharge.itemID, 1);
        }


        int bloodOrbID = NightmareUtils.getIsBloodMoon() ? NMItems.bloodOrb.itemID : 0;
        if (bloodOrbID > 0) {
            int var4 = this.rand.nextInt(3);
            // 0 - 2
            if (iLootingModifier > 0) {
                var4 += this.rand.nextInt(iLootingModifier + 1);
            }
            for (int var5 = 0; var5 < var4; ++var5) {
                this.dropItem(bloodOrbID, 1);
            }
        }
    }

    public boolean interact(EntityPlayer player) {
        ItemStack playersCurrentItem = player.inventory.getCurrentItem();
        if (playersCurrentItem != null && playersCurrentItem.getItem() instanceof ItemShears) {
            if (!this.worldObj.isRemote) {
                boolean var2 = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
                if (this.getPowered() || this.isBurning()) {
                    this.worldObj.newExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, (float)(this.explosionRadius * 2),true, var2);
                } else {
                    this.worldObj.newExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, (float)this.explosionRadius, true, var2);
                }
                this.setDead();
            }
        }
        return false;
    }

    public void playLivingSound() {
        if (this.getNeuteredState() > 0) {
            String var1 = this.getLivingSound();
            if (var1 != null) {
                this.playSound(var1, 0.25F, this.getSoundPitch() + 0.25F);
            }
        } else {
            super.playLivingSound();
        }

    }

    protected String getLivingSound() {
        return this.getNeuteredState() > 0 ? "mob.creeper.say" : super.getLivingSound();
    }

    public void onKickedByAnimal(KickingAnimal kickingAnimal) {
        this.determinedToExplode = true;
    }

    public void checkForScrollDrop() {}

    public boolean getIsDeterminedToExplode() {
        return this.determinedToExplode;
    }


    public int getTrackerViewDistance() {
        return 80;
    }

    public int getTrackerUpdateFrequency() {
        return 3;
    }

    public boolean getTrackMotion() {
        return true;
    }

    public boolean shouldServerTreatAsOversized() {
        return false;
    }

    public void setTimeSinceIgnited(int timeSinceIgnited) {
        this.timeSinceIgnited = timeSinceIgnited;
    }


}
