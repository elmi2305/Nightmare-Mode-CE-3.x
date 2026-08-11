package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.agriculture.ChunkAttribute;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;

import java.util.List;
import java.util.Locale;

public class ItemSoilSample extends Item {
    public ItemSoilSample(int id) {
        super(id);
        this.setMaxStackSize(1);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) return;
        tooltip.add(String.format(Locale.ROOT, "%016X", tag.getLong("Roll")));
        for (ChunkAttribute attribute : ChunkAttribute.values()) {
            tooltip.add(attribute.getDisplayName() + ": "
                    + String.format(Locale.ROOT, "%.1f", tag.getFloat(attribute.name())));
        }
    }
}
