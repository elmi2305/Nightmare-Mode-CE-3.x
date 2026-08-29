package com.itlesports.nightmaremode.mixin;

import api.item.items.AxeItem;
import btw.block.BTWBlocks;
import btw.block.blocks.MushroomBlockBrown;
import btw.block.blocks.MushroomCapBlock;
import btw.block.blocks.legacy.LegacyMushroomCapBlock;
import btw.item.BTWItems;
import btw.item.items.ChiselItem;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.template.NMItem;
import com.itlesports.nightmaremode.integration.emi.NightmareEmiRegistry;
import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.api.EmiRegistry;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.plugin.BTWPlugin;
import net.minecraft.src.Block;
import net.minecraft.src.BlockMushroom;
import net.minecraft.src.BlockMushroomCap;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(BTWPlugin.class)
public abstract class BTWPluginMixin {
    @Shadow protected abstract void info(EmiRegistry registry, Item item, String info);
    @Shadow protected abstract void info(EmiRegistry registry, Block block, String info);
    @Shadow protected abstract void info(EmiRegistry registry, Item item, int metadata, String info);
    @Shadow protected abstract void info(EmiRegistry registry, Block block, int metadata, String info);

    @Inject(method = "register", at = @At("TAIL"), remap = false)
    private void registerNightmareRecipes(EmiRegistry registry, CallbackInfo ci) {
        NightmareEmiRegistry.register(registry);
    }

    @Inject(method = "addWorldRecipes", at = @At("TAIL"), remap = false)
    private void correctWorldInteractionRecipes(EmiRegistry registry, CallbackInfo ci) {
        List<EmiStack> logTools = getLogConversionTools();
        List<EmiStack> stumpTools = getStumpConversionTools();
        List<EmiStack> logs = getLogs();

        registry.addRecipe(EmiWorldInteractionRecipe.builder().id(worldRecipeId("crafting_stump"))
                .leftInput(EmiIngredient.of(stumpTools)).rightInput(EmiIngredient.of(logs), false, sw -> {
                    sw.appendTooltip(EmiPort.translatable("emi.world_interaction.btw.crafting_stump"));
                    return sw;
                }).output(EmiStack.of(BTWBlocks.workStump)).supportsRecipeTree(false).build());

        registry.addRecipe(EmiWorldInteractionRecipe.builder().id(worldRecipeId("string_from_web"))
                .leftInput(EmiStack.of(BTWItems.sharpStone)).rightInput(EmiStack.of(BTWBlocks.web), false)
                .output(EmiStack.of(NMItems.spiderSilk)).supportsRecipeTree(true).build());

        registry.addRecipe(EmiWorldInteractionRecipe.builder().id(worldRecipeId("brick_sundrying"))
                .leftInput(EmiStack.of(BTWItems.unfiredCrudeBrick)).rightInput(EmiStack.of(Item.pocketSundial), false, sw -> {
                    sw.appendTooltip(EmiPort.literal("Requires 36,000 ticks of direct daylight (about three full sunny days)."));
                    return sw;
                }).output(EmiStack.of(Item.brick)).supportsRecipeTree(true).build());

        replaceLogInteractionRecipe(registry, "shaft_from_chiseling", logTools, logs, EmiStack.of(Item.stick),
                "emi.world_interaction.btw.shaft_from_chiseling");
        replaceLogInteractionRecipe(registry, "sawdust_from_chiseling", logTools, logs, EmiStack.of(BTWItems.sawDust),
                "emi.world_interaction.btw.sawdust_from_chiseling");
        replaceLogInteractionRecipe(registry, "oak_bark_from_chiseling", logTools,
                List.of(EmiStack.of(new ItemStack(Block.wood, 1, 0))), EmiStack.of(new ItemStack(BTWItems.bark, 1, 0)),
                "emi.world_interaction.btw.bark_from_chiseling");
        replaceLogInteractionRecipe(registry, "spruce_bark_from_chiseling", logTools,
                List.of(EmiStack.of(new ItemStack(Block.wood, 1, 1))), EmiStack.of(new ItemStack(BTWItems.bark, 1, 1)),
                "emi.world_interaction.btw.bark_from_chiseling");
        replaceLogInteractionRecipe(registry, "birch_bark_from_chiseling", logTools,
                List.of(EmiStack.of(new ItemStack(Block.wood, 1, 2))), EmiStack.of(new ItemStack(BTWItems.bark, 1, 2)),
                "emi.world_interaction.btw.bark_from_chiseling");
        replaceLogInteractionRecipe(registry, "jungle_bark_from_chiseling", logTools,
                List.of(EmiStack.of(new ItemStack(Block.wood, 1, 3))), EmiStack.of(new ItemStack(BTWItems.bark, 1, 3)),
                "emi.world_interaction.btw.bark_from_chiseling");
        registry.addRecipe(EmiWorldInteractionRecipe.builder().id(worldRecipeId("wood_clump_from_chiseling"))
                .leftInput(EmiIngredient.of(logTools)).rightInput(EmiIngredient.of(logs), false)
                .output(EmiStack.of(new ItemStack(NMItems.woodClump, 1, 199))).supportsRecipeTree(true).build());

        replaceStoneBrickRecipe(registry, "stone_brick_from_chiseling", EmiIngredient.of(List.of(
                EmiStack.of(BTWItems.ironChisel), EmiStack.of(BTWItems.diamondChisel))), 0);
        replaceStoneBrickRecipe(registry, "deepslate_brick_from_chiseling", EmiIngredient.of(List.of(
                EmiStack.of(BTWItems.ironChisel), EmiStack.of(BTWItems.diamondChisel))), 1);
        replaceStoneBrickRecipe(registry, "blackstone_brick_from_chiseling", EmiStack.of(BTWItems.diamondChisel), 2);
    }

