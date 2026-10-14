package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.agriculture.ChunkAttribute;
import com.itlesports.nightmaremode.agriculture.ChunkAttributeManager;
import com.itlesports.nightmaremode.item.items.template.NMItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class ItemChunkFertilizer extends NMItem {
    private final ChunkAttribute attribute;
    private final float gain;
    private final float penalty;

    public ItemChunkFertilizer(int itemId, ChunkAttribute attribute) {
        this(itemId, attribute, ChunkAttributeManager.FERTILIZER_GAIN, ChunkAttributeManager.FERTILIZER_PENALTY);
    }

    public ItemChunkFertilizer(int itemId, ChunkAttribute attribute, float gain, float penalty) {
        super(itemId);
        this.attribute = attribute;
        this.gain = gain;
        this.penalty = penalty;
    }

    public ChunkAttribute getAttribute() {
        return this.attribute;
    }

    public boolean apply(World world, int x, int y, int z) {
        return ChunkAttributeManager.applyFertilizer(world, x, y, z, this.attribute, this.gain, this.penalty);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ) {
        if (!this.apply(world, x, y, z)) {
            return false;
        }
        if (player.capabilities == null || !player.capabilities.isCreativeMode) {
            --stack.stackSize;
        }
        return true;
    }
}
