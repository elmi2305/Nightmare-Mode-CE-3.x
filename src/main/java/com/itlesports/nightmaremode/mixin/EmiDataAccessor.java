package com.itlesports.nightmaremode.mixin;

import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.data.EmiData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = EmiData.class, remap = false)
public interface EmiDataAccessor {
    @Accessor(value = "hiddenStacks", remap = false)
    static List<EmiIngredient> getHiddenStacks() {
        throw new AssertionError();
    }
}
