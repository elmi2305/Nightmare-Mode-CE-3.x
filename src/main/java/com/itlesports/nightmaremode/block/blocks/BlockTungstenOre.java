package com.itlesports.nightmaremode.block.blocks;

import api.item.items.PickaxeItem;
import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.ItemSoulFlint;
import net.minecraft.src.*;

public class BlockTungstenOre extends Block {
    public BlockTungstenOre(int id) {
        super(id, BTWBlocks.netherRockMaterial);
        this.setHardness(3.0F);
        this.setResistance(8.0F);
        this.setPicksEffectiveOn();
        this.setStepSound(BTWBlocks.oreStepSound);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setUnlocalizedName("ifhyTungstenOre");
        this.setTextureName("iron_ore");
    }

    @Override
    public int getHarvestToolLevel(IBlockAccess world, int x, int y, int z) {
        return 0;
    }

    @Override
    public int getEfficientToolLevel(IBlockAccess world, int x, int y, int z) {
        return 0;
    }

    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z) {
        ItemStack held = player.getCurrentEquippedItem();
        if (held != null && held.getItem() instanceof ItemSoulFlint) {
            return player.getCurrentPlayerStrVsBlock(this, x, y, z) / this.blockHardness / 30.0F;
        }
        return super.getPlayerRelativeBlockHardness(player, world, x, y, z);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        if (world.isRemote) {
            return;
        }
        ItemStack held = player.getCurrentEquippedItem();
        if (held != null && held.getItem() instanceof ItemSoulFlint) {
            this.dropBlockAsItem_do(world, x, y, z, new ItemStack(NMItems.tungstenDust));
            held.damageItem(1, player);
            world.setBlock(x, y, z, Block.netherrack.blockID, 0, 3);
        } else if (held != null && held.getItem() instanceof PickaxeItem) {
            this.dropBlockAsItem_do(world, x, y, z, new ItemStack(NMItems.tungstenChunk));
        }
        player.addStat(StatList.mineBlockStatArray[this.blockID], 1);
    }
}
