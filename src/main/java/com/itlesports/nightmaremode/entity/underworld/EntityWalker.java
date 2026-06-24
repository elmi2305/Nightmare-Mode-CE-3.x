package com.itlesports.nightmaremode.entity.underworld;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Unique;

public class EntityWalker extends EntityZombie {
    public EntityWalker(World w) {
        super(w);
        this.setSize(5.0f, 2.0f);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.motionX = this.motionY = this.motionZ = 0;
    }
    @Override
    public boolean getCanSpawnHere() {
        return this.worldObj.skylightSubtracted <= 2 && super.getCanSpawnHere() && this.rand.nextInt(16) == 0 && this.worldObj.getClosestVulnerablePlayerToEntity(this, 16f) == null;
    }

    @Unique private int seenCounter;
    @Override
    protected void despawnEntity() {

        if(this.ticksExisted % 8 != 0) return;
        int iChunkZ = MathHelper.floor_double(this.posZ / 16.0);
        int iChunkX = MathHelper.floor_double(this.posX / 16.0);

        if (!this.worldObj.isChunkActive(iChunkX, iChunkZ)) {
            this.setDead();
        } else {
            EntityPlayer closestPlayer = this.worldObj.getClosestPlayerToEntity(this, 128);
            if (closestPlayer != null) {
                if(this.isPlayerStaringAtMe(closestPlayer)){
                    this.getLookHelper().setLookPositionWithEntity(closestPlayer, 180.0f, 180.0f);
                    seenCounter++;
                }
                if(seenCounter > 1){
                    seenCounter = 0;
                    this.setDead();
                } else{
                    this.entityAge = 0;
                }
            } else if (this.entityAge > 6000 && this.rand.nextInt(1000) == 0) {
                this.setDead();
            }
        }
    }
    protected boolean isPlayerStaringAtMe(EntityPlayer player) {
        Vec3 vLook = player.getLook(1.0f).normalize();
        Vec3 vDelta = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX - player.posX, this.posY + (double)this.getEyeHeight() - (player.posY + (double)player.getEyeHeight()), this.posZ - player.posZ);
        double dotDelta = vLook.dotProduct(vDelta.normalize());
        if (dotDelta > 0) {
            return player.canEntityBeSeen(this);
        }
        return false;
    }

    @Override public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {return false;}
    @Override protected void damageEntity(DamageSource par1DamageSource, float par2) {}
    @Override public void moveEntity(double dMoveX, double dMoveY, double dMoveZ) {}
    @Override public void moveEntityWithHeading(float par1, float par2) {}
    @Override public void moveFlying(float par1, float par2, float par3) {}
    @Override public boolean isPushedByWater() {return false;}
    @Override protected boolean pushOutOfBlocks(double par1, double par3, double par5) {return false;}
    @Override protected boolean canDespawn() { return true; }
    @Override public boolean isEntityInvulnerable() {return true;}
    @Override public boolean doesEntityNotTriggerPressurePlate() {return true;}
    @Override public float getShadowSize() {return 0.0F;}
    @Override protected void fall(float par1) {}
    @Override public boolean canBeCollidedWith() {return false;}
    @Override public boolean canBePushed() {return false;}
    @Override protected void checkForCatchFireInSun() {}
    @Override public void setFire(int par1) {}
}
