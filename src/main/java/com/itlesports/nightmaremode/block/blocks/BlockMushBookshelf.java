package com.itlesports.nightmaremode.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.BlockBookshelf;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;

public class BlockMushBookshelf extends BlockBookshelf {
    private static Icon iconTop;
    public BlockMushBookshelf(int blockID) {
        super(blockID);
    }

    @Override
    public String getModId() {
        return "nightmare";
    }

    @Override
    public void registerIcons(IconRegister reg) {
        iconTop = reg.registerIcon("nmMushBookshelfTop");
        super.registerIcons(reg);
    }

    @Override
    @Environment(value= EnvType.CLIENT)
    public Icon getIcon(int side, int meta) {
        return side != 1 && side != 0 ? super.getIcon(side, meta) : iconTop;
    }
}
