package com.itlesports.nightmaremode.mixin.entity;

import api.entity.mob.KickingAnimal;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.util.interfaces.CarcassAnimal;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KickingAnimal.class)
public abstract class KickingAnimalMixin extends EntityAnimal {
    public KickingAnimalMixin(World par1World) {
        super(par1World);
    }

    @Shadow public abstract Vec3 computeKickAttackCenter();

    @ModifyArg(method = "updateKickAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getEntitiesWithinAABB(Ljava/lang/Class;Lnet/minecraft/src/AxisAlignedBB;)Ljava/util/List;"), index = 1)
    private AxisAlignedBB changeTipBox(AxisAlignedBB providedAABB){
        KickingAnimal self = (KickingAnimal) (Object)this;
        if (self instanceof EntityHorse) {
            Vec3 kickCenter = this.computeKickAttackCenter();
            return AxisAlignedBB.getAABBPool().getAABB(kickCenter.xCoord - 1.45, kickCenter.yCoord - 1.2, kickCenter.zCoord - 1.45, kickCenter.xCoord + 1.45, kickCenter.yCoord + 1.2, kickCenter.zCoord + 1.45);
        } else if (self instanceof EntityCow){
            Vec3 kickCenter = this.computeKickAttackCenter();
            double horizontalOffsetMin = 1.0;
            double verticalOffsetMin = 0.85;
            double verticalOffsetMax = 1.2;
            double horizontalOffsetMax = 1.45;

            double worldTimeReach = NMUtils.getFirstFiveDaysMultiplier(worldObj);

            double horizontalOffset = horizontalOffsetMin + (horizontalOffsetMax - horizontalOffsetMin) * worldTimeReach;
            double verticalOffset = verticalOffsetMin + (verticalOffsetMax - verticalOffsetMin) * worldTimeReach;

            return AxisAlignedBB.getAABBPool().getAABB(kickCenter.xCoord - horizontalOffset, kickCenter.yCoord - verticalOffset, kickCenter.zCoord - horizontalOffset, kickCenter.xCoord + horizontalOffset, kickCenter.yCoord + verticalOffset, kickCenter.zCoord + horizontalOffset);

        }
        return providedAABB;
    }
    @Inject(method = "updateKickAttack", at = @At("HEAD"),cancellable = true, remap = false)
    private void corpsesDoNotKick(CallbackInfo ci) {
        KickingAnimal self = (KickingAnimal) (Object)this;

        if(self instanceof CarcassAnimal carcassAnimal && carcassAnimal.nm$isCarcass()){
            ci.cancel();
        }
    }
}
