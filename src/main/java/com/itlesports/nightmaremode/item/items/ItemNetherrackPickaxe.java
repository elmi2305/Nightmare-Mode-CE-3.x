package com.itlesports.nightmaremode.item.items;

import api.item.items.PickaxeItem;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.interfaces.INetherItem;
import net.minecraft.src.*;

public class ItemNetherrackPickaxe extends PickaxeItem implements INetherItem {
    public ItemNetherrackPickaxe(int id) {
        super(id, EnumToolMaterial.STONE);
    }

    @Override
    public String getModId() {
        return NMFields.modID;
    }

    @Override
    public boolean canHarvestBlock(ItemStack stack, World world, Block block, int x, int y, int z) {
        return block == Block.netherrack || super.canHarvestBlock(stack, world, block, x, y, z);
    }

    @Override
    public float getStrVsBlock(ItemStack stack, World world, Block block, int x, int y, int z) {
        return block == Block.netherrack ? this.efficiencyOnProperMaterial : super.getStrVsBlock(stack, world, block, x, y, z);
    }
}
