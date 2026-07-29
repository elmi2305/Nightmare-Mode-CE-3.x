package com.itlesports.nightmaremode.mixin.component;

import com.itlesports.nightmaremode.mixin.interfaces.MapGenStructureIOAccess;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.BigMushroom;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.RibcageClosed;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.RibcageOpen;
import com.itlesports.nightmaremode.structure.ComponentNetherDesertTemple;
import com.itlesports.nightmaremode.structure.ComponentOceanDesertTemple;
import com.itlesports.nightmaremode.structure.StructureNetherDesertTempleStart;
import com.itlesports.nightmaremode.structure.StructureOceanDesertTempleStart;
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
        MapGenStructureIOAccess.invokeFunction(BigMushroom.class, "TeBM");
        MapGenStructureIOAccess.invokeFunction(RibcageClosed.class, "TeRCC");
        MapGenStructureIOAccess.invokeFunction(RibcageOpen.class, "TeRCO");
        MapGenStructureIOAccess.invokeFunction(ComponentNetherDesertTemple.class, "TeNDP");
        MapGenStructureIOAccess.invokeFunction(ComponentOceanDesertTemple.class, "TeODP");
        MapGenStructureIOAccess.invokeFunctionB(StructureNetherDesertTempleStart.class, "NMNetherTemple");
        MapGenStructureIOAccess.invokeFunctionB(StructureOceanDesertTempleStart.class, "NMOceanTemple");
    }
}
