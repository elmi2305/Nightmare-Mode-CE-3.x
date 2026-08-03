package com.itlesports.nightmaremode.util;

import btw.item.BTWItems;
import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.util.interfaces.INetherItem;
import com.itlesports.nightmaremode.skill.NMSkillNodes;
import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.skill.SkillNode;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemFood;
import net.minecraft.src.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class NetherItemHelper {
    private static final List<SkillTransferRule> SKILL_TRANSFER_RULES = new ArrayList<>();

    static {
        registerSkillTransfer(NMSkillNodes.NETHER_OBSIDIAN, NetherItemHelper::isDiamondTool);
        registerSkillTransfer(NMSkillNodes.NETHER_OBSIDIAN,
                stack -> stack != null && stack.getItem() instanceof ItemFood);
    }

    private NetherItemHelper() {
    }

    public static boolean survivesNetherEntry(ItemStack stack) {
        return survivesNetherEntry(stack, null);
    }

    public static boolean survivesNetherEntry(ItemStack stack, EntityPlayer player) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        if (item instanceof INetherItem) {
            return true;
        }
        if (player != null && SkillHandler.getWorldData(player.worldObj).netherVillagerTier1Complete) {
            return true;
        }
        int id = stack.itemID;
        if (id == Block.netherrack.blockID
                || id == Block.slowSand.blockID
                || id == Block.glowStone.blockID
                || id == Block.oreNetherQuartz.blockID
                || id == Block.netherBrick.blockID
                || id == Block.netherFence.blockID
                || id == Block.stairsNetherBrick.blockID
                || id == Block.blockNetherQuartz.blockID
                || id == Block.netherStalk.blockID
                || id == BTWBlocks.netherGroth.blockID
                || id == BTWBlocks.netherSludge.blockID
                || id == BTWBlocks.looseNetherBrick.blockID
                || id == BTWBlocks.looseNetherBrickSlab.blockID
                || id == BTWBlocks.looseNetherBrickStairs.blockID
                || id == BTWBlocks.nethercoalBlock.blockID
                || id == Item.glowstone.itemID
                || id == Item.blazeRod.itemID
                || id == Item.ghastTear.itemID
                || id == Item.netherStalkSeeds.itemID
                || id == Item.blazePowder.itemID
                || id == Item.magmaCream.itemID
                || id == Item.netherStar.itemID
                || id == Item.netherQuartz.itemID
                || id == Item.netherrackBrick.itemID
                || id == BTWItems.nethercoal.itemID
                || id == BTWItems.hellfireDust.itemID
                || id == BTWItems.concentratedHellfire.itemID
                || id == BTWItems.soulUrn.itemID
                || id == BTWItems.soulDust.itemID
                || id == BTWItems.netherBrick.itemID
                || id == BTWItems.netherGrothSpores.itemID
                || id == BTWItems.brimstone.itemID
                || id == BTWItems.groundNetherrack.itemID
                || id == BTWItems.soulSandPile.itemID
                || id == BTWItems.soulFlux.itemID
                || id == BTWItems.netherSludge.itemID
                || id == BTWItems.unfiredNetherBrick.itemID) {
            return true;
        }
        if (player != null) {
            for (SkillTransferRule rule : SKILL_TRANSFER_RULES) {
                if (SkillHandler.isUnlocked(player, rule.skill) && rule.items.test(stack)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void registerSkillTransfer(SkillNode skill, Predicate<ItemStack> items) {
        if (skill != null && items != null) {
            SKILL_TRANSFER_RULES.add(new SkillTransferRule(skill, items));
        }
    }

    private static boolean isDiamondTool(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        int id = stack.itemID;
        return id == Item.pickaxeDiamond.itemID
                || id == Item.axeDiamond.itemID
                || id == Item.shovelDiamond.itemID
                || id == Item.hoeDiamond.itemID
                || id == Item.swordDiamond.itemID;
    }

    private static final class SkillTransferRule {
        private final SkillNode skill;
        private final Predicate<ItemStack> items;

        private SkillTransferRule(SkillNode skill, Predicate<ItemStack> items) {
            this.skill = skill;
            this.items = items;
        }
    }
}
