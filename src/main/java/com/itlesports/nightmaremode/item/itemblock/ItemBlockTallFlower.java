package com.itlesports.nightmaremode.item.itemblock;

import com.itlesports.nightmaremode.block.blocks.BlockTallFlower;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;
import net.minecraft.src.ItemStack;

import static com.itlesports.nightmaremode.util.NMFields.FLOWER_NAMES;

public class ItemBlockTallFlower extends NMItemBlock {
//    private final String[] names = {"dandelion", "dandelion2", "dandelion3", "dandelion4", "dandelion5"};
    private final Icon[] icons = new Icon[FLOWER_NAMES.length];
    public ItemBlockTallFlower(int par1) {
        super(par1);
        this.setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int meta = stack.getItemDamage();
        String name = FLOWER_NAMES[meta & 7];
        return "tile.nmTallFlower." + name;
    }

    @Override
    public void registerIcons(IconRegister reg) {
        for(int i = 0; i < icons.length; i ++){
            String path = "_top";
            if(i == BlockTallFlower.LAVAFLOWER){
                path = "_bottom";
            }
            icons[i] = reg.registerIcon("nightmare:nmTallFlower_" + FLOWER_NAMES[i] + path);
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
