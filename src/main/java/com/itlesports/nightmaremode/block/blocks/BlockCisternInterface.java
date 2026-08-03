package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.tileEntities.CisternInterfaceTileEntity;
import com.itlesports.nightmaremode.block.tileEntities.CisternTileEntity;
import net.minecraft.src.*;

/** A nonflammable horizontal automation port attached directly to one cistern. */
public class BlockCisternInterface extends BlockContainer {
    private static final int[][] SIDES = {{0, 0, -1, 2}, {0, 0, 1, 3}, {-1, 0, 0, 4}, {1, 0, 0, 5}};

    public BlockCisternInterface(int id) {
        super(id, Material.iron);
        this.setHardness(4.0F);
        this.setResistance(20.0F);
        this.setPicksEffectiveOn();
        this.setStepSound(soundMetalFootstep);
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setUnlocalizedName("ifhyCisternInterface");
        this.setTextureName("nightmare:ifhyCisternInterface");
    }

    @Override public TileEntity createNewTileEntity(World world) { return new CisternInterfaceTileEntity(); }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        int cisterns = 0;
        for (int[] offset : SIDES) {
            int id = world.getBlockId(x + offset[0], y, z + offset[2]);
            if (id == NMBlocks.cistern.blockID) ++cisterns;
            if (id == this.blockID) return false;
        }
        return cisterns == 1 && super.canPlaceBlockAt(world, x, y, z);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        this.updateAttachment(world, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborId) {
        if (!this.updateAttachment(world, x, y, z) && !world.isRemote) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
    }

    private boolean updateAttachment(World world, int x, int y, int z) {
        for (int[] offset : SIDES) {
            if (world.getBlockId(x + offset[0], y, z + offset[2]) == NMBlocks.cistern.blockID) {
                world.setBlockMetadataWithNotify(x, y, z, offset[3], 2);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
                                    int side, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (!world.isRemote && tile instanceof CisternInterfaceTileEntity) {
            CisternTileEntity cistern = ((CisternInterfaceTileEntity)tile).getCistern();
            player.sendChatToPlayer(new ChatMessageComponent().addText(cistern == null
                    ? "Cistern interface is detached."
                    : "Cistern interface: " + CisternTileEntity.getFluidDisplayName(cistern.getFluid())));
        }
        return true;
    }
}
