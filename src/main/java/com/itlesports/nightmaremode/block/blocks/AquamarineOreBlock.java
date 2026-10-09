package com.itlesports.nightmaremode.block.blocks;

import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.EasyBalance;
import net.minecraft.src.World;

import java.util.Random;

public class AquamarineOreBlock extends ProcessingOreBlock {
    public AquamarineOreBlock(int id) {
        super(id, NMItems.aquamarine.itemID, 2);
    }

    @Override
    public int quantityDropped(Random random) {
        return EasyBalance.resourceCount(1 + random.nextInt(2));
    }

    @Override
    public int quantityDroppedOnConversion(Random random) {
        return this.quantityDropped(random);
    }

    @Override
    protected void dropItemsIndividually(World world, int x, int y, int z, int itemID, int count, int metadata, float chance) {
        if (itemID == BTWItems.stone.itemID) {
            itemID = BTWItems.sandPile.itemID;
            metadata = 0;
        }
        super.dropItemsIndividually(world, x, y, z, itemID, count, metadata, chance);
    }
}
