package com.itlesports.nightmaremode.entity.creepers;

import com.itlesports.nightmaremode.mixin.entity.EntitySlimeAccessor;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;

public class EntityFireCreeper extends EntityCreeperVariant{
    private int rangedAttackCooldown = 20; // starts at 20 so the creeper doesn't shoot instantly when spawned in a bloodmoon


    public EntityFireCreeper(World w) {
        super(w);
        this.variantType = NMFields.CREEPER_FIRE;
        this.soundPitchModifier = 0.2f;
        this.fuseTime = 25;
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.29 * (1 + (NMUtils.getNiteMultiplier() - 1) / 20));
        if (this.worldObj != null) {
            int postNetherBoost = NMUtils.getWorldProgress() >= 1 ? 12 : 0;
            this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(24.0 + postNetherBoost);
        }
    }

    @Override
    public void checkForScrollDrop() {}

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (NMUtils.getIsBloodMoon() && this.rangedAttackCooldown == 0) {
            EntityLivingBase target = this.getAttackTarget();
            if(this.getDistanceSqToEntity(target) > 64.0 && this.getEntitySenses().canSee(target)){
                double dx = target.posX - this.posX;
                double dy = target.boundingBox.minY + (double) (target.height / 2.0F) - (this.posY + (double) (this.height / 2.0F)) - 0.5;
                double dz = target.posZ - this.posZ;

                EntitySmallFireball fireball = new EntitySmallFireball(this.worldObj, this, dx, dy, dz);
                this.worldObj.playAuxSFXAtEntity(null, 1009, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                fireball.posY = this.posY + (double) (this.height / 2.0f) + 0.5;

                this.worldObj.spawnEntityInWorld(fireball);
                this.rangedAttackCooldown = 50 + rand.nextInt(30);
            }
        }
        this.rangedAttackCooldown = Math.max(--this.rangedAttackCooldown,0);
    }

    @Override
    protected void onDeathEffect() {
        if(NMUtils.getIsMobEclipsed(this)){
            EntityLivingBase target = this.getAttackTarget();
            if (target instanceof EntityPlayer) {
                for (int i = -1; i < 2 ; i++) {for (int j = -1; j < 2 ; j++) {for (int k = 0; k < 2; k++) {
                    if(rand.nextInt(4) == 0){
                        continue;
                    }
                    EntityMagmaCube cube = new EntityMagmaCube(this.worldObj);
                    cube.setLocationAndAngles(this.posX + (i * 0.2), this.posY + (k * 0.5), this.posZ + (j * 0.2),this.rotationYaw,this.rotationPitch);
                    ((EntitySlimeAccessor)(cube)).invokeSetSlimeSize(1);
                    this.worldObj.spawnEntityInWorld(cube);
                }}}
            }
        }
        super.onDeathEffect();
    }

    @Override
    protected void dropFewItems(boolean bKilledByPlayer, int iLootingModifier) {
        super.dropFewItems(bKilledByPlayer, iLootingModifier);
        if (this.getNeuteredState() == 0 && this.rand.nextInt(8) == 0) {
            this.dropItem(Item.fireballCharge.itemID, 1);
        }
    }
}
