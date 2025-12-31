package com.itlesports.nightmaremode.mixin.entity;

import api.entity.mob.KickingAnimal;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.EntityHorse;
import net.minecraft.src.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(KickingAnimal.class)
public abstract class KickingAnimalMixin {
    @Shadow public abstract Vec3 computeKickAttackCenter();

    @ModifyArg(method = "updateKickAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getEntitiesWithinAABB(Ljava/lang/Class;Lnet/minecraft/src/AxisAlignedBB;)Ljava/util/List;"), index = 1)
    private AxisAlignedBB changeTipBox(AxisAlignedBB providedAABB){
        KickingAnimal self = (KickingAnimal) (Object)this;
        if (self instanceof EntityHorse) {
            Vec3 kickCenter = this.computeKickAttackCenter();
            return AxisAlignedBB.getAABBPool().getAABB(kickCenter.xCoord - 1.45, kickCenter.yCoord - 1.2, kickCenter.zCoord - 1.45, kickCenter.xCoord + 1.45, kickCenter.yCoord + 1.2, kickCenter.zCoord + 1.45);
        }
        return providedAABB;
    }
}
