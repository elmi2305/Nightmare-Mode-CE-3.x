package com.itlesports.nightmaremode.entity.underworld;

import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.util.elements.NMDifficultyParam;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.*;

public class EntityVoidSlime extends EntitySlimeVariant{
    public EntityVoidSlime(World par1World) {
        super(par1World);
        this.slimeType = NMFields.SLIME_VOID;
    }

    @Override
    public float knockbackMagnitude() {
        return 0f;
    }

    @Override
    protected int getTrailBlockId() {
        return BTWBlocks.ashCoverBlock.blockID;
    }


    @Override
    protected void variantUpdate() {
        if(this.targetPlayer != null){
            if((this.ticksExisted + this.entityId * 2) % 256 == 0){
                int range = this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) ? 400 : 100;
                double distance = this.getDistanceSqToEntity(this.targetPlayer);

                if(distance < range){
                    Vec3 look = this.getLookVec();
                    double behindDistance = 1.0D + this.rand.nextInt(3);

                    int targetX = MathHelper.floor_double(this.targetPlayer.posX + look.xCoord * behindDistance);
                    int targetY = MathHelper.floor_double(this.targetPlayer.posY);
                    int targetZ = MathHelper.floor_double(this.targetPlayer.posZ + look.zCoord * behindDistance);

                    targetX += this.rand.nextInt(3) - 1;
                    targetZ += this.rand.nextInt(3) - 1;

                    if(this.canTeleportHere(this.worldObj, targetX, targetY, targetZ)){
                        this.setPositionAndUpdate(targetX, targetY, targetZ);
                        this.playSound("mob.endermen.portal", 2.0F, 1.0F);


                        this.getLookHelper().setLookPositionWithEntity(this.targetPlayer, this.rotationYaw, this.rotationPitch);
                    }
                }
            }
        }
        super.variantUpdate();
    }

    @Override
    protected EntitySlimeVariant createInstance() {
        return new EntityVoidSlime(this.worldObj);
    }

    private boolean canTeleportHere(World world, int targetX, int targetY, int targetZ) {
        return world.getBlockId(targetX, targetY, targetZ) == 0
                && world.getBlockId(targetX, targetY + 1, targetZ) == 0
                && world.getBlockId(targetX, targetY - 1, targetZ) != 0
                && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty();
    }

    @Override
    public void knockBack(Entity par1Entity, float par2, double par3, double par5) {
        return;
    }

}
