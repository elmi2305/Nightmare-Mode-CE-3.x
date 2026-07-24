package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.block.NMBlocks;
import emi.dev.emi.emi.api.plugin.VanillaPlugin;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = VanillaPlugin.class, remap = false)
public class VanillaPluginMixin {
    @ModifyArg(
            method = "register",
            at = @At(
                    value = "INVOKE",
                    target = "Lemi/dev/emi/emi/api/EmiRegistry;addWorkstation(Lemi/dev/emi/emi/api/recipe/EmiRecipeCategory;Lemi/dev/emi/emi/api/stack/EmiIngredient;)V",
                    ordinal = 1
            ),
            index = 1,
            remap = false
    )
    private EmiIngredient replaceAnvilCraftingWorkstation(EmiIngredient workstation) {
        return EmiStack.of(NMBlocks.netherWorkbench);
    }
}
