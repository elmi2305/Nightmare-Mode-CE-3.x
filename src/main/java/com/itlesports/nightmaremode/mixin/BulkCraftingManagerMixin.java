package com.itlesports.nightmaremode.mixin;

import api.item.tag.TagOrStack;
import btw.crafting.manager.BulkCraftingManager;
import com.itlesports.nightmaremode.skill.SkillLockedBulkCrafting;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BulkCraftingManager.class)
public abstract class BulkCraftingManagerMixin {
    @Inject(method = "getCraftingResult(Lnet/minecraft/src/IInventory;)Ljava/util/List;", at = @At("HEAD"), cancellable = true)
    private void filterLockedCraftingResult(IInventory inventory, CallbackInfoReturnable<List<ItemStack>> cir) {
        cir.setReturnValue(SkillLockedBulkCrafting.getCraftingResult((BulkCraftingManager)(Object)this, inventory));
    }

    @Inject(method = "getValidCraftingIngrediants", at = @At("HEAD"), cancellable = true)
    private void filterLockedIngredients(IInventory inventory, CallbackInfoReturnable<List<TagOrStack>> cir) {
        cir.setReturnValue(SkillLockedBulkCrafting.getValidIngredients((BulkCraftingManager)(Object)this, inventory));
    }

    @Inject(method = "consumeIngredientsAndReturnResult", at = @At("HEAD"), cancellable = true)
    private void filterLockedConsumption(IInventory inventory, CallbackInfoReturnable<List<ItemStack>> cir) {
        cir.setReturnValue(SkillLockedBulkCrafting.consumeIngredientsAndReturnResult((BulkCraftingManager)(Object)this, inventory));
    }
}
