package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(MobSpawnerBaseLogic.class)
public abstract class MobSpawnerBaseLogicMixin {
    @Shadow private Entity field_98291_j;

    @Shadow public abstract World getSpawnerWorld();

    @Shadow private String mobID;

    @Inject(method = "func_98281_h", at = @At("RETURN"), cancellable = true)
    private void silverfishSpawnerMakesWitherSkellies(CallbackInfoReturnable<Entity> cir){
        if (this.field_98291_j instanceof EntitySilverfish) {
            EntitySkeleton skeleton = new EntitySkeleton(this.getSpawnerWorld());
            skeleton.setSkeletonType(1);
            cir.setReturnValue(skeleton);
        }
    }

    @ModifyArg(method = "func_98265_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;spawnEntityInWorld(Lnet/minecraft/src/Entity;)Z"))
    private Entity spawnWitherSkeletonsInsteadOfSilverfish(Entity par1Entity){
        if (Objects.equals(this.mobID, "Silverfish")) {
            EntitySkeleton skeleton = new EntitySkeleton(par1Entity.worldObj);
            skeleton.copyLocationAndAnglesFrom(par1Entity);
            skeleton.setCurrentItemOrArmor(0, new ItemStack(Item.swordStone));
            skeleton.setSkeletonType(1);
            return skeleton;
        }
        return par1Entity;
    }
}
