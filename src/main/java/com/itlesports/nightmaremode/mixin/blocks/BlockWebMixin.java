package com.itlesports.nightmaremode.mixin.blocks;

import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.BlockWeb;
import net.minecraft.src.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BlockWeb.class)
public class BlockWebMixin {
    @ModifyArg(method = "convertBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemStack;<init>(Lnet/minecraft/src/Item;I)V"), index = 0)
    private Item spiderSilk(Item par1Item)
    {
        return NMItems.spiderSilk;
    }
}
