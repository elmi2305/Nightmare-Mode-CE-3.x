package com.itlesports.nightmaremode.entity;

import btw.block.blocks.BlockDispenserBlock;
import btw.block.blocks.GroundCoverBlock;
import btw.block.tileentity.dispenser.BlockDispenserTileEntity;
import btw.world.util.BlockPos;
import net.minecraft.src.*;

public class EntityFallingChicken extends EntityChicken {
    public EntityFallingChicken(World par1World) {
        super(par1World);
        this.tasks.removeAllTasks();
        this.noClip = true;
    }
    @Override
    public boolean isAIEnabled() {
        return false;
    }
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((double)200f);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute((double)0f);
    }
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.field_70888_h = this.field_70886_e;
        this.field_70884_g = this.destPos;
        this.destPos = (float)((double)this.destPos + (double)(this.onGround ? -1 : 4) * 0.3);
        if (this.destPos < 0.0F) {
            this.destPos = 0.0F;
        }

        if (this.destPos > 1.0F) {
            this.destPos = 1.0F;
        }

        if (!this.onGround && this.field_70889_i < 1.0F) {
            this.field_70889_i = 1.0F;
        }

        this.field_70889_i = (float)((double)this.field_70889_i * 0.9);
        if (!this.onGround && this.motionY < (double)0.0F) {
            this.motionY *= 1.2;
        }

        this.field_70886_e += this.field_70889_i * 2.0F;
        if(this.posY <= 0 || this.ticksExisted > 1000){
            this.setDead();
        }
    }

    protected void fall(float par1) {}

    @Override
    protected String getLivingSound() {
        return null;
    }

    @Override
    protected String getHurtSound() {
        return null;
    }

    @Override
    protected String getDeathSound() {
        return null;
    }

    @Override
    protected int getDropItemId() {
        return 0;
    }

    public EntityAgeable createChild(EntityAgeable par1EntityAgeable) {
        return this.spawnBabyAnimal(par1EntityAgeable);
    }

    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setLong("fcTimeToLayEgg", this.timeToLayEgg);
    }

    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        if (tag.hasKey("fcTimeToLayEgg")) {
            this.timeToLayEgg = tag.getLong("fcTimeToLayEgg");
        } else {
            this.timeToLayEgg = 0L;
        }
    }

    @Override
    protected void dropFewItems(boolean killedByPlayer, int lootingModifier) {}
    public EntityChicken spawnBabyAnimal(EntityAgeable parent) {
        return new EntityChicken(this.worldObj);
    }

    @Override
    public boolean isReadyToEatBreedingItem() {return false;}

    public void onEatBreedingItem() {}

    public boolean isAffectedByMovementModifiers() {
        return false;
    }
    @Override
    public boolean getCanCreatureTypeBePossessed() {
        return false;
    }
    @Override
    public void onFullPossession() {}

    public double getMountedYOffset() {
        return (double)this.height * (double)1.3F;
    }
    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }
    @Override
    public boolean isSubjectToHunger() {
        return false;
    }

    public int getFoodValueMultiplier() {
        return 1;
    }

    public boolean shouldNotifyBlockOnGraze() {
        return false;
    }

    public void playGrazeFX(int i, int j, int k, int iBlockID) {}

    public BlockPos getGrazeBlockForPos() {
        BlockPos pos = super.getGrazeBlockForPos();
        return pos != null && GroundCoverBlock.isGroundCoverRestingOnBlock(this.worldObj, pos.x, pos.y, pos.z) ? null : pos;
    }

    public int getGrazeDuration() {
        return 20;
    }

    public boolean shouldStayInPlaceToGraze() {
        return true;
    }

    public boolean isHungryEnoughToForceMoveToGraze() {
        return false;
    }

    public int getItemFoodValue(ItemStack stack) {
        return 0;
    }

    public void onBecomeFamished() {}

    public void updateHungerState() {}
    public float getGrazeHeadRotationMagnitudeDivisor() {
        return 3.0F;
    }

    public float getGrazeHeadRotationRateMultiplier() {
        return 14.35F;
    }

    public boolean onBlockDispenserConsume(BlockDispenserBlock blockDispenser, BlockDispenserTileEntity tileEntity) {
        return true;
    }
}
