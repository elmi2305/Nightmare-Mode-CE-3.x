package com.itlesports.nightmaremode.mixin.blocks;

import api.inventory.InventoryUtils;
import btw.block.tileentity.CauldronTileEntity;
import btw.block.tileentity.CookingVesselTileEntity;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.crafting.CauldronBatchCooking;
import com.itlesports.nightmaremode.util.NMCookedFoodTracking;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CauldronTileEntity.class)
public abstract class CauldronTileEntityMixin extends CookingVesselTileEntity {
    @Shadow(remap = false)
    private boolean taintAllNonFoulFoodInInventory() { throw new AssertionError(); }

    @Shadow(remap = false)
    private boolean containsNonFoulFood() { throw new AssertionError(); }

    @Inject(method = "validateContentsForState", at = @At("TAIL"), remap = false)
    private void validateParallelIngredients(CallbackInfo ci) {
        boolean normal = this.fireUnderType == 1;
        boolean stoked = this.fireUnderType == 2;
        this.containsValidIngredientsForState = (normal
                && InventoryUtils.getFirstOccupiedStackOfItem(this, BTWItems.dung.itemID) >= 0
                && this.containsNonFoulFood())
                || (stoked && this.doesContainExplosives())
                || ((normal || stoked) && CauldronBatchCooking.hasRecipe(
                        this.contents, this.getCraftingManager(this.fireUnderType), this.worldObj, normal));
    }

    @Inject(method = "attemptToCookNormal", at = @At("HEAD"), cancellable = true, remap = false)
    private void cookParallelNormal(CallbackInfoReturnable<Boolean> cir) {
        if (this.worldObj == null || this.worldObj.isRemote) {
            cir.setReturnValue(false);
            return;
        }
        if (InventoryUtils.getFirstOccupiedStackOfItem(this, BTWItems.dung.itemID) >= 0
                && this.taintAllNonFoulFoodInInventory()) {
            cir.setReturnValue(true);
            return;
        }
        cir.setReturnValue(cookParallel(1));
    }

    @Override
    protected boolean attemptToCookStoked() {
        return cookParallel(2);
    }

    @Unique
    private boolean cookParallel(int fireType) {
        if (this.worldObj == null || this.worldObj.isRemote) return false;
        ItemStack[] result = CauldronBatchCooking.cook(this.contents, this.getCraftingManager(fireType),
                this.worldObj, fireType == 1, this.getInventoryStackLimit());
        if (result == null) return false;
        ItemStack[] before = this.contents;
        this.contents = result;
        this.onInventoryChanged();
        NMCookedFoodTracking.creditCauldronCycle(this.worldObj, this.xCoord, this.yCoord, this.zCoord, before, result);
        return true;
    }
}
