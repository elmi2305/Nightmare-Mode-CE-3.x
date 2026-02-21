package com.itlesports.nightmaremode.AITasks;

import com.itlesports.nightmaremode.entity.creepers.EntityCreeperVariant;
import com.itlesports.nightmaremode.util.NMDifficultyParam;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.EntityAIBase;
import net.minecraft.src.EntityLivingBase;

public class EntityAICreeperVariantSwell extends EntityAIBase {
    // identical to EntityAICreeperSwell, but takes EntityCreeperVariant as param instead of EntityCreeper, and has all the mixins applied
    public EntityCreeperVariant swellingCreeper;
    public EntityLivingBase creeperAttackTarget;

    public EntityAICreeperVariantSwell(EntityCreeperVariant c) {
        this.swellingCreeper = c;
        this.setMutexBits(1);
    }

    @Override
    public void startExecuting() {
        this.swellingCreeper.getNavigator().clearPathEntity();
        this.creeperAttackTarget = this.swellingCreeper.getAttackTarget();
    }

    @Override
    public void resetTask() {
        this.creeperAttackTarget = null;
    }

    @Override
    public boolean shouldExecute() {
        if (this.swellingCreeper.getCreeperState() <= 0 && this.swellingCreeper.getNeuteredState() > 0) {
            return false;
        }
        if (this.swellingCreeper.getIsDeterminedToExplode()) {
            return true;
        }
        EntityLivingBase var1 = this.swellingCreeper.getAttackTarget();
        int range = 9;
        if(this.swellingCreeper.variantType == NMFields.CREEPER_LIGHTNING){
            range = 5;
        }
        return this.swellingCreeper.getCreeperState() > 0 || var1 != null && this.swellingCreeper.getDistanceSqToEntity(var1) < range;
    }

    @Override
    public void updateTask() {
        double retentionDistance = 36.0;
        if(this.swellingCreeper.variantType == NMFields.CREEPER_OBSIDIAN){
            retentionDistance = 81;
        }
        if(this.swellingCreeper.variantType == NMFields.CREEPER_LIGHTNING){
            retentionDistance = 4096;
        }

        if (this.swellingCreeper.getNeuteredState() > 0) {
            this.swellingCreeper.setCreeperState(-1);
        } else if (!(this.swellingCreeper.getIsDeterminedToExplode() || this.creeperAttackTarget != null && !(this.swellingCreeper.getDistanceSqToEntity(this.creeperAttackTarget) > retentionDistance) && (this.swellingCreeper.getEntitySenses().canSee(this.creeperAttackTarget) || this.creeperAttackTarget.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)))) {
            this.swellingCreeper.setCreeperState(-1);
        } else {
            this.swellingCreeper.setCreeperState(1);
        }
    }
}