package com.itlesports.nightmaremode.entity.outer;

import com.itlesports.nightmaremode.world.BalanceProfile;

import btw.entity.mob.BTWSquidEntity;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.SharedMonsterAttributes;
import net.minecraft.src.World;

public class EntityAngelSquid extends BTWSquidEntity {
    private static final double VISUAL_BOUND_PADDING = 3.5D;

    public EntityAngelSquid(World world) {
        super(world);
        this.setSize(1.15F, 1.15F);
        this.noClip = true;
    }

    @Override
    public AxisAlignedBB getVisualBoundingBox() {
        return this.boundingBox.expand(VISUAL_BOUND_PADDING, VISUAL_BOUND_PADDING, VISUAL_BOUND_PADDING);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(BalanceProfile.isCombatEasy() ? 20.0D : 28.0D);
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.worldObj.difficultySetting > 0 && this.worldObj.checkNoEntityCollision(this.boundingBox);
    }

    @Override
    public void readEntityFromNBT(net.minecraft.src.NBTTagCompound tag) {
        com.itlesports.nightmaremode.util.EasyCombatStats defaults = com.itlesports.nightmaremode.util.EasyCombatStats.beforeLoad(this);
        super.readEntityFromNBT(tag);
        com.itlesports.nightmaremode.util.EasyCombatStats.afterLoad(this, defaults);
    }
}
