package com.itlesports.nightmaremode;

import btw.entity.attribute.BTWAttributes;
import btw.entity.mob.behavior.ZombieBreakBarricadeBehavior;
import btw.item.BTWItems;
import net.minecraft.src.*;

import java.util.Iterator;
import java.util.List;

public class SocksMobsEntityGoatPossessed extends EntityMob implements IAnimals
{
    public int m_iHowlingCountdown = 0;
    public int m_iHeardHowlCountdown = 0;

    public SocksMobsEntityGoatPossessed(World var1)
    {
        super(var1);
        this.setSize(0.9F, 1.3F);
        this.isImmuneToFire = true;
        this.getNavigator().setAvoidsWater(true);
        this.tasks.removeAllTasks();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0, false));
        this.tasks.removeAllTasksOfClass(EntityAIRestrictSun.class);
        this.tasks.removeAllTasksOfClass(EntityAIFleeSun.class);
        this.tasks.addTask(5, new EntityAIWander(this, 0.3F));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.targetTasks.removeAllTasks();
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 32.0F, 0, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityVillager.class, 16.0F, 0, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityChicken.class, 16.0F, 0, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityCow.class, 16.0F, 0, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityPig.class, 16.0F, 0, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntitySheep.class, 16.0F, 0, false));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.4f);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(6.0);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(28.0);
        this.getEntityAttribute(BTWAttributes.armor).setAttribute(2.0);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(32.0);
    }
    
    /**
     * Called to update the entity's position/logic.
     */

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        this.setFire(20);
    }

    
    public void extinguish(){}

    /**
     * Returns true if the newer Entity AI code should be run
     */
    public boolean isAIEnabled()
    {
        return true;
    }

    protected void entityInit()
    {
        super.entityInit();
    }

    /**
     * Plays step sound at given x, y, z for the entity
     */
    @Override
    protected void playStepSound(int var1, int var2, int var3, int var4)
    {
        this.playSound("mob.sheep.step", 0.15F, 1.0F);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeEntityToNBT(NBTTagCompound var1)
    {
        super.writeEntityToNBT(var1);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readEntityFromNBT(NBTTagCompound var1)
    {
        super.readEntityFromNBT(var1);
    }

    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    @Override
    protected boolean canDespawn()
    {
        return false;
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    @Override
    protected String getLivingSound()
    {
        return "mob.sheep.say";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
    protected String getHurtSound()
    {
        return "mob.wolf.growl";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
    protected String getDeathSound()
    {
        return "mob.wolf.death";
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    @Override
    protected float getSoundVolume()
    {
        return 3.0F;
    }

    /**
     * Gets the pitch of living sounds in living entities.
     */
    @Override
    protected float getSoundPitch()
    {
        return (this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F + 0.55F;
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    @Override
    protected int getDropItemId()
    {
        return !this.worldObj.isRemote ? BTWItems.rawLiver.itemID : -1;
    }

    /**
     * Drop 0-2 items of this living's type. @param par1 - Whether this entity has recently been hit by a player. @param
     * par2 - Level of Looting used to kill this mob.
     */
    @Override
    protected void dropFewItems(boolean var1, int var2)
    {
        this.dropItem(BTWItems.rawLiver.itemID, 1);
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        if (this.worldObj.isRemote)
        {
            this.m_iHowlingCountdown = Math.max(0, this.m_iHowlingCountdown - 1);
        }
        else
        {
            this.m_iHeardHowlCountdown = Math.max(0, this.m_iHeardHowlCountdown - 1);

            if (this.worldObj.isDaytime())
            {
                float var1 = this.getBrightness(1.0F);

                if (var1 > 0.5F && this.rand.nextFloat() * 30.0F < (var1 - 0.4F) * 2.0F && this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)))
                {
                    this.setFire(8);
                }
            }
        }

        super.onLivingUpdate();
    }

    public float getEyeHeight()
    {
        return this.height * 0.8F;
    }

    /**
     * Takes a coordinate in and returns a weight to determine how likely this creature will try to path to the block.
     * Args: x, y, z
     */
    public float getBlockPathWeight(int var1, int var2, int var3)
    {
        return 0.5F - this.worldObj.getLightBrightness(var1, var2, var3);
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.UNDEAD;
    }

    /**
     * Gets the username of the entity.
     */
    public String getEntityName()
    {
        return "The Big Bad Goat";
    }

//
//    public float getHeadRotationPointOffset(float var1)
//    {
//        if (this.m_iHowlingCountdown > 0)
//        {
//            float var2 = 1.0F;
//
//            if (this.m_iHowlingCountdown < 5)
//            {
//                var2 = (float)this.m_iHowlingCountdown / 5.0F;
//            }
//            else if (this.m_iHowlingCountdown > 70)
//            {
//                var2 = (float)(81 - this.m_iHowlingCountdown) / 10.0F;
//            }
//
//            return var2 * -0.5F;
//        }
//        else
//        {
//            return 0.0F;
//        }
//    }
//
//    public float GetHeadRotation(float var1)
//    {
//        if (this.m_iHowlingCountdown > 0)
//        {
//            float var2 = 1.0F;
//
//            if (this.m_iHowlingCountdown < 5)
//            {
//                var2 = (float)this.m_iHowlingCountdown / 5.0F;
//            }
//            else if (this.m_iHowlingCountdown > 70)
//            {
//                var2 = (float)(81 - this.m_iHowlingCountdown) / 10.0F;
//            }
//
//            return var2 * -((float)Math.PI / 5F);
//        }
//        else
//        {
//            return this.rotationPitch / (180F / (float)Math.PI);
//        }
//    }

    public void handleHealthUpdate(byte var1)
    {
        if (var1 == 10)
        {
            this.m_iHowlingCountdown = 80;
        }
        else
        {
            super.handleHealthUpdate(var1);
        }
    }
}
