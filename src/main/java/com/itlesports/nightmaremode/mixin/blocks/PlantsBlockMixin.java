package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.PlantsBlock;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlantsBlock.class)
public class PlantsBlockMixin {
    @Inject(method = "onBlockActivated", at = @At(value = "INVOKE",
            target = "Lbtw/block/blocks/PlantsBlock;removeWeeds(Lnet/minecraft/src/World;III)V", shift = At.Shift.AFTER))
    private void trackWeedRemoval(World world, int x, int y, int z, EntityPlayer player, int facing,
                                  float clickX, float clickY, float clickZ, CallbackInfoReturnable<Boolean> cir) {
        SkillHandler.incrementWeedsRemoved(player);
    }
}
