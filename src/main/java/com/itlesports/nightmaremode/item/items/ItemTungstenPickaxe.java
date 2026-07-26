package com.itlesports.nightmaremode.item.items;

import api.item.items.PickaxeItem;
import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.interfaces.INetherItem;
import net.minecraft.src.*;

public class ItemTungstenPickaxe extends PickaxeItem implements INetherItem {
    public ItemTungstenPickaxe(int id) {
        super(id, EnumToolMaterial.IRON);
    }

    @Override
    public String getModId() {
        return NMFields.modID;
    }

    @Override
    public boolean canHarvestBlock(ItemStack stack, World world, Block block, int x, int y, int z) {
        return block.blockID == Block.netherBrick.blockID || block.blockID == Block.netherrack.blockID || block.blockID == Block.oreNetherQuartz.blockID || block.blockID == NMBlocks.tungstenOre.blockID || super.canHarvestBlock(stack, world, block, x, y, z);
    }
}
