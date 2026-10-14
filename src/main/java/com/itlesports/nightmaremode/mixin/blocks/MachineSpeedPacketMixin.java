package com.itlesports.nightmaremode.mixin.blocks;

import api.block.TileEntityDataPacketHandler;
import btw.block.tileentity.MillstoneTileEntity;
import btw.block.tileentity.TurntableTileEntity;
import com.itlesports.nightmaremode.util.interfaces.MachineSpeedAccess;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet132TileEntityData;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityBrewingStand;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({MillstoneTileEntity.class, TurntableTileEntity.class, TileEntityBrewingStand.class})
public abstract class MachineSpeedPacketMixin extends TileEntity implements TileEntityDataPacketHandler {
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        ((MachineSpeedAccess)this).nm$getMachineSpeedState().write(tag);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, tag);
    }

    @Override
    public void readNBTFromPacket(NBTTagCompound tag) {
        ((MachineSpeedAccess)this).nm$getMachineSpeedState().read(tag);
    }
}
