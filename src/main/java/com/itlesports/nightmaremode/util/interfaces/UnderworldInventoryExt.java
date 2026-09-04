package com.itlesports.nightmaremode.util.interfaces;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;

public interface UnderworldInventoryExt {
    void nm$swapInventoryForDimension(int destinationDimension);
    void nm$snapshotCurrentInventory();
    NBTTagCompound nm$getOverworldInventoryState();
    NBTTagCompound nm$getUnderworldInventoryState();
    boolean nm$applyPendingUnderworldInventoryReset();
    void nm$copyInventoryStateFrom(EntityPlayer source, boolean keepCurrentInventory);
}
