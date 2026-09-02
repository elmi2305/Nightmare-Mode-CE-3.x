package com.itlesports.nightmaremode.integration.emi;

import btw.item.BTWItems;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.crafting.manager.BrewingStandRecipeManager;
import com.itlesports.nightmaremode.crafting.manager.CisternRecipeManager;
import com.itlesports.nightmaremode.crafting.manager.EnderAssemblerRecipeManager;
import com.itlesports.nightmaremode.crafting.manager.HammerCraftingManager;
import com.itlesports.nightmaremode.crafting.manager.MiscRecipeManager;
import com.itlesports.nightmaremode.crafting.manager.WashingRecipeManager;
import com.itlesports.nightmaremode.crafting.recipe.types.CisternRecipe;
import com.itlesports.nightmaremode.crafting.recipe.types.EnderAssemblerRecipe;
import com.itlesports.nightmaremode.crafting.recipe.types.BrewingStandRecipe;
import com.itlesports.nightmaremode.crafting.recipe.types.HammerRecipe;
import com.itlesports.nightmaremode.crafting.recipe.types.MiscRecipe;
import com.itlesports.nightmaremode.crafting.recipe.types.WashingRecipe;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.NMTags;
import com.itlesports.nightmaremode.mixin.EmiDataAccessor;
import com.itlesports.nightmaremode.util.NMFields;
import emi.dev.emi.emi.api.EmiRegistry;
import emi.dev.emi.emi.api.plugin.BTWPlugin;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.data.EmiRemoveFromIndex;
import emi.dev.emi.emi.recipe.EmiBrewingRecipe;
import emi.dev.emi.emi.recipe.btw.EmiProgressiveRecipe;
import emi.dev.emi.emi.runtime.EmiHidden;
import emi.shims.java.com.unascribed.retroemi.RetroEMI;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;

public final class NightmareEmiRegistry {
    public static final EmiRecipeCategory HAMMERING = new EmiRecipeCategory(
            new ResourceLocation("nightmare", "hammering"), EmiStack.of(NMItems.ironHammer));
    public static final EmiRecipeCategory CISTERN = new EmiRecipeCategory(
            new ResourceLocation("nightmare", "cistern"), EmiStack.of(Item.cauldron));
    public static final EmiRecipeCategory ENDER_ASSEMBLER = new EmiRecipeCategory(
            new ResourceLocation("nightmare", "ender_assembler"), EmiStack.of(NMBlocks.enderAssembler));
    public static final EmiRecipeCategory WASHING = new EmiRecipeCategory(
            new ResourceLocation("nightmare", "washing"), EmiStack.of(Item.bucketWater));
    public static final EmiRecipeCategory MISC = new EmiRecipeCategory(
            new ResourceLocation("nightmare", "misc"), EmiStack.of(Item.paper));

    private NightmareEmiRegistry() {
    }

