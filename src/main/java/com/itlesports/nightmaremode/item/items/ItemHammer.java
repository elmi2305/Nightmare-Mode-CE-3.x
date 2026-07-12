package com.itlesports.nightmaremode.item.items;

import api.item.items.ToolItem;
import com.itlesports.nightmaremode.crafting.manager.HammerCraftingManager;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.*;

public class ItemHammer extends ToolItem {
    public ItemHammer(int itemID, EnumToolMaterial material) {
        super(itemID, 1, material);
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
    public float getStrVsBlock(ItemStack stack, World world, Block block, int i, int j, int k) {
        return this.hasHammerRecipe(world, block, i, j, k) && this.toolMaterial.getHarvestLevel() >= block.getHarvestToolLevel(world,i,j,k) ? this.efficiencyOnProperMaterial : 0.0F;
    }

    @Override
    public boolean canHarvestBlock(ItemStack stack, World world, Block block, int i, int j, int k) {
        return this.hasHammerRecipe(world, block, i, j, k) && this.toolMaterial.getHarvestLevel() >= block.getHarvestToolLevel(world,i,j,k);
    }

    @Override
    public boolean isEfficientVsBlock(ItemStack stack, World world, Block block, int i, int j, int k) {
        return this.hasHammerRecipe(world, block, i, j, k) && this.toolMaterial.getHarvestLevel() >= block.getHarvestToolLevel(world,i,j,k);
    }

    @Override
    public boolean isToolTypeEfficientVsBlockType(Block block) {
        return false;
    }

    @Override
    public String getModId() {
        return NMFields.modID;
    }

    private boolean hasHammerRecipe(World world, Block block, int i, int j, int k) {
        if (world == null || block == null) {
            return false;
        }

        return HammerCraftingManager.instance.getRecipe(block, world.getBlockMetadata(i, j, k)) != null;
    }
}
