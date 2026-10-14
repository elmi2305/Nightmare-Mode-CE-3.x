package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.ScrewPumpBlock;
import api.block.util.MechPowerUtils;
import com.itlesports.nightmaremode.util.MachineSpeedBonuses;
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
    private void applyPumpSkillSpeed(World world, int x, int y, int z, int blockId, int ticks) {
        float bonus = MachineSpeedBonuses.get(world, x, y, z);
        world.scheduleBlockUpdate(x, y, z, blockId, Math.max(1, Math.round(ticks / (1.0F + com.itlesports.nightmaremode.util.EasyBalance.processingBonus(bonus)))));
    }

    @Inject(method = "isInputtingMechanicalPower", at = @At("HEAD"),cancellable = true)
    private void makeScrewPumpAlwaysPowered(World world, int i, int j, int k, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(world.isBlockGettingPowered(i,j,k) || MechPowerUtils.isBlockPoweredByAxleToSide(world, i,j,k,0));
    }
}
