package com.itlesports.nightmaremode.mixin.entity;

import api.entity.mob.KickingAnimal;
import api.world.difficulty.DifficultyParam;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(EntityMooshroom.class)
public class EntityMooshroomMixin extends EntityCow {

    public EntityMooshroomMixin(World par1World) {
        super(par1World);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(30);
    }

    @Override
    public boolean interact(EntityPlayer player) {
        return super.entityAnimalInteract(player);
    }

    @Override
    protected void updateKickAttack() {
        if(this.kickAttackCooldownTimer > 10){
            this.kickAttackCooldownTimer = 10;
        }
        // differs in kick range and cooldown
        if (this.kickAttackInProgressCounter >= 0) {
            ++this.kickAttackInProgressCounter;
            if (this.kickAttackInProgressCounter >= 20) {
                this.kickAttackInProgressCounter = -1;
            }
        } else if (!this.worldObj.isRemote) {
            --this.kickAttackCooldownTimer;
            if (this.isEntityAlive() && !this.isChild() && !this.getWearingBreedingHarness() && this.kickAttackCooldownTimer <= 0 && (this.isBurning() || this.getAITarget() != null) && ((Boolean)this.worldObj.getDifficultyParameter(DifficultyParam.ShouldLargeAnimalsKick.class)).booleanValue()) {
                Vec3 kickCenter = this.computeKickAttackCenter();
                AxisAlignedBB tipBox = AxisAlignedBB.getAABBPool().getAABB(kickCenter.xCoord - 1.7, kickCenter.yCoord - 1.5, kickCenter.zCoord - 1.7, kickCenter.xCoord + 1.7, kickCenter.yCoord + 1.5, kickCenter.zCoord + 1.7);
                List potentialCollisionList = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, tipBox);
                if (!potentialCollisionList.isEmpty()) {
                    boolean bAttackLaunched = false;
                    Vec3 lineOfSightOrigin = Vec3.createVectorHelper(this.posX, this.posY + (double)(this.height / 2.0f), this.posZ);
                    for (Object o : potentialCollisionList) {
                        EntityLivingBase tempEntity = (EntityLivingBase)o;
                        if (tempEntity instanceof KickingAnimal || !tempEntity.isEntityAlive() || tempEntity.ridingEntity == this || !this.canEntityBeSeenForAttackToCenterOfMass(tempEntity, lineOfSightOrigin)) continue;
                        bAttackLaunched = true;
                        ((KickingAnimalAccessor)(this)).invokeKickAttackHitTarget(tempEntity);
                    }
                    if (bAttackLaunched) {
                        ((KickingAnimalAccessor)(this)).invokeLaunchKickAttack();
                    }
                }
            }
        }
    }
}
