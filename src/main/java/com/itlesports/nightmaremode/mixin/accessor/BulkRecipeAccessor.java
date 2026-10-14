package com.itlesports.nightmaremode.mixin.accessor;

import btw.crafting.recipe.types.BulkRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BulkRecipe.class)
public interface BulkRecipeAccessor {
    @Accessor(value = "metadataExclusive", remap = false)
    boolean nm$isMetadataExclusive();
}
