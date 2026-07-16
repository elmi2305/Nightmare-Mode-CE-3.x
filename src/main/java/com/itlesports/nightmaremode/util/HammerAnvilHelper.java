package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.block.tileEntities.TileEntityHammerAnvil;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

public abstract class HammerAnvilHelper {
    public static boolean tryHammerHeldItem(World world, int x, int y, int z, EntityPlayer player, TileEntityHammerAnvil anvil) {
        return anvil != null && anvil.tryStartHammerOperation(player);
    }
}
