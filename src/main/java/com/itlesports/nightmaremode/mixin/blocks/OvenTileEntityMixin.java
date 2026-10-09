package com.itlesports.nightmaremode.mixin.blocks;

import api.block.TileEntityDataPacketHandler;
import btw.block.tileentity.OvenTileEntity;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.util.elements.NMDifficultyParam;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.NMOvenCookTimes;
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

    @Unique private int burnItemId = -1;
    @Unique private int burnItemMetadata;

    @Inject(method = "updateEntity", at = @At(value = "INVOKE", target = "Lbtw/block/tileentity/OvenTileEntity;isBurning()Z", ordinal = 1))
    private void checkIfItemShouldBurn(CallbackInfo ci) {
        if (this.worldObj.isRemote) return;
        ItemStack output = this.furnaceItemStacks[2];
        boolean cake = output != null && output.getItem() == NMItems.chocolateCake;
        boolean food = NMOvenCookTimes.canOvercookToMeat(output)
                && this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class);
        if (output == null || this.furnaceBurnTime <= 0 || (!cake && !food)) {
            this.burnCounter = 0;
            this.burnItemId = -1;
            return;
        }
        if (output.itemID != this.burnItemId || output.getItemDamage() != this.burnItemMetadata) {
            this.burnCounter = 0;
            this.burnItemId = output.itemID;
            this.burnItemMetadata = output.getItemDamage();
        }
        if (++this.burnCounter >= (cake ? 40 : 1600)) {
            this.burnCounter = 0;
            this.burnItemId = -1;
            this.furnaceItemStacks[2] = new ItemStack(cake ? NMItems.burnedChocolateCake : BTWItems.burnedMeat, output.stackSize);
            this.onInventoryChanged();
        }
    }

    @Inject(method = "getCookTimeForCurrentItem", at = @At("HEAD"), cancellable = true)
    private void setJourneyCookTime(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(com.itlesports.nightmaremode.util.EasyBalance.processingTicks(NMOvenCookTimes.getCookTime(this.furnaceItemStacks[0])));
    }

    @Inject(method = "writeToNBT", at = @At("TAIL"))
    private void writeCakeBurnTime(NBTTagCompound tag, CallbackInfo ci) {
        tag.setInteger("NmCakeBurnTime", this.burnCounter);
        tag.setInteger("NmBurnItemId", this.burnItemId);
        tag.setInteger("NmBurnItemMetadata", this.burnItemMetadata);
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"))
    private void readCakeBurnTime(NBTTagCompound tag, CallbackInfo ci) {
        this.burnCounter = tag.getInteger("NmCakeBurnTime");
        ItemStack output = this.furnaceItemStacks[2];
        this.burnItemId = tag.hasKey("NmBurnItemId") ? tag.getInteger("NmBurnItemId") : output == null ? -1 : output.itemID;
        this.burnItemMetadata = tag.hasKey("NmBurnItemMetadata") ? tag.getInteger("NmBurnItemMetadata") : output == null ? 0 : output.getItemDamage();
    }

    @ModifyConstant(method = "updateEntity", constant = @Constant(floatValue = 0.01f, ordinal = 0))
    private float modifyChanceOfFireSpread(float constant) {
        return 10000f;
    }
}
