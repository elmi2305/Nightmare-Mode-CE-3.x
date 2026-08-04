package com.itlesports.nightmaremode.block.blocks;

import btw.block.blocks.FarmlandBlock;
import com.itlesports.nightmaremode.agriculture.ChunkAttribute;
import com.itlesports.nightmaremode.agriculture.ChunkAttributeManager;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.tileEntities.CisternTileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

/** Nether crop soil hydrated by ordinary water or a nearby water-filled cistern. */
public class BlockNetherFarmland extends FarmlandBlock {
    @Environment(value= EnvType.CLIENT)
    protected Icon iconTopWet;
    @Environment(value=EnvType.CLIENT)
    protected Icon iconTopDry;

    public BlockNetherFarmland(int id) {
        super(id);
        this.setUnlocalizedName("ifhyNetherFarmland");
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }


    @Override
    @Environment(value=EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.blockIcon = register.registerIcon("nightmare:ifhyFertileNetherrack");
        this.iconTopWet = register.registerIcon("nightmare:ifhyNetherFarmlandWet");
        this.iconTopDry = register.registerIcon("nightmare:ifhyNetherFarmlandDry");
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Icon getIcon(int iSide, int iMetadata) {
        if (iSide == 1) {
            if (this.isHydrated(iMetadata)) {
                return this.iconTopWet;
            }
            return this.iconTopDry;
        }
        return this.blockIcon;
    }

    @Override
    protected boolean hasIrrigatingBlocks(World world, int x, int y, int z) {
        if (super.hasIrrigatingBlocks(world, x, y, z)) return true;
        int range = this.getHorizontalHydrationRange(world, x, y, z);
        for (int checkX = x - range; checkX <= x + range; ++checkX) {
            for (int checkY = y; checkY <= y + 1; ++checkY) {
                for (int checkZ = z - range; checkZ <= z + range; ++checkZ) {
                    TileEntity tile = world.getBlockTileEntity(checkX, checkY, checkZ);
                    if (tile instanceof CisternTileEntity
                            && ((CisternTileEntity)tile).getFluid() == CisternTileEntity.FLUID_WATER) return true;
                }
            }
        }
        return false;
    }

    private void revert(World world, int x, int y, int z) {
        world.setBlockWithNotify(x, y, z, NMBlocks.fertileNetherrack.blockID);
    }

    @Override public int idDropped(int metadata, java.util.Random random, int fortune) { return NMBlocks.fertileNetherrack.blockID; }
    @Override public int idPicked(World world, int x, int y, int z) { return NMBlocks.fertileNetherrack.blockID; }

    @Override
    protected boolean isFertilized(IBlockAccess access, int x, int y, int z) {
        return access instanceof World && ChunkAttributeManager.hasActiveFertilizer((World)access, x, y, z);
    }

    @Override
    protected void setFertilized(World world, int x, int y, int z) {
        ChunkAttributeManager.applyFertilizer(world, x, y, z, ChunkAttribute.NITROGEN);
    }

    @Override
    protected void checkForSoilReversion(World world, int x, int y, int z) {
        if (!this.doesBlockAbovePreventSoilReversion(world, x, y, z)) this.revert(world, x, y, z);
    }

    @Override
    public void notifyOfPlantAboveRemoved(World world, int x, int y, int z, Block plant) {
        if (world.getBlockId(x, y + 1, z) != Block.tallGrass.blockID
                && ((Boolean)world.getDifficultyParameter(api.world.difficulty.DifficultyParam.ShouldFarmlandRequireReTilling.class))) {
            this.revert(world, x, y, z);
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborId) {
        if (world.getBlockMaterial(x, y + 1, z).isSolid()) this.revert(world, x, y, z);
    }

    @Override
    public void onFallenUpon(World world, int x, int y, int z, Entity entity, float fallDistance) {
        if (!world.isRemote && world.rand.nextFloat() < fallDistance - 0.75F) this.revert(world, x, y, z);
    }

    @Override
    public void onVegetationAboveGrazed(World world, int x, int y, int z, EntityAnimal animal) {
        if (animal.getDisruptsEarthOnGraze()) this.revert(world, x, y, z);
    }
}
