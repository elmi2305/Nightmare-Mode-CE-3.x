package com.itlesports.nightmaremode.mixin.blocks;

import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.BlockNetherStalk;
import net.minecraft.src.World;
import net.minecraft.src.WorldProvider;
import java.util.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockNetherStalk.class)
public class BlockNetherStalkMixin {
    @Inject(method = "updateTick", at = @At("HEAD"), cancellable = true)
    private void gateNetherWartFarming(World world, int x, int y, int z, Random random, CallbackInfo ci) {
        EntityPlayer player = world.getClosestPlayer(x + 0.5D, y + 0.5D, z + 0.5D, 16.0D);
        if (player == null || !SkillHandler.getPlayerData(player).canFarmNetherWart) {
            ci.cancel();
        }
    }
    @Redirect(method = "updateTick", at = @At(value = "FIELD", target = "Lnet/minecraft/src/WorldProvider;dimensionId:I"))
    private int ensureGrowsInAnyDimension(WorldProvider instance){
        return -1;
    }
}
