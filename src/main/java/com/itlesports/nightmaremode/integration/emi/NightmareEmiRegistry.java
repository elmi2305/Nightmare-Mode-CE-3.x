package com.itlesports.nightmaremode.integration.emi;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.crafting.manager.CisternRecipeManager;
import com.itlesports.nightmaremode.crafting.manager.HammerCraftingManager;
import com.itlesports.nightmaremode.crafting.recipe.types.CisternRecipe;
import com.itlesports.nightmaremode.crafting.recipe.types.HammerRecipe;
import com.itlesports.nightmaremode.item.NMItems;
import emi.dev.emi.emi.api.EmiRegistry;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.stack.EmiStack;
import net.minecraft.src.Block;
import net.minecraft.src.ResourceLocation;

public final class NightmareEmiRegistry {
    public static final EmiRecipeCategory HAMMERING = new EmiRecipeCategory(
            new ResourceLocation("nightmare", "hammering"), EmiStack.of(NMItems.ironHammer));
    public static final EmiRecipeCategory CISTERN = new EmiRecipeCategory(
            new ResourceLocation("nightmare", "cistern"), EmiStack.of(NMBlocks.cistern));

    private NightmareEmiRegistry() {
    }

    public static void register(EmiRegistry registry) {
        registry.addCategory(HAMMERING);
        registry.addCategory(CISTERN);

        registry.addWorkstation(HAMMERING, EmiStack.of(NMItems.woodHammer));
        registry.addWorkstation(HAMMERING, EmiStack.of(NMItems.stoneHammer));
        registry.addWorkstation(HAMMERING, EmiStack.of(NMItems.ironHammer));
        registry.addWorkstation(HAMMERING, EmiStack.of(NMItems.diamondHammer));
        registry.addWorkstation(HAMMERING, EmiStack.of(NMItems.goldHammer));
        registry.addWorkstation(HAMMERING, EmiStack.of(NMItems.steelHammer));
        registry.addWorkstation(HAMMERING, EmiStack.of(Block.anvil));
        registry.addWorkstation(HAMMERING, EmiStack.of(NMBlocks.stoneAnvil));
        registry.addWorkstation(HAMMERING, EmiStack.of(NMBlocks.diamondAnvil));
        registry.addWorkstation(CISTERN, EmiStack.of(NMBlocks.cistern));

        registry.addDeferredRecipes(addRecipe -> {
            int index = 0;
            for (HammerRecipe recipe : HammerCraftingManager.instance.getRecipes()) {
                addRecipe.accept(new EmiHammerRecipe(recipe, index++));
            }

            index = 0;
            for (CisternRecipe recipe : CisternRecipeManager.instance.getRecipes()) {
                addRecipe.accept(new EmiCisternRecipe(recipe, index++));
            }
        });
    }
}
