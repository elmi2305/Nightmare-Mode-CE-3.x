package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.block.tileEntities.TungstenForgeTileEntity;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import java.util.Random;

public class BlockTungstenForge extends BlockHellforge {
    public BlockTungstenForge(int id) {
        super(id, true);
        this.setUnlocalizedName("ifhyTungstenForge");
        this.setTextureName("nightmare:ifhyTungstenForge");
        this.setHardness(5.0F);
        this.setResistance(10.0F);
    }

    @Override public TileEntity createNewTileEntity(World world) { return new TungstenForgeTileEntity(); }
    @Override public boolean isValidCookItem(ItemStack stack) {
        return stack != null && (stack.getItem() == NMItems.tungstenChunk || super.isValidCookItem(stack));
    }
    @Override public int idDropped(int metadata, Random random, int fortune) { return this.blockID; }
    @Override public int quantityDropped(Random random) { return 1; }
}
