package com.itlesports.nightmaremode.mixin.blocks;

import api.item.util.ItemUtils;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.BlockStone;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockStone.class)
public class BlockStoneMixin {
    @Redirect(
            method = "convertBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lapi/item/util/ItemUtils;ejectStackFromBlockTowardsFacing(Lnet/minecraft/src/World;IIILnet/minecraft/src/ItemStack;I)V"))
    private void replaceChiseledStoneBrick(
            World world, int x, int y, int z, ItemStack stack, int side) {
        ItemStack output = stack.getItem() == BTWItems.stoneBrick
                ? new ItemStack(NMItems.roughStoneBrick, stack.stackSize)
                : stack;
        ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, output, side);
    }
}
