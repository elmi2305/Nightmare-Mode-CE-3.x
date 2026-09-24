package com.itlesports.nightmaremode.mixin.blocks;

import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.BlockGravel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(BlockGravel.class)
public abstract class BlockGravelMixin {
    @ModifyConstant(method = {"idDropped", "onBlockDestroyedWithImproperTool"}, constant = @Constant(intValue = 10))
    private int increaseFlintChance(int original) {
        MinecraftServer server = MinecraftServer.getServer();
        WorldServer world = server == null ? null : server.worldServerForDimension(0);
        return world != null && NMUtils.getWorldProgress() >= NMFields.HARDMODE ? 6 : 8;
    }
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
