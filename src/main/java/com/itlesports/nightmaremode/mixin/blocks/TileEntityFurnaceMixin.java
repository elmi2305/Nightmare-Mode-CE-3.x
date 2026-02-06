package com.itlesports.nightmaremode.mixin.blocks;

import btw.item.BTWItems;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntityFurnace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(TileEntityFurnace.class)
public class TileEntityFurnaceMixin {
    @Shadow protected ItemStack[] furnaceItemStacks;

    @Unique
    private static final List<Integer> UNIQUE_ITEMS = new ArrayList<>(Arrays.asList(
            BTWItems.unfiredCrudeBrick.itemID,
            BTWItems.unfiredNetherBrick.itemID
    ));

    @Inject(method = "getCookTimeForCurrentItem", at = @At("HEAD"), cancellable = true)
    private void makeClayCookEarly(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = this.furnaceItemStacks[0];
        if (getIsSpecial(stack)){
            cir.setReturnValue(150);
        }
    }


    @Unique private boolean getIsSpecial(ItemStack stack){
        if(stack == null) return false;
        return UNIQUE_ITEMS.contains(stack.itemID);
    }
}
