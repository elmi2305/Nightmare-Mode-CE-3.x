package com.itlesports.nightmaremode.util;

import btw.block.blocks.KilnBlock;
import btw.block.blocks.SawBlock;
import btw.block.blocks.ScrewPumpBlock;
import btw.block.tileentity.MillstoneTileEntity;
import com.itlesports.nightmaremode.block.tileEntities.CisternTileEntity;
import com.itlesports.nightmaremode.block.tileEntities.MachineSpeedTileEntity;
import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.skill.SkillTreeData;
import com.itlesports.nightmaremode.util.interfaces.MachineSpeedAccess;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityBrewingStand;
import net.minecraft.src.World;

public final class MachineSpeedBonuses {
    private MachineSpeedBonuses() {}

    public static boolean isMachine(TileEntity tile) {
        return tile instanceof MillstoneTileEntity
                || tile instanceof CisternTileEntity || tile instanceof TileEntityBrewingStand
                || tile instanceof MachineSpeedTileEntity;
    }

    public static float get(TileEntity tile) {
        return !isMachine(tile) ? 0.0F : ((MachineSpeedAccess)tile).nm$getMachineSpeedState().getBonus();
    }

    public static float get(World world, int x, int y, int z) {
        return get(world.getBlockTileEntity(x, y, z));
    }

    public static void interact(World world, int x, int y, int z, EntityPlayer player) {
        if (world == null || world.isRemote) return;
        interact(world.getBlockTileEntity(x, y, z), player);
        // kiln inputs and outputs occupy the block above the kiln itself.
        TileEntity below = world.getBlockTileEntity(x, y - 1, z);
        if (below instanceof MachineSpeedTileEntity && below.getBlockType() instanceof KilnBlock) {
            interact(below, player);
        }
    }

    public static void interact(TileEntity tile, EntityPlayer player) {
        if (!isMachine(tile) || player == null || tile.worldObj == null || tile.worldObj.isRemote) return;
        SkillTreeData data = SkillHandler.getPlayerData(player);
        float bonus;
        if (tile instanceof MillstoneTileEntity) {
            bonus = data.machineSpeedBonus + data.millstoneSpeedBonus;
        } else if (tile instanceof CisternTileEntity) {
            bonus = data.cisternSpeedBonus;
        } else if (tile instanceof TileEntityBrewingStand) {
            bonus = data.brewingSpeedBonus;
        } else {
            Block block = tile.getBlockType();
            if (block instanceof KilnBlock) bonus = data.kilnSpeedBonus;
            else if (block instanceof SawBlock || block instanceof ScrewPumpBlock) bonus = data.machineSpeedBonus;
            else return;
        }
        if (((MachineSpeedAccess)tile).nm$getMachineSpeedState().raiseBonus(bonus)) {
            tile.worldObj.markTileEntityChunkModified(tile.xCoord, tile.yCoord, tile.zCoord, tile);
            tile.worldObj.markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
        }
    }
}
