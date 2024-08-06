package com.itlesports.nightmaremode.mixin;

import btw.item.items.FireStarterItem;
import net.minecraft.src.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


// I am utterly ashamed of this code. it modifies the fire plough durability because I kept crashing if I tried to modify
// it in a different way. will probably re-do this code eventually
@Mixin(FireStarterItem.class)
public class FireStarterItemMixin extends Item {
    public FireStarterItemMixin(int par1) {
        super(par1);
    }

    @Inject(method = "<init>", at = @At("TAIL"),remap = false)
    private void modifyFirePLoughDurability(int iItemID, int iMaxUses, float fExhaustionPerUse, CallbackInfo ci){
        if(fExhaustionPerUse==0.05F){
            this.setMaxDamage(200);
        }
    }
}
