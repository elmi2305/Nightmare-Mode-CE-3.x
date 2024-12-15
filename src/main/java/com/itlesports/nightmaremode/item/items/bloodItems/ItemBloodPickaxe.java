package com.itlesports.nightmaremode.item.items.bloodItems;

import btw.block.BTWBlocks;
import btw.item.items.PickaxeItem;
import net.minecraft.src.*;

public class ItemBloodPickaxe extends PickaxeItem {
    public ItemBloodPickaxe(int i, EnumToolMaterial enumToolMaterial, int iMaxUses) {
        super(i, enumToolMaterial);
        this.maxStackSize = 1;
        this.setMaxDamage(iMaxUses);
        this.setDamageVsEntity(3);
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setBuoyant();
        this.setUnlocalizedName("nmBloodPickaxe");
        this.efficiencyOnProperMaterial = 6.0f;
    }

    @Override
    public boolean canHarvestBlock(ItemStack stack, World world, Block block, int i, int j, int k) {
        int iToolLevel = 4;
        int iBlockToolLevel = block.getHarvestToolLevel(world, i, j, k);
        if (iBlockToolLevel > iToolLevel) {
            return false;
        }
        if (block == Block.obsidian) {
            return this.toolMaterial.getHarvestLevel() >= 3;
        }
        if (block == Block.blockDiamond || block == BTWBlocks.diamondIngot || block == Block.blockEmerald || block == Block.blockGold) {
            return this.toolMaterial.getHarvestLevel() >= 2;
        }
        if (block == Block.blockIron || block == Block.blockLapis) {
            return this.toolMaterial.getHarvestLevel() >= 1;
        }
        return block.blockMaterial == Material.rock || block.blockMaterial == Material.iron || block.blockMaterial == Material.anvil || block.blockMaterial == BTWBlocks.netherRockMaterial;
    }
}
