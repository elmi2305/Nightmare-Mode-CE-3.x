package com.itlesports.nightmaremode.mixin.component;

import com.itlesports.nightmaremode.mixin.interfaces.MapGenStructureIOAccess;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.BigMushroom;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.RibcageClosed;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.RibcageOpen;
import com.itlesports.nightmaremode.structure.ComponentNetherDesertTemple;
import com.itlesports.nightmaremode.structure.ComponentOceanDesertTemple;
import com.itlesports.nightmaremode.structure.StructureNetherDesertTempleStart;
import com.itlesports.nightmaremode.structure.StructureOceanDesertTempleStart;
import com.itlesports.nightmaremode.structure.StructureNetherVillagerPostStart;
import com.itlesports.nightmaremode.structure.StructureSkyZiggurathStart;
import com.itlesports.nightmaremode.structure.SkyZiggurath;
import com.itlesports.nightmaremode.structure.Tier1VillagerPost;
import com.itlesports.nightmaremode.structure.Tier2VillagerPost;
import com.itlesports.nightmaremode.structure.Tier3VillagerPost;
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
        MapGenStructureIOAccess.invokeFunction(Tier1VillagerPost.class, "NmNVP1");
        MapGenStructureIOAccess.invokeFunction(Tier2VillagerPost.class, "NmNVP2");
        MapGenStructureIOAccess.invokeFunction(Tier3VillagerPost.class, "NmNVP3");
        MapGenStructureIOAccess.invokeFunction(SkyZiggurath.class, "NmSkyZig");
        MapGenStructureIOAccess.invokeFunctionB(StructureNetherDesertTempleStart.class, "NMNetherTemple");
        MapGenStructureIOAccess.invokeFunctionB(StructureOceanDesertTempleStart.class, "NMOceanTemple");
        MapGenStructureIOAccess.invokeFunctionB(StructureNetherVillagerPostStart.class, "NMNetherVillagerPost");
        MapGenStructureIOAccess.invokeFunctionB(StructureSkyZiggurathStart.class, "NMSkyZiggurath");
    }
}
