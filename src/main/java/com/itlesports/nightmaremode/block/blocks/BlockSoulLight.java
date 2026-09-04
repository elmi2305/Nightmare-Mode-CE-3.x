package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.block.blocks.templates.NMBlock;
import net.minecraft.src.*;

public class BlockSoulLight extends NMBlock {
    public BlockSoulLight(int id, float light, String name) {
        super(id, Material.glass);
        this.setLightValue(light);
        this.setHardness(0.5F);
        this.setResistance(2.0F);
        this.setStepSound(Block.soundGlassFootstep);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setUnlocalizedName(name);
        this.setTextureName("nightmare:" + name);
    }
}
