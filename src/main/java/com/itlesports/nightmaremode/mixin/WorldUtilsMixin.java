package com.itlesports.nightmaremode.mixin;

import api.world.WorldUtils;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldUtils.class)
public class WorldUtilsMixin {
    @Inject(method = "isValidLightLevelForMobSpawning", at = @At("HEAD"),cancellable = true)
    private static void underworldMobsDoNotCareForLight(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir){
        if(world.provider.dimensionId == NMFields.UNDERWORLD_DIMENSION){
            cir.setReturnValue(true);
        }
    }
}
