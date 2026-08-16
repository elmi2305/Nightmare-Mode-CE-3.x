package com.itlesports.nightmaremode.mixin.entity;

import com.itlesports.nightmaremode.util.interfaces.IHighSpeedMinecart;
import com.itlesports.nightmaremode.util.interfaces.IFurnaceMinecartEngine;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityMinecartFurnace;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityMinecartFurnace.class)
public abstract class EntityMinecartFurnaceMixin implements IFurnaceMinecartEngine {
    private static final int FUEL_PER_COAL = 3600;
    private static final int STANDARD_FUEL_CAPACITY = 28800;
    private static final int HIGH_SPEED_FUEL_CAPACITY = 43200;

    @Shadow private int fuel;
    @Shadow public double pushX;
    @Shadow public double pushZ;

    private boolean nightmareMode$hadFuel;

    @Override
    public boolean nightmareMode$hasEngineFuel() {
        return this.fuel > 0;
    }

    /**
     * The vanilla furnace cart is an autonomous pusher.  An engine only accepts
     * power from its driver, so clear that push before its superclass moves it.
     */
    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void preparePlayerDrivenEngine(CallbackInfo ci) {
        this.nightmareMode$hadFuel = this.fuel > 0;
        this.pushX = 0.0D;
        this.pushZ = 0.0D;
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void consumeFuelOnlyWhileMoving(CallbackInfo ci) {
        EntityMinecartFurnace engine = (EntityMinecartFurnace) (Object) this;
        double speedSquared = engine.motionX * engine.motionX + engine.motionZ * engine.motionZ;

        // Vanilla consumes once per tick. Restore that tick when the engine did
        // not actually travel, including while the driver is braking.
        if (this.nightmareMode$hadFuel && speedSquared < 1.0E-4D) {
            ++this.fuel;
        }
        this.nightmareMode$refuelFromDriver(engine);
    }

    @Inject(method = "interactFirst", at = @At("HEAD"), cancellable = true)
    private void fuelAndBoardEngine(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        EntityMinecartFurnace engine = (EntityMinecartFurnace) (Object) this;
        if (engine.riddenByEntity != null && engine.riddenByEntity != player) {
            cir.setReturnValue(true);
            return;
        }

        ItemStack held = player.inventory.getCurrentItem();
        if (held != null && held.itemID == Item.coal.itemID) {
            int capacity = engine instanceof IHighSpeedMinecart highSpeed && highSpeed.nightmareMode$isHighSpeed()
                    ? HIGH_SPEED_FUEL_CAPACITY : STANDARD_FUEL_CAPACITY;
            if (this.fuel < capacity) {
                this.fuel = Math.min(capacity, this.fuel + FUEL_PER_COAL);
                if (!player.capabilities.isCreativeMode && --held.stackSize == 0) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }
            }
        }

        if (!engine.worldObj.isRemote) {
            player.mountEntity(engine);
        }
        cir.setReturnValue(true);
    }

    private void nightmareMode$refuelFromDriver(EntityMinecartFurnace engine) {
        if (!(engine.riddenByEntity instanceof EntityPlayer player)) {
            return;
        }
        int capacity = engine instanceof IHighSpeedMinecart highSpeed && highSpeed.nightmareMode$isHighSpeed()
                ? HIGH_SPEED_FUEL_CAPACITY : STANDARD_FUEL_CAPACITY;
        if (this.fuel >= FUEL_PER_COAL || this.fuel > capacity - FUEL_PER_COAL) {
            return;
        }
        ItemStack held = player.inventory.getCurrentItem();
        if (held == null || held.itemID != Item.coal.itemID) {
            return;
        }
        this.fuel += FUEL_PER_COAL;
        if (!player.capabilities.isCreativeMode && --held.stackSize == 0) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
        }
    }
}
