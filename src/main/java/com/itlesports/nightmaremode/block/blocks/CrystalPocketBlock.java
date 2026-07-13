package com.itlesports.nightmaremode.block.blocks;

import btw.item.items.ChiselItem;
import com.itlesports.nightmaremode.block.blocks.templates.NMBlock;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;

import java.util.Random;

public class CrystalPocketBlock extends NMBlock {
    private static final int MAX_ATTEMPTS = 4;

    public CrystalPocketBlock(int id) {
        super(id, Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setHardness(4.0F);
        this.setResistance(8.0F);
        this.setPicksEffectiveOn();
        this.setChiselsEffectiveOn();
        this.setChiselsCanHarvest(true);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        ItemStack held = player.getHeldItem();
        if (held == null || !(held.getItem() instanceof ChiselItem)) {
            return false;
        }
        if (!world.isRemote) {
            int attempts = world.getBlockMetadata(x, y, z);
            if (world.rand.nextBoolean()) {
                this.dropBlockAsItem_do(world, x, y, z, new ItemStack(NMItems.uncleanedCrystalShard));
            }
            held.damageItem(1, player);
            if (attempts >= MAX_ATTEMPTS - 1) {
                world.setBlockToAir(x, y, z);
            } else {
                world.setBlockMetadataWithNotify(x, y, z, attempts + 1, 3);
            }
            world.playAuxSFX(2001, x, y, z, this.blockID + (attempts << 12));
        }
        return true;
    }

    @Override
    public int idDropped(int metadata, Random random, int fortune) {
        return 0;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float chance, int fortune) {
    }
}
