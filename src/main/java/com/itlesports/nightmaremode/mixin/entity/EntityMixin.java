package com.itlesports.nightmaremode.mixin.entity;

import api.entity.mob.KickingAnimal;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.util.interfaces.PhaseTransitEntity;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements PhaseTransitEntity {

    @Unique private int nm$phaseOrigin = -1;
    @Unique private boolean nm$mustLeavePhasePortal;

    @Shadow public abstract void flingAwayFromEntity(Entity repulsingEntity, double dForceMultiplier);

    @Override public int nightmareMode$getPhaseOrigin() { return this.nm$phaseOrigin; }
    @Override public void nightmareMode$setPhaseOrigin(int origin) { this.nm$phaseOrigin = origin; }
    @Override public boolean nm$mustLeavePhasePortal() { return this.nm$mustLeavePhasePortal; }
    @Override public void nm$setMustLeavePhasePortal(boolean mustLeave) { this.nm$mustLeavePhasePortal = mustLeave; }

    @Inject(method = "writeToNBT", at = @At("TAIL"))
    private void writePhaseOrigin(NBTTagCompound tag, CallbackInfo ci) {
        if (this.nm$phaseOrigin >= 0) tag.setInteger("NmPhaseOrigin", this.nm$phaseOrigin);
        if (this.nm$mustLeavePhasePortal) tag.setBoolean("NmMustLeavePhasePortal", true);
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"))
    private void readPhaseOrigin(NBTTagCompound tag, CallbackInfo ci) {
        this.nm$phaseOrigin = tag.hasKey("NmPhaseOrigin") ? tag.getInteger("NmPhaseOrigin") : -1;
        this.nm$mustLeavePhasePortal = tag.getBoolean("NmMustLeavePhasePortal");
    }

    @Inject(method = "onEntityUpdate", at = @At("TAIL"))
    private void releasePhasePortalExitLock(CallbackInfo ci) {
        Entity entity = (Entity)(Object)this;
        if (entity.worldObj.isRemote || !this.nm$mustLeavePhasePortal) return;
        AxisAlignedBB box = entity.boundingBox;
        int minX = MathHelper.floor_double(box.minX);
        int maxX = MathHelper.floor_double(box.maxX - 1.0E-7D);
        int minY = MathHelper.floor_double(box.minY);
        int maxY = MathHelper.floor_double(box.maxY - 1.0E-7D);
        int minZ = MathHelper.floor_double(box.minZ);
        int maxZ = MathHelper.floor_double(box.maxZ - 1.0E-7D);

        for (int x = minX; x <= maxX; ++x) for (int y = minY; y <= maxY; ++y) for (int z = minZ; z <= maxZ; ++z) {
            if (entity.worldObj.getBlockId(x, y, z) == NMBlocks.phasePortal.blockID) return;
        }
        this.nm$mustLeavePhasePortal = false;
    }


    @Inject(method = "tryToSetFireToBlocksInContact", at = @At("HEAD"), cancellable = true)
    private void manageFireSpreadFromBurningEntities(CallbackInfo ci){
        Entity self = (Entity) (Object) this;
        int prog = NMUtils.getWorldProgress();

        if(prog > 0 && self instanceof EntityZombie) return;

        if (prog > 1) return;

        ci.cancel();
    }

    @Inject(method = "getBlockExplosionResistance", at = @At("HEAD"),cancellable = true)
    private void injectCorrectlyParameterizedExplosionMethod(Explosion par1Explosion, World par2World, int par3, int par4, int par5, Block par6Block, CallbackInfoReturnable<Float> cir){
        Entity thisObj = (Entity) (Object) this;
        cir.setReturnValue(par6Block.getExplosionResistance(thisObj, par2World, par3, par4, par5));
        return;
    }


    @Inject(method = "updateRiderPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Entity;setPosition(DDD)V", shift = At.Shift.AFTER))
    private void riderHeightOffset(CallbackInfo ci) {
        Entity thisObj = (Entity) (Object) this;
        if (thisObj.riddenByEntity instanceof EntityEnderCrystal) {
            thisObj.riddenByEntity.setPosition(thisObj.posX, thisObj.posY - 0.5125D + thisObj.riddenByEntity.getYOffset(), thisObj.posZ);
        }
    }

    @Redirect(method = "onStruckByLightning", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Entity;dealFireDamage(I)V"))
    private void endermenImmuneToLightning(Entity instance, int par1) {
        Entity thisObj = (Entity) (Object) this;
        if (!thisObj.isImmuneToFire() && !(thisObj instanceof EntityEnderman)) {
            thisObj.attackEntityFrom(DamageSource.inFire, par1);
        }
    }

    @Redirect(method = "moveEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Entity;isSneaking()Z"))
    private boolean manageAprilFoolsSneaking(Entity instance){
        if(NightmareMode.isAprilFools){
            return false;
        }
        return instance.isSneaking();
    }

    @Inject(method = "onKickedByAnimal", at = @At("HEAD"),cancellable = true)
    private void increaseKickKnockbackOnEclipse(KickingAnimal kickingAnimal, CallbackInfo ci){
        if(NMUtils.getIsMobEclipsed(kickingAnimal)){
            this.flingAwayFromEntity(kickingAnimal, 3);
            ci.cancel();
        }
    }
}
