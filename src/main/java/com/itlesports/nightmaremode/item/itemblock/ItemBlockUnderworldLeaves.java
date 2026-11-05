package com.itlesports.nightmaremode.item.itemblock;

import com.itlesports.nightmaremode.block.blocks.BlockUnderworldLeaves;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.ItemStack;

public class ItemBlockUnderworldLeaves extends NMItemBlock {
    public ItemBlockUnderworldLeaves(int id) {
        super(id);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override public int getMetadata(int damage) { return damage & 15; }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "tile.nmUnderworldLeaves." + BlockUnderworldLeaves.VARIANT_NAMES[BlockUnderworldLeaves.getVariant(stack.getItemDamage())];
    }

    @Override @Environment(EnvType.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass) {
        return BlockUnderworldLeaves.getColor(stack.getItemDamage());
    }
}
