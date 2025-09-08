package com.itlesports.nightmaremode.block.blocks;

import net.minecraft.src.*;

import static btw.community.nightmaremode.NightmareMode.UNDERWORLD_DIMENSION;

public class BlockUnderworldPortal extends BlockBreakable {

    public BlockUnderworldPortal(int id) {
        super(id, "underworld_portal", Material.portal, false);
        setHardness(-1.0f);
        setLightValue(0.75f);
        setTickRandomly(true);
    }

    @Override public boolean isOpaqueCube() { return false; }
    @Override public boolean renderAsNormalBlock() { return false; }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (entity.ridingEntity == null && entity.riddenByEntity == null && entity instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP)entity;
            if (player.timeUntilPortal > 0) return;  // cooldown
            player.timeUntilPortal = 100;
            // transfer:
            player.mcServer.getConfigurationManager()
                    .transferPlayerToDimension(player, UNDERWORLD_DIMENSION);
        }
    }


    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
        return null;
    }

}
