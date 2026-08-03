package com.itlesports.nightmaremode.block.blocks;

import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.block.blocks.templates.BlockMetaMultiTextured;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;

import java.util.List;

/** The three structure-only post gems, exposed individually in creative mode. */
public class BlockNetherProgressionGems extends BlockMetaMultiTextured {
    public BlockNetherProgressionGems(int id) {
        super(id, Material.rock,
                Variant.allSides("nightmare:ifhyRedGem").hardness(4.0F).resistance(20.0F).name("ifhyRedGem").build(),
                Variant.allSides("nightmare:ifhyPurpleGem").hardness(6.0F).resistance(30.0F).name("ifhyPurpleGem").build(),
                Variant.allSides("nightmare:ifhyBlackGem").hardness(8.0F).resistance(40.0F).name("ifhyBlackGem").build());
        this.setStepSound(BTWBlocks.gemStepSound);
    }

    @Override
    public void getSubBlocks(int blockId, CreativeTabs tab, List list) {
        for (int metadata = 0; metadata < 3; ++metadata) {
            list.add(new ItemStack(blockId, 1, metadata));
        }
    }
}
