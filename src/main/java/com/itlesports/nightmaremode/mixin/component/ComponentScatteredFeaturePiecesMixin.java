package com.itlesports.nightmaremode.mixin.component;

import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.BigMushroom;
import net.minecraft.src.ComponentScatteredFeaturePieces;
import net.minecraft.src.MapGenStructureIO;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ComponentScatteredFeaturePieces.class)
public class ComponentScatteredFeaturePiecesMixin {
    @Inject(method = "func_143045_a", at = @At("TAIL"))
    private static void addMushroomGen(CallbackInfo ci){
        MapGenStructureIO.func_143031_a(BigMushroom.class, "TeBM");
    }
}
