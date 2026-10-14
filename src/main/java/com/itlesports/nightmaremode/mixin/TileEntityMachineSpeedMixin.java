package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.util.MachineSpeedBonuses;
import com.itlesports.nightmaremode.util.MachineSpeedState;
import com.itlesports.nightmaremode.util.interfaces.MachineSpeedAccess;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntity.class)
public class TileEntityMachineSpeedMixin implements MachineSpeedAccess {
    @Unique private final MachineSpeedState machineSpeedState = new MachineSpeedState();

    @Override
    public MachineSpeedState nm$getMachineSpeedState() {
        return this.machineSpeedState;
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"))
    private void readMachineSpeed(NBTTagCompound tag, CallbackInfo ci) {
        this.machineSpeedState.read(tag);
    }

    @Inject(method = "writeToNBT", at = @At("TAIL"))
    private void writeMachineSpeed(NBTTagCompound tag, CallbackInfo ci) {
        if (MachineSpeedBonuses.isMachine((TileEntity)(Object)this)) this.machineSpeedState.write(tag);
    }
}
