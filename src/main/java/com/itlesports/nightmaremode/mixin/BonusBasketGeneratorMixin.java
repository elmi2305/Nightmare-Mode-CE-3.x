package com.itlesports.nightmaremode.mixin;

import btw.world.feature.BonusBasketGenerator;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BonusBasketGenerator.class)
public class BonusBasketGeneratorMixin {
    @ModifyArg(method = "generate", at = @At(value = "INVOKE", target = "Lbtw/block/tileentity/BarkBoxTileEntity;setStorageStack(Lnet/minecraft/src/ItemStack;)V"))
    private ItemStack makeSpecialDrop(ItemStack stack){
        return new ItemStack(Item.swordStone);
    }
}
