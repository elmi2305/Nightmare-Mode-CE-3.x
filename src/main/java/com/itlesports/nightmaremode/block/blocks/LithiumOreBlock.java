package com.itlesports.nightmaremode.block.blocks;

import api.item.items.PickaxeItem;
import api.item.util.ItemUtils;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class LithiumOreBlock extends ProcessingOreBlock {
    public LithiumOreBlock(int id) {
        super(id, NMItems.lithiumRaw.itemID, 2);
    }



    @Override public int getHarvestToolLevel(IBlockAccess world, int x, int y, int z) {
        return this.getRequiredToolLevelForOre(world, x, y, z);
    }

    public boolean isValidMiningTool(ItemStack stack, World world, int x, int y, int z) {
        if (stack == null) return false;
        Item item = stack.getItem();
        return (item instanceof PickaxeItem || item instanceof ItemPickaxe)
                && item.canHarvestBlock(stack, world, this, x, y, z);
    }

    public boolean mineDeposit(World world, EntityPlayer player, int x, int y, int z, int side) {
        if (world.isRemote || !this.isValidMiningTool(player.getCurrentEquippedItem(), world, x, y, z)) return false;
        int metadata = world.getBlockMetadata(x, y, z);
        int count = 1 + world.rand.nextInt(2);
        if (SkillHandler.getPlayerData(player).doubleLithiumDrops) count *= 2;
        ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z,
                new ItemStack(NMItems.lithiumRaw, com.itlesports.nightmaremode.util.EasyBalance.resourceCount(count)), side);
        player.addStat(StatList.mineBlockStatArray[this.blockID], 1);
        player.addHarvestBlockExhaustion(this.blockID, x, y, z, metadata);
        if ((metadata >> 2) >= 3) {
            world.setBlockToAir(x, y, z);
            SkillHandler.incrementBlocksMined(player, this.blockID, metadata & 3);
        } else world.setBlockMetadataWithNotify(x, y, z, metadata + 4, 3);
        return true;
    }

    @Override public int getMetadataConversionForStrataLevel(int strata, int metadata) {
        return (metadata & 12) | strata;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
        int shade = 255 - (world.getBlockMetadata(x, y, z) >> 2) * 24;
        return (shade << 16) | (shade << 8) | shade;
    }

    @Override public boolean canConvertBlock(ItemStack stack, World world, int x, int y, int z) { return false; }
    @Override public boolean convertBlock(ItemStack stack, World world, int x, int y, int z, int side) { return false; }
    @Override public int quantityDropped(Random random) { return 0; }
    @Override public int quantityDroppedOnConversion(Random random) { return 0; }
    @Override public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int fortune) {}

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int metadata) {
        return super.getIcon(side, metadata & 3);
    }
}
