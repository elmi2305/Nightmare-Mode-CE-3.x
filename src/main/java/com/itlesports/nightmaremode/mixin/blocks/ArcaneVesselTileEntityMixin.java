package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.tileentity.ArcaneVesselTileEntity;
import net.minecraft.src.EntityXPOrb;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ArcaneVesselTileEntity.class)
public abstract class ArcaneVesselTileEntityMixin extends TileEntity {
    @Shadow private int containedRegularExperience;
    @Shadow private int containedDragonExperience;
    @Shadow public abstract void setContainedRegularExperience(int experience);
    @Shadow public abstract void setContainedDragonExperience(int experience);

    @ModifyConstant(method = {"readFromNBT", "validateVisualExperience"}, constant = @Constant(floatValue = 1000.0f), remap = false)
    private float increaseExperienceCapacity(float capacity) {
        return 100000.0f;
    }

    @ModifyConstant(method = "ejectContentsOnBlockBreak", constant = @Constant(intValue = 20), remap = false)
    private int increaseBreakEjectSize(int amount) {
        return 1000;
    }

    /**
     * @author nightmare mode
     * @reason increase capacity and preserve the remainder of oversized xp orbs
     */
    @Overwrite
    public boolean attemptToSwallowXPOrb(World world, int x, int y, int z, EntityXPOrb xpOrb) {
        int remainingSpace = 100000 - this.containedRegularExperience - this.containedDragonExperience;
        if (remainingSpace <= 0) {
            return false;
        }

        int xpToAdd = Math.min(xpOrb.xpValue, remainingSpace);
        if (xpOrb.xpValue <= remainingSpace) {
            xpOrb.setDead();
        } else {
            xpOrb.xpValue -= xpToAdd;
        }

        if (xpOrb.notPlayerOwned) {
            this.setContainedDragonExperience(this.containedDragonExperience + xpToAdd);
        } else {
            this.setContainedRegularExperience(this.containedRegularExperience + xpToAdd);
        }
        return true;
    }
}
