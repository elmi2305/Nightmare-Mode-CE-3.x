package com.itlesports.nightmaremode.block.blocks;

import api.item.items.HoeItem;
import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.*;

/** Stable, portal-safe soil precursor. A hoe turns it into Nether farmland. */
public class BlockFertileNetherrack extends Block {
    public BlockFertileNetherrack(int id) {
        super(id, Material.ground);
        this.setHardness(1.0F);
        this.setResistance(5.0F);
        this.setShovelsEffectiveOn();
        this.setHoesEffectiveOn();
        this.setStepSound(soundGravelFootstep);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setUnlocalizedName("ifhyFertileNetherrack");
        this.setTextureName("nightmare:ifhyFertileNetherrack");
    }

    @Override
    public boolean canConvertBlock(ItemStack stack, World world, int x, int y, int z) {
        return stack != null && stack.getItem() instanceof HoeItem && world.isAirBlock(x, y + 1, z);
    }

    @Override
    public boolean convertBlock(ItemStack stack, World world, int x, int y, int z, int side) {
        world.setBlockAndMetadataWithNotify(x, y, z, NMBlocks.netherFarmland.blockID, 0);
        if (!world.isRemote) world.playAuxSFX(2291, x, y, z, 0);
        return true;
    }

    @Override public boolean shouldPlayStandardConvertSound(World world, int x, int y, int z) { return false; }
    @Override public boolean canDomesticatedCropsGrowOnBlock(World world, int x, int y, int z) { return false; }
}
