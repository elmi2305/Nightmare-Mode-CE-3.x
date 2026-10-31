package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.worldgen.OverworldTierHelper;
import net.minecraft.src.EntityPlayerMP;

public final class OuterDeathRespawn {
    private OuterDeathRespawn() {}

    public static boolean returnsToSpawn(EntityPlayerMP player) {
        return !player.playerConqueredTheEnd && player.worldObj.provider.dimensionId == 0
                && OverworldTierHelper.isOuterOverworld(player.worldObj, player.posX, player.posZ);
    }
}
