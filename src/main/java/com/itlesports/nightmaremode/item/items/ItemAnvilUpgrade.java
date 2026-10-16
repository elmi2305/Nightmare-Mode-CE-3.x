package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.item.items.template.NMItem;
import com.itlesports.nightmaremode.util.AutoAnvilUpgrade;
import net.minecraft.src.*;

public class ItemAnvilUpgrade extends NMItem {
    public final AutoAnvilUpgrade upgrade;

    public ItemAnvilUpgrade(int id, AutoAnvilUpgrade upgrade) {
        super(id);
        this.upgrade = upgrade;
        this.setMaxStackSize(16);
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setUnlocalizedName(upgrade.textureName());
        this.setTextureName("nightmare:" + upgrade.textureName());
    }

}
