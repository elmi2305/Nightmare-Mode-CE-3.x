package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.tileentity.UnfiredBrickTileEntity;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(UnfiredBrickTileEntity.class)
public class UnfiredBrickTileEntityMixin extends TileEntity {

    @ModifyConstant(method = "updateCooking",
            constant = @Constant(
    intValue = 11900),remap = false)
    private int reduceClayCookTime(int constant){
        EntityPlayer player = this.worldObj == null ? null : this.worldObj.getClosestPlayer(
                this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D, 8.0D);
        int reduction = player == null ? 0 : SkillHandler.getPlayerData(player).clayCookTimeReductionTicks;
        return Math.max(12000, 36000 - reduction);
    }

}
