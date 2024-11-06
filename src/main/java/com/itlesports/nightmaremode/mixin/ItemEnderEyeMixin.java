package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemEnderEye;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemEnderEye.class)
public class ItemEnderEyeMixin {
    @Inject(method = "onItemUse", at = @At(value = "RETURN",ordinal = 0))
    private void a(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10, CallbackInfoReturnable<Boolean> cir){

    }
}