    public static void register(EmiRegistry registry) {
        unhideWoodenTools();
        allowIngredientInRecipes(BTWItems.ancientProphecy);

        registry.addCategory(HAMMERING);
        registry.addCategory(CISTERN);
        registry.addCategory(ENDER_ASSEMBLER);
        registry.addCategory(WASHING);
        registry.addCategory(MISC);

        registry.addWorkstation(HAMMERING, EmiIngredient.of(NMTags.hammers));
        registry.addWorkstation(HAMMERING, EmiIngredient.of(NMTags.anvils));
        registry.addWorkstation(CISTERN, EmiStack.of(NMBlocks.cistern));
        registry.addWorkstation(CISTERN, EmiStack.of(NMBlocks.cisternStirrer));
        registry.addWorkstation(ENDER_ASSEMBLER, EmiStack.of(NMBlocks.enderAssembler));
        registry.addWorkstation(WASHING, EmiStack.of(Item.bucketWater));

        registry.addDeferredRecipes(addRecipe -> {
            int index = 0;
            for (HammerRecipe recipe : HammerCraftingManager.instance.getRecipes()) {
                addRecipe.accept(new EmiHammerRecipe(recipe, index++));
            }

            index = 0;
            for (CisternRecipe recipe : CisternRecipeManager.instance.getRecipes()) {
                addRecipe.accept(new EmiCisternRecipe(recipe, index++));
            }

            index = 0;
            for (EnderAssemblerRecipe recipe : EnderAssemblerRecipeManager.instance.getRecipes()) {
                addRecipe.accept(new EmiEnderAssemblerRecipe(recipe, index++));
            }

            index = 0;
            for (WashingRecipe recipe : WashingRecipeManager.instance.getRecipes()) {
                addRecipe.accept(new EmiWashingRecipe(recipe, index++));
            }

            index = 0;
            for (MiscRecipe recipe : MiscRecipeManager.instance.getRecipes()) {
                addRecipe.accept(new EmiMiscRecipe(recipe, index++));
            }

            index = 0;
            for (BrewingStandRecipe recipe : BrewingStandRecipeManager.instance.getRecipes()) {
                addRecipe.accept(new EmiBrewingRecipe(
                        EmiStack.of(recipe.getBottleInput()),
                        createBrewingIngredient(recipe.getIngredient()),
                        EmiStack.of(recipe.getOutput()),
                        new ResourceLocation(NMFields.modID, "brewing_stand/" + index++)));
            }

        });
        // progressive crafting
        BTWPlugin.addRecipeSafe(registry, () -> new EmiProgressiveRecipe(new ResourceLocation(NMFields.modID, "wood_clump"), new ItemStack(NMItems.woodClump), new ItemStack(Item.stick)));
        BTWPlugin.addRecipeSafe(registry, () -> new EmiProgressiveRecipe(new ResourceLocation(NMFields.modID, "twig_sharpening"), new ItemStack(NMItems.twigSharpening), new ItemStack(NMItems.sharpTwig)));
        BTWPlugin.addRecipeSafe(registry, () -> new EmiProgressiveRecipe(new ResourceLocation(NMFields.modID, "sharp_twig_bark_wrapping"), new ItemStack(NMItems.sharpTwigBarkWrapping), new ItemStack(NMItems.sharpBarkTwig)));
        BTWPlugin.addRecipeSafe(registry, () -> new EmiProgressiveRecipe(new ResourceLocation(NMFields.modID, "clean_crystal_shard"), new ItemStack(NMItems.crystalCleanShard), new ItemStack(NMItems.crystalPolishedShard)));
        BTWPlugin.addRecipeSafe(registry, () -> new EmiProgressiveRecipe(new ResourceLocation(NMFields.modID, "fish_flesh"), new ItemStack(NMItems.fishFlesh), new ItemStack(NMItems.debonedRawFish)));
        BTWPlugin.addRecipeSafe(registry, () -> new EmiProgressiveRecipe(new ResourceLocation(NMFields.modID, "flint_axe_crafting"), new ItemStack(NMItems.flintAxeCrafting), new ItemStack(NMItems.flintAxe)));
        BTWPlugin.addRecipeSafe(registry, () -> new EmiProgressiveRecipe(new ResourceLocation(NMFields.modID, "crude_string_crafting"), new ItemStack(NMItems.crudeStringCrafting), new ItemStack(NMItems.crudeString)));
        BTWPlugin.addRecipeSafe(registry, () -> new EmiProgressiveRecipe(new ResourceLocation(NMFields.modID, "string_crafting"), new ItemStack(NMItems.stringCrafting), new ItemStack(Item.silk)));
        BTWPlugin.addRecipeSafe(registry, () -> new EmiProgressiveRecipe(new ResourceLocation(NMFields.modID, "wood_cup_crafting"), new ItemStack(NMItems.woodCupCrafting), new ItemStack(NMItems.woodCup)));
        BTWPlugin.addRecipeSafe(registry, () -> new EmiProgressiveRecipe(new ResourceLocation(NMFields.modID, "unshaped_wet_clay_brick"), new ItemStack(NMItems.unshapedWetClayBrick), new ItemStack(BTWItems.unfiredCrudeBrick)));
        BTWPlugin.addRecipeSafe(registry, () -> new EmiProgressiveRecipe(new ResourceLocation(NMFields.modID, "reed_peeling"), new ItemStack(NMItems.reedPeeling), new ItemStack(NMItems.reedStem)));
        BTWPlugin.addRecipeSafe(registry, () -> new EmiProgressiveRecipe(new ResourceLocation(NMFields.modID, "pighide_string_crafting"), new ItemStack(NMItems.pighideStringCrafting), new ItemStack(NMItems.pighideString)));

    }

    private static void unhideWoodenTools() {
        unhideItem(Item.swordWood);
        unhideItem(Item.shovelWood);
        unhideItem(Item.pickaxeWood);
        unhideItem(Item.axeWood);
        unhideItem(Item.hoeWood);
        unhideItem(Item.minecartPowered);
    }

    private static EmiIngredient createBrewingIngredient(ItemStack stack) {
        return stack.getItemDamage() == BrewingStandRecipe.ANY_METADATA
                ? RetroEMI.wildcardIngredient(stack)
                : EmiStack.of(stack);
    }

    private static void unhideItem(Item item) {
        EmiStack stack = EmiStack.of(item);
        EmiRemoveFromIndex.removed.removeIf(hidden -> EmiIngredient.areEqual(hidden, stack));
        EmiDataAccessor.getHiddenStacks().removeIf(hidden -> EmiIngredient.areEqual(hidden, stack));
        EmiHidden.disabledStacks.removeIf(hidden -> EmiIngredient.areEqual(hidden, stack));
    }

    /**
     * Keeps a deliberately hidden item out of EMI's item index while allowing recipes that
     * consume it to survive EMI's disabled-ingredient recipe filter.
     */
    private static void allowIngredientInRecipes(Item item) {
        EmiStack stack = EmiStack.of(item);
        EmiDataAccessor.getHiddenStacks().removeIf(hidden -> EmiIngredient.areEqual(hidden, stack));
        EmiHidden.disabledStacks.removeIf(hidden -> EmiIngredient.areEqual(hidden, stack));

        EmiIngredient recipeIngredient = item.getHasSubtypes()
                ? RetroEMI.wildcardIngredient(new ItemStack(item, 1, Short.MAX_VALUE))
                : stack;
        if (EmiHidden.isDisabled(recipeIngredient)) {
            throw new IllegalStateException(
                    item.getUnlocalizedName() + " is still disabled as an EMI recipe ingredient");
        }
    }
}
