package com.itlesports.nightmaremode.mixin.blocks;

import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.BlockGravel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(BlockGravel.class)
public abstract class BlockGravelMixin {
    @Inject(method = "idDropped", at = @At("RETURN"), cancellable = true)
    private void replaceFlintDropWithChips(int metadata, Random random, int fortune, CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() == net.minecraft.src.Item.flint.itemID) {
            cir.setReturnValue(NMItems.flintChip.itemID);
        }
    }

    @ModifyArg(method = "onBlockDestroyedWithImproperTool", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/BlockGravel;dropItemsIndividually(Lnet/minecraft/src/World;IIIIIIF)V"), index = 4)
    private int replaceImproperToolFlintDrop(int itemId) {
        return itemId == net.minecraft.src.Item.flint.itemID ? NMItems.flintChip.itemID : itemId;
    }
}
