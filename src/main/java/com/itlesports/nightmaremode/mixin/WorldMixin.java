package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.Item;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(World.class)
public class WorldMixin {
    @Inject(method = "isBoundingBoxBurning", at = @At("RETURN"),cancellable = true)
    private void manageBurningItemImmunity(Entity entity, CallbackInfoReturnable<Boolean> cir){
        if(entity instanceof EntityItem item && ((Objects.equals(item.getEntityName(), "item.item.magmaCream")) || Objects.equals(item.getEntityName(), "item.item.blazeRod"))){
            cir.setReturnValue(false);
        }
    }
}
