package com.itlesports.nightmaremode.mixin.blocks;

import api.block.TileEntityDataPacketHandler;
import btw.block.tileentity.OvenTileEntity;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.util.elements.NMDifficultyParam;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntityFurnace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OvenTileEntity.class)
public abstract class OvenTileEntityMixin extends TileEntityFurnace implements TileEntityDataPacketHandler {

    @Shadow public abstract int getItemBurnTime(ItemStack stack);
    @Unique private int burnCounter;

    @Inject(method = "updateEntity", at = @At(value = "INVOKE", target = "Lbtw/block/tileentity/OvenTileEntity;isBurning()Z", ordinal = 1))
    private void checkIfItemShouldBurn(CallbackInfo ci){
        if (this.furnaceItemStacks[2] != null && this.furnaceItemStacks[2].itemID == NMItems.chocolateCake.itemID) {
            if (this.furnaceBurnTime > 0 && ++this.burnCounter >= 40) {
                this.burnCounter = 0;
                this.furnaceItemStacks[2] = new ItemStack(NMItems.burnedChocolateCake);
                this.onInventoryChanged();
            } else if (this.furnaceBurnTime <= 0) {
                this.burnCounter = 0;
            }
            return;
        }
        if (this.furnaceItemStacks[2] != null && this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) && this.furnaceBurnTime > 0) {
            String cookName = this.furnaceItemStacks[2].toString();
            if(cookName.contains("Cooked") || cookName.contains("Fried") || cookName.contains("Roast")){
                if(cookName.contains("Carrot")) return;
                this.burnCounter++;
                if(this.burnCounter >= 1600) {
                    this.burnCounter = 0;
                    ItemStack var2 = new ItemStack(BTWItems.burnedMeat);
                    this.furnaceItemStacks[2] = var2.copy();
                }
            }
        } else{
            this.burnCounter = 0;
        }
    }

    @Inject(method = "getCookTimeForCurrentItem", at = @At("HEAD"), cancellable = true)
    private void setChocolateCakeCookTime(CallbackInfoReturnable<Integer> cir) {
        if (this.furnaceItemStacks[0] != null && this.furnaceItemStacks[0].itemID == NMItems.unbakedChocolateCake.itemID) {
            cir.setReturnValue(2400);
        }
    }

    @Inject(method = "writeToNBT", at = @At("TAIL"))
    private void writeCakeBurnTime(NBTTagCompound tag, CallbackInfo ci) {
        tag.setInteger("NmCakeBurnTime", this.burnCounter);
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"))
    private void readCakeBurnTime(NBTTagCompound tag, CallbackInfo ci) {
        this.burnCounter = tag.getInteger("NmCakeBurnTime");
    }

    @ModifyConstant(method = "updateEntity", constant = @Constant(floatValue = 0.01f, ordinal = 0))
    private float modifyChanceOfFireSpread(float constant) {
        return 10000f;
    }
}
