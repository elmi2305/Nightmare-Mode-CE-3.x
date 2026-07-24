package com.itlesports.nightmaremode.mixin.component;

import com.itlesports.nightmaremode.util.KnowledgeBookLoot;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.ComponentNetherBridgeCorridor2;
import net.minecraft.src.WeightedRandomChestContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ComponentNetherBridgeCorridor2.class)
public class ComponentNetherBridgeCorridor2KnowledgeBookMixin {
    @ModifyArg(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ComponentNetherBridgeCorridor2;generateStructureChestContents(Lnet/minecraft/src/World;Lnet/minecraft/src/StructureBoundingBox;Ljava/util/Random;III[Lnet/minecraft/src/WeightedRandomChestContent;I)Z"), index = 6)
    private WeightedRandomChestContent[] addKnowledgeBooksToFortressLoot(WeightedRandomChestContent[] loot) {
        return KnowledgeBookLoot.addWeightedBooks(loot, NMFields.KNOWLEDGE_BOOKS_NETHER_FORTRESS, 5);
    }
}
