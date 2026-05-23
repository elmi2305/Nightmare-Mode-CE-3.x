package com.itlesports.nightmaremode.entity;

import btw.item.BTWItems;
import com.itlesports.nightmaremode.entity.underworld.EntityPollenCloud;
import net.minecraft.src.*;

public class EntityMushWorm extends EntitySilverfish {
    public EntityMushWorm(World par1World) {
        super(par1World);
        this.experienceValue = 0;
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(12f);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.4F);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(2.0F);
    }

    protected void dropFewItems(boolean bKilledByPlayer, int looting) {
        this.dropItem(BTWItems.redMushroom.itemID, looting + this.rand.nextInt(2));
    }

    public void onUpdate() {
        this.renderYawOffset = this.rotationYaw;
        super.onUpdate();
    }

    @Override
    protected void updateEntityActionState() {
        super.updateEntityActionState();
        if (!this.worldObj.isRemote) {
            if (this.entityToAttack == null && !this.hasPath()) {
                int var1 = MathHelper.floor_double(this.posX);
                int var2 = MathHelper.floor_double(this.posY + (double)0.5F);
                int var3 = MathHelper.floor_double(this.posZ);
                int var11 = this.rand.nextInt(6);
                int var5 = this.worldObj.getBlockId(var1 + Facing.offsetsXForSide[var11], var2 + Facing.offsetsYForSide[var11], var3 + Facing.offsetsZForSide[var11]);
                if (var5 != 0) {
                    this.updateWanderPath();
                }
            } else if (this.entityToAttack != null && !this.hasPath()) {
                this.entityToAttack = null;
            }
        }
    }

    public float getBlockPathWeight(int par1, int par2, int par3) {
        return this.worldObj.getBlockMaterial(par1, par2 - 1, par3) == Material.wood ? 10.0F : super.getBlockPathWeight(par1, par2, par3);
    }

    public void checkForScrollDrop() {}

    @Override
    public void onDeath(DamageSource par1DamageSource) {
        super.onDeath(par1DamageSource);
        if (!this.worldObj.isRemote) {
            EntityPollenCloud cloud = new EntityPollenCloud(this.worldObj, this.posX, this.posY + 0.6D, this.posZ, 2 + this.rand.nextDouble());
            this.worldObj.spawnEntityInWorld(cloud);
        }
    }
}