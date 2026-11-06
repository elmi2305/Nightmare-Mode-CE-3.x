package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.tileentity.UnfiredBrickTileEntity;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(UnfiredBrickTileEntity.class)
public class UnfiredBrickTileEntityMixin extends TileEntity {

    @ModifyConstant(method = "updateCooking",
            constant = @Constant(
    intValue = 11900),remap = false)
    private int reduceClayCookTime(int constant){
        return this.requiredCookTicks();
    }

    @ModifyConstant(method = "computeCookLevel", constant = @Constant(floatValue = 11900.0F), remap = false)
    private float scaleClayCookingVisual(float constant) {
        return this.requiredCookTicks();
    }

    @Unique
    private int requiredCookTicks() {
        EntityPlayer player = this.worldObj == null ? null : this.worldObj.getClosestPlayer(
                this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D, 8.0D);
        int reduction = player == null ? 0 : SkillHandler.getPlayerData(player).clayCookTimeReductionTicks;
        return Math.max(12000, 36000 - reduction);
    }

}
