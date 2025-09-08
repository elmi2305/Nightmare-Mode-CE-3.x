package com.itlesports.nightmaremode.item.items.bloodItems;

import btw.block.BTWBlocks;
import btw.item.items.AxeItem;
import net.minecraft.src.*;

public class ItemBloodAxe extends AxeItem implements IBloodTool{

    public ItemBloodAxe(int i, EnumToolMaterial enumToolMaterial, int iMaxUses) {
        super(i, enumToolMaterial);
        this.setMaxDamage(iMaxUses);
        this.setDamageVsEntity(4);
        this.maxStackSize = 1;
        this.setInfernalMaxNumEnchants(4);
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setBuoyant();
        this.setUnlocalizedName("nmBloodAxe");
    }

    public String getModId() {
        return "nightmare_mode";
    }

    @Override
    public float getStrVsBlock(ItemStack toolItemStack, World world, Block block, int i, int j, int k) {
        int iToolLevel = this.toolMaterial.getHarvestLevel();
        int iBlockToolLevel = block.getEfficientToolLevel(world, i, j, k);
        if (iBlockToolLevel > iToolLevel) {
            return 1.0f;
        }
        if (block.getIsProblemToRemove(toolItemStack, world, i, j, k)) {
            return 1.0f;
        }
        return super.getStrVsBlock(toolItemStack, world, block, i, j, k);
    }

    @Override
    public boolean canHarvestBlock(ItemStack stack, World world, Block block, int i, int j, int k) {
        int iToolLevel = this.toolMaterial.getHarvestLevel();
        int iBlockToolLevel = block.getHarvestToolLevel(world, i, j, k);
        if (iBlockToolLevel > iToolLevel) {
            return false;
        }
        if (block.getIsProblemToRemove(stack, world, i, j, k)) {
            return false;
        }
        if (this.isToolTypeEfficientVsBlockType(block)) {
            return true;
        }
        return super.canHarvestBlock(stack, world, block, i, j, k);
    }

    @Override
    public boolean isEfficientVsBlock(ItemStack stack, World world, Block block, int i, int j, int k) {
        int iToolLevel = this.toolMaterial.getHarvestLevel();
        int iBlockToolLevel = block.getEfficientToolLevel(world, i, j, k);
        if (iBlockToolLevel > iToolLevel) {
            return false;
        }
        if (block.getIsProblemToRemove(stack, world, i, j, k)) {
            return false;
        }
        return super.isEfficientVsBlock(stack, world, block, i, j, k);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, int iBlockID, int i, int j, int k, EntityLivingBase destroyingEntityLiving) {
        Block block;
        if (!this.getIsDamagedByVegetation() && (block = Block.blocksList[iBlockID]) != null && block.blockMaterial.getAxesTreatAsVegetation()) {
            return true;
        }
        return super.onBlockDestroyed(stack, world, iBlockID, i, j, k, destroyingEntityLiving);
    }

    @Override
    public float getExhaustionOnUsedToHarvestBlock(int iBlockID, World world, int i, int j, int k, int iBlockMetadata) {
        Block block;
        if (!this.getConsumesHungerOnZeroHardnessVegetation() && (block = Block.blocksList[iBlockID]) != null && (double)block.getBlockHardness(world, i, j, k) == 0.0 && block.blockMaterial.getAxesTreatAsVegetation()) {
            return 0.0f;
        }
        return super.getExhaustionOnUsedToHarvestBlock(iBlockID, world, i, j, k, iBlockMetadata);
    }

    @Override
    public boolean canToolStickInBlock(ItemStack stack, Block block, World world, int i, int j, int k) {
        if (block.blockMaterial == BTWBlocks.logMaterial || block.blockMaterial == BTWBlocks.plankMaterial) {
            return true;
        }
        return super.canToolStickInBlock(stack, block, world, i, j, k);
    }
}
