package com.itlesports.nightmaremode.block.blocks.templates;

import api.block.blocks.GroundCoverBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class NMBlockGroundLayer extends GroundCoverBlock {
    private int ticksExisted;
    public NMBlockGroundLayer(int iBlockID, Material material) {
        super(iBlockID, material);
    }

    @Environment(EnvType.CLIENT)
    private Icon[] icons;

    @Override
    public void registerIcons(IconRegister reg) {
        super.registerIcons(reg);
        icons = new Icon[3];
        icons[0] = reg.registerIcon(this.textureName + "_meta_0");
        icons[1] = reg.registerIcon(this.textureName + "_meta_1");
        icons[2] = reg.registerIcon(this.textureName + "_meta_2");
    }


    public int quantityDropped(Random par1Random) {
        return 1;
    }

    public boolean canDropFromExplosion(Explosion explosion) {
        return false;
    }


    @Override
    public Icon getIcon(int side, int meta) {
        return icons[meta];
    }

    @Override
    public void updateTick(World w, int x, int y, int z, Random rand) {
        this.ticksExisted += tickRate(w);
        int meta = w.getBlockMetadata(x,y,z);
        // 100 * 4 = 400 ticks, which is 20 seconds
        if(this.ticksExisted > 400){
            w.setBlockMetadata(x,y,z,Math.min(meta + 1,3));
            this.ticksExisted = 0;
        }
        super.updateTick(w, x, y, z, rand);
    }

    @Override
    public int tickRate(World par1World) {
        return 4;
    }

    @Override
    public String getModId() {
        return "nightmare";
    }
}
