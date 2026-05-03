package com.itlesports.nightmaremode.block.blocks;


import com.itlesports.nightmaremode.block.tileEntities.TileEntityPortalCore;
import net.minecraft.src.*;

import static com.itlesports.nightmaremode.util.underworld.RitualState.VALID_IDLE;

/**
 * PortalCoreBlock — the block the player places at the center of the altar.
 *
 * Registration (in your mod init):
 *   NightmareBlocks.PORTAL_CORE = new PortalCoreBlock(YOUR_BLOCK_ID);
 *   GameRegistry.registerBlock(NightmareBlocks.PORTAL_CORE, "portalCore");
 *   ClientRegistry.bindTileEntitySpecialRenderer(
 *       TileEntityPortalCore.class, new TileEntityPortalCoreRenderer());
 *   GameRegistry.registerTileEntity(TileEntityPortalCore.class, "portalCore");
 *
 * Player interaction:
 *   Right-click with Wither Soul → catalyst inserted if structure is valid.
 *   Right-click empty-handed    → chat feedback about current ritual state.
 */
public class BlockPortalCore extends BlockContainer {

    public BlockPortalCore(int id) {
        super(id, Material.rock);
        this.setHardness(5.0f);
        this.setResistance(2000.0f); // Very hard to blast away mid-ritual
        this.setUnlocalizedName("portalCore");
        this.setTextureName("nightmare:portalCore");
    }

    // -----------------------------------------------------------------------
    //  Player interaction
    // -----------------------------------------------------------------------

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z,
                                    EntityPlayer player,
                                    int side, float hitX, float hitY, float hitZ) {
        // All state mutation happens server-side only
        if (world.isRemote) return true;

        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (!(te instanceof TileEntityPortalCore)) return false;

        TileEntityPortalCore core = (TileEntityPortalCore) te;
        ItemStack held = player.getHeldItem();

        // Attempt catalyst insertion
        if (held != null && core.tryInsertCatalyst(held)) {
            consumeOneItem(player, held);
            return true;
        }

        // Otherwise give state feedback
        sendStateFeedback(player, core.getState());
        return true;
    }

    private void consumeOneItem(EntityPlayer player, ItemStack stack) {
        stack.stackSize--;
        if (stack.stackSize <= 0) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
        }
    }

    private void sendStateFeedback(EntityPlayer player, com.itlesports.nightmaremode.util.underworld.RitualState state) {
        switch (state) {
            case INVALID:
                System.out.println("The altar is incomplete.");
                break;
            case VALID_IDLE:
                System.out.println("The altar waits. Place a soul within it.");
                break;
            case ACTIVE:
                System.out.println("The ritual consumes.");
                break;
            case COMPLETE:
                System.out.println("The gate is open.");
                break;
            case FAILED:
                System.out.println("The ritual has failed. The altar needs time.");
                break;
        }
    }

    // -----------------------------------------------------------------------
    //  Lifecycle
    // -----------------------------------------------------------------------

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        // The TileEntity's first updateEntity() tick will pick up validation.
        // Nothing extra needed here — the TESR will check immediately.
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int blockId, int meta) {
        // Give the tile entity a chance to shut down the ritual cleanly
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te instanceof TileEntityPortalCore) {
            ((TileEntityPortalCore) te).onCoreRemoved();
        }
        super.breakBlock(world, x, y, z, blockId, meta);
    }

    // -----------------------------------------------------------------------
    //  Rendering flags — TESR handles all visuals
    // -----------------------------------------------------------------------

    @Override
    public boolean renderAsNormalBlock() { return false; }

    @Override
    public boolean isOpaqueCube() { return false; }

    @Override
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k) {
        // prevents rendering it while placed. tile entity does render
        return false;
    }

//    @Override
//    public int getRenderType()           { return -1; } // TESR only, no standard render

    @Override
    public boolean hasTileEntity(){return true;}

    // -----------------------------------------------------------------------
    //  Tile entity factory
    // -----------------------------------------------------------------------

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityPortalCore();
    }
}