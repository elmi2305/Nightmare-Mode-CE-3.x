package com.itlesports.nightmaremode.integration.emi;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.crafting.recipe.types.MiscRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;
import java.util.List;

public class EmiTungstenForgeRecipe extends EmiMiscRecipe {
    public EmiTungstenForgeRecipe() {
        super(new MiscRecipe(new ItemStack(NMItems.tungstenNugget), new ItemStack(NMItems.tungstenChunk),
                "Near lava"), 0);
    }

    @Override public ResourceLocation getId() { return new ResourceLocation("nightmare", "tungsten_forge/chunk"); }
    @Override public EmiRecipeCategory getCategory() { return NightmareEmiRegistry.TUNGSTEN_FORGE; }
    @Override public List<EmiIngredient> getCatalysts() { return List.of(EmiStack.of(NMBlocks.tungstenForge)); }
}
