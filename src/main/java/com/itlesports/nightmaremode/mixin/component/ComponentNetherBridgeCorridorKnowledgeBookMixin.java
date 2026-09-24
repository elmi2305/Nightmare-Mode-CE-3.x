package com.itlesports.nightmaremode.mixin.component;

import com.itlesports.nightmaremode.util.KnowledgeBookLoot;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.FortressJournalLoot;
import net.minecraft.src.ComponentNetherBridgeCorridor;
import net.minecraft.src.WeightedRandomChestContent;
import net.minecraft.src.World;
import net.minecraft.src.StructureBoundingBox;
import java.util.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ComponentNetherBridgeCorridor.class)
public class ComponentNetherBridgeCorridorKnowledgeBookMixin {
    @ModifyArg(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ComponentNetherBridgeCorridor;generateStructureChestContents(Lnet/minecraft/src/World;Lnet/minecraft/src/StructureBoundingBox;Ljava/util/Random;III[Lnet/minecraft/src/WeightedRandomChestContent;I)Z"), index = 6)
    private WeightedRandomChestContent[] addKnowledgeBooksToFortressLoot(World world, StructureBoundingBox bounds,
                                                                          Random random, int x, int y, int z,
                                                                          WeightedRandomChestContent[] loot, int count) {
        return FortressJournalLoot.add(KnowledgeBookLoot.addWeightedBooks(loot,
                NMFields.KNOWLEDGE_BOOKS_NETHER_FORTRESS, 5), world, bounds);
    }
}
