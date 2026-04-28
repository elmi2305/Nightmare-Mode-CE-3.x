package com.itlesports.nightmaremode.mixin.entity;

import btw.entity.LocatorPileEntity;
import btw.entity.SoulSandEntity;
import com.itlesports.nightmaremode.util.interfaces.LocatorPileEntityExt;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LocatorPileEntity.class)
public class LocatorPileEntityMixin extends SoulSandEntity implements LocatorPileEntityExt {
    @Unique private double denominator = 250d;

    public LocatorPileEntityMixin(World world) {
        super(world);
    }

    @ModifyConstant(method = "moveTowards", constant = @Constant(doubleValue = 250),remap = false)
    private double useField(double constant){
        return denominator;
    }

    @Override
    public void nightmareMode$moveTowards3D(double x, double y, double z){
        double dX = x - this.posX;
        double dY = y - this.posY;
        double dZ = z - this.posZ;
        double dDistance = Math.sqrt(dX * dX + dZ * dZ + dY * dY) / denominator;
        this.maxDespawnTimer = (int)(200);
        if (dDistance < 0.2) { // technically this should always run, and 0.2 should be scaled to 1/denominator, but it doesn't matter
            this.targetX = this.posX + Math.min(Math.abs(dX), 16.0) * Math.signum(dX) / 4.0;
            this.targetY = this.posY + Math.min(Math.abs(dY), 16.0) * Math.signum(dY) / 4.0;
            this.targetZ = this.posZ + Math.min(Math.abs(dZ), 16.0) * Math.signum(dZ) / 4.0;
            this.maxDespawnTimer += 100;
            this.dataWatcher.updateObject(2, (byte)1);
        } else {
            // this case runs for the blood altar
            this.targetX = this.posX + dX / denominator;
            this.targetY = this.posY + dY / denominator;
            this.targetZ = this.posZ + dZ / denominator;
        }
        this.despawnTimer = 0;
    }

    @Override
    public void nightmareMode$setDenominator(double d) {
        this.denominator = d;
    }

    @Override
    public double nightmareMode$getDenominator() {
        return this.denominator;
    }
}
