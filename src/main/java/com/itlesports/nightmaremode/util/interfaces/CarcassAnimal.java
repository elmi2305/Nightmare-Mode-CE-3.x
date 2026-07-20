package com.itlesports.nightmaremode.util.interfaces;

import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;

public interface CarcassAnimal {
    boolean nm$isCarcass();

    int nm$getHarvesterId();

    int nm$getHarvestProgress();

    void nm$becomeCarcass(DamageSource source);

    void nm$continueHarvest(EntityPlayer player);

    void nm$cancelHarvest(EntityPlayer player);

    void nm$tickCarcass();

    void nm$writeCarcassToNBT(NBTTagCompound tag);

    void nm$readCarcassFromNBT(NBTTagCompound tag);

    void nm$spawnCarcassPoof();
}