    @Unique
    private static void replaceLogInteractionRecipe(EmiRegistry registry, String id, List<EmiStack> tools,
                                                    List<EmiStack> logs, EmiStack output, String tooltip) {
        registry.addRecipe(EmiWorldInteractionRecipe.builder().id(worldRecipeId(id))
                .leftInput(EmiIngredient.of(tools)).rightInput(EmiIngredient.of(logs), false, sw -> {
                    sw.appendTooltip(EmiPort.translatable(tooltip));
                    return sw;
                }).output(output).supportsRecipeTree(true).build());
    }

    @Unique
    private static void replaceStoneBrickRecipe(EmiRegistry registry, String id, EmiIngredient chisel, int strata) {
        registry.addRecipe(EmiWorldInteractionRecipe.builder().id(worldRecipeId(id))
                .leftInput(chisel).rightInput(EmiStack.of(new ItemStack(Block.stone, 1, strata)), false)
                .output(EmiStack.of(NMItems.roughStoneBrick)).supportsRecipeTree(true).build());
    }

    @Unique
    private static ResourceLocation worldRecipeId(String name) {
        return new ResourceLocation("emi", "/world/block_interaction/btw/" + name);
    }

    @Redirect(method = "addWorldRecipes", at = @At(value = "INVOKE",
            target = "Lemi/dev/emi/emi/api/EmiRegistry;addRecipe(Lemi/dev/emi/emi/api/recipe/EmiRecipe;)V"), remap = false)
    private void replaceModifiedWorldRecipes(EmiRegistry registry, EmiRecipe recipe) {
        ResourceLocation id = recipe.getId();
        if (id == null || !isModifiedWorldRecipe(id.getResourcePath())) {
            registry.addRecipe(recipe);
        }
    }

    @Unique
    private static boolean isModifiedWorldRecipe(String path) {
        return path.equals("/world/block_interaction/btw/crafting_stump")
                || path.equals("/world/block_interaction/btw/string_from_web")
                || path.equals("/world/block_interaction/btw/brick_sundrying")
                || path.equals("/world/block_interaction/btw/shaft_from_chiseling")
                || path.equals("/world/block_interaction/btw/sawdust_from_chiseling")
                || path.equals("/world/block_interaction/btw/oak_bark_from_chiseling")
                || path.equals("/world/block_interaction/btw/spruce_bark_from_chiseling")
                || path.equals("/world/block_interaction/btw/birch_bark_from_chiseling")
                || path.equals("/world/block_interaction/btw/jungle_bark_from_chiseling")
                || path.equals("/world/block_interaction/btw/stone_brick_from_chiseling")
                || path.equals("/world/block_interaction/btw/deepslate_brick_from_chiseling")
                || path.equals("/world/block_interaction/btw/blackstone_brick_from_chiseling");
    }

    @Unique
    private static List<EmiStack> getLogs() {
        ArrayList<ItemStack> logs = new ArrayList<>();
        Block.wood.getSubBlocks(Block.wood.blockID, Block.wood.getCreativeTabToDisplayOn(), logs);
        return logs.stream().map(EmiStack::of).toList();
    }

    @Unique
    private static List<EmiStack> getLogConversionTools() {
        List<EmiStack> tools = new ArrayList<>();
        for (Item item : Item.itemsList) {
            if (item != null && (item == NMItems.sharpTwig || item == NMItems.sharpBarkTwig
                    || item instanceof ChiselItem || item instanceof AxeItem)) {
                tools.add(EmiStack.of(item));
            }
        }
        return tools;
    }

