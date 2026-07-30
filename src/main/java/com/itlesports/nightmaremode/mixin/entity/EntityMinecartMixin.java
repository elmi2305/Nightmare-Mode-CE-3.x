package com.itlesports.nightmaremode.mixin.entity;

import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.interfaces.IHighSpeedMinecart;
import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityMinecart;
import net.minecraft.src.Item;
import net.minecraft.src.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityMinecart.class)
public abstract class EntityMinecartMixin implements IHighSpeedMinecart {
    @Shadow
    protected Item minecartItemToDrop;

    @Unique
    private boolean nightmareMode$highSpeed;

    @Override
    public boolean nightmareMode$isHighSpeed() {
        return this.nightmareMode$highSpeed;
    }

    @Override
    public void nightmareMode$setHighSpeed(boolean highSpeed) {
        this.nightmareMode$highSpeed = highSpeed;
    }

    @ModifyConstant(method = "onUpdate", constant = @Constant(doubleValue = 0.4D))
    private double increaseHighSpeedCartLimit(double original) {
        return this.nightmareMode$highSpeed ? original * 3.0D : original;
    }

    @Inject(method = "writeEntityToNBT", at = @At("TAIL"))
    private void writeHighSpeedState(NBTTagCompound tag, CallbackInfo ci) {
        tag.setBoolean("nmHighSpeed", this.nightmareMode$highSpeed);
    }

    @Inject(method = "readEntityFromNBT", at = @At("TAIL"))
    private void readHighSpeedState(NBTTagCompound tag, CallbackInfo ci) {
        this.nightmareMode$highSpeed = tag.getBoolean("nmHighSpeed");
    }

    @Inject(method = "killMinecart", at = @At("HEAD"))
    private void dropHighSpeedCartItem(DamageSource source, CallbackInfo ci) {
        if (!this.nightmareMode$highSpeed) {
            return;
        }
        EntityMinecart minecart = (EntityMinecart) (Object) this;
        this.minecartItemToDrop = minecart.getMinecartType() == 1
                && this.minecartItemToDrop == Item.minecartCrate
                ? NMItems.highSpeedChestMinecart
                : NMItems.highSpeedMinecart;
    }
}
