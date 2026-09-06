package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.ItemSword;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemSword.class)
public class ItemSwordMixin {
    @Shadow
    private float weaponDamage;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void adjustVanillaSwordDamage(int id, EnumToolMaterial material, CallbackInfo ci) {
        if (id == 11) { // iron sword
            this.weaponDamage -= 1.0F;
        }
        else if (id == 20) { // diamond sword
            this.weaponDamage -= 1.0F;
        }
        else if (id == 23) { // diamond axe
            this.weaponDamage -= 1.0F;
        }
    }
}
