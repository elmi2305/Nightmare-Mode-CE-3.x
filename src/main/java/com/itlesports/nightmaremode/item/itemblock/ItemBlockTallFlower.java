package com.itlesports.nightmaremode.item.itemblock;

import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;
import net.minecraft.src.ItemStack;

public class ItemBlockTallFlower extends NMItemBlock {
    private final String[] names = {"dandelion", "dandelion2", "dandelion3", "dandelion4", "dandelion5", "dandelion5", "dandelion5", "dandelion5"};
//    private final String[] names = {"dandelion", "dandelion2", "dandelion3", "dandelion4", "dandelion5"};
    private final Icon[] icons = new Icon[names.length];
    public ItemBlockTallFlower(int par1) {
        super(par1);
        this.setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int meta = stack.getItemDamage();
        String name = names[meta & 7];
        return "tile.nmTallFlower." + name;
    }

    @Override
    public void registerIcons(IconRegister reg) {
        for(int i = 0; i < icons.length; i ++){
            icons[i] = reg.registerIcon("nightmare:nmTallFlower_" + names[i] + "_top");
        }
    }

    @Override
    public Icon getIconFromDamage(int meta) {
        return icons[meta & 7];
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }
}
