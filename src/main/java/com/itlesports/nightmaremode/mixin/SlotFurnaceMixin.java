package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemFood;
import net.minecraft.src.ItemStack;
import net.minecraft.src.SlotFurnace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlotFurnace.class)
public class SlotFurnaceMixin {
    @Shadow private EntityPlayer thePlayer;
    @Shadow private int field_75228_b;

    @Inject(method = "onCrafting(Lnet/minecraft/src/ItemStack;)V", at = @At("HEAD"))
    private void trackSkillCookedFood(ItemStack stack, CallbackInfo ci) {
        if (stack != null && stack.getItem() instanceof ItemFood) {
            SkillHandler.incrementFoodCooked(this.thePlayer, this.field_75228_b);
        }
    }
}
