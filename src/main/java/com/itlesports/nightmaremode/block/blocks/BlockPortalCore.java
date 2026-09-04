package com.itlesports.nightmaremode.block.blocks;


import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityPortalCore;
import com.itlesports.nightmaremode.util.underworld.RitualState;
import net.minecraft.src.*;

public class BlockPortalCore extends BlockContainer {

    public BlockPortalCore(int id) {
        super(id, Material.rock);
        this.setHardness(5.0f);
        this.setResistance(2000.0f);
        this.setLightOpacity(1);
    }


    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;

        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (!(te instanceof TileEntityPortalCore)) return false;

        TileEntityPortalCore core = (TileEntityPortalCore) te;
        ItemStack held = player.getHeldItem();

        if (NightmareMode.devMode && player.isSneaking() && held == null && core.spawnDebugRift()) {
            player.sendChatToPlayer(new ChatMessageComponent().addKey("nm.portal.debug_rift"));
            return true;
        }

        if (held != null && side == 1 && core.tryInsertCatalyst(held)) {
            if (!player.capabilities.isCreativeMode) consumeOneItem(player, held);
            return true;
        }

        sendStateFeedback(player, core.getState());
        return true;
    }

    private void consumeOneItem(EntityPlayer player, ItemStack stack) {
        stack.stackSize--;
        if (stack.stackSize <= 0) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
        }
    }

    @Override
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k) {
        return false;
    }

    private void sendStateFeedback(EntityPlayer player, RitualState state) {
        String key = switch (state) {
            case INVALID -> "nm.portal.invalid";
            case VALID_IDLE -> "nm.portal.valid_idle";
            case ACTIVE -> "nm.portal.active";
            case COMPLETE -> "nm.portal.complete";
            case FAILED -> "nm.portal.failed";
        };
        player.sendChatToPlayer(new ChatMessageComponent().addKey(key));
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int blockId, int meta) {
        // give the tile entity a chance to shut down the ritual cleanly
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te instanceof TileEntityPortalCore) {
            ((TileEntityPortalCore) te).onCoreRemoved();
        }
        super.breakBlock(world, x, y, z, blockId, meta);
    }

    @Override
    public boolean isOpaqueCube() { return false; }


    @Override
    public boolean hasTileEntity(){return true;}

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityPortalCore();
    }
}
