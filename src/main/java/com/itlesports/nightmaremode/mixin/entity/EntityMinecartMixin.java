package com.itlesports.nightmaremode.mixin.entity;

import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.interfaces.IHighSpeedMinecart;
import net.minecraft.src.DamageSource;
import net.minecraft.src.Block;
import net.minecraft.src.BlockRailBase;
import net.minecraft.src.EntityMinecart;
import net.minecraft.src.Item;
import net.minecraft.src.MathHelper;
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
    @Unique
    private static final int NIGHTMARE_MODE_HIGH_SPEED_WATCHER = 23;

    @Shadow
    protected Item minecartItemToDrop;

    @Unique
    private boolean nightmareMode$highSpeed;

    @Inject(method = "entityInit", at = @At("TAIL"))
    private void initializeHighSpeedWatcher(CallbackInfo ci) {
        ((EntityMinecart) (Object) this).getDataWatcher()
                .addObject(NIGHTMARE_MODE_HIGH_SPEED_WATCHER, (byte) 0);
    }

    @Override
    public boolean nightmareMode$isHighSpeed() {
        EntityMinecart minecart = (EntityMinecart) (Object) this;
        return this.nightmareMode$highSpeed
                || minecart.getDataWatcher().getWatchableObjectByte(NIGHTMARE_MODE_HIGH_SPEED_WATCHER) != 0;
    }

    @Override
    public void nightmareMode$setHighSpeed(boolean highSpeed) {
        this.nightmareMode$highSpeed = highSpeed;
        ((EntityMinecart) (Object) this).getDataWatcher()
                .updateObject(NIGHTMARE_MODE_HIGH_SPEED_WATCHER, (byte) (highSpeed ? 1 : 0));
    }

    @ModifyConstant(method = "onUpdate", constant = @Constant(doubleValue = 0.4D))
    private double increaseHighSpeedCartLimit(double original) {
        if (!this.nightmareMode$isHighSpeed()) {
            return original;
        }
        return this.nightmareMode$isApproachingCurveOrIncline() ? original : original * 3.0D;
    }

    @Inject(method = "writeEntityToNBT", at = @At("TAIL"))
    private void writeHighSpeedState(NBTTagCompound tag, CallbackInfo ci) {
        tag.setBoolean("nmHighSpeed", this.nightmareMode$highSpeed);
    }

    @Inject(method = "readEntityFromNBT", at = @At("TAIL"))
    private void readHighSpeedState(NBTTagCompound tag, CallbackInfo ci) {
        this.nightmareMode$setHighSpeed(tag.getBoolean("nmHighSpeed"));
    }

    @Inject(method = "killMinecart", at = @At("HEAD"))
    private void dropHighSpeedCartItem(DamageSource source, CallbackInfo ci) {
        if (!this.nightmareMode$isHighSpeed()) {
            return;
        }
        EntityMinecart minecart = (EntityMinecart) (Object) this;
        this.minecartItemToDrop = minecart.getMinecartType() == 1
                && this.minecartItemToDrop == Item.minecartCrate
                ? NMItems.highSpeedChestMinecart
                : NMItems.highSpeedMinecart;
    }

    @Unique
    private boolean nightmareMode$isApproachingCurveOrIncline() {
        EntityMinecart minecart = (EntityMinecart) (Object) this;
        int motionX = minecart.motionX > 0.01D ? 1 : minecart.motionX < -0.01D ? -1 : 0;
        int motionZ = minecart.motionZ > 0.01D ? 1 : minecart.motionZ < -0.01D ? -1 : 0;
        int baseX = MathHelper.floor_double(minecart.posX);
        int baseY = MathHelper.floor_double(minecart.posY);
        int baseZ = MathHelper.floor_double(minecart.posZ);

        for (int step = 0; step <= 2; ++step) {
            int x = baseX + motionX * step;
            int z = baseZ + motionZ * step;
            for (int yOffset = -1; yOffset <= 1; ++yOffset) {
                int y = baseY + yOffset;
                int blockId = minecart.worldObj.getBlockId(x, y, z);
                if (!BlockRailBase.isRailBlock(blockId)) {
                    continue;
                }
                int metadata = minecart.worldObj.getBlockMetadata(x, y, z);
                if (((BlockRailBase) Block.blocksList[blockId]).isPowered()) {
                    metadata &= 7;
                }
                if (metadata >= 6 && metadata <= 9) {
                    return true;
                }
                if ((metadata == 2 && motionX > 0)
                        || (metadata == 3 && motionX < 0)
                        || (metadata == 4 && motionZ < 0)
                        || (metadata == 5 && motionZ > 0)) {
                    return true;
                }
            }
        }
        return false;
    }
}
