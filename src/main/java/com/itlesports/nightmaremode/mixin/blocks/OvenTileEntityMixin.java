package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.tileentity.OvenTileEntity;
import btw.block.tileentity.TileEntityDataPacketHandler;
import btw.item.BTWItems;
import btw.world.util.difficulty.Difficulties;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntityFurnace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OvenTileEntity.class)
public abstract class OvenTileEntityMixin extends TileEntityFurnace implements TileEntityDataPacketHandler {

    @Shadow public abstract int getItemBurnTime(ItemStack stack);
    @Unique private int burnCounter;

    @Inject(method = "updateEntity", at = @At(value = "INVOKE", target = "Lbtw/block/tileentity/OvenTileEntity;isBurning()Z", ordinal = 1))
    private void checkIfItemShouldBurn(CallbackInfo ci){
        if (this.furnaceItemStacks[2] != null && this.worldObj.getDifficulty() == Difficulties.HOSTILE && this.furnaceBurnTime > 0) {
            if((
                    this.furnaceItemStacks[2].toString().contains("Cooked")
                    || this.furnaceItemStacks[2].toString().contains("Fried")
                    || this.furnaceItemStacks[2].toString().contains("Roast"))
                    && !this.furnaceItemStacks[2].toString().contains("Carrot")){
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
}
