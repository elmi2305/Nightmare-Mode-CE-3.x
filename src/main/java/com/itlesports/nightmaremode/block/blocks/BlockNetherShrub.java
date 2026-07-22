package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.*;

import java.util.Random;

public class BlockNetherShrub extends BlockFlower {
    public static final int STICK_DROP_CHANCE = 8;

    public BlockNetherShrub(int id) {
        super(id, Material.vine);
        this.setHardness(1.0F);
        this.setStepSound(Block.soundGrassFootstep);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setUnlocalizedName("ifhyNetherShrub");
        this.setTextureName("nightmare:ifhyNetherShrub");
    }

    @Override
    protected boolean canGrowOnBlock(World world, int x, int y, int z) {
        return world.getBlockId(x, y, z) == Block.netherrack.blockID;
    }

    @Override
    public int idDropped(int metadata, Random random, int fortune) {
        return random.nextInt(STICK_DROP_CHANCE) == 0 ? NMItems.netherStick.itemID : -1;
    }

    @Override
    public String getModId() {
        return NMFields.modID;
    }
}
