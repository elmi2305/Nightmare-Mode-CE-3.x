package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.world.PhasePortalData;
import com.itlesports.nightmaremode.world.PhasePortalManager;
import net.minecraft.src.*;

import java.util.Random;

public class BlockPhasePortal extends BlockPortal {
    private static final int[] COLORS = {0x191919,0x993333,0x667F33,0x664C33,0x334CB2,0x7F3FB2,0x4C7F99,0x999999,
            0x4C4C4C,0xF27FA5,0x7FCC19,0xE5E533,0x6699D8,0xB24CD8,0xD87F33,0xF2F2F2};

    public BlockPhasePortal(int id) { super(id); this.setUnlocalizedName("ifhyPhasePortal"); }
    @Override public int getRenderColor(int metadata) { return COLORS[metadata & 15]; }
    @Override public int colorMultiplier(IBlockAccess world, int x, int y, int z) { return COLORS[world.getBlockMetadata(x,y,z) & 15]; }
    @Override public void updateTick(World world, int x, int y, int z, Random random) {}

    @Override public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (world.isRemote) return;
        int color = world.getBlockMetadata(x,y,z) & 15;
        PhasePortalData.Endpoint source = PhasePortalManager.find(world, color, x, y, z);
        if (source == null) return;
        PhasePortalData.Endpoint target = PhasePortalManager.counterpart(world, source);
        if (target != null) PhasePortalManager.teleport(entity, target);
    }

    @Override public void onNeighborBlockChange(World world, int x, int y, int z, int neighborId) {
        int color = world.getBlockMetadata(x,y,z) & 15;
        PhasePortalData.Endpoint source = PhasePortalManager.find(world, color, x, y, z);
        if (source == null || !validFrame(world, source)) {
            if (source != null) {
                PhasePortalManager.remove(world, source.dimension, source.x, source.y, source.z);
                for (int across = 0; across < 2; ++across) for (int up = 0; up < 3; ++up) {
                    int px = source.x + (source.axis == 0 ? across : 0);
                    int pz = source.z + (source.axis == 1 ? across : 0);
                    if (world.getBlockId(px, source.y + up, pz) == this.blockID) world.setBlockToAir(px, source.y + up, pz);
                }
            } else world.setBlockToAir(x,y,z);
        }
    }

    private boolean validFrame(World world, PhasePortalData.Endpoint endpoint) {
        for (int across = -1; across <= 2; ++across) for (int up = -1; up <= 3; ++up) {
            boolean edge = across == -1 || across == 2 || up == -1 || up == 3;
            int px = endpoint.x + (endpoint.axis == 0 ? across : 0);
            int pz = endpoint.z + (endpoint.axis == 1 ? across : 0);
            int id = world.getBlockId(px, endpoint.y + up, pz);
            if (edge ? id != NMBlocks.phasePortalFrame.blockID : id != this.blockID) return false;
            if ((world.getBlockMetadata(px, endpoint.y + up, pz) & 15) != endpoint.color) return false;
        }
        return true;
    }
}
