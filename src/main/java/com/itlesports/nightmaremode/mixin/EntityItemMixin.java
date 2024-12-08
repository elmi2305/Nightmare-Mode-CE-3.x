package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityItem.class)
public abstract class EntityItemMixin extends Entity {
    @Shadow public abstract ItemStack getEntityItem();

    public EntityItemMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"),cancellable = true)
    private void bloodOrbImmunity(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        if(this.getEntityItem() != null && (this.getEntityItem().itemID == NMItems.bloodOrb.itemID || this.getEntityItem().itemID == Item.netherStar.itemID)){
            cir.setReturnValue(false);
        }
    }
}
