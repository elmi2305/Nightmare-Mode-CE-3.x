package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.util.NMBlockBreakingRules;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class BlockNetherOreNode extends BlockOreNode {
    public BlockNetherOreNode(int id, int droppedItemId, int requiredDrillTier, float hardness,
                              String name, String texture) {
        super(id, droppedItemId, Block.obsidian, requiredDrillTier, name, texture);
        this.setHardness(hardness);
    }

    @Override
    public boolean isValidMiningTool(ItemStack held, World world, int x, int y, int z) {
        return NMBlockBreakingRules.isSteelMiningTool(held);
    }
}
