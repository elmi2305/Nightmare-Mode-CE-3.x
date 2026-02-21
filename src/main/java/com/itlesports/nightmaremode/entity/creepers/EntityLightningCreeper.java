package com.itlesports.nightmaremode.entity.creepers;

import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;

public class EntityLightningCreeper extends EntityCreeperVariant{
    public EntityLightningCreeper(World par1World) {
        super(par1World);
        this.variantType = NMFields.CREEPER_LIGHTNING;
        this.soundPitchModifier = 0.3f;
        this.fuseTime = 90 - NMUtils.getWorldProgress() * 10;
        this.canLunge = false;
    }

    @Override
    public boolean interact(EntityPlayer player) {
        return false;
    }

    @Override
    protected int shouldSpawnCharged() {
        return 1;
    }

    @Override
    public boolean getCanSpawnHere() {
        if (this.worldObj == null) return false;
        int worldProgress = NMUtils.getWorldProgress();
        boolean finalSpawn = this.worldObj.isThundering() || worldProgress > 0 && super.getCanSpawnHere() && this.posY > 50;

        return finalSpawn;
    }
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(32);
    }

    @Override
    protected boolean isValidLightLevel() {
        int z;
        int y;
        int x = MathHelper.floor_double(this.posX);
        if (this.worldObj.getSavedLightValue(EnumSkyBlock.Sky, x, y = MathHelper.floor_double(this.boundingBox.minY), z = MathHelper.floor_double(this.posZ)) > this.rand.nextInt(32)) {
            return false;
        }
        int blockLightValue = this.worldObj.getBlockLightValueNoSky(x, y, z);
        if (blockLightValue > 0) {
            return false;
        }
        int naturalLightValue = this.worldObj.getBlockNaturalLightValue(x, y, z);
        if (this.worldObj.isThundering()) {
            naturalLightValue = Math.min(naturalLightValue, 3);
        }
        return naturalLightValue <= this.rand.nextInt(9);
    }

    @Override
    public void onUpdate() {
        if(!this.hasAttackTarget() && this.ticksExisted % 10 == 0){
            EntityPlayer player = this.worldObj.getClosestPlayerToEntity(this,70);
            if(player != null && !player.capabilities.isCreativeMode){
                this.setAttackTarget(player);
                this.getMoveHelper().setMoveTo(player.posX,player.posY,player.posZ, 1.0f);
            }
        }
        super.onUpdate();
    }

}
