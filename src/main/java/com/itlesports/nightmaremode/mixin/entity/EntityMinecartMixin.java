package com.itlesports.nightmaremode.mixin.entity;

import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.interfaces.IFurnaceMinecartEngine;
import com.itlesports.nightmaremode.util.interfaces.IHighSpeedMinecart;
import com.itlesports.nightmaremode.util.interfaces.ITrainMinecart;
import net.minecraft.src.DamageSource;
import net.minecraft.src.Block;
import net.minecraft.src.BlockRailBase;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityMinecart;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.MathHelper;
import net.minecraft.src.Material;
import net.minecraft.src.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(EntityMinecart.class)
public abstract class EntityMinecartMixin implements IHighSpeedMinecart, ITrainMinecart {
    @Unique
    private static final int NIGHTMARE_MODE_HIGH_SPEED_WATCHER = 23;

    @Shadow
    protected Item minecartItemToDrop;

    @Shadow
    public abstract int getMinecartType();

    @Unique
    private boolean nightmareMode$highSpeed;

    @Unique
    private UUID nightmareMode$trainEngineId;

    @Unique
    private UUID nightmareMode$trainPreviousCartId;

    @Unique
    private int nightmareMode$derailTicks;

    @Unique
    private int nightmareMode$fluidTicks;

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
        EntityMinecart minecart = (EntityMinecart) (Object) this;
        if (minecart.getMinecartType() == 2) {
            this.nightmareMode$detachTrain(minecart.getUniqueID());
        }
        if (!this.nightmareMode$isHighSpeed()) {
            return;
        }
        this.minecartItemToDrop = switch (minecart.getMinecartType()) {
            case 1 -> NMItems.highSpeedChestMinecart;
            case 2 -> NMItems.highSpeedFurnaceMinecart;
            default -> NMItems.highSpeedMinecart;
        };
    }

    @Override
    public UUID nightmareMode$getTrainEngineId() {
        return this.nightmareMode$trainEngineId;
    }

    @Override
    public UUID nightmareMode$getTrainPreviousCartId() {
        return this.nightmareMode$trainPreviousCartId;
    }

    @Override
    public void nightmareMode$joinTrain(UUID engineId, UUID previousCartId) {
        this.nightmareMode$trainEngineId = engineId;
        this.nightmareMode$trainPreviousCartId = previousCartId;
    }

    @Override
    public void nightmareMode$leaveTrain() {
        this.nightmareMode$trainEngineId = null;
        this.nightmareMode$trainPreviousCartId = null;
    }

    @Override
    public int nightmareMode$getTrainLength() {
        EntityMinecart cart = (EntityMinecart) (Object) this;
        if (cart.getMinecartType() != 2) {
            return 1;
        }

        int length = 1;
        EntityMinecart current = cart;
        while ((current = this.nightmareMode$findNextCart(current, cart.getUniqueID())) != null) {
            ++length;
        }
        return length;
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void maintainTrainCouplings(CallbackInfo ci) {
        EntityMinecart cart = (EntityMinecart) (Object) this;
        if (cart.worldObj.isRemote) {
            return;
        }

        if (cart.getMinecartType() == 2 && cart.riddenByEntity instanceof EntityPlayer) {
            this.nightmareMode$attachAdjacentCarts(cart);
        } else if (this.nightmareMode$trainEngineId != null) {
            this.nightmareMode$followPreviousCart(cart);
        }

        if (this.nightmareMode$isInFluid(cart)) {
            if (++this.nightmareMode$fluidTicks >= 20) {
                this.nightmareMode$detachTrain(cart.getMinecartType() == 2
                        ? cart.getUniqueID() : this.nightmareMode$trainEngineId);
            }
        } else {
            this.nightmareMode$fluidTicks = 0;
        }
    }

    @Inject(method = "updateOnTrack", at = @At("HEAD"))
    private void resetEngineDerailTimer(int x, int y, int z, double maxSpeed, double slopeAcceleration,
                                        int railId, int railMeta, CallbackInfo ci) {
        EntityMinecart cart = (EntityMinecart) (Object) this;
        if (cart.getMinecartType() == 2) {
            this.nightmareMode$derailTicks = 0;
        }
    }

    @Inject(method = "updateOnTrack", at = @At("TAIL"))
    private void improvePlayerCartControl(int x, int y, int z, double maxSpeed, double slopeAcceleration,
                                          int railId, int railMeta, CallbackInfo ci) {
        EntityMinecart cart = (EntityMinecart) (Object) this;
        if (!(cart.riddenByEntity instanceof EntityPlayer driver)) {
            if (cart.getMinecartType() == 2) {
                // An unmanned engine is deliberately unable to coast or run from
                // powered rails; leaving the seat brings the whole train to rest.
                cart.motionX *= 0.45D;
                cart.motionZ *= 0.45D;
            }
            return;
        }

        if (cart.getMinecartType() == 2
                && (!(cart instanceof IFurnaceMinecartEngine engine) || !engine.nightmareMode$hasEngineFuel())) {
            cart.motionX *= 0.45D;
            cart.motionZ *= 0.45D;
            return;
        }

        double input = driver.moveForward;
        if (Math.abs(input) < 0.01D) {
            if (cart.getMinecartType() == 2) {
                cart.motionX *= 0.45D;
                cart.motionZ *= 0.45D;
            }
            return;
        }

        double forwardX = -Math.sin(driver.rotationYaw * Math.PI / 180.0D);
        double forwardZ = Math.cos(driver.rotationYaw * Math.PI / 180.0D);
        double speed = Math.sqrt(cart.motionX * cart.motionX + cart.motionZ * cart.motionZ);
        double alignment = speed < 1.0E-4D ? 1.0D
                : (forwardX * cart.motionX + forwardZ * cart.motionZ) / speed;

        if (speed > 0.01D && input * alignment < 0.0D) {
            // Reversing the input is a real brake, not the vanilla cart's very
            // long coast. This applies to both ordinary carts and engines.
            double brakeFactor = this.nightmareMode$isHighSpeed() ? 0.35D : 0.45D;
            cart.motionX *= brakeFactor;
            cart.motionZ *= brakeFactor;
            return;
        }

        double acceleration = cart.getMinecartType() == 2 ? 0.145D : 0.12D;
        if (this.nightmareMode$isHighSpeed()) {
            acceleration *= 1.25D;
        }
        if (cart.getMinecartType() == 2) {
            int extraCarts = Math.max(0, this.nightmareMode$getTrainLength() - 5);
            acceleration /= 1.0D + extraCarts * 0.18D;
        }
        cart.motionX += forwardX * acceleration * input;
        cart.motionZ += forwardZ * acceleration * input;
    }

    @Inject(method = "func_94088_b", at = @At("HEAD"), cancellable = true)
    private void preventCoupledCartDerailment(double maxSpeed, CallbackInfo ci) {
        EntityMinecart cart = (EntityMinecart) (Object) this;
        if (cart.getMinecartType() == 2) {
            if (++this.nightmareMode$derailTicks >= 80) {
                this.nightmareMode$detachTrain(cart.getUniqueID());
                this.nightmareMode$derailTicks = 0;
            }
            return;
        }
        if (cart.getMinecartType() != 2 && this.nightmareMode$trainEngineId != null) {
            // A trailing cart remains coupled to the track even if a turn, chunk
            // load, or relog briefly leaves it without a rail under its wheels.
            cart.setPosition(cart.prevPosX, cart.prevPosY, cart.prevPosZ);
            cart.motionX = 0.0D;
            cart.motionY = 0.0D;
            cart.motionZ = 0.0D;
            ci.cancel();
        }
    }

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Entity;applyEntityCollision(Lnet/minecraft/src/Entity;)V"))
    private void ignoreCollisionsInsideTrain(Entity cartToPush, Entity collidingCart) {
        if (cartToPush instanceof EntityMinecart left && collidingCart instanceof EntityMinecart right
                && this.nightmareMode$shareTrain(left, right)) {
            return;
        }
        cartToPush.applyEntityCollision(collidingCart);
    }

    @Inject(method = "writeEntityToNBT", at = @At("TAIL"))
    private void writeTrainCoupling(NBTTagCompound tag, CallbackInfo ci) {
        if (this.nightmareMode$trainEngineId != null) {
            tag.setLong("nmTrainEngineMost", this.nightmareMode$trainEngineId.getMostSignificantBits());
            tag.setLong("nmTrainEngineLeast", this.nightmareMode$trainEngineId.getLeastSignificantBits());
        }
        if (this.nightmareMode$trainPreviousCartId != null) {
            tag.setLong("nmTrainPreviousMost", this.nightmareMode$trainPreviousCartId.getMostSignificantBits());
            tag.setLong("nmTrainPreviousLeast", this.nightmareMode$trainPreviousCartId.getLeastSignificantBits());
        }
        tag.setInteger("nmTrainDerailTicks", this.nightmareMode$derailTicks);
        tag.setInteger("nmTrainFluidTicks", this.nightmareMode$fluidTicks);
    }

    @Inject(method = "readEntityFromNBT", at = @At("TAIL"))
    private void readTrainCoupling(NBTTagCompound tag, CallbackInfo ci) {
        this.nightmareMode$leaveTrain();
        if (tag.hasKey("nmTrainEngineMost") && tag.hasKey("nmTrainEngineLeast")) {
            this.nightmareMode$trainEngineId = new UUID(tag.getLong("nmTrainEngineMost"), tag.getLong("nmTrainEngineLeast"));
        }
        if (tag.hasKey("nmTrainPreviousMost") && tag.hasKey("nmTrainPreviousLeast")) {
            this.nightmareMode$trainPreviousCartId = new UUID(tag.getLong("nmTrainPreviousMost"), tag.getLong("nmTrainPreviousLeast"));
        }
        this.nightmareMode$derailTicks = tag.getInteger("nmTrainDerailTicks");
        this.nightmareMode$fluidTicks = tag.getInteger("nmTrainFluidTicks");
    }

    @Unique
    private void nightmareMode$attachAdjacentCarts(EntityMinecart engine) {
        EntityMinecart last = engine;
        EntityMinecart next;
        while ((next = this.nightmareMode$findNextCart(last, engine.getUniqueID())) != null) {
            last = next;
        }

        EntityMinecart candidate = null;
        double bestDistance = 1.7D * 1.7D;
        for (Object object : engine.worldObj.loadedEntityList) {
            if (!(object instanceof EntityMinecart other) || other.isDead || other.getMinecartType() > 1) {
                continue;
            }
            ITrainMinecart trainCart = (ITrainMinecart) other;
            if (trainCart.nightmareMode$getTrainEngineId() != null) {
                continue;
            }
            double dx = other.posX - last.posX;
            double dy = other.posY - last.posY;
            double dz = other.posZ - last.posZ;
            double distance = dx * dx + dz * dz;
            if (Math.abs(dy) <= 1.25D && distance < bestDistance) {
                bestDistance = distance;
                candidate = other;
            }
        }
        if (candidate != null) {
            ((ITrainMinecart) candidate).nightmareMode$joinTrain(engine.getUniqueID(), last.getUniqueID());
        }
    }

    @Unique
    private void nightmareMode$followPreviousCart(EntityMinecart cart) {
        EntityMinecart previous = this.nightmareMode$findCart(this.nightmareMode$trainPreviousCartId);
        EntityMinecart engine = this.nightmareMode$findCart(this.nightmareMode$trainEngineId);
        if (previous == null || engine == null) {
            return; // Chunks load independently; persistent UUIDs reconnect them.
        }

        double dx = previous.posX - cart.posX;
        double dz = previous.posZ - cart.posZ;
        double distance = Math.sqrt(dx * dx + dz * dz);
        if (distance > 2.0D) {
            // Covers a relog/chunk-reload tick before normal rail motion resumes.
            cart.setPosition(previous.prevPosX, previous.prevPosY, previous.prevPosZ);
            cart.motionX = previous.motionX;
            cart.motionY = previous.motionY;
            cart.motionZ = previous.motionZ;
            return;
        }
        if (distance > 0.9D) {
            double pull = Math.min(0.20D, (distance - 0.9D) * 0.22D);
            cart.motionX += dx / distance * pull;
            cart.motionZ += dz / distance * pull;
        }
        cart.motionX = (cart.motionX + previous.motionX) * 0.5D;
        cart.motionZ = (cart.motionZ + previous.motionZ) * 0.5D;
    }

    @Unique
    private EntityMinecart nightmareMode$findNextCart(EntityMinecart previous, UUID engineId) {
        for (Object object : previous.worldObj.loadedEntityList) {
            if (object instanceof EntityMinecart cart && !cart.isDead) {
                ITrainMinecart trainCart = (ITrainMinecart) cart;
                if (engineId.equals(trainCart.nightmareMode$getTrainEngineId())
                        && previous.getUniqueID().equals(trainCart.nightmareMode$getTrainPreviousCartId())) {
                    return cart;
                }
            }
        }
        return null;
    }

    @Unique
    private EntityMinecart nightmareMode$findCart(UUID id) {
        if (id == null) {
            return null;
        }
        EntityMinecart self = (EntityMinecart) (Object) this;
        for (Object object : self.worldObj.loadedEntityList) {
            if (object instanceof EntityMinecart cart && id.equals(cart.getUniqueID()) && !cart.isDead) {
                return cart;
            }
        }
        return null;
    }

    @Unique
    private boolean nightmareMode$shareTrain(EntityMinecart left, EntityMinecart right) {
        UUID leftEngine = left.getMinecartType() == 2 ? left.getUniqueID()
                : ((ITrainMinecart) left).nightmareMode$getTrainEngineId();
        UUID rightEngine = right.getMinecartType() == 2 ? right.getUniqueID()
                : ((ITrainMinecart) right).nightmareMode$getTrainEngineId();
        return leftEngine != null && leftEngine.equals(rightEngine);
    }

    @Unique
    private boolean nightmareMode$isInFluid(EntityMinecart cart) {
        return cart.worldObj.isMaterialInBB(cart.boundingBox, Material.water)
                || cart.worldObj.isMaterialInBB(cart.boundingBox, Material.lava);
    }

    @Unique
    private void nightmareMode$detachTrain(UUID engineId) {
        if (engineId == null) {
            return;
        }
        EntityMinecart self = (EntityMinecart) (Object) this;
        for (Object object : self.worldObj.loadedEntityList) {
            if (object instanceof EntityMinecart cart && !cart.isDead) {
                ITrainMinecart trainCart = (ITrainMinecart) cart;
                if (engineId.equals(trainCart.nightmareMode$getTrainEngineId())) {
                    trainCart.nightmareMode$leaveTrain();
                }
            }
        }
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
