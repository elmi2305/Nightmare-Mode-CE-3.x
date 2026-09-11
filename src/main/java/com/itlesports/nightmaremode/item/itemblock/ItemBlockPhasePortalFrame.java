package com.itlesports.nightmaremode.item.itemblock;

import com.itlesports.nightmaremode.world.PhasePortalManager;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

public class ItemBlockPhasePortalFrame extends ItemBlock {
    public ItemBlockPhasePortalFrame(int id) { super(id); this.setHasSubtypes(true); this.setMaxDamage(0); }
    @Override public int getMetadata(int damage) { return damage & 15; }
    @Override public String getUnlocalizedName(ItemStack stack) {
        return "tile.ifhyPhasePortalFrame." + PhasePortalManager.COLOR_NAMES[stack.getItemDamage() & 15].replace(" ", "_");
    }
}
