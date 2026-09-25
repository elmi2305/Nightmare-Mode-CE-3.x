package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.tileentity.TurntableTileEntity;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TurntableTileEntity.class)
public abstract class TurntableTileEntityMixin extends TileEntity {
    @Inject(method = "getTicksToRotate", at = @At("RETURN"), cancellable = true, remap = false)
    private void nightmareMode$applyMachineSkillSpeed(CallbackInfoReturnable<Integer> cir) {
        EntityPlayer player = this.worldObj.getClosestPlayer(this.xCoord + 0.5D, this.yCoord + 0.5D,
                this.zCoord + 0.5D, 16.0D);
        float bonus = player == null ? 0.0F : SkillHandler.getPlayerData(player).machineSpeedBonus;
        cir.setReturnValue(Math.max(1, Math.round(cir.getReturnValue() / (1.0F + bonus))));
    }
    @Inject(method = "rotateTurntable", at = @At("TAIL"), remap = false)
    private void recordCompletedRotation(CallbackInfo ci) {
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }
        EntityPlayer player = this.worldObj.getClosestPlayer(
                this.xCoord + 0.5D,
                this.yCoord + 0.5D,
                this.zCoord + 0.5D,
                16.0D);
        SkillHandler.incrementTurntableRotations(player);
    }
}
