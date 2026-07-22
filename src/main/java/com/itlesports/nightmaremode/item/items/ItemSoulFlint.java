package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.item.items.template.NetherItem;
import net.minecraft.src.*;

public class ItemSoulFlint extends NetherItem {
    public ItemSoulFlint(int id) {
        super(id);
        this.setMaxDamage(12);
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
    public boolean canHarvestBlock(ItemStack stack, World world, Block block, int x, int y, int z) {
        return block == Block.netherrack || block == Block.oreNetherQuartz || block == NMBlocks.tungstenOre;
    }
}
