package com.itlesports.nightmaremode.util;

import btw.item.items.ArcaneScrollItem;
import btw.item.BTWItems;
import btw.block.BTWBlocks;
import btw.block.blocks.AestheticOpaqueBlock;
import btw.block.blocks.AestheticVegetationBlock;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.item.items.ItemLateGameMaterial;
import com.itlesports.nightmaremode.item.items.template.NMItem;
import com.itlesports.nightmaremode.util.interfaces.INetherItem;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public final class NMFireproofItems {
    private NMFireproofItems() {
    }

    public static boolean isFireproof(ItemStack stack) {
        if (stack == null || stack.getItem() == Item.pickaxeDiamond) return false;
        return isIndestructible(stack) || stack != null
                && (stack.itemID == BTWItems.creeperOysters.itemID || stack.itemID == Item.magmaCream.itemID
                    || stack.itemID == Block.netherrack.blockID || stack.getItem() == BTWItems.netherSludge
                    || stack.getItem() == BTWItems.diamondPile
                    || isNetherEquipment(stack.getItem()) || isPhaseSteelRelated(stack) || isNetherSupply(stack));
    }

    private static boolean isNetherSupply(ItemStack stack) {
        int id = stack.itemID;
        int metadata = stack.getItemDamage();
        return stack.getItem() == BTWItems.soulUrn || stack.getItem() == BTWItems.hellfireDust
                || stack.getItem() == BTWItems.concentratedHellfire
                || id == BTWBlocks.hibachi.blockID
                || id == BTWBlocks.aestheticOpaque.blockID && metadata == AestheticOpaqueBlock.SUBTYPE_HELLFIRE
                || id == BTWBlocks.aestheticVegetation.blockID
                    && (metadata == AestheticVegetationBlock.SUBTYPE_BLOOD_WOOD_SAPLING
                        || metadata == AestheticVegetationBlock.SUBTYPE_BLOOD_LEAVES)
                || id == BTWBlocks.bloodWoodLog.blockID || id == BTWBlocks.bloodWoodLeaves.blockID
                || id == Block.planks.blockID && metadata == 4
                || (id == Block.woodSingleSlab.blockID || id == Block.woodDoubleSlab.blockID) && (metadata & 7) == 4
                || id == BTWBlocks.bloodWoodStairs.blockID
                || id == BTWBlocks.bloodWoodSidingAndCorner.blockID
                || id == BTWBlocks.bloodWoodMouldingAndDecorative.blockID;
    }

    private static boolean isNetherEquipment(Item item) {
        return item == NMItems.netherrackSword
                || item == NMItems.tungstenSword || item == NMItems.tungstenShovel
                || item == NMItems.blackglassSword || item == NMItems.blackglassScythe || item == NMItems.blackglassKnife
                || item == NMItems.coresteelSword || item == NMItems.coresteelPickaxe
                || item == NMItems.coresteelAxe || item == NMItems.coresteelShovel
                || item == NMItems.deadzoneSword || item == NMItems.deadzoneScythe
                || item == BTWItems.steelSword || item == BTWItems.steelPickaxe || item == BTWItems.steelAxe
                || item == BTWItems.steelShovel || item == BTWItems.steelHoe
                || item == BTWItems.battleaxe || item == BTWItems.mattock || item == NMItems.steelHammer
                || item == NMItems.steelFishingPole || item == NMItems.steelFishingPoleBaited;
    }

    private static boolean isPhaseSteelRelated(ItemStack stack) {
        Item item = stack.getItem();
        return item == NMItems.bloodPickaxe || item == NMItems.bloodAxe || item == NMItems.bloodShovel
                || item == NMItems.bloodHoe || item == NMItems.bloodSword
                || item == NMItems.bloodHelmet || item == NMItems.bloodChestplate
                || item == NMItems.bloodLeggings || item == NMItems.bloodBoots || item == NMItems.bloodIngot
                || item == NMItems.bloodAnvilUpgrade
                || item == NMItems.enderSword || item == NMItems.enderPickaxe || item == NMItems.enderAxe
                || item == NMItems.enderShovel || item == NMItems.enderHoe
                || item == NMItems.enderHelmet || item == NMItems.enderChestplate
                || item == NMItems.enderLeggings || item == NMItems.enderBoots
                || item == NMItems.phaseSteelCharge || item == NMItems.phaseSteelIngot || item == NMItems.phaseSteelPlate
                || item == NMItems.phaseSteelHorseArmor || item == NMItems.phaseCell || item == NMItems.phaseAnvilUpgrade
                || item == NMItems.enderCrystal || item == NMItems.enderDust || item == NMItems.enderShell
                || item == NMItems.enderShellPowder || item == NMItems.enderMechanism
                || item == NMItems.lateGameMaterial && (stack.getItemDamage() == ItemLateGameMaterial.PHASE_ATTUNEMENT_ESSENCE
                    || stack.getItemDamage() == ItemLateGameMaterial.CONVERGENCE_ESSENCE
                    || stack.getItemDamage() == ItemLateGameMaterial.NIGHTMARE_COVENANT_ESSENCE)
                || NMBlocks.phasePortalFrame != null && stack.itemID == NMBlocks.phasePortalFrame.blockID
                || NMBlocks.enderAssembler != null && stack.itemID == NMBlocks.enderAssembler.blockID
                || NMBlocks.enderCeramic != null && stack.itemID == NMBlocks.enderCeramic.blockID
                || NMBlocks.voidExtractor != null && stack.itemID == NMBlocks.voidExtractor.blockID
                || NMBlocks.minerDrillTier4 != null && stack.itemID == NMBlocks.minerDrillTier4.blockID;
    }

    public static boolean isIndestructible(ItemStack stack) {
        if (stack == null || stack.getItem() == Item.pickaxeDiamond) return false;
        Item item = stack.getItem();
        int id = stack.itemID;
        return id == NMItems.bloodOrb.itemID
                || id == Item.netherStar.itemID
                || id == NMItems.starOfTheBloodGod.itemID
                || id == Item.blazeRod.itemID
                || id == Item.blazePowder.itemID
                || item instanceof ArcaneScrollItem
                || item instanceof INetherItem
                || item instanceof NMItem && ((NMItem)item).isIndestructible();
    }
}
