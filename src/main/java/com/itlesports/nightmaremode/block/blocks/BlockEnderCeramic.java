package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.block.blocks.templates.BlockMetaMultiTextured;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;

import java.util.List;

public class BlockEnderCeramic extends BlockMetaMultiTextured {
    public BlockEnderCeramic(int id) {
        super(id, Material.clay,
                Variant.allSides("nightmare:ifhyEnderCeramicBlank").hardness(0.6F).name("ifhyEnderCeramicBlank").build(),
                Variant.allSides("nightmare:ifhyUnfiredCrucibleLiner").hardness(0.6F).name("ifhyUnfiredCrucibleLiner").build());
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }

    @Override public void getSubBlocks(int id, CreativeTabs tab, List list) {
        list.add(new ItemStack(id, 1, 0));
        list.add(new ItemStack(id, 1, 1));
    }
}
