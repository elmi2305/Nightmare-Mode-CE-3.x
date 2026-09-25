package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.ScrewPumpBlock;
import api.block.util.MechPowerUtils;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScrewPumpBlock.class)
public class ScrewPumpBlockMixin {
    @Redirect(method = "updateTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/World;scheduleBlockUpdate(IIIII)V"))
    private void nightmareMode$applyPumpSkillSpeed(World world, int x, int y, int z, int blockId, int ticks) {
        EntityPlayer player = world.getClosestPlayer(x + 0.5D, y + 0.5D, z + 0.5D, 16.0D);
        float bonus = player == null ? 0.0F : SkillHandler.getPlayerData(player).machineSpeedBonus;
        world.scheduleBlockUpdate(x, y, z, blockId, Math.max(1, Math.round(ticks / (1.0F + bonus))));
    }

    @Inject(method = "isInputtingMechanicalPower", at = @At("HEAD"),cancellable = true)
    private void makeScrewPumpAlwaysPowered(World world, int i, int j, int k, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(world.isBlockGettingPowered(i,j,k) || MechPowerUtils.isBlockPoweredByAxleToSide(world, i,j,k,0));
    }
}