    @Unique
    private static List<EmiStack> getStumpConversionTools() {
        List<EmiStack> tools = new ArrayList<>();
        for (Item item : Item.itemsList) {
            if (item != null && (item == NMItems.sharpTwig || item == NMItems.sharpBarkTwig
                    || item == BTWItems.ironChisel || item == BTWItems.diamondChisel || item instanceof AxeItem)) {
                tools.add(EmiStack.of(item));
            }
        }
        return tools;
    }

    @Inject(method = "addInfoRecipes", at = @At("TAIL"),remap = false)
    private void addNightmareInfo(EmiRegistry registry, CallbackInfo ci){
        this.info(registry, NMItems.rpg, 0, "nm.rpg.info");
        this.info(registry, NMItems.rifle, 0, "nm.rifle.info");
        this.info(registry, NMItems.bandage, 0, "nm.bandage.info");
        this.info(registry, NMItems.witchLocator, 0, "nm.alchemydust.info");
        this.info(registry, NMItems.ironKnittingNeedles, 0, "nm.ironneedles.info");
        this.info(registry, NMItems.bloodOrb, 0, "nm.bloodorb.info");
        this.info(registry, NMItems.bloodPickaxe, 0, "nm.bloodpickaxe.info");
        this.info(registry, NMItems.bloodAxe, 0, "nm.bloodtool.info");
        this.info(registry, NMItems.bloodShovel, 0, "nm.bloodtool.info");
        this.info(registry, NMItems.bloodHoe, 0, "nm.bloodtool.info");
        this.info(registry, NMItems.bloodSword, 0, "nm.bloodsword.info");
        this.info(registry, NMItems.bloodHelmet, 0, "nm.bloodarmor.info");
        this.info(registry, NMItems.bloodChestplate, 0, "nm.bloodarmor.info");
        this.info(registry, NMItems.bloodLeggings, 0, "nm.bloodarmor.info");
        this.info(registry, NMItems.bloodBoots, 0, "nm.bloodarmor.info");
        this.info(registry, NMItems.bloodIngot, 0, "nm.bloodingot.info");
        this.info(registry, NMItems.darksunFragment, 0, "nm.eclipseshard.info");
        this.info(registry, NMItems.bloodMilk, 0, "nm.bloodMilk.info");
        this.info(registry, NMItems.creeperChop, 0, "nm.creeperChop.info");
        this.info(registry, NMItems.voidSack, 0, "nm.voidSack.info");
        this.info(registry, NMItems.charredFlesh, 0, "nm.charredFlesh.info");
        this.info(registry, NMItems.spiderFangs, 0, "nm.spiderFangs.info");
        this.info(registry, NMItems.fireRod, 0, "nm.fireRod.info");
        this.info(registry, NMItems.waterRod, 0, "nm.waterRod.info");
        this.info(registry, NMItems.sulfur, 0, "nm.sulfur.info");
        this.info(registry, NMItems.creeperTear, 0, "nm.creeperTear.info");
        this.info(registry, NMItems.silverLump, 0, "nm.silverLump.info");
        this.info(registry, NMItems.witheredBone, 0, "nm.witheredBone.info");
        this.info(registry, NMItems.voidMembrane, 0, "nm.voidMembrane.info");
        this.info(registry, NMItems.decayedFlesh, 0, "nm.decayedFlesh.info");
        this.info(registry, NMItems.ghastTentacle, 0, "nm.ghastTentacle.info");
        this.info(registry, NMItems.elementalRod, 0, "nm.elementalRod.info");
        this.info(registry, NMItems.shadowRod, 0, "nm.shadowRod.info");
        this.info(registry, NMItems.speedCoil, 0, "nm.speedCoil.info");
        this.info(registry, NMItems.starOfTheBloodGod, 0, "nm.bloodstar.info");
        this.info(registry, NMItems.calamari, 0, "nm.calamari.info");
        this.info(registry, NMItems.eclipseBow, 0, "nm.eclipsebow.info");
        this.info(registry, NMItems.magicArrow, 0, "nm.magicarrow.info");
        this.info(registry, NMItems.ironFishingPole, 0, "nm.ironfishingrod.info");
        this.info(registry, NMItems.templeLocator, 0, "nm.ancientsand.info");
        this.info(registry, NMItems.refinedDiamondIngot, 0, "nm.refineddiamond.info");
        this.info(registry, NMItems.witherSoul, 0, "nm.withersoul.info");
        this.info(registry, NMItems.obsidianShard, 0, "nm.obsidianshard.info");
        this.info(registry, NMItems.heatResistantHelmet, 0, "nm.heatResistantArmor.info");
        this.info(registry, NMItems.divingTank, 0, "nm.divingTank.info");
        this.info(registry, NMItems.saturatedCoresteelCharge, 0, "nm.coresteelCooling.info");
        this.info(registry, NMItems.coresteelChestplate, 0, "nm.coresteelArmor.info");
        this.info(registry, NMItems.deadzoneChestplate, 0, "nm.deadzoneArmor.info");
        this.info(registry, NMItems.sunReservoir, 0, "nm.sunArmor.info");
        this.info(registry, NMItems.signalChestplate, 0, "nm.signalArmor.info");
        this.info(registry, NMItems.azureChestplate, 0, "nm.azureArmor.info");
        this.info(registry, NMItems.prismaticChestplate, 0, "nm.prismaticArmor.info");
        this.info(registry, NMItems.refinedPrismaChestplate, 0, "nm.refinedPrismaArmor.info");
        this.info(registry, NMItems.verdantChestplate, 0, "nm.verdantArmor.info");
        this.info(registry, NMItems.glassChestplate, 0, "nm.glassArmor.info");
        this.info(registry, NMItems.blackglassChestplate, 0, "nm.blackglassArmor.info");
        this.info(registry, NMItems.quartzglassChestplate, 0, "nm.quartzglassArmor.info");
        this.info(registry, NMItems.darkChestplate, 0, "nm.darkArmor.info");
        this.info(registry, NMItems.quicksilverChestplate, 0, "nm.quicksilverArmor.info");
        this.info(registry, NMItems.anchorChestplate, 0, "nm.anchorArmor.info");

        // blocks

        this.info(registry, NMBlocks.steelOre, 0, "nm.steelOre.info");
        this.info(registry, NMBlocks.bloodChest, 0, "nm.bloodChest.info");
        this.info(registry, NMBlocks.steelLocker, 0, "nm.steelLocker.info");
        this.info(registry, NMBlocks.blockRoad, 0, "nm.blockRoad.info");
        this.info(registry, NMBlocks.blockAsphalt, 0, "nm.blockAsphalt.info");
        this.info(registry, NMBlocks.stoneLadder, 0, "nm.upgradedLadder.info");
        this.info(registry, NMBlocks.ironLadder, 0, "nm.upgradedLadder.info");
        this.info(registry, NMBlocks.hellforge, 0, "nm.hellforge.info");
        this.info(registry, NMBlocks.asphaltLayer, 0, "nm.blockAsphalt.info");
        this.info(registry, NMBlocks.bloodSaw, 0, "nm.bloodSaw.info");
        this.info(registry, NMBlocks.disenchantmentTable, 0, "nm.disenchantmentTable.info");
        this.info(registry, NMBlocks.cistern, 0, "nm.cistern.info");
        this.info(registry, NMBlocks.cisternInterface, 0, "nm.cisternInterface.info");
        this.info(registry, NMBlocks.cisternStirrer, 0, "nm.cisternStirrer.info");
        this.info(registry, NMBlocks.cisternDrain, 0, "nm.cisternDrain.info");
        this.info(registry, NMBlocks.terrainExtractor, 0, "nm.potassiumExtractor.info");
        this.info(registry, NMBlocks.terrainExtractor, 1, "nm.nitrogenExtractor.info");
        this.info(registry, NMBlocks.terrainExtractor, 2, "nm.moistureExtractor.info");
        this.info(registry, NMBlocks.terrainExtractor, 3, "nm.porosityExtractor.info");
        this.info(registry, NMBlocks.terrainExtractor, 4, "nm.acidityExtractor.info");
        this.info(registry, NMItems.potassiumCrystal, 0, "nm.potassiumCrystal.info");
        this.info(registry, NMItems.nitrogenCrystal, 0, "nm.nitrogenCrystal.info");
        this.info(registry, NMItems.acidCrystal, 0, "nm.acidCrystal.info");
        this.info(registry, NMItems.porosityAggregate, 0, "nm.porosityAggregate.info");

        // vanilla blocks
        this.info(registry, Block.obsidian, 0, "nm.obsidian.info");
        this.info(registry, Block.obsidian, 1, "nm.crudeObsidian.info");
        this.info(registry, BTWBlocks.carvedPumpkin, 1, "nm.pumpkin.info");

        // vanilla items
        this.info(registry, Item.horseArmorDiamond, 0, "nm.horseArmor.info");
        this.info(registry, Item.horseArmorGold, 0, "nm.horseArmor.info");
        this.info(registry, Item.horseArmorIron, 0, "nm.horseArmor.info");
        this.info(registry, Item.appleGold, 0, "nm.goldenApple.info");
        this.info(registry, Item.appleGold, 1, "nm.goldenAppleEnchanted.info");
        this.info(registry, BTWItems.brownMushroom, 0, "nm.brownMushroom.info");

    }
}
