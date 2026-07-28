package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.agriculture.ChunkAttribute;
import com.itlesports.nightmaremode.agriculture.ChunkAttributeManager;
import com.itlesports.nightmaremode.item.items.template.NMItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class ItemChunkFertilizer extends NMItem {
    private final ChunkAttribute attribute;

    public ItemChunkFertilizer(int itemId, ChunkAttribute attribute) {
        super(itemId);
        this.attribute = attribute;
    }

    public ChunkAttribute getAttribute() {
        return this.attribute;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ) {
        if (!ChunkAttributeManager.applyFertilizer(world, x, y, z, this.attribute)) {
            return false;
        }
        if (player.capabilities == null || !player.capabilities.isCreativeMode) {
            --stack.stackSize;
        }
        return true;
    }
}
