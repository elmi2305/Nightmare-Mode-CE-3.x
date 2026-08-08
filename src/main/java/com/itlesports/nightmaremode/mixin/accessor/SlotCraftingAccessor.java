package com.itlesports.nightmaremode.mixin.accessor;

import net.minecraft.src.IRecipe;
import net.minecraft.src.SlotCrafting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SlotCrafting.class)
public interface SlotCraftingAccessor {
    @Accessor("currentRecipe")
    IRecipe nightmareMode$getCurrentRecipe();
}
