package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.block.blocks.templates.NMBlockOre;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class ProcessingOreBlock extends NMBlockOre {
    private final int dropItemID;
    private final int requiredToolLevel;

    public ProcessingOreBlock(int id, int dropItemID, int requiredToolLevel) {
        super(id);
        this.dropItemID = dropItemID;
        this.requiredToolLevel = requiredToolLevel;
        this.setHardness(3.0F);
        this.setResistance(5.0F);
        this.setPicksEffectiveOn();
    }

    @Override
    public boolean canBeMined(IBlockAccess world, int i, int j, int k) {
        return true;
    }

    @Override
    public int idDropped(int metadata, Random rand, int fortune) {
        return this.dropItemID;
    }

    @Override
    public int quantityDropped(Random random) {
        return com.itlesports.nightmaremode.util.EasyBalance.resourceCount(1);
    }

    @Override public int quantityDroppedOnConversion(Random random) {
        return com.itlesports.nightmaremode.util.EasyBalance.resourceCount(1);
    }

    @Override
    protected void dropBlockAsItem_do(World world, int x, int y, int z, ItemStack stack) {
        super.dropBlockAsItem_do(world, x, y, z, this.withMiningBonus(world, x, y, z, stack));
    }

    @Override
    protected void ejectItemsOnGoodPickConversion(ItemStack tool, World world, int x, int y, int z,
                                                   int metadata, int side) {
        if (world.isRemote) return;
        ItemStack result = new ItemStack(this.dropItemID, this.quantityDropped(world.rand), this.damageDropped(metadata));
        api.item.util.ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z,
                this.withMiningBonus(world, x, y, z, result), side);
    }

    private ItemStack withMiningBonus(World world, int x, int y, int z, ItemStack stack) {
        if (world.isRemote || stack == null || stack.itemID != this.dropItemID) return stack;
        EntityPlayer player = com.itlesports.nightmaremode.util.OreMiningContext.miner(world, x, y, z);
        if (player == null || player.capabilities.isCreativeMode) return stack;
        ItemStack tool = player.getCurrentEquippedItem();
        if (tool == null || !tool.getItem().canHarvestBlock(tool, world, this, x, y, z)
                || EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, tool) > 0) return stack;
        ItemStack result = stack.copy();
        if (this == NMBlocks.lithiumOre && SkillHandler.getPlayerData(player).doubleLithiumDrops) {
            result.stackSize *= 2;
        } else if (this == NMBlocks.nickelOre) {
            result.stackSize = com.itlesports.nightmaremode.util.NickelMiningDrops.count(result.stackSize,
                    SkillHandler.getPlayerData(player).doubleNickelRockChance, tool.getItem() == Item.pickaxeDiamond, world.rand);
        }
        return result;
    }

    @Override
    public int getRequiredToolLevelForOre(IBlockAccess blockAccess, int i, int j, int k) {
        return this.requiredToolLevel;
    }

    @Override
    public int idDroppedOnConversion(boolean dropPiles, int metadata) {
        return this.dropItemID;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void getSubBlocks(int blockID, CreativeTabs creativeTabs, List list) {
        list.add(new ItemStack(blockID, 1, 0));
    }
}
