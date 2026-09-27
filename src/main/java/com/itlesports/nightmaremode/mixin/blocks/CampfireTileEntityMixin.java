package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.tileentity.CampfireTileEntity;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.elements.NMDifficultyParam;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireTileEntity.class)
public class CampfireTileEntityMixin {
    @Shadow(remap = false) private int cookBurningCounter;
    @Shadow(remap = false) private ItemStack cookStack;

    @ModifyVariable(method = "updateCookState", at = @At(value = "STORE"), ordinal = 0, remap = false)
    private int keepSapFromBurning(int fireLevel) {
        return this.cookStack != null && (this.cookStack.itemID == NMItems.cupOfSap.itemID
                || this.cookStack.itemID == NMItems.thickenedSap.itemID)
                ? Math.min(fireLevel, 2) : fireLevel;
    }

    @Inject(method = "getIsFoodBurning", at = @At("HEAD"), cancellable = true, remap = false)
    private void sapIsNotFood(CallbackInfoReturnable<Boolean> cir) {
        if (this.cookStack != null && (this.cookStack.itemID == NMItems.cupOfSap.itemID
                || this.cookStack.itemID == NMItems.thickenedSap.itemID)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "updateCookState", at = @At(value = "FIELD", target ="Lbtw/item/BTWItems;burnedMeat:Lnet/minecraft/src/Item;", shift = At.Shift.AFTER))
    private void incrementBurnTimer(CallbackInfo ci){
        this.cookBurningCounter += (((CampfireTileEntity)(Object)this).worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) ? 5 : 1);
        // food burns 6x faster, taking 20 seconds to burn instead of 2 minutes
    }

    @ModifyConstant(method = "updateEntity", constant = @Constant(floatValue = 0.05f, ordinal = 0))
    private float modifyChanceOfFireSpread(float constant) {
        return 10000f;
    }

    @ModifyConstant(method = "updateCookState", constant = @Constant(intValue = 4800), remap = false)
    private int increaseCookTime(int constant) {
        return 7000;
    }
}
